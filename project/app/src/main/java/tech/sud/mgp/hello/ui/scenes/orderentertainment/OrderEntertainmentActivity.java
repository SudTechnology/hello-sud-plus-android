package tech.sud.mgp.hello.ui.scenes.orderentertainment;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.OrderInviteModel;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomService;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.chat.SceneRoomChatView;
import tech.sud.mgp.hello.ui.scenes.orderentertainment.activity.AbsOrderRoomActivity;
import tech.sud.mgp.hello.ui.scenes.orderentertainment.dialog.OrderDialog;
import tech.sud.mgp.hello.ui.scenes.orderentertainment.viewmodel.OrderViewModel;

/**
 * 点单娱乐类场景
 */
public class OrderEntertainmentActivity extends AbsOrderRoomActivity<OrderViewModel> {

    private ConstraintLayout orderRootView;
    private TextView startGameBtn, hangupGameBtn, enterGameBtn;
    private int topBtnState = 0;//当前按钮状态 0可以点单状态 1可以挂起游戏状态 2可以进入游戏状态

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
        gameViewModel.gameConfigModel.ui.game_bg.hide = true;
        gameViewModel.gameConfigModel.ui.lobby_players.hide = false;
//        gameViewModel.gameConfigModel.ui.level.hide = true;
//        gameViewModel.gameConfigModel.ui.lobby_help_btn.hide = true;
        roomConfig.isShowGameNumber = false; // 不显示游戏人数
        roomConfig.isShowASRTopHint = false; // 右上角不展示ASR提示

        orderRootView = findViewById(R.id.order_root_view);
        addTopBtn();
        changeTopBtn(0);

        orderRootView.removeView(gameContainer);
        //修改游戏容器位置以及大小
        ConstraintLayout.LayoutParams cparams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        cparams.topToBottom = R.id.room_top_view;
        cparams.bottomToTop = R.id.room_bottom_view;
        cparams.bottomMargin = DensityUtils.dp2px(106);
        orderRootView.addView(gameContainer, cparams);

