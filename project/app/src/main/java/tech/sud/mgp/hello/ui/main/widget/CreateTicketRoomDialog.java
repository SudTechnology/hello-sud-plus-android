package tech.sud.mgp.hello.ui.main.widget;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.ClickUtils;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.CreatRoomResp;
import tech.sud.mgp.hello.ui.main.constant.GameLevel;
import tech.sud.mgp.hello.ui.main.constant.SceneType;
import tech.sud.mgp.hello.ui.scenes.base.utils.EnterRoomUtils;

/**
 * 创建门票房间的弹窗
 */
public class CreateTicketRoomDialog extends BaseDialogFragment implements View.OnClickListener {

    private boolean isCreating = false;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_create_ticket_room;
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected int getWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getHeight() {
        return DensityUtils.dp2px(246);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        ClickUtils.applyGlobalDebouncing(findViewById(R.id.cl_primary), this);
        ClickUtils.applyGlobalDebouncing(findViewById(R.id.cl_middle), this);
        ClickUtils.applyGlobalDebouncing(findViewById(R.id.cl_high), this);
    }

    @Override
    protected void customStyle(Window window) {
        super.customStyle(window);
        window.setWindowAnimations(R.style.BottomToTopAnim);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cl_primary) {
            createRoom(GameLevel.PRIMARY);
        } else if (id == R.id.cl_middle) {
            createRoom(GameLevel.MIDDLE);
        } else if (id == R.id.cl_high) {
            createRoom(GameLevel.HIGH);
        }
    }

    private void createRoom(int gameLevel) {
        if (isCreating) return;
        isCreating = true;
        HomeRepository.creatRoom(SceneType.TICKET, null, gameLevel, this, new RxCallback<CreatRoomResp>() {
            @Override
            public void onNext(BaseResponse<CreatRoomResp> t) {
                super.onNext(t);
                FragmentActivity activity = getActivity();
                if (activity == null || activity.isDestroyed()) return;
                if (t.getRetCode() == RetCode.SUCCESS) {
                    dismiss();
                    EnterRoomUtils.enterRoom(activity, t.getData().roomId);
                }
                isCreating = false;
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                isCreating = false;
            }
        });
    }

}
