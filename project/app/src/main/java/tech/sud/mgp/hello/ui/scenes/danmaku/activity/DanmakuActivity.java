package tech.sud.mgp.hello.ui.scenes.danmaku.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.List;

import me.jessyan.autosize.AutoSize;
import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.internal.CustomAdapt;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.app.HelloSudApplication;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.common.utils.AnimUtils;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.service.room.resp.DanmakuListResp;
import tech.sud.mgp.hello.ui.common.utils.CompletedListener;
import tech.sud.mgp.hello.ui.common.utils.LifecycleUtils;
import tech.sud.mgp.hello.ui.scenes.base.interaction.base.activity.BaseInteractionRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.RoleType;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.utils.UserInfoConverter;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.SceneRoomTopView;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;
import tech.sud.mgp.hello.ui.scenes.danmaku.widget.AutoLandDialog;
import tech.sud.mgp.hello.ui.scenes.danmaku.widget.DanmakuListView;

/**
 * 弹幕场景房间
 */
public class DanmakuActivity extends BaseInteractionRoomActivity<AppGameViewModel> implements CustomAdapt {

    public static boolean isShowGuide = true; // 是否要显示引导

    private View viewPortrait;
    private View videoView;
    private View viewFullscreen;
    private DanmakuListView danmakuListView;
    private View viewShrink;
    private View viewSpread;

    // 横屏时的view
    private View viewLandscape;
    private View videoViewLand;
    private View viewExitFullscreen;
    private SceneRoomTopView topViewLand;
    private DanmakuListView danmakuListViewLand;
    private View viewShrinkLand;
    private View viewSpreadLand;
    private View viewTopArrowLand;
    private View viewShowWidget;
    private View viewGuide;
    private TextView tvGuide;

    private boolean isFullscreen;
    private View playingVideoView;

    private DanmakuListResp danmakuListResp;

    @Override
    protected AppGameViewModel initGameViewModel() {
        return new AppGameViewModel();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_danmaku;
    }

    @Override
    protected void updatePageStyle() {
        super.updatePageStyle();
    }

    @Override
    protected boolean beforeSetContentView() {
        roomConfig.isSudGame = false;
        return super.beforeSetContentView();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        viewPortrait = findViewById(R.id.cl_portrait);
        videoView = findViewById(R.id.video_view);
        viewFullscreen = findViewById(R.id.view_fullscreen);
        danmakuListView = findViewById(R.id.danmaku_list_view);
        viewShrink = findViewById(R.id.view_shrink);
        viewSpread = findViewById(R.id.view_spread);

        viewLandscape = findViewById(R.id.cl_landscape);
        videoViewLand = findViewById(R.id.video_view_land);
        viewExitFullscreen = findViewById(R.id.view_exit_fullscreen);
        topViewLand = findViewById(R.id.room_top_view_land);
        danmakuListViewLand = findViewById(R.id.danmaku_list_view_land);
        viewShrinkLand = findViewById(R.id.view_shrink_land);
        viewSpreadLand = findViewById(R.id.view_spread_land);
        viewTopArrowLand = findViewById(R.id.view_top_arrow_land);
        viewShowWidget = findViewById(R.id.view_show_widget);
        viewGuide = findViewById(R.id.cl_guide);
        tvGuide = findViewById(R.id.tv_guide);

        topView.setSelectGameVisible(false);
        topViewLand.setName(roomInfoModel.roomName);
        topViewLand.setId(getString(R.string.audio_room_number) + " " + roomInfoModel.roomNumber);
        topViewLand.setBackVisibility(View.VISIBLE);
        topViewLand.setFinishGameVisible(false);
        topViewLand.setSelectGameVisible(false);

        bottomView.setLeftContainerChildViewsVisibility(View.GONE);

        ConstraintLayout.LayoutParams chatParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 0);
        chatParams.bottomToTop = R.id.room_bottom_view;
        chatParams.topToBottom = R.id.cl_video;
        chatView.setLayoutParams(chatParams);
        chatView.normalTopMargin = DensityUtils.dp2px(this, 26);
        chatView.updateStyle();

        bringToFrontViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDanmakuList();
    }

    /** 刷新弹幕列表 */
    private void refreshDanmakuList() {
        RoomRepository.danmakuList(this, roomInfoModel.gameId, getRoomId(), new RxCallback<DanmakuListResp>() {
            @Override
            public void onSuccess(DanmakuListResp resp) {
                super.onSuccess(resp);
                tvGuide.setText(resp.guideText);
                danmakuListResp = resp;
                danmakuListView.setDatas(resp);
                danmakuListViewLand.setDatas(resp);
            }
        });
    }

    /** 判断是否要显示横屏提示 */
    private void checkShowLandHint() {
        if (interceptShowAutoLandHint()) {
            return;
        }
        viewFullscreen.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFullscreen) {
                    return;
                }
                LifecycleUtils.safeLifecycle(context, new CompletedListener() {
                    @Override
                    public void onCompleted() {
                        showAutoLandDialog();
                    }
                });
            }
        }, 5000);
        AppData.getInstance().setDanmakuLandHintCompleted(true);
    }

    /** 是否要阻拦横屏提示弹窗显示 */
    private boolean interceptShowAutoLandHint() {
        return AppData.getInstance().isDanmakuLandHintCompleted() || isFullscreen;
    }

    /** 显示横屏弹窗 */
    private void showAutoLandDialog() {
        AutoLandDialog dialog = new AutoLandDialog();
        dialog.setCompletedListener(new CompletedListener() {
            @Override
            public void onCompleted() {
                LifecycleUtils.safeLifecycle(context, new CompletedListener() {
                    @Override
                    public void onCompleted() {
                        startFullscreen();
                    }
                });
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    private void startVideo() {
        if (binder != null) {
            if (isFullscreen) {
                binder.startVideo(roomInfoModel.streamId, null, videoViewLand);
                playingVideoView = videoViewLand;
            } else {
                binder.startVideo(roomInfoModel.streamId, null, videoView);
                playingVideoView = videoView;
            }
        }
    }

    private void stopVideo() {
        if (binder != null && roomInfoModel != null) {
            binder.stopVideo(roomInfoModel.streamId, playingVideoView);
        }
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        // 竖屏的
        viewShrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                danmakuListView.setVisibility(View.GONE);
                viewShrink.setVisibility(View.GONE);
                viewSpread.setVisibility(View.VISIBLE);
            }
        });
        viewSpread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                danmakuListView.setVisibility(View.VISIBLE);
                viewShrink.setVisibility(View.VISIBLE);
                viewSpread.setVisibility(View.GONE);
            }
        });
        viewFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFullscreen();
            }
        });
        danmakuListView.setDanmakuOnClickListener(danmakuOnClickListener);

        // 横屏的
        viewShrinkLand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                danmakuListViewLand.setVisibility(View.GONE);
                viewShrinkLand.setVisibility(View.GONE);
                viewSpreadLand.setVisibility(View.VISIBLE);
                AnimUtils.shakeVertical(viewTopArrowLand, 500, DensityUtils.dp2px(2));
                // 让引导View消失
                isShowGuide = false;
                viewGuide.setVisibility(View.GONE);
            }
        });
        viewSpreadLand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spreadLandDanmakuList();
            }
        });
        viewExitFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitFullscreen();
            }
        });
        topViewLand.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitFullscreen();
            }
        });
        topViewLand.setMoreOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickMore();
            }
        });
        viewShowWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (topViewLand.getVisibility() == View.VISIBLE) {
                    hideFullscreenWidget();
                } else {
                    showFullscreenWidget();
                }
            }
        });
        viewGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowGuide = false;
                viewGuide.setVisibility(View.GONE);
                cancelDelayShowGuide();
            }
        });
        danmakuListViewLand.setDanmakuOnClickListener(danmakuOnClickListener);
    }

    /** 展开横屏的弹幕列表 */
    private void spreadLandDanmakuList() {
        danmakuListViewLand.setVisibility(View.VISIBLE);
        viewShrinkLand.setVisibility(View.VISIBLE);
        viewSpreadLand.setVisibility(View.GONE);
    }

    private DanmakuListView.DanmakuOnClickListener danmakuOnClickListener = new DanmakuListView.DanmakuOnClickListener() {
        @Override
        public void onClickTeam(DanmakuListResp.JoinTeam model) {
            fastDanmaku(model.content);
        }

        @Override
        public void onClickProp(DanmakuListResp.Prop model) {
            if (model.callMode == DanmakuListResp.CALL_MODE_DANMAKU) {
                fastDanmaku(model.content);
            } else if (model.callMode == DanmakuListResp.CALL_MODE_GIFT) {
                fastSendGift(model);
            }
        }
    };

    /** 快捷指令送出礼物 */
    private void fastSendGift(DanmakuListResp.Prop model) {
        // 送给房主
        UserInfo toUser = null;
        if (binder != null) {
            List<AudioRoomMicModel> micList = binder.getMicList();
            if (micList != null) {
                for (AudioRoomMicModel audioRoomMicModel : micList) {
                    if (audioRoomMicModel.roleType == RoleType.OWNER) {
                        toUser = UserInfoConverter.conver(audioRoomMicModel);
                    }
                }
            }
        }

        if (toUser == null) {
            return;
        }

        // 发送http到后端
        UserInfo finalToUser = toUser;
        RoomRepository.sendGift(this, roomInfoModel.roomId, model.giftId, 1, 2, model.giftPrice, new RxCallback<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);

                // 展示礼物
                GiftModel giftModel = new GiftModel();
                giftModel.giftId = model.giftId;
                giftModel.type = 1;
                giftModel.giftName = model.name;
                giftModel.giftUrl = model.giftUrl;
                giftModel.animationUrl = model.animationUrl;
                giftModel.giftPrice = model.giftPrice;
                showGift(giftModel);

                // 发送"送礼消息"
                if (binder != null) {
                    binder.sendGift(giftModel, 1, finalToUser);
                }
            }
        });
    }

    /** 快捷指令发弹幕 */
    private void fastDanmaku(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        // 发送弹幕内容到后端
        RoomRepository.sendDanmaku(this, roomInfoModel.roomId, content, new RxCallback<>());

        // 发送公屏
        if (binder != null) {
            binder.sendPublicMsg(content);
        }
    }

    /** 发送了公屏 */
    @Override
    public void onSelfSendMsg(String msg) {
        super.onSelfSendMsg(msg);
        checkHitDanmaku(msg);
    }

    /** 检查公屏消息是否命中了弹幕 */
    private void checkHitDanmaku(String msg) {
        DanmakuListResp resp = danmakuListResp;
        if (TextUtils.isEmpty(msg) || resp == null) {
            return;
        }
        if (resp.joinTeamList != null) {
            for (DanmakuListResp.JoinTeam model : resp.joinTeamList) {
                if (msg.equals(model.content)) {
                    // 发送弹幕内容到后端
                    RoomRepository.sendDanmaku(this, roomInfoModel.roomId, msg, new RxCallback<>());
                }
            }
        }
        if (resp.propList != null) {
            for (DanmakuListResp.Prop model : resp.propList) {
                if (model.callMode == DanmakuListResp.CALL_MODE_DANMAKU && msg.equals(model.content)) {
                    // 发送弹幕内容到后端
                    RoomRepository.sendDanmaku(this, roomInfoModel.roomId, msg, new RxCallback<>());
                }
            }
        }
    }

    /** 开启全屏 */
    private void startFullscreen() {
        dismissAllDialog();
        AppData.getInstance().setDanmakuLandHintCompleted(true);
        isFullscreen = true;
        stopVideo();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        viewPortrait.setVisibility(View.GONE);
        viewLandscape.setVisibility(View.VISIBLE);
        updateStatusBar();
        startVideo();
        showFullscreenWidget();
        checkShowGuide();
        spreadLandDanmakuList();
    }

    private void dismissAllDialog() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof DialogFragment) {
                DialogFragment dialogFragment = (DialogFragment) fragment;
                dialogFragment.dismiss();
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogUtils.d("density onConfigurationChanged:" + getResources().getDisplayMetrics().density);
        HelloSudApplication.instance.setAutoSizeScreenSize();
        AutoSize.autoConvertDensityOfCustomAdapt(this, this);
    }

    private void checkShowGuide() {
        if (isShowGuide) {
            startDelayShowGuide();
        }
    }

    private void startDelayShowGuide() {
        cancelDelayShowGuide();
        viewGuide.postDelayed(delayShowGuide, 3000);
    }

    private void cancelDelayShowGuide() {
        viewGuide.removeCallbacks(delayShowGuide);
    }

    private final Runnable delayShowGuide = new Runnable() {
        @Override
        public void run() {
            isShowGuide = false;
            if (isFullscreen && danmakuListViewLand.getVisibility() == View.VISIBLE) {
                viewGuide.setVisibility(View.VISIBLE);
            }
        }
    };

    /** 退出全屏 */
    private void exitFullscreen() {
        isFullscreen = false;
        updateStatusBar();
        stopVideo();
        ScreenUtils.setPortrait(context);
        viewPortrait.setVisibility(View.VISIBLE);
        viewLandscape.setVisibility(View.GONE);
        startVideo();
    }

    /** 显示全屏时的"更多"控件 */
    private void showFullscreenWidget() {
        startDelayHideFullscreenWidget();
        topViewLand.setVisibility(View.VISIBLE);
        viewExitFullscreen.setVisibility(View.VISIBLE);
    }

    /** 隐藏全屏时的"更多"控件 */
    private void hideFullscreenWidget() {
        topViewLand.setVisibility(View.GONE);
        viewExitFullscreen.setVisibility(View.GONE);
    }

    /** 一定时间之后，自动隐藏全屏时的"更多"控件 */
    private void startDelayHideFullscreenWidget() {
        cancelDelayHideFullscreenWidget();
        topViewLand.postDelayed(delayHideFullscreenWidget, 3000);
    }

    /** 取消delay 自动隐藏全屏时的"更多"控件 */
    private void cancelDelayHideFullscreenWidget() {
        topViewLand.removeCallbacks(delayHideFullscreenWidget);
    }

    /** delay 自动隐藏全屏时的"更多"控件 */
    private final Runnable delayHideFullscreenWidget = new Runnable() {
        @Override
        public void run() {
            hideFullscreenWidget();
        }
    };

    @Override
    protected void updateStatusBar() {
        super.updateStatusBar();
        if (isFullscreen) { // 横屏时，设置为全屏
            ImmersionBar.with(this).statusBarColor(R.color.transparent).fullScreen(true).hideBar(BarHide.FLAG_HIDE_BAR).init();
        } else {
            ImmersionBar.with(this).statusBarColor(R.color.transparent).fullScreen(false).hideBar(BarHide.FLAG_SHOW_BAR).init();
        }
    }

    @Override
    public boolean isBaseOnWidth() {
        return !isFullscreen;
    }

    @Override
    public float getSizeInDp() {
        return AutoSizeConfig.getInstance().getDesignWidthInDp();
    }

    // region service回调
    @Override
    public void onRoomOnlineUserCountUpdate(int count) {
        super.onRoomOnlineUserCountUpdate(count);
        topViewLand.setNumber(count + "");
    }

    @Override
    public void onRecoverCompleted() {
        super.onRecoverCompleted();
        startVideo();
        checkShowLandHint();
    }

    @Override
    public void onEnterRoomSuccess() {
        super.onEnterRoomSuccess();
        startVideo();
        checkShowLandHint();
    }
    // endregion service回调

    @Override
    public void onBackPressed() {
        if (isFullscreen) {
            exitFullscreen();
            return;
        }
        super.onBackPressed();
    }

    /** 释放资源 */
    @Override
    protected void releaseService() {
        stopVideo();
        super.releaseService();
    }

    /** 是否可以展示礼物 */
    @Override
    protected boolean canShowGift() {
        return !isFullscreen; // 非全屏时才展示礼物动画
    }

    /** 业务上，自动上麦 */
    @Override
    protected void businessAutoUpMic() {
        if (roomInfoModel != null && roomInfoModel.roleType == RoleType.OWNER) {
            super.businessAutoUpMic();
        }
    }

    /** 获取初始化礼物弹窗时的麦位数据 */
    @Override
    protected List<AudioRoomMicModel> getGiftDialogMicList() {
        if (binder != null) {
            List<AudioRoomMicModel> filterList = new ArrayList<>();
            List<AudioRoomMicModel> micList = binder.getMicList();
            if (micList != null) {
                for (AudioRoomMicModel model : micList) {
                    if (model.roleType == RoleType.OWNER) {
                        filterList.add(model);
                    }
                }
            }
            return filterList;
        }
        return null;
    }

    /** 更新礼物弹窗上面的麦位数据 */
    @Override
    protected void updateGiftDialogMicUsers(AudioRoomMicModel model) {
        if (model.roleType == RoleType.OWNER) {
            super.updateGiftDialogMicUsers(model);
        }
    }

}
