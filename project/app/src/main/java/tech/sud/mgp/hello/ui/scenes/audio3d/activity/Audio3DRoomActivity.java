package tech.sud.mgp.hello.ui.scenes.audio3d.activity;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState;
import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState.AppCustomCrSetSeats.CrSeatModel;
import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.common.utils.WebPUtils;
import tech.sud.mgp.hello.common.widget.dialog.BottomOptionDialog;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.service.room.req.Audio3DLockMicReq;
import tech.sud.mgp.hello.service.room.req.Audio3DSwitchMicReq;
import tech.sud.mgp.hello.service.room.req.Audio3DUpdateMicrophoneStateReq;
import tech.sud.mgp.hello.ui.scenes.audio.activity.AbsAudioRoomActivity;
import tech.sud.mgp.hello.ui.scenes.audio3d.model.EmojiModel;
import tech.sud.mgp.hello.ui.scenes.audio3d.viewmodel.Audio3DGameViewModel;
import tech.sud.mgp.hello.ui.scenes.audio3d.widget.adapter.EmojiAdapter;
import tech.sud.mgp.hello.ui.scenes.audio3d.widget.dialog.Audio3DSettingsDialog;
import tech.sud.mgp.hello.ui.scenes.audio3d.widget.dialog.PutOtherUpMicDialog;
import tech.sud.mgp.hello.ui.scenes.audio3d.widget.dialog.RoomUserInfoDialog;
import tech.sud.mgp.hello.ui.scenes.audio3d.widget.view.Audio3DGiftEffectView;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.GameViewParams;
import tech.sud.mgp.hello.ui.scenes.base.model.MicAnimModel;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.utils.UserInfoConverter;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.audio3d.Audio3DCmdFaceNotifyModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.listener.PlayResultListener;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.PlayResult;

/**
 * 3D语聊房页面
 */
public class Audio3DRoomActivity extends AbsAudioRoomActivity<Audio3DGameViewModel> {

    private View mViewSettings;
    private View mContainerTip;
    private TextView mTvTipGotIt;
    private EmojiAdapter mEmojiAdapter;
    private Audio3DSettingsDialog mAudio3DSettingsDialog;
    private boolean isFirstShowTip = true;
    private FrameLayout mContainerGameLoading;
    private TextView mTvGameProgress;
    private ImageView mIvLoadingAnim;
    private TextView mTvReloadGame;
    private ProgressBar mProgressBarGame;
    private Audio3DGiftEffectView mAudio3DGiftEffectView;
    private int mBossSeatIndex = 0; // 老板位位置

    @Override
    protected Audio3DGameViewModel initGameViewModel() {
        return new Audio3DGameViewModel();
    }

    @Override
    protected boolean beforeSetContentView() {
//        roomConfig.isShowInteractionGame = false;
        roomConfig.isSupportAddRobot = true;
        return super.beforeSetContentView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_audio_3d_room;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        gameViewModel.isShowLoadingGameBg = false;
        gameViewModel.isShowCustomLoading = true;

        mContainerTip = findViewById(R.id.container_tip);
        mTvTipGotIt = findViewById(R.id.tv_got_it);
        mContainerGameLoading = findViewById(R.id.container_game_loading);
        mAudio3DGiftEffectView = findViewById(R.id.audio3d_gift_effect_view);

        tvGameNumber.setVisibility(View.GONE);

        initSettingsView();

        initEmojiList();

        // 自定义进度条
        View inflate = View.inflate(this, R.layout.layout_audio3d_game_loading, null);
        mContainerGameLoading.addView(inflate, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mProgressBarGame = findViewById(R.id.progress_bar_game);
        mTvGameProgress = findViewById(R.id.tv_game_progress);
        mIvLoadingAnim = findViewById(R.id.iv_loading_anim);
        mTvReloadGame = findViewById(R.id.tv_reload_game);
        mContainerGameLoading.setVisibility(View.GONE);

        bringToFrontViews();

        ViewUtils.setMarginBottom(mAudio3DGiftEffectView,
                DensityUtils.dp2px(259) + ImmersionBar.getNavigationBarHeight(this) + DensityUtils.dp2px(20));
    }

    @Override
    protected void updatePageStyle() {
        super.updatePageStyle();
        if (playingGameId > 0) { // 玩着游戏
            bottomView.setEmojiVisibility(View.VISIBLE);
            inputMsgView.setEmojiVisibility(View.VISIBLE);
            mViewSettings.setVisibility(View.VISIBLE);
        } else {
            bottomView.setEmojiVisibility(View.GONE);
            inputMsgView.setEmojiVisibility(View.GONE);
            mViewSettings.setVisibility(View.GONE);
        }
    }

    private void initEmojiList() {
        RecyclerView recyclerView = inputMsgView.getRecyclerViewEmoji();
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4, RecyclerView.VERTICAL, false));
        mEmojiAdapter = new EmojiAdapter();
        recyclerView.setAdapter(mEmojiAdapter);

