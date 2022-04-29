package tech.sud.mgp.hello.ui.scenes.crossroom.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import tech.sud.mgp.hello.common.base.BaseViewModel;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;

/**
 * 跨房Pk业务处理
 */
public class CrossRoomViewModel extends BaseViewModel {

    public MutableLiveData<Boolean> pkSwitchLiveData = new MutableLiveData<>();

    /**
     * 打开跨房Pk匹配
     */
    public void roomPkSwitch(LifecycleOwner owner, long roomId, boolean pkSwitch) {
        RoomRepository.roomPkSwitch(owner, roomId, pkSwitch, new RxCallback<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                pkSwitchLiveData.setValue(pkSwitch);
            }
        });
    }

}
