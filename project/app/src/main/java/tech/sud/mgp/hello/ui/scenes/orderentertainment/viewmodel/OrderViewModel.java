package tech.sud.mgp.hello.ui.scenes.orderentertainment.viewmodel;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;

import java.util.List;

import tech.sud.mgp.core.ISudFSMStateHandle;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.SudMGPWrapper.model.GameViewInfoModel;
import tech.sud.mgp.hello.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.widget.dialog.SimpleChooseDialog;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.service.room.resp.RoomOrderCreateResp;
import tech.sud.mgp.hello.ui.common.utils.DialogUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.OrderInviteModel;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.GameViewModel;
import tech.sud.mgp.hello.ui.scenes.orderentertainment.OrderEntertainmentActivity;
import tech.sud.mgp.hello.ui.scenes.orderentertainment.model.OrderDataModel;
import tech.sud.mgp.hello.ui.scenes.orderentertainment.model.OrderGameModel;

/**
 * 点单场景vm
 */
public class OrderViewModel extends GameViewModel {

    private SimpleChooseDialog finishDialog;//结束弹窗
    // 5结束弹窗确定 6取消
    public MutableLiveData<Integer> dialogResult = new MutableLiveData<>();

    //用户主动点单数据（自己主动下单邀请
    public MutableLiveData<OrderDataModel> orderDataLiveData = new MutableLiveData<>();
    //游戏结束后，切回到语音房间
    public MutableLiveData<Integer> changeToAduio = new MutableLiveData<>();
    // 0订单是别人发起的 1订单是自己发起的
    public int isSelfOrder = 0;
    //点单
    public OrderInviteModel orderModel;

    public void roomOrderCreate(LifecycleOwner owner,
                                long roomId,
                                List<Long> userIdList,
                                OrderGameModel game) {
        RoomRepository.roomOrderCreate(owner, roomId, userIdList, game.gameModel.gameId, new RxCallback<RoomOrderCreateResp>() {
            @Override
            public void onSuccess(RoomOrderCreateResp roomOrderCreateResp) {
                super.onSuccess(roomOrderCreateResp);
                OrderDataModel model = new OrderDataModel();
                model.resp = roomOrderCreateResp;
                model.roomId = roomId;
                model.userIdList = userIdList;
                model.game = game;
                orderDataLiveData.postValue(model);
            }
        });
    }

    /** 结束弹窗 */
    public void finishDialog(FragmentActivity activity) {
        LogUtils.i("finishDialog game over1");
        if (finishDialog == null || !finishDialog.isShowing()) {
            finishDialog = new SimpleChooseDialog(activity, activity.getString(R.string.room_round_game_over),
                    activity.getString(R.string.order_finish_left_text), activity.getString(R.string.order_finish_right_text));
            finishDialog.setOnChooseListener(index -> {
                if (index == 1) {
                    dialogResult.postValue(5);
                } else {
                    dialogResult.postValue(6);
                }
                finishDialog.dismiss();
                finishDialog = null;
            });
            DialogUtils.safeShowDialog(activity, finishDialog);
        }
    }

    @Override
    public void onGameMGCommonGameState(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameState model) {
        super.onGameMGCommonGameState(handle, model);
        LogUtils.i("onGameMGCommonGameState" + model.gameState);
        if (model.gameState == SudMGPMGState.MGCommonGameState.IDLE) {
            LogUtils.i("onGameMGCommonGameState游戏闲置");
        } else if (model.gameState == SudMGPMGState.MGCommonGameState.LOADING) {
            LogUtils.i("onGameMGCommonGameState游戏加载");
        } else if (model.gameState == SudMGPMGState.MGCommonGameState.PLAYING) {
            LogUtils.i("onGameMGCommonGameState游戏中");
        }
    }

    @Override
    public void onGameMGCommonGameSettle(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameSettle model) {
        super.onGameMGCommonGameSettle(handle, model);
        LogUtils.i("onGameMGCommonGameSettle");
        if (isSelfOrder == 1) {
            //游戏结束
            if (ActivityUtils.getTopActivity() instanceof OrderEntertainmentActivity) {
                finishDialog((FragmentActivity) ActivityUtils.getTopActivity());
            }
        }
        isSelfOrder = 0;
        orderDataLiveData.setValue(null);
        //查找积分榜第一个未逃跑的人切回游戏到语音
        if (model.results != null && model.results.size() > 0) {
            for (SudMGPMGState.MGCommonGameSettle.PlayerResult playerResult : model.results) {
                if (playerResult.isEscaped == 0) {
                    if (playerResult.uid.equals(HSUserInfo.userId + "")) {
                        changeToAduio.setValue(1);
                    }
                    break;
                }
            }
        }
    }

    @Override
    protected void getGameRect(GameViewInfoModel gameViewInfoModel) {
        gameViewInfoModel.view_game_rect.left = 0;
        gameViewInfoModel.view_game_rect.top = 0;
        gameViewInfoModel.view_game_rect.right = 0;
        gameViewInfoModel.view_game_rect.bottom = 0;
    }

}