package tech.sud.mgp.hello.ui.scenes.quiz.widget;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ClickUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.app.APPConfig;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.widget.view.round.RoundedImageView;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.GetAccountResp;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.service.room.resp.QuizGamePlayerResp;
import tech.sud.mgp.hello.ui.common.utils.FormatUtils;

/**
 * 竞猜场景内，猜玩家输赢的弹窗
 */
public class QuizGuessDialog extends BaseDialogFragment {

    private RecyclerView recyclerView;
    private TextView tvConfirm;
    private TextView tvCoin;
    private TextView tvEmpty;

    private long roomId;
    private List<Long> players;
    private MyAdapter adapter = new MyAdapter();
    public int spanCount = 3;
    private int gameState; // 游戏状态

    private BetListener betListener;

    public int singleGuessMoney = APPConfig.QUIZ_SINGLE_BET_COUNT; // 猜单个人，固定数量

    public static QuizGuessDialog newInstance(long roomId, List<Long> players, int gameState) {
        QuizGuessDialog dialog = new QuizGuessDialog();
        Bundle bundle = new Bundle();
        GuessDialogParams params = new GuessDialogParams();
        params.roomId = roomId;
        params.players = players;
        params.gameState = gameState;
        bundle.putSerializable("params", params);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            GuessDialogParams params = (GuessDialogParams) arguments.getSerializable("params");
            if (params != null) {
                roomId = params.roomId;
                players = params.players;
                gameState = params.gameState;
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_quiz_guess;
    }

    @Override
    protected int getWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getHeight() {
        return (int) (DensityUtils.getScreenHeight() * 0.77f);
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected void customStyle(Window window) {
        super.customStyle(window);
        window.setWindowAnimations(R.style.BottomToTopAnim);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        recyclerView = findViewById(R.id.recycler_view);
        tvConfirm = findViewById(R.id.tv_confirm);
        tvCoin = findViewById(R.id.tv_coin);
        tvEmpty = findViewById(R.id.tv_empty);

        recyclerView.addItemDecoration(new MyItemDecoration());
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), spanCount, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        RecyclerView.ItemAnimator itemAnimator = recyclerView.getItemAnimator();
        if (itemAnimator != null) {
            itemAnimator.setChangeDuration(0);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        HomeRepository.getAccount(this, new RxCallback<GetAccountResp>() {
            @Override
            public void onSuccess(GetAccountResp getAccountResp) {
                super.onSuccess(getAccountResp);
                if (getAccountResp != null) {
                    tvCoin.setText(FormatUtils.formatMoney(getAccountResp.coin));
                }
            }
        });
        if (players != null && players.size() > 0) {
            RoomRepository.quizGamePlayer(this, roomId, players, new RxCallback<QuizGamePlayerResp>() {
                @Override
                public void onSuccess(QuizGamePlayerResp resp) {
                    super.onSuccess(resp);
                    if (resp != null) {
                        adapter.setList(resp.playerList);
                    }
                    if (resp == null || resp.playerList == null || resp.playerList.size() == 0) {
                        tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                    }
                }
            });
        }
        updateConfirmBtn();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter, @NonNull View view, int position) {
                if (gameState != SudMGPMGState.MGCommonGameState.IDLE) {
                    return;
                }
                QuizGamePlayerResp.Player item = adapter.getItem(position);
                if (item.support) return;
                item.isSelected = !item.isSelected;
                adapter.notifyItemChanged(position);
                updateConfirmBtn();
            }
        });
        ClickUtils.applySingleDebouncing(tvConfirm, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<QuizGamePlayerResp.Player> list = getSelectedList();
                if (list.size() == 0) {
                    return;
                }
                dismiss();
                if (betListener != null) {
                    betListener.onSelected(list);
                }
            }
        });
    }

    public void setGameState(int gameState) {
        this.gameState = gameState;
        if (tvConfirm != null) {
            updateConfirmBtn();
        }
    }

    private void updateConfirmBtn() {
        if (gameState == SudMGPMGState.MGCommonGameState.IDLE) {
            List<QuizGamePlayerResp.Player> list = getSelectedList();
            if (list.size() == 0) {
                tvConfirm.setEnabled(false);
                tvConfirm.setText(getString(R.string.affirm_coin, singleGuessMoney + ""));
            } else {
                tvConfirm.setEnabled(true);
                tvConfirm.setText(getString(R.string.affirm_coin, (singleGuessMoney * list.size()) + ""));
            }
        } else {
            tvConfirm.setEnabled(false);
            tvConfirm.setText(R.string.is_end);
        }
    }

    private List<QuizGamePlayerResp.Player> getSelectedList() {
        List<QuizGamePlayerResp.Player> list = new ArrayList<>();
        for (QuizGamePlayerResp.Player item : adapter.getData()) {
            if (item.isSelected && !item.support) {
                list.add(item);
            }
        }
        return list;
    }

    private class MyItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int position = parent.getChildAdapterPosition(view);
            int columnPostion = position % spanCount;
            if (columnPostion == 0) { // 第一个
                outRect.left = DensityUtils.dp2px(16);
                outRect.right = DensityUtils.dp2px(19);
            } else if (columnPostion == (spanCount - 1)) { // 最后一个
                outRect.left = DensityUtils.dp2px(19);
                outRect.right = DensityUtils.dp2px(16);
            } else {
                outRect.left = DensityUtils.dp2px(17.5f);
                outRect.right = DensityUtils.dp2px(17.5f);
            }
        }
    }

    private static class MyAdapter extends BaseQuickAdapter<QuizGamePlayerResp.Player, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.item_quiz_guess);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, QuizGamePlayerResp.Player model) {
            RoundedImageView ivIcon = holder.getView(R.id.iv_icon);
            ImageLoader.loadAvatar(ivIcon, model.header);

            TextView tvGuess = holder.getView(R.id.tv_guess);
            tvGuess.setEnabled(model.isSelected || model.support);
            if (model.support) {
                tvGuess.setText(R.string.have_support);
            } else {
                tvGuess.setText(R.string.guess_he_win);
            }

            holder.setVisible(R.id.view_selected, model.isSelected || model.support);

            holder.setText(R.id.tv_support_count, getContext().getString(R.string.support_count, model.supportedUserCount + ""));
        }
    }

    public static class GuessDialogParams implements Serializable {
        public long roomId;
        public List<Long> players;
        public int gameState;
    }

    public void setBetListener(BetListener betListener) {
        this.betListener = betListener;
    }

    public interface BetListener {
        void onSelected(List<QuizGamePlayerResp.Player> list);
    }

}
