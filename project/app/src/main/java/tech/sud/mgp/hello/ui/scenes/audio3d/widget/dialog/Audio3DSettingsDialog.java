package tech.sud.mgp.hello.ui.scenes.audio3d.widget.dialog;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.ToastUtils;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;

public class Audio3DSettingsDialog extends BaseDialogFragment {

    private ImageView mIvIconTurn;
    private ImageView mIvIconLight;
    private ImageView mIvIconSound;

    private TextView mTvStatusTurn;
    private TextView mTvStatusLight;
    private TextView mTvStatusSound;

    private View mContainerTurn;
    private View mContainerLight;
    private View mContainerSound;

    private long mRoomId;
    private SudMGPAPPState.AppCustomCrSetRoomConfig mConfig;
    private final MutableLiveData<Object> mUpdateConfigLiveData = new MutableLiveData<>();
    private OnConfigChangeListener mOnConfigChangeListener;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_audio3d_settings;
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
    protected void customStyle(Window window) {
        super.customStyle(window);
        window.setWindowAnimations(R.style.BottomToTopAnim);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mIvIconTurn = findViewById(R.id.iv_icon_turn);
        mIvIconLight = findViewById(R.id.iv_icon_light);
        mIvIconSound = findViewById(R.id.iv_icon_sound);
        mTvStatusTurn = findViewById(R.id.tv_status_turn);
        mTvStatusLight = findViewById(R.id.tv_status_light);
        mTvStatusSound = findViewById(R.id.tv_status_sound);
        mContainerTurn = findViewById(R.id.container_turn);
        mContainerLight = findViewById(R.id.container_light);
        mContainerSound = findViewById(R.id.container_sound);
    }

    @Override
    protected void initData() {
        super.initData();
        updateConfig();
    }

    private void updateConfig() {
        SudMGPAPPState.AppCustomCrSetRoomConfig config = mConfig;
        if (config == null) {
            return;
        }

        if (config.platformRotate == 1) {
            mIvIconTurn.setBackgroundResource(R.drawable.ic_audio3d_turn);
            setTvStatus(mTvStatusTurn, true);
        } else {
            mIvIconTurn.setBackgroundResource(R.drawable.ic_audio3d_turn_closed);
            setTvStatus(mTvStatusTurn, false);
        }

        if (config.flashVFX == 1) {
            mIvIconLight.setBackgroundResource(R.drawable.ic_audio3d_light);
            setTvStatus(mTvStatusLight, true);
        } else {
            mIvIconLight.setBackgroundResource(R.drawable.ic_audio3d_light_closed);
            setTvStatus(mTvStatusLight, false);
        }

        if (config.micphoneWave == 1) {
            mIvIconSound.setBackgroundResource(R.drawable.ic_audio3d_sound);
            setTvStatus(mTvStatusSound, true);
        } else {
            mIvIconSound.setBackgroundResource(R.drawable.ic_audio3d_sound_closed);
            setTvStatus(mTvStatusSound, false);
        }
    }

    private void setTvStatus(TextView tvStatus, boolean isOpen) {
        if (isOpen) {
            tvStatus.setBackgroundResource(R.drawable.shape_r20_6ca545);
            tvStatus.setText(R.string.now_on);
        } else {
            tvStatus.setBackgroundResource(R.drawable.shape_r20_f55050);
            tvStatus.setText(R.string.closed);
        }
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        mUpdateConfigLiveData.observe(this, obj -> {
            updateConfig();
        });
        mContainerTurn.setOnClickListener(v -> {
            if (mConfig != null) {
                mConfig.platformRotate = mConfig.platformRotate == 1 ? 0 : 1;
                ToastUtils.showShort(mConfig.platformRotate == 1 ? R.string.turn_effect_opened : R.string.turn_effect_closed);
                reqSetConfig();
            }
        });
        mContainerLight.setOnClickListener(v -> {
            if (mConfig != null) {
                mConfig.flashVFX = mConfig.flashVFX == 1 ? 0 : 1;
                ToastUtils.showShort(mConfig.flashVFX == 1 ? R.string.light_effect_opened : R.string.light_effect_closed);
                reqSetConfig();
            }
        });
        mContainerSound.setOnClickListener(v -> {
            if (mConfig != null) {
                mConfig.micphoneWave = mConfig.micphoneWave == 1 ? 0 : 1;
                ToastUtils.showShort(mConfig.micphoneWave == 1 ? R.string.sound_effect_opened : R.string.sound_effect_closed);
                reqSetConfig();
            }
        });
    }

    private void reqSetConfig() {
        SudMGPAPPState.AppCustomCrSetRoomConfig config = mConfig;
        if (config == null) {
            return;
        }
        boolean isSync = false; // 2023-08-17 是否要同步房间里的所有人，现在改为不同步
        if (isSync) {
            RoomRepository.audio3DSetConfig(this, mRoomId, config, new RxCallback<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    updateConfig();
                }
            });
        } else {
            updateConfig();
            if (mOnConfigChangeListener != null) {
                mOnConfigChangeListener.onChange(config);
            }
        }
    }

    public void setConfig(long roomId, SudMGPAPPState.AppCustomCrSetRoomConfig config) {
        mRoomId = roomId;
        mConfig = config;
        mUpdateConfigLiveData.setValue(config);
    }

    public void setOnConfigChangeListener(OnConfigChangeListener onConfigChangeListener) {
        mOnConfigChangeListener = onConfigChangeListener;
    }

    public interface OnConfigChangeListener {
        void onChange(SudMGPAPPState.AppCustomCrSetRoomConfig config);
    }

}
