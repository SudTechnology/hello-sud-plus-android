package tech.sud.mgp.hello.ui.scenes.quiz.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ViewUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.CustomCountdownTimer;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.ui.common.utils.FormatUtils;
import tech.sud.mgp.hello.ui.common.utils.SceneUtils;

/**
 * 更多竞猜列表
 */
public class MoreQuizAdapter extends BaseQuickAdapter<GameModel, BaseViewHolder> implements LoadMoreModule {

    public MoreQuizAdapter() {
        super(R.layout.item_more_quiz);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, GameModel model) {
        // 赢得的奖励
        holder.setText(R.id.tv_win_award, model.winCoin + "");

        // 倒计时
        TextView tvCountdown = holder.getView(R.id.tv_countdown);
        startCountdown(model.gameCountDownCycle, tvCountdown);

        // 图标
        ImageView ivIcon = holder.getView(R.id.iv_icon);
        ImageLoader.loadAvatar(ivIcon, model.gamePic);

        // 名称
        holder.setText(R.id.tv_name, model.gameName);

        // 入场
        if (model.ticketCoin == 0) {
            holder.setText(R.id.tv_entrance, getContext().getString(R.string.entrance) + " "
                    + getContext().getString(R.string.free));
        } else {
            holder.setText(R.id.tv_entrance, getContext().getString(R.string.entrance) + " "
                    + model.ticketCoin + getContext().getString(R.string.gold_coin));
        }

        View viewCountdown = holder.getView(R.id.ll_countdown);
        if (ViewUtils.isLayoutRtl()) {
            viewCountdown.setTranslationX(-DensityUtils.dp2px(getContext(), 1));
        } else {
            viewCountdown.setTranslationX(DensityUtils.dp2px(getContext(), 1));
        }
    }

    // 开启倒计时
    private void startCountdown(int cycle, TextView tv) {
        cancelCountdown(tv);
        int totalCount = SceneUtils.getTotalCount(cycle);
        CustomCountdownTimer timer = new CustomCountdownTimer(totalCount) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onTick(int count) {
                tv.setText(FormatUtils.formatTimeDay(count));
            }

            @Override
            protected void onFinish() {
                startCountdown(cycle, tv);
            }
        };
        timer.start();
        tv.setTag(R.id.obj, timer);
    }

    private void cancelCountdown(TextView tv) {
        CustomCountdownTimer oldTimer = (CustomCountdownTimer) tv.getTag(R.id.obj);
        if (oldTimer != null) {
            oldTimer.cancel();
        }
    }

    @NonNull
    @Override
    public BaseLoadMoreModule addLoadMoreModule(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter) {
        return new BaseLoadMoreModule(baseQuickAdapter);
    }
}
