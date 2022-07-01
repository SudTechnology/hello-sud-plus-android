package tech.sud.mgp.hello.ui.scenes.disco.widget;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.DanceModel;

/**
 * 跳舞节目单
 */
public class DancingMenuDialog extends BaseDialogFragment {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private View viewRule;
    private final MyAdapter adapter = new MyAdapter();

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_dancing_menu;
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
        return DensityUtils.dp2px(405);
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
        tvEmpty = findViewById(R.id.tv_empty);
        viewRule = findViewById(R.id.ll_rule);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(null);

        checkEmpty();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        viewRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDanceRuleDialog();
            }
        });
    }

    private void showDanceRuleDialog() {
        DanceRuleDialog dialog = new DanceRuleDialog();
        dialog.show(getChildFragmentManager(), null);
    }

    private void checkEmpty() {
        if (tvEmpty == null) {
            return;
        }
        if (adapter.getData().size() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }

    /** 设置数据 */
    public void notifyDataSetChange(List<DanceModel> list) {
        adapter.setList(list);
        checkEmpty();
    }

    /** 更新某一条数据 */
    public void notifyItemChanged(int position) {
        adapter.notifyItemChanged(position);
    }

    private static class MyAdapter extends BaseQuickAdapter<DanceModel, BaseViewHolder> {
        public MyAdapter() {
            super(R.layout.item_dancing_menu);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, DanceModel model) {
            String iconUrl;
            String fromName;
            String toName;

            if (model.fromUser != null) {
                iconUrl = model.fromUser.icon;
                fromName = model.fromUser.name;
            } else {
                iconUrl = null;
                fromName = null;
            }

            if (model.toUser != null) {
                toName = model.toUser.name;
            } else {
                toName = null;
            }

            ImageView ivIcon = holder.getView(R.id.iv_icon);
            ImageLoader.loadAvatar(ivIcon, iconUrl);

            holder.setText(R.id.tv_name, fromName);

            holder.setText(R.id.tv_info, getContext().getString(R.string.invite_dance_info, toName, (model.duration / 60) + ""));

            TextView tvFinished = holder.getView(R.id.tv_finished);
            TextView tvCountdown = holder.getView(R.id.tv_countdown);
            TextView tvWaiting = holder.getView(R.id.tv_waiting);
            if (model.isCompleted) { // 已结束
                if (model.isShowCompletedTitle) {
                    tvFinished.setVisibility(View.VISIBLE);
                } else {
                    tvFinished.setVisibility(View.GONE);
                }
                tvCountdown.setVisibility(View.GONE);
                tvWaiting.setVisibility(View.GONE);
            } else { // 未结束
                tvFinished.setVisibility(View.GONE);
                if (model.beginTime > 0) { // 在执行中了
                    tvCountdown.setVisibility(View.VISIBLE);
                    tvWaiting.setVisibility(View.GONE);
                    if (model.countdown >= 60) {
                        tvCountdown.setText(getContext().getString(R.string.residue_minute, model.countdown / 60 + ""));
                    } else {
                        tvCountdown.setText(getContext().getString(R.string.residue_second, model.countdown + ""));
                    }
                } else { // 等待中
                    tvCountdown.setVisibility(View.GONE);
                    tvWaiting.setVisibility(View.VISIBLE);
                }
            }
        }
    }

}
