package tech.sud.mgp.hello.ui.scenes.orderentertainment.viewmodel;

import android.content.Context;
import android.view.ViewTreeObserver;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.Utils;

import java.util.List;

import tech.sud.mgp.core.ISudFSMStateHandle;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.SudMGPWrapper.model.GameViewInfoModel;
import tech.sud.mgp.hello.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.widget.dialog.SimpleChooseDialog;
import tech.sud.mgp.hello.service.room.repository.AudioRepository;
import tech.sud.mgp.hello.service.room.response.RoomOrderCreateResp;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.GameViewModel;
import tech.sud.mgp.hello.ui.scenes.orderentertainment.model.OrderDataModel;
import tech.sud.mgp.hello.ui.scenes.orderentertainment.model.OrderGameModel;

/**
 * 点单场景vm
 */
public class OrderViewModel extends GameViewModel {

    // 1邀请弹窗确定 2取消
    // 3拒绝弹窗确定 4取消
    // 5结束弹窗确定 6取消
    public MutableLiveData<Integer> dialogResult = new MutableLiveData<>();
    private SimpleChooseDialog inviteDialog;//邀请弹窗
    private SimpleChooseDialog operateDialog;//拒绝弹窗
    private SimpleChooseDialog finishDialog;//结束弹窗
    //被邀请弹窗数据（别人邀请
    public InviteOrderModel orderModel;
    //用户主动点单数据（自己主动下单邀请
    public MutableLiveData<OrderDataModel> orderDataLiveData = new MutableLiveData<>();

    public void roomOrderCreate(LifecycleOwner owner,
                                long roomId,
                                List<Long> userIdList,
                                OrderGameModel game) {
        AudioRepository.roomOrderCreate(owner, roomId, userIdList, game.gameModel.gameId, new RxCallback<RoomOrderCreateResp>() {
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

    public void roomOrderReceive(LifecycleOwner owner, long orderId) {
        AudioRepository.roomOrderReceive(owner, orderId, new RxCallback<Object>() {
        });
    }

    /** 邀请弹窗 */
    public void inviteDialog(Context context, long orderId, long gameId, String gameName, String userID, String nickname, List<String> toUsers) {
        if (inviteDialog == null || !inviteDialog.isShowing()) {
            orderModel = new InviteOrderModel();
            orderModel.orderId = orderId;
            orderModel.gameId = gameId;
            orderModel.gameName = gameName;
            orderModel.sendUserId = userID;
            orderModel.sendUserName = nickname;
            orderModel.toUsers = toUsers;
            inviteDialog = new SimpleChooseDialog(context, context.getString(R.string.order_invite_conent, nickname, gameName),
                    context.getString(R.string.order_invite_left_btn),
                    context.getString(R.string.order_invite_right_btn));
            inviteDialog.setOnChooseListener(index -> {
                if (index == 1) {
                    dialogResult.postValue(1);
                } else {
                    dialogResult.postValue(2);
                }
                inviteDialog.dismiss();
                inviteDialog = null;
            });
            inviteDialog.show();
        }
    }

    /** 被拒绝弹窗 */
    public void operateDialog(Context context, String nickName) {
        if (operateDialog == null || !operateDialog.isShowing()) {
            operateDialog = new SimpleChooseDialog(context, context.getString(R.string.order_result_conent, nickName),
                    context.getString(R.string.cancel), context.getString(R.string.confirm));
            operateDialog.setOnChooseListener(index -> {
                if (index == 1) {
                    dialogResult.postValue(3);
                } else {
                    dialogResult.postValue(4);
                }
                operateDialog.dismiss();
                operateDialog = null;
            });
            operateDialog.show();
        }
    }

    /** 结束弹窗 */
    public void finishDialog(Context context) {
        if (finishDialog == null || !finishDialog.isShowing()) {
            finishDialog = new SimpleChooseDialog(context, context.getString(R.string.order_finish_conent),
                    context.getString(R.string.order_finish_left_text), context.getString(R.string.order_finish_right_text));
            finishDialog.setOnChooseListener(index -> {
                if (index == 1) {
                    dialogResult.postValue(5);
                } else {
                    dialogResult.postValue(6);
                }
                finishDialog.dismiss();
                finishDialog = null;
            });
            finishDialog.show();
        }
    }

    @Override
    public void onGameMGCommonGameSettle(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameSettle model) {
        super.onGameMGCommonGameSettle(handle, model);
        if (orderDataLiveData.getValue() != null) {
            //游戏结束
            finishDialog(ActivityUtils.getTopActivity());
        }
        orderDataLiveData.setValue(null);
        orderModel = null;
    }

    @Override
    protected void getGameRect(GameViewInfoModel gameViewInfoModel) {
        gameViewInfoModel.view_game_rect.left = 0;
        gameViewInfoModel.view_game_rect.top = 0;
        gameViewInfoModel.view_game_rect.right = 0;
        gameViewInfoModel.view_game_rect.bottom = 0;
    }

    /** 别人邀请我 */
    public class InviteOrderModel {
        public String sendUserId;//邀请者id
        public String sendUserName;//邀请者昵称
        public long orderId; // 订单id
        public long gameId; //游戏id
        public String gameName; //游戏名字
        public List<String> toUsers; // 下单邀请的主播id列表
    }

}