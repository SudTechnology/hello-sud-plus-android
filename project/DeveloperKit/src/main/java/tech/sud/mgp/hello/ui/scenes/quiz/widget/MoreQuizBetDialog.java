package tech.sud.mgp.hello.ui.scenes.quiz.widget;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ShapeUtils;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.GetAccountResp;
import tech.sud.mgp.hello.ui.common.utils.FormatUtils;

/**
 * 竞猜活动内的 投注弹窗
 */
public class MoreQuizBetDialog extends BaseDialogFragment {

    private TextView tvCoin;
    private TextView tvTitle;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private OnSelectedListener onSelectedListener;
    private String nickName;

    public static MoreQuizBetDialog newInstance(String nickName) {
        MoreQuizBetDialog dialog = new MoreQuizBetDialog();
        Bundle bundle = new Bundle();
        bundle.putString("nickName", nickName);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            nickName = arguments.getString("nickName");
        }
    }

    @Override
    protected void customStyle(Window window) {
        super.customStyle(window);
        window.setWindowAnimations(R.style.BottomToTopAnim);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_quiz_bet;
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
    protected void initWidget() {
        super.initWidget();
        tvCoin = findViewById(R.id.tv_coin);
        tvTitle = findViewById(R.id.tv_title);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false));
        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);
        tvTitle.setText(getString(R.string.quiz_bet_title) + nickName);
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
        initBetList();
    }

    private void initBetList() {
        List<BetModel> list = new ArrayList<>();
        list.add(new BetModel(false, 10));
        list.add(new BetModel(true, 100));
        list.add(new BetModel(false, 500));
        adapter.setList(list);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        findViewById(R.id.tv_support).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSelectedListener != null) {
                    onSelectedListener.onSelected(getSelectedMoney());
                }
                dismiss();
            }
        });
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                List<BetModel> list = MoreQuizBetDialog.this.adapter.getData();
                BetModel item = list.get(position);
                if (item.isSelected) {
                    return;
                }
                for (int i = 0; i < list.size(); i++) {
                    BetModel model = list.get(i);
                    if (model.isSelected) {
                        model.isSelected = false;
                        adapter.notifyItemChanged(i);
                    }
                }
                item.isSelected = true;
                adapter.notifyItemChanged(position);
            }
        });
    }

    private long getSelectedMoney() {
        for (BetModel model : adapter.getData()) {
            if (model.isSelected) {
                return model.money;
            }
        }
        return 0;
    }

    /** 设置选择支持的监听器 */
    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }

    public interface OnSelectedListener {
        void onSelected(long money);
    }

    private static class BetModel {
        public boolean isSelected;
        public long money; // 投注金额

        public BetModel(boolean isSelected, long money) {
            this.isSelected = isSelected;
            this.money = money;
        }
    }

    private static class MyAdapter extends BaseQuickAdapter<BetModel, BaseViewHolder> {

        private final float radius = DensityUtils.dp2px(8);

        public MyAdapter() {
            super(R.layout.item_quiz_bet);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, BetModel model) {
            View rootView = holder.getView(R.id.root_view);
            TextView tvCoin = holder.getView(R.id.tv_coin);
            TextView tvTitle = holder.getView(R.id.tv_title);
            tvCoin.setText(model.money + "");
            if (model.isSelected) {
                int selectedColor = Color.parseColor("#ffbf3a");
                rootView.setBackground(ShapeUtils.createShape(DensityUtils.dp2px(1), radius,
                        null, GradientDrawable.RECTANGLE, selectedColor,
                        Color.parseColor("#ffffff")));
                tvCoin.setTextColor(selectedColor);
                tvTitle.setTextColor(selectedColor);
            } else {
                tvCoin.setTextColor(Color.parseColor("#000000"));
                tvTitle.setTextColor(Color.parseColor("#666666"));
                rootView.setBackground(ShapeUtils.createShape(null, radius,
                        null, GradientDrawable.RECTANGLE, null,
                        Color.parseColor("#ffffff")));
            }
        }
    }

}