        giftContainer.bringToFront();
        inputMsgView.bringToFront();
        clOpenMic.bringToFront();
    }

    private void addTopBtn() {
        startGameBtn = new TextView(this);
        startGameBtn.setTextSize(12);
        startGameBtn.setTextColor(Color.WHITE);
        startGameBtn.setGravity(Gravity.CENTER);
        startGameBtn.setText(getString(R.string.order_start_order));
        startGameBtn.setBackgroundResource(R.drawable.shape_gradient_f963ff_cc00e7);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(DensityUtils.dp2px(66), DensityUtils.dp2px(20));
        params1.setMarginEnd(DensityUtils.dp2px(20));
        topView.addCustomView(startGameBtn, params1);
        startGameBtn.setVisibility(View.GONE);
        startGameBtn.setOnClickListener(v -> {
            iWantOrder();
        });
        hangupGameBtn = new TextView(this);
        hangupGameBtn.setTextSize(12);
        hangupGameBtn.setTextColor(Color.parseColor("#6AD04E"));
        hangupGameBtn.setGravity(Gravity.CENTER);
        hangupGameBtn.setText(getString(R.string.order_hang_up_game));
        hangupGameBtn.setBackgroundColor(Color.parseColor("#4d4E9C39"));
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(DensityUtils.dp2px(66), DensityUtils.dp2px(20));
        params2.setMarginEnd(DensityUtils.dp2px(20));
        topView.addCustomView(hangupGameBtn, params2);
        hangupGameBtn.setVisibility(View.GONE);
        hangupGameBtn.setOnClickListener(v -> {
            changeTopBtn(2);
        });

        enterGameBtn = new TextView(this);
        enterGameBtn.setTextSize(12);
        enterGameBtn.setTextColor(Color.parseColor("#6AD04E"));
        enterGameBtn.setGravity(Gravity.CENTER);
        enterGameBtn.setText(getString(R.string.order_enter_game));
        enterGameBtn.setBackgroundColor(Color.parseColor("#4d4E9C39"));
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(DensityUtils.dp2px(66), DensityUtils.dp2px(20));
        params3.setMarginEnd(DensityUtils.dp2px(20));
        topView.addCustomView(enterGameBtn, params3);
        enterGameBtn.setVisibility(View.GONE);
        enterGameBtn.setOnClickListener(v -> {
            changeTopBtn(1);
        });
    }

    //0我要点单 1挂起游戏 2进入游戏 (tips：这是显示文案
    private void changeTopBtn(int state) {
        topBtnState = state;
        switch (state) {
            case 0: {
                startGameBtn.setVisibility(View.VISIBLE);
                hangupGameBtn.setVisibility(View.GONE);
                enterGameBtn.setVisibility(View.GONE);
                gameContainer.setVisibility(View.INVISIBLE);
                micView.setVisibility(View.VISIBLE);
                switchChatViewStyle(SceneRoomChatView.AudioRoomChatStyle.NORMAL);
                break;
            }
            case 1: {
                startGameBtn.setVisibility(View.GONE);
                hangupGameBtn.setVisibility(View.VISIBLE);
                enterGameBtn.setVisibility(View.GONE);
                gameContainer.setVisibility(View.VISIBLE);
                micView.setVisibility(View.INVISIBLE);
                switchChatViewStyle(SceneRoomChatView.AudioRoomChatStyle.GAME);
                break;
            }
            case 2: {
                startGameBtn.setVisibility(View.GONE);
                hangupGameBtn.setVisibility(View.GONE);
                enterGameBtn.setVisibility(View.VISIBLE);
                gameContainer.setVisibility(View.INVISIBLE);
                micView.setVisibility(View.VISIBLE);
                switchChatViewStyle(SceneRoomChatView.AudioRoomChatStyle.NORMAL);
                break;
            }
        }
    }

    @Override
    protected void updatePageStyle() {
        super.updatePageStyle();
        if (playingGameId > 0 && binder != null) {
            changeTopBtn(1);
        } else {
            changeTopBtn(0);
        }
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        gameViewModel.dialogResult.observe(this, integer -> {
            if (integer == 5) {
                //继续点单
                iWantOrder();
            }
        });
        gameViewModel.orderDataLiveData.observe(this, data -> {
            if (data != null && data.userIdList.size() > 0) {
                List<String> userStringList = new ArrayList<>();
                for (Long userId : data.userIdList) {
                    userStringList.add(userId + "");
                }
                sendOrder(data.resp.orderId, data.game.gameModel.gameId, data.game.gameModel.gameName, userStringList);
            }
        });
        gameViewModel.changeToAduio.observe(this, integer -> {
            if (integer == 1) {
                intentSwitchGame(0);
            }
        });
    }

    /** 主动发起点单 */
    private void sendOrder(long orderId, long gameId, String gameName, List<String> toUsers) {
        if (binder != null) {
            binder.broadcastOrder(orderId, gameId, gameName, toUsers);
        }
    }

    /** 有点单邀请来了 */
    @Override
    public void onOrderInvite(OrderInviteModel model) {
        super.onOrderInvite(model);
//        gameViewModel.inviteDialog(this, this, orderId, gameId, gameName, userID, nickname, toUsers);
        gameViewModel.orderDataLiveData.postValue(null);
        gameViewModel.isSelfOrder = 0;
        gameViewModel.orderModel = model;
    }

    /** 有点单结果来了 */
    @Override
    public void onOrderOperate(long orderId, long gameId, String gameName, String userId, String userName, boolean operate) {
        super.onOrderOperate(orderId, gameId, gameName, userId, userName, operate);
        if (operate) {
            gameViewModel.isSelfOrder = 1;
        }
    }

    //接受邀请接口是否成功
    @Override
    public void onReceiveInvite(boolean agreeState) {
        super.onReceiveInvite(agreeState);
        if (agreeState){
            //接受了并且切换游戏
            intentSwitchGame(gameViewModel.orderModel.gameId);
            changeTopBtn(1);
        }
    }

    /**
     * 我要点单弹窗
     */
    private void iWantOrder() {
        if (binder != null) {
            OrderDialog dialog = OrderDialog.getInstance(SceneRoomService.getSceneRoomData().roomInfoModel.sceneType);
            dialog.updateMicList(binder.getMicList(), 0);
            dialog.setCreateListener((userList, game) -> {
                if (userList.size() > 0 && game != null) {
                    gameViewModel.roomOrderCreate(
                            OrderEntertainmentActivity.this,
                            roomInfoModel.roomId,
                            userList,
                            game);
                    dialog.dismiss();
                    ToastUtils.showLong(R.string.order_inivte_complete);
                }
            });
            dialog.show(getSupportFragmentManager(), null);
        }
    }
}