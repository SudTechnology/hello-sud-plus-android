package tech.sud.mgp.hello.ui.scenes.orderentertainment;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.ui.scenes.audio.activity.AbsAudioRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomService;
import tech.sud.mgp.hello.ui.scenes.orderentertainment.dialog.OrderDialog;
import tech.sud.mgp.hello.ui.scenes.orderentertainment.viewmodel.OrderViewModel;

/**
 * 点单娱乐类场景
 */
public class OrderEntertainmentActivity extends AbsAudioRoomActivity<OrderViewModel> {

    private TextView startGameBtn, hangupGameBtn, enterGameBtn;

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
        addTopBtn();
        changeTopBtn(0);
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
            LogUtils.i("startGameBtn onClick");
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
                    }
                });
                dialog.show(getSupportFragmentManager(), null);
            }
        });
        hangupGameBtn = new TextView(this);
        hangupGameBtn.setTextSize(12);
        hangupGameBtn.setTextColor(Color.parseColor("#6AD04E"));
        hangupGameBtn.setGravity(Gravity.CENTER);
        hangupGameBtn.setText(getString(R.string.order_hang_up_game));
        hangupGameBtn.setBackgroundColor(Color.parseColor("#4E9C39"));
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(DensityUtils.dp2px(66), DensityUtils.dp2px(20));
        params2.setMarginEnd(DensityUtils.dp2px(20));
        topView.addCustomView(hangupGameBtn, params2);
        hangupGameBtn.setVisibility(View.GONE);
        hangupGameBtn.setOnClickListener(v -> {
            LogUtils.i("hangupGameBtn onClick");
//            gameViewModel.
        });

        enterGameBtn = new TextView(this);
        enterGameBtn.setTextSize(12);
        enterGameBtn.setTextColor(Color.parseColor("#6AD04E"));
        enterGameBtn.setGravity(Gravity.CENTER);
        enterGameBtn.setText(getString(R.string.order_enter_game));
        enterGameBtn.setBackgroundColor(Color.parseColor("#4E9C39"));
        enterGameBtn.setBackgroundResource(R.drawable.shape_gradient_f963ff_cc00e7);
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(DensityUtils.dp2px(66), DensityUtils.dp2px(20));
        params3.setMarginEnd(DensityUtils.dp2px(20));
        topView.addCustomView(enterGameBtn, params3);
        enterGameBtn.setVisibility(View.GONE);
        enterGameBtn.setOnClickListener(v -> LogUtils.i("enterGameBtn onClick"));
    }

    //0开始游戏 1挂起游戏 2进入游戏
    private void changeTopBtn(int state) {
        switch (state) {
            case 0: {
                startGameBtn.setVisibility(View.VISIBLE);
                hangupGameBtn.setVisibility(View.GONE);
                enterGameBtn.setVisibility(View.GONE);
                break;
            }
            case 1: {
                startGameBtn.setVisibility(View.GONE);
                hangupGameBtn.setVisibility(View.VISIBLE);
                enterGameBtn.setVisibility(View.GONE);
                break;
            }
            case 2: {
                startGameBtn.setVisibility(View.GONE);
                hangupGameBtn.setVisibility(View.GONE);
                enterGameBtn.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @Override
    protected void setListeners() {
        super.setListeners();
        gameViewModel.dialogResult.observe(this, integer -> {
            LogUtils.i("dialogResult.observe integer=" + integer);
            if (integer == 1) {
                operateOrder(
                        gameViewModel.orderModel.orderId,
                        gameViewModel.orderModel.gameId,
                        gameViewModel.orderModel.gameName,
                        gameViewModel.orderModel.sendUserId,
                        true);
            } else if (integer == 2) {
                operateOrder(
                        gameViewModel.orderModel.orderId,
                        gameViewModel.orderModel.gameId,
                        gameViewModel.orderModel.gameName,
                        gameViewModel.orderModel.sendUserId,
                        false);
            } else if (integer == 3) {

            } else if (integer == 4) {

            } else if (integer == 5) {
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
                        }
                    });
                    dialog.show(getSupportFragmentManager(), null);
                }
            } else if (integer == 6) {

            }
        });
        gameViewModel.orderDataLiveData.observe(this, data -> {
            if (data.userIdList.size() > 0) {
                List<String> userStringList = new ArrayList<>();
                for (Long userId : data.userIdList) {
                    userStringList.add(userId + "");
                }
                sendOrder(data.resp.orderId, data.game.gameModel.gameId, data.game.gameModel.gameName, userStringList);
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
    public void onOrderInvite(long orderId, long gameId, String gameName, String userID, String nickname, List<String> toUsers) {
        super.onOrderInvite(orderId, gameId, gameName, userID, nickname, toUsers);
        gameViewModel.inviteDialog(this, orderId, gameId, gameName, userID, nickname, toUsers);
    }

    /** 有点单结果来了 */
    @Override
    public void onOrderOperate(long orderId, long gameId, String gameName, String userId, String userName, boolean operate) {
        super.onOrderOperate(orderId, gameId, gameName, userId, userName, operate);
        if (!operate) {
            gameViewModel.operateDialog(this, userName);
        }
    }

}