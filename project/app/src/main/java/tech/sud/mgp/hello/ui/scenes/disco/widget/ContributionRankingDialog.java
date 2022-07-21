package tech.sud.mgp.hello.ui.scenes.disco.widget;

import android.annotation.SuppressLint;
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
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.ContributionModel;

/**
 * 蹦迪贡献榜
 */
public class ContributionRankingDialog extends BaseDialogFragment {

    private final MyAdapter adapter = new MyAdapter();

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_contribution_ranking;
    }

    @Override
    protected int getWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getHeight() {
        return (int) (DensityUtils.getScreenHeight() * 0.4987);
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
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    /** 设置数据 */
    public void setDatas(List<ContributionModel> list) {
        adapter.setList(list);
    }

    private static class MyAdapter extends BaseQuickAdapter<ContributionModel, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.item_contribution);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void convert(@NonNull BaseViewHolder holder, ContributionModel model) {
            int ranking = getItemPosition(model) + 1;
            View viewRanking = holder.getView(R.id.view_ranking);
            TextView tvRanking = holder.getView(R.id.tv_ranking);
            switch (ranking) {
                case 1:
                    viewRanking.setVisibility(View.VISIBLE);
                    tvRanking.setVisibility(View.GONE);
                    viewRanking.setBackgroundResource(R.drawable.ic_ranking_1);
                    break;
                case 2:
                    viewRanking.setVisibility(View.VISIBLE);
                    tvRanking.setVisibility(View.GONE);
                    viewRanking.setBackgroundResource(R.drawable.ic_ranking_2);
                    break;
                case 3:
                    viewRanking.setVisibility(View.VISIBLE);
                    tvRanking.setVisibility(View.GONE);
                    viewRanking.setBackgroundResource(R.drawable.ic_ranking_3);
                    break;
                default:
                    viewRanking.setVisibility(View.GONE);
                    tvRanking.setVisibility(View.VISIBLE);
                    tvRanking.setText(ranking + "");
                    break;
            }

            ImageView ivicon = holder.getView(R.id.iv_icon);
            if (model.fromUser == null) {
                ImageLoader.loadAvatar(ivicon, null);
                holder.setText(R.id.tv_name, null);
            } else {
                ImageLoader.loadAvatar(ivicon, model.fromUser.icon);
                holder.setText(R.id.tv_name, model.fromUser.name);
            }

            holder.setText(R.id.tv_value, model.count + "");
        }
    }

}
