package tech.sud.mgp.audio.example.widget.dialog;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.audio.R;
import tech.sud.mgp.common.base.BaseDialogFragment;
import tech.sud.mgp.common.http.rx.RxCallback;
import tech.sud.mgp.common.http.use.repository.CommonRepository;
import tech.sud.mgp.common.http.use.resp.GameListResp;
import tech.sud.mgp.common.http.use.resp.GameModel;
import tech.sud.mgp.common.utils.DensityUtils;
import tech.sud.mgp.common.utils.ImageLoader;

/**
 * 选择游戏模式的弹窗
 */
public class GameModeDialog extends BaseDialogFragment {

    private RecyclerView recyclerView;
    private final int spanCount = 4;
    private final GameModeAdapter gameModeAdapter = new GameModeAdapter();
    private long playingGameId; // 当前正在玩的游戏id
    private SelectGameModeListener selectGameModeListener;

    /**
     * 设置当前正在玩的游戏id
     *
     * @param playingGameId
     */
    public void setPlayingGameId(long playingGameId) {
        this.playingGameId = playingGameId;
    }

    /**
     * 设置选中游戏模式的监听
     */
    public void setSelectGameModeListener(SelectGameModeListener listener) {
        this.selectGameModeListener = listener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.audio_dialog_game_mode;
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected int getWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getHeight() {
        return (int) (DensityUtils.getScreenHeight() * 0.7);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        recyclerView = mRootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount, GridLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(gameModeAdapter);

        View headView = View.inflate(getContext(), R.layout.audio_view_game_mode_head, null);
        gameModeAdapter.addHeaderView(headView);

        // 设置点击监听
        headView.findViewById(R.id.fl_audio_chat_room).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGameMode(0);
            }
        });
        gameModeAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                GameModel gameModel = gameModeAdapter.getItem(position);
                selectGameMode(gameModel.getGameId());
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        CommonRepository.gameList(this, new RxCallback<GameListResp>() {
            @Override
            public void onSuccess(GameListResp gameListResp) {
                super.onSuccess(gameListResp);
                if (gameListResp != null) {
                    gameModeAdapter.setList(gameListResp.getGameList());
                }
            }
        });
    }

    private void selectGameMode(long gameId) {
        dismiss();
        if (selectGameModeListener != null) {
            selectGameModeListener.onSelectGameMode(gameId);
        }
    }

    private class GameModeAdapter extends BaseQuickAdapter<GameModel, BaseViewHolder> {

        public GameModeAdapter() {
            super(R.layout.audio_item_game_mode);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder baseViewHolder, GameModel gameModel) {
            ImageView ivIcon = baseViewHolder.getView(R.id.item_game_mode_iv_icon);
            ImageLoader.loadImage(ivIcon, gameModel.getGamePic());

            if (playingGameId == gameModel.getGameId()) {
                baseViewHolder.setVisible(R.id.item_game_mode_tv_gameing, true);
            } else {
                baseViewHolder.setVisible(R.id.item_game_mode_tv_gameing, false);
            }

            baseViewHolder.setText(R.id.item_game_mode_tv_name, gameModel.getGameName());
        }
    }

    public interface SelectGameModeListener {
        /**
         * 选择了游戏模式
         *
         * @param gameId 如果为0，则没有选择游戏
         */
        void onSelectGameMode(long gameId);
    }

}