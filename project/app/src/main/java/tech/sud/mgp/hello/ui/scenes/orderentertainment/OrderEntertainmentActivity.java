package tech.sud.mgp.hello.ui.scenes.orderentertainment;

import android.widget.TextView;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.widget.dialog.SimpleChooseDialog;
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
    }

    /** 主动发起点单 */
    private void sendOrder(long orderId, long gameId, String gameName, List<Integer> toUsers) {
        if (binder != null) {
            binder.broadcastOrder(orderId, gameId, gameName, toUsers);
        }
    }

    /** 有点单邀请来了 */
    @Override
    public void onOrderInvite(long orderId, long gameId, String gameName, List<Integer> toUsers) {
        super.onOrderInvite(orderId, gameId, gameName, toUsers);
        gameViewModel.inviteDialog(this, );
    }

    /** 有点单结果来了，主播同意了后者拒绝了 */
    @Override
    public void onOrderResult(long orderId, long gameId, String gameName, int userId, String userName) {
        super.onOrderResult(orderId, gameId, gameName, userId, userName);
        gameViewModel.inviteDialog(this, );
    }

}