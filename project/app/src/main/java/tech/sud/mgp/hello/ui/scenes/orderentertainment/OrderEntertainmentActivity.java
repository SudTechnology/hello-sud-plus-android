package tech.sud.mgp.hello.ui.scenes.orderentertainment;

import android.widget.TextView;

import androidx.lifecycle.Observer;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.ui.scenes.audio.activity.AbsAudioRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomService;
import tech.sud.mgp.hello.ui.scenes.orderentertainment.dialog.OrderDialog;
import tech.sud.mgp.hello.ui.scenes.orderentertainment.viewmodel.OrderViewModel;

/**
 * 点单娱乐类场景
 */
public class OrderEntertainmentActivity extends AbsAudioRoomActivity<OrderViewModel> {

    private TextView orderEnterTv;


    @Override
    protected OrderViewModel initGameViewModel() {
        return new OrderViewModel();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_entertainment;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        orderEnterTv = findViewById(R.id.order_enter_tv);
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @Override
    protected void setListeners() {
        super.setListeners();
        orderEnterTv.setOnClickListener(v -> {
            if (binder != null) {
                OrderDialog dialog = OrderDialog.getInstance(SceneRoomService.getSceneRoomData().roomInfoModel.sceneType);
                dialog.updateMicList(binder.getMicList(), 0);
                dialog.show(getSupportFragmentManager(), null);
            }
        });
        gameViewModel.dialogResult.observe(this, integer -> {
            if (integer == 1) {

            } else if (integer == 2) {

            } else if (integer == 3) {

            }
        });
    }

    /** 主动发起点单 */
    private void sendOrder(long orderId, long gameId, String gameName, List<String> toUsers) {
        if (binder != null) {
            binder.broadcastOrder(orderId, gameId, gameName, toUsers);
        }
    }

    /** 同意或者拒绝点单 */
    private void operateOrder(long orderId, long gameId, String gameName, String toUser, boolean state) {
        if (binder != null) {
            binder.operateOrder(orderId, gameId, gameName, toUser, state);
        }
    }

    /** 有点单邀请来了 */
    @Override
    public void onOrderInvite(long orderId, long gameId, String gameName, String nickname, List<String> toUsers) {
        super.onOrderInvite(orderId, gameId, gameName, nickname, toUsers);
        gameViewModel.inviteDialog(this, nickname, gameName);
    }

    /** 有点单结果来了，显示操作弹窗 */
    @Override
    public void onOrderOperate(long orderId, long gameId, String gameName, String userId, String userName, boolean operate) {
        super.onOrderOperate(orderId, gameId, gameName, userId, userName, operate);
        gameViewModel.operateDialog(this, userName);
    }

}