        List<EmojiModel> list = new ArrayList<>();
        EmojiModel flashModel = new EmojiModel(getString(R.string.light_flash), R.drawable.ic_audio3d_light_flash, 0);
        flashModel.type = 1;
        list.add(flashModel);
        list.add(new EmojiModel(getString(R.string.dance), R.drawable.ic_audio3d_dance, 1));
        list.add(new EmojiModel(getString(R.string.fly_kiss), R.drawable.ic_audio3d_fly_kiss, 2));
        list.add(new EmojiModel(getString(R.string.thanks), R.drawable.ic_audio3d_thanks, 3));
        list.add(new EmojiModel(getString(R.string.handclap), R.drawable.ic_audio3d_handclap, 4));
        list.add(new EmojiModel(getString(R.string.shy), R.drawable.ic_audio3d_shy, 5));
        list.add(new EmojiModel(getString(R.string.cheer), R.drawable.ic_audio3d_cheer, 6));
        list.add(new EmojiModel(getString(R.string.sad), R.drawable.ic_audio3d_sad, 7));
        list.add(new EmojiModel(getString(R.string.anger), R.drawable.ic_audio3d_anger, 8));
        mEmojiAdapter.setList(list);
    }

    private void initSettingsView() {
        int size = DensityUtils.dp2px(32);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.setMarginEnd(DensityUtils.dp2px(16));
        mViewSettings = new View(this);
        mViewSettings.setBackgroundResource(R.drawable.ic_audio3d_settings);
        bottomView.addViewToRight(mViewSettings, 1, params);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        gameViewModel.mOnInitDataLiveData.observe(this, model -> {
            if (binder != null) {
                binder.audio3DInitData(model);
            }
        });
        gameViewModel.mOnClickSeatLiveData.observe(this, this::onClickSeat);
        mContainerTip.setOnClickListener(v -> {
            mContainerTip.setVisibility(View.GONE);
        });
        mTvTipGotIt.setOnClickListener(v -> {
            mContainerTip.setVisibility(View.GONE);
        });
        mViewSettings.setOnClickListener(v -> {
            showSettingsDialog();
        });
        bottomView.setEmojiClickListener(v -> {
            if (isCanShowEmoji()) {
                inputMsgView.showEmoji();
            } else {
                ToastUtils.showShort(R.string.surprise_on_the_anchor);
            }
        });
        inputMsgView.setEmojiClickListener(v -> {
            onClickEmoji();
        });
        mEmojiAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                EmojiModel item = mEmojiAdapter.getItem(position);
                onClickEmojiItem(item);
            }
        });
        inputMsgView.setOnVisibilityListener(visibility -> {
            if (visibility == View.VISIBLE) {
                pauseRotate();
            } else {
                startRotate();
            }
        });
        gameViewModel.gameLoadingProgressLiveData.observe(this, model -> {
            // 提示处理
            if (model.progress == 100) {
                mContainerTip.postDelayed(() -> {
                    if (playingGameId > 0) {
                        if (isFirstShowTip) {
                            isFirstShowTip = false;
                            mContainerTip.setVisibility(View.VISIBLE);
                        }
                    }
                }, 10_000);
            }
            // 自定义进度条处理
//            public int stage; // 阶段：start=1,loading=2,end=3
            if (model.stage == 3) {
                mTvGameProgress.setText(getString(R.string.game_loading, model.progress + "%"));
                mProgressBarGame.setProgress(model.progress);
                mContainerGameLoading.setVisibility(View.GONE);
            } else {
                if (model.retCode == RetCode.SUCCESS) {
                    mTvGameProgress.setText(getString(R.string.game_loading, model.progress + "%"));
                    mProgressBarGame.setProgress(model.progress);
                    mTvReloadGame.setVisibility(View.GONE);
                } else {
                    mTvGameProgress.setText(getString(R.string.game_loading, model.progress + "%" + "(" + model.retCode + ")"));
                    mTvReloadGame.setVisibility(View.VISIBLE);
                }
            }
        });
        gameViewModel.gameViewLiveData.observe(this, new Observer<GameViewParams>() {
            @Override
            public void onChanged(GameViewParams params) {
                if (params.gameView == null) {
                    mContainerGameLoading.setVisibility(View.GONE);
                } else {
                    WebPUtils.loadWebP(mIvLoadingAnim, R.raw.audio3d_cube, -1, new PlayResultListener() {
                        @Override
                        public void onResult(PlayResult result) {
                        }
                    });
                    mContainerGameLoading.setVisibility(View.VISIBLE);
                }
            }
        });
        mTvReloadGame.setOnClickListener(v -> {
            gameViewModel.reloadMG();
            mTvReloadGame.setVisibility(View.GONE);
        });
    }

    private void showSettingsDialog() {
        if (binder == null) {
            return;
        }
        SudMGPAPPState.AppCustomCrSetRoomConfig config = binder.getAudio3DRoomConfig();
        if (config == null) {
            return;
        }

        mAudio3DSettingsDialog = new Audio3DSettingsDialog();
        mAudio3DSettingsDialog.setConfig(getRoomId(), config);
        mAudio3DSettingsDialog.show(getSupportFragmentManager(), null);
        mAudio3DSettingsDialog.setOnDestroyListener(() -> {
            mAudio3DSettingsDialog = null;
        });
        mAudio3DSettingsDialog.setOnConfigChangeListener(gameViewModel::notifyAppCustomCrSetRoomConfig);
    }

    private void onClickEmojiItem(EmojiModel item) {
        CrSeatModel selfSeatModel = findUserSeatModel(HSUserInfo.userId + "");
        if (selfSeatModel == null) {
            return;
        }
        if (binder != null) {
            binder.sendFaceNotify(item.type, item.actionId, selfSeatModel.seatIndex);
        }
        notifyEmoji2Game(item.type, item.actionId, selfSeatModel.seatIndex);
    }

    private void notifyEmoji2Game(int type, int actionId, int seatIndex) {
        if (type == 1) {
            SudMGPAPPState.AppCustomCrSetLightFlash model = new SudMGPAPPState.AppCustomCrSetLightFlash();
            model.seatIndex = seatIndex;
            gameViewModel.notifyAppCustomCrSetLightFlash(model);
            notifyAudioMicLight(seatIndex);
        } else {
            SudMGPAPPState.AppCustomCrPlayAnim model = new SudMGPAPPState.AppCustomCrPlayAnim();
            model.seatIndex = seatIndex;
            model.animId = actionId;
            gameViewModel.notifyAppCustomCrPlayAnim(model);
        }
    }

    /** 默认语聊房麦位爆灯效果 */
    private void notifyAudioMicLight(int seatIndex) {
        if (binder == null) {
            return;
        }
        CrSeatModel seatModel = findSeatModel(binder.getAudio3DRoomSeats(), seatIndex);
        if (seatModel == null) {
            return;
        }
        List<AudioRoomMicModel> micList = binder.getMicList();
        if (micList == null || micList.size() == 0) {
            return;
        }
        for (AudioRoomMicModel audioRoomMicModel : micList) {
            if ((audioRoomMicModel.userId + "").equals(seatModel.userId)) {
                MicAnimModel micAnimModel = new MicAnimModel();
                EmojiModel emojiModel = new EmojiModel();
                emojiModel.type = 1;
                micAnimModel.animModel = emojiModel;
                micView.startAnim(audioRoomMicModel.micIndex, micAnimModel);
                return;
            }
        }
    }

    private void onClickEmoji() {
        if (isCanShowEmoji()) {
            inputMsgView.clickEmoji();
        } else {
            ToastUtils.showShort(R.string.surprise_on_the_anchor);
        }
    }

    /** 在非0位置上的主播位，才可以展示自定义表情 */
    private boolean isCanShowEmoji() {
        CrSeatModel userSeatModel = findUserSeatModel(HSUserInfo.userId + "");
        return userSeatModel != null && userSeatModel.seatIndex != 0;
    }

    private void startRotate() {
        SudMGPAPPState.AppCustomCrPauseRotate model = new SudMGPAPPState.AppCustomCrPauseRotate();
        model.pause = 0;
        gameViewModel.notifyAppCustomCrPauseRotate(model);
    }

    private void pauseRotate() {
        SudMGPAPPState.AppCustomCrPauseRotate model = new SudMGPAPPState.AppCustomCrPauseRotate();
        model.pause = 1;
        gameViewModel.notifyAppCustomCrPauseRotate(model);
    }

    private void onClickSeat(SudMGPMGState.MGCustomCrClickSeat model) {
        if (binder == null || model == null) {
            return;
        }
        CrSeatModel seatModel = findSeatModel(binder.getAudio3DRoomSeats(), model.seatIndex);
        if (seatModel == null) {
            return;
        }

        if (seatModel.microState == CrSeatModel.MICRO_STATE_SOMEONE) { // 有人
            if ((HSUserInfo.userId + "").equals(seatModel.userId)) {
                onClickSelfSeat(seatModel);
            } else {
                onClickOtherUserSeat(seatModel);
            }
        } else if (seatModel.microState == CrSeatModel.MICRO_STATE_NO_ONE) { // 空位
            onClickEmptySeat(seatModel);
        } else if (seatModel.microState == CrSeatModel.MICRO_STATE_LOCKED) { // 麦位被锁
            onClickLockSeat(seatModel);
        }
    }

    private void onClickSelfSeat(CrSeatModel seatModel) {
        pauseRotate();
        BottomOptionDialog dialog = new BottomOptionDialog(this);
        int downMicKey = 1;
        dialog.addOption(downMicKey, getString(R.string.audio_down_mic)); // 增加下麦按钮
        dialog.setOnItemClickListener(new BottomOptionDialog.OnItemClickListener() {
            @Override
            public void onItemClick(BottomOptionDialog.BottomOptionModel model) {
                dialog.dismiss();
                if (model.key == downMicKey) {
                    sendAudio3DSwitchMic(HSUserInfo.userId, seatModel.seatIndex, false, null);
                }
            }
        });
        dialog.show();
        dialog.setOnDestroyListener(this::startRotate);
    }

    private void onClickOtherUserSeat(CrSeatModel seatModel) {
        if (!isOwner()) {
            showUserInfoDialog(seatModel.userId);
            return;
        }
        pauseRotate();
        BottomOptionDialog dialog = new BottomOptionDialog(this);
        int checkInfo = 1; // 查看资料
        int downMic = 2; // 抱下麦
        int lockMic = 3; // 解/锁麦
        int kickRoom = 5; // 踢出房间

        dialog.addOption(checkInfo, getString(R.string.check_infomation));
        dialog.addOption(downMic, getString(R.string.put_other_down_mic));
        dialog.addOption(lockMic, getString(R.string.lock_mic));
        dialog.addOption(kickRoom, getString(R.string.kick_out_room));

        dialog.setOnItemClickListener(new BottomOptionDialog.OnItemClickListener() {
            @Override
            public void onItemClick(BottomOptionDialog.BottomOptionModel model) {
                dialog.dismiss();
                if (model.key == checkInfo) {
                    showUserInfoDialog(seatModel.userId);
                } else if (model.key == downMic) {
                    sendAudio3DSwitchMic(seatModel.userId, seatModel.seatIndex, false, null);
                } else if (model.key == lockMic) {
                    sendAudio3DSwitchMic(seatModel.userId, seatModel.seatIndex, false, new RxCallback<Object>() {
                        @Override
                        public void onSuccess(Object o) {
                            super.onSuccess(o);
                            lockMic(seatModel.seatIndex, true);
                        }
                    });
                } else if (model.key == kickRoom) {
                    sendAudio3DSwitchMic(seatModel.userId, seatModel.seatIndex, false, null);
                    if (binder != null) {
                        try {
                            binder.kickOutRoom(Long.parseLong(seatModel.userId));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        dialog.show();
        dialog.setOnDestroyListener(() -> startRotate());
    }

    private void showUserInfoDialog(String userId) {
        try {
            showUserInfoDialog(Long.parseLong(userId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showUserInfoDialog(long userId) {
        pauseRotate();
        RoomUserInfoDialog dialog = RoomUserInfoDialog.newInstance(userId);
        dialog.show(getSupportFragmentManager(), null);
        dialog.setOnClickSendGiftListener(userInfoResp -> {
            showSendGiftDialog(UserInfoConverter.conver(userInfoResp), 0);
        });
        dialog.setOnDestroyListener(this::startRotate);
        Lifecycle lifecycle = dialog.getLifecycle();
        lifecycle.addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (lifecycle.getCurrentState() == Lifecycle.State.DESTROYED) {
                    lifecycle.removeObserver(this);
                    return;
                }
                pauseRotate();
            }
        });
    }

    private void onClickLockSeat(CrSeatModel seatModel) {
        if (!isOwner()) { // 非房主不能操作
            ToastUtils.showShort(R.string.mic_seat_locked);
            return;
        }
        pauseRotate();
        BottomOptionDialog dialog = new BottomOptionDialog(this);
        int unlock = 3; // 解锁
        dialog.addOption(unlock, getString(R.string.unlock));

        dialog.setOnItemClickListener(new BottomOptionDialog.OnItemClickListener() {
            @Override
            public void onItemClick(BottomOptionDialog.BottomOptionModel model) {
                dialog.dismiss();
                if (model.key == unlock) {
                    lockMic(seatModel.seatIndex, false);
                }
            }
        });
        dialog.show();
        dialog.setOnDestroyListener(this::startRotate);
    }

    private void onClickEmptySeat(CrSeatModel seatModel) {
        pauseRotate();
        BottomOptionDialog dialog = new BottomOptionDialog(this);
        int selfUpMic = 1; // 自己上麦
        int putOtherUpMic = 2; // 抱Ta上麦
        int lockMic = 3; // 锁麦
        if (seatModel.seatIndex == mBossSeatIndex) {
            if (isOwner()) {
                dialog.addOption(selfUpMic, getString(R.string.audio_got_mic));
                dialog.addOption(lockMic, getString(R.string.lock_mic));
            } else {
                dialog.addOption(selfUpMic, getString(R.string.audio_got_mic));
            }
        } else {
            if (isOwner()) {
                dialog.addOption(selfUpMic, getString(R.string.self_up_mic));
                dialog.addOption(putOtherUpMic, getString(R.string.put_other_up_mic));
                dialog.addOption(lockMic, getString(R.string.lock_mic));
            } else {
                dialog.addOption(selfUpMic, getString(R.string.audio_got_mic));
            }
        }

        dialog.setOnItemClickListener(new BottomOptionDialog.OnItemClickListener() {
            @Override
            public void onItemClick(BottomOptionDialog.BottomOptionModel model) {
                dialog.dismiss();
                if (model.key == selfUpMic) {
                    sendAudio3DSwitchMic(HSUserInfo.userId, seatModel.seatIndex, true, null);
                } else if (model.key == putOtherUpMic) {
                    showPutOtherUpMicDialog(seatModel);
                } else if (model.key == lockMic) {
                    lockMic(seatModel.seatIndex, true);
                }
            }
        });
        dialog.show();
        dialog.setOnDestroyListener(this::startRotate);
    }

    private void lockMic(int seatIndex, boolean isLock) {
        Audio3DLockMicReq req = new Audio3DLockMicReq();
        req.roomId = getRoomId();
        req.micIndex = seatIndex;
        req.handleType = isLock ? 0 : 1;
        RoomRepository.audio3DLockMic(this, req, new RxCallback<>());
    }

    private void showPutOtherUpMicDialog(CrSeatModel seatModel) {
        if (binder == null) {
            return;
        }

        PutOtherUpMicDialog dialog = new PutOtherUpMicDialog();
        dialog.seatList = binder.getAudio3DRoomSeats();

        List<AudioRoomMicModel> micList = binder.getMicList();
        List<AudioRoomMicModel> availableMicList = new ArrayList<>();
        if (micList != null) {
            for (AudioRoomMicModel audioRoomMicModel : micList) {
                if (audioRoomMicModel.userId > 0) {
                    availableMicList.add(audioRoomMicModel);
                }
            }
        }
        dialog.micList = availableMicList;
        dialog.seatIndex = seatModel.seatIndex;
        dialog.show(getSupportFragmentManager(), null);
        dialog.setOnPutOtherUpMicClickListener((seatIndex, model) -> {
            sendAudio3DSwitchMic(model.userId, seatIndex, true, null);
        });
    }

    private void sendAudio3DSwitchMic(long userId, int micIndex, boolean isUpMic, RxCallback<Object> callback) {
        Audio3DSwitchMicReq req = new Audio3DSwitchMicReq();
        req.roomId = getRoomId();
        req.userId = userId;
        req.micIndex = micIndex;
        req.handleType = isUpMic ? 0 : 1;
        if (callback == null) {
            callback = new RxCallback<>();
        }
        RoomRepository.audio3DSwitchMic(this, req, callback);
    }

    private void sendAudio3DSwitchMic(String userId, int micIndex, boolean isUpMic, RxCallback<Object> callback) {
        try {
            sendAudio3DSwitchMic(Long.parseLong(userId), micIndex, isUpMic, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CrSeatModel findSeatModel(List<CrSeatModel> seats, int seatIndex) {
        if (seats != null) {
            for (CrSeatModel seat : seats) {
                if (seat.seatIndex == seatIndex) {
                    return seat;
                }
            }
        }
        return null;
    }

    private CrSeatModel findUserSeatModel(String userId) {
        if (binder != null) {
            List<CrSeatModel> list = binder.getAudio3DRoomSeats();
            if (list == null || list.size() == 0) {
                return null;
            }
            for (CrSeatModel crSeatModel : list) {
                if (userId.equals(crSeatModel.userId)) {
                    return crSeatModel;
                }
            }
        }
        return null;
    }

    @Override
    public void onAudio3DSeats(SudMGPAPPState.AppCustomCrSetSeats model) {
        super.onAudio3DSeats(model);
        gameViewModel.notifyAppCustomCrSetSeats(model);
    }

    @Override
    public void onAudio3DConfig(SudMGPAPPState.AppCustomCrSetRoomConfig model) {
        super.onAudio3DConfig(model);
        gameViewModel.notifyAppCustomCrSetRoomConfig(model);
        if (mAudio3DSettingsDialog != null) {
            mAudio3DSettingsDialog.setConfig(getRoomId(), model);
        }
    }

    @Override
    protected void showGift(GiftModel giftModel, int giftCount, UserInfo fromUser, List<UserInfo> toUserList, boolean isAllSeat) {
        super.showGift(giftModel, giftCount, fromUser, toUserList, isAllSeat);
        mAudio3DGiftEffectView.showGift(giftModel, giftCount, fromUser, toUserList, isAllSeat);
        if (giftModel == null || toUserList == null || toUserList.size() == 0) {
            return;
        }
        if (playingGameId <= 0) {
            return;
        }

        SudMGPAPPState.AppCustomCrPlayGiftEffect model = new SudMGPAPPState.AppCustomCrPlayGiftEffect();
        model.giverUserId = fromUser.userID;
        model.giftList = new ArrayList<>();
        model.isAllSeat = isAllSeat;

        for (UserInfo toUser : toUserList) {
            CrSeatModel userSeatModel = findUserSeatModel(toUser.userID);
            if (userSeatModel == null) {
                continue;
            }
            SudMGPAPPState.AppCustomCrPlayGiftEffect.CrGiftModel crGiftModel = new SudMGPAPPState.AppCustomCrPlayGiftEffect.CrGiftModel();
            crGiftModel.seatIndex = userSeatModel.seatIndex;
            crGiftModel.level = randomAudio3DGiftLevel(giftModel);
            crGiftModel.count = giftCount;
            model.giftList.add(crGiftModel);
        }

        if (model.giftList.size() > 0) {
            gameViewModel.notifyAppCustomCrPlayGiftEffect(model);
        }
    }

    @Override
    protected boolean canShowGift() {
        return false;
    }

    /**
     * 业务规则是：
     * 当触发送礼时，需要将收礼主播所在面的ID、礼物等级（A/B/C）、礼物数量、是否全麦，通知游戏侧
     * （金币数是1和10的礼物，A级；金币数是50的礼物，B级；金币数是100的礼物，C级）
     * <p>
     * 当游戏收到对应通知时，随机掉落该级别的礼盒即可，A级（1-10）、B级（11-20）、C级（21-30）
     * 随机业务，由APP侧来做
     */
    private int randomAudio3DGiftLevel(GiftModel giftModel) {
        int fromLevel;
        int toLevel;
        if (giftModel.giftPrice >= 1 && giftModel.giftPrice <= 10) {
            fromLevel = 1;
            toLevel = 10;
        } else if (giftModel.giftPrice == 50) {
            fromLevel = 11;
            toLevel = 20;
        } else {
            fromLevel = 21;
            toLevel = 30;
        }
        return new Random().nextInt(toLevel - fromLevel) + fromLevel;
    }

    @Override
    public void onSoundLevel(String userId, int micIndex, float soundLevel) {
        super.onSoundLevel(userId, micIndex, soundLevel);
        CrSeatModel crSeatModel = findUserSeatModel(userId);
        if (crSeatModel == null) {
            return;
        }
        SudMGPAPPState.AppCustomCrMicphoneValueSeat model = new SudMGPAPPState.AppCustomCrMicphoneValueSeat();
        model.seatIndex = crSeatModel.seatIndex;
        model.value = (int) soundLevel;
        gameViewModel.notifyAppCustomCrMicphoneValueSeat(model);
    }

    @Override
    public void onMicStateChanged(boolean isOpened) {
        super.onMicStateChanged(isOpened);
        setAudio3DMicrophoneState(isOpened);
    }

    private void setAudio3DMicrophoneState(boolean isOpened) {
        Audio3DUpdateMicrophoneStateReq req = new Audio3DUpdateMicrophoneStateReq();
        req.roomId = getRoomId();
        req.userId = HSUserInfo.userId;
        // 麦克风状态  -1:禁麦  0:闭麦  1:开麦
        req.micphoneState = isOpened ? 1 : 0;
        RoomRepository.audio3DUpdateMicrophoneState(this, req, new RxCallback<>());
    }

    @Override
    public void onAudio3DFaceNotify(Audio3DCmdFaceNotifyModel model) {
        super.onAudio3DFaceNotify(model);
        notifyEmoji2Game(model.type, model.faceId, model.seatIndex);
    }

}