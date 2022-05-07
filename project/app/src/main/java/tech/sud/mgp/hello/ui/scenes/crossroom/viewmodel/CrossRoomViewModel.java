package tech.sud.mgp.hello.ui.scenes.crossroom.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import tech.sud.mgp.hello.common.base.BaseViewModel;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.service.room.model.PkStatus;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.service.room.response.RoomPkAgreeResp;
import tech.sud.mgp.hello.service.room.response.RoomPkModel;
import tech.sud.mgp.hello.service.room.response.RoomPkRoomInfo;
import tech.sud.mgp.hello.service.room.response.RoomPkStartResp;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;

/**
 * 跨房Pk业务处理
 */
public class CrossRoomViewModel extends BaseViewModel {

    public final MutableLiveData<RoomPkAgreeResp> againPkLiveData = new MutableLiveData<>(); // 再来一次pk返回成功
    public final MutableLiveData<Boolean> roomPkSwitchLiveData = new MutableLiveData<>(); // 开启匹配或者关闭PK
    public final MutableLiveData<Object> roomPkStartLiveData = new MutableLiveData<>(); // 开始pk
    public final MutableLiveData<Integer> roomPkSettingsLiveData = new MutableLiveData<>(); // 设置时长

    /**
     * 初始化设置游戏房间的Id
     *
     * @param model
     */
    public long getGameRoomId(RoomInfoModel model) {
        long srcRoomId = getSrcRoomId(model);
        if (srcRoomId != 0) {
            return srcRoomId;
        }
        return model.roomId;
    }

    /** 获取发起方的房间Id */
    private long getSrcRoomId(RoomInfoModel model) {
        if (model.roomPkModel != null && model.roomPkModel.srcRoomInfo != null) {
            return model.roomPkModel.srcRoomInfo.roomId;
        }
        return 0;
    }

    /** 再来一轮pk */
    public void againPk(LifecycleOwner owner, RoomInfoModel model) {
        if (model.roomPkModel == null
                || model.roomPkModel.getPkRival() == null
                || model.roomPkModel.srcRoomInfo == null
                || model.roomPkModel.destRoomInfo == null
                || model.roomPkModel.pkStatus != PkStatus.PK_END) {
            return;
        }
        RoomRepository.roomPkAgree(owner, model.roomPkModel.srcRoomInfo.roomId,
                model.roomPkModel.destRoomInfo.roomId, new RxCallback<RoomPkAgreeResp>() {
                    @Override
                    public void onSuccess(RoomPkAgreeResp roomPkAgreeResp) {
                        super.onSuccess(roomPkAgreeResp);
                        if (roomPkAgreeResp == null) {
                            return;
                        }
                        if (model.roomPkModel == null
                                || model.roomPkModel.getPkRival() == null
                                || model.roomPkModel.srcRoomInfo == null
                                || model.roomPkModel.destRoomInfo == null
                                || model.roomPkModel.pkStatus != PkStatus.PK_END) {
                            return;
                        }
                        againPkLiveData.setValue(roomPkAgreeResp);
                    }
                });
    }

    /** 跨房pk，开启匹配或者关闭Pk */
    public void roomPkSwitch(LifecycleOwner owner, long roomId, boolean pkSwitch) {
        RoomRepository.roomPkSwitch(null, roomId, pkSwitch, new RxCallback<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                roomPkSwitchLiveData.setValue(pkSwitch);
            }
        });
    }

    /** 跨房pk，开始 */
    public void roomPkStart(LifecycleOwner owner, RoomInfoModel roomInfoModel, int minute) {
        RoomPkModel roomPkModel = roomInfoModel.roomPkModel;
        if (roomPkModel == null || roomPkModel.pkStatus != PkStatus.MATCHED) return;
        RoomPkRoomInfo pkRival = roomPkModel.getPkRival();
        if (pkRival == null) return;
        long pkRivalRoomId = pkRival.roomId;
        // 发送http请求
        RoomRepository.roomPkStart(owner, roomInfoModel.roomId, minute,
                new RxCallback<RoomPkStartResp>() {
                    @Override
                    public void onSuccess(RoomPkStartResp resp) {
                        super.onSuccess(resp);
                        if (resp == null || roomPkModel.pkStatus != PkStatus.MATCHED
                                || roomPkModel.getPkRival() == null
                                || roomPkModel.getPkRival().roomId != pkRivalRoomId) {
                            return;
                        }

                        roomPkModel.pkId = resp.pkId;
                        roomPkModel.totalMinute = minute;

                        roomPkStartLiveData.setValue(null);
                    }
                }
        );
    }

    /**
     * 跨房pk，重新设置时长
     *
     * @param minute 分钟数
     */
    public void roomPkSettings(LifecycleOwner owner, RoomInfoModel roomInfoModel, int minute) {
        RoomPkModel roomPkModel = roomInfoModel.roomPkModel;
        if (roomPkModel == null || roomPkModel.pkStatus != PkStatus.STARTED) return;
        RoomPkRoomInfo pkRival = roomPkModel.getPkRival();
        if (pkRival == null) return;
        // 发送http请求
        RoomRepository.roomPkDuration(owner, roomInfoModel.roomId, minute,
                new RxCallback<Object>() {
                    @Override
                    public void onSuccess(Object resp) {
                        super.onSuccess(resp);
                        if (roomPkModel.pkStatus != PkStatus.STARTED) return;

                        roomPkModel.totalMinute = minute;

                        roomPkSettingsLiveData.setValue(minute);
                    }
                }
        );
    }

}
