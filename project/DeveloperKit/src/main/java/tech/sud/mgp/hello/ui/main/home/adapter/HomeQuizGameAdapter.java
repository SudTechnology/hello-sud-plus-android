package tech.sud.mgp.hello.ui.main.home.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.CustomCountdownTimer;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.ui.common.utils.FormatUtils;
import tech.sud.mgp.hello.ui.common.utils.SceneUtils;

/**
 * 首页竞猜游戏列表
 */
public class HomeQuizGameAdapter extends BaseQuickAdapter<GameModel, BaseViewHolder> {

    public HomeQuizGameAdapter(@Nullable List<GameModel> data) {
        super(R.layout.item_home_quiz_game, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GameModel item) {
        // 图标
        ImageView ivIcon = helper.getView(R.id.iv_icon);
        ImageLoader.loadGameCover(ivIcon, item.homeGamePic);

        ImageView ivIconMini = helper.getView(R.id.iv_icon_mini);
        ImageLoader.loadGameCover(ivIconMini, item.gamePic);

        // 赢得的奖励
        helper.setText(R.id.tv_win_award, item.winCoin + "");

        // 游戏名称
        helper.setText(R.id.tv_game_name, item.gameName);

        // 剩余时间
        TextView tvResidueDuration = helper.getView(R.id.tv_residue_duration);
        startCountdown(item, tvResidueDuration);

        // 参与人数上限
        helper.setText(R.id.tv_number_upper_limit, "∞");

        // 入场
        if (item.ticketCoin == 0) {
            helper.setText(R.id.tv_tickets, R.string.free);
        } else {
            helper.setText(R.id.tv_tickets, item.ticketCoin + getContext().getString(R.string.gold_coin));
        }
    }

    private void startCountdown(GameModel item, TextView tvResidueDuration) {
        int totalCount = SceneUtils.getTotalCount(item.gameCountDownCycle);
        CustomCountdownTimer oldTimer = (CustomCountdownTimer) tvResidueDuration.getTag(R.id.obj);
        if (oldTimer != null) {
            oldTimer.cancel();
        }
        CustomCountdownTimer timer = new CustomCountdownTimer(totalCount) {
            @Override
            protected void onTick(int count) {
                tvResidueDuration.setText(FormatUtils.formatTimeDay(count));
            }

            @Override
            protected void onFinish() {
                startCountdown(item, tvResidueDuration);
            }
        };
        timer.start();
        tvResidueDuration.setTag(R.id.obj, timer);
    }

}