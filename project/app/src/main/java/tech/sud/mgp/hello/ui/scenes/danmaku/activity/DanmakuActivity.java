package tech.sud.mgp.hello.ui.scenes.danmaku.activity;

import android.content.pm.ActivityInfo;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.ScreenUtils;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.internal.CustomAdapt;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.ui.common.utils.CompletedListener;
import tech.sud.mgp.hello.ui.common.utils.LifecycleUtils;
import tech.sud.mgp.hello.ui.scenes.base.activity.BaseRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.SceneRoomTopView;
import tech.sud.mgp.hello.ui.scenes.danmaku.widget.AutoLandDialog;
import tech.sud.mgp.hello.ui.scenes.danmaku.widget.DanmakuListView;

/**
 * 弹幕场景房间
 */
public class DanmakuActivity extends BaseRoomActivity<AppGameViewModel> implements CustomAdapt {

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

    private boolean isFullscreen;

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

        topView.setSelectGameVisible(false);
        topViewLand.setName(roomInfoModel.roomName);
        topViewLand.setId(getString(R.string.audio_room_number) + " " + roomInfoModel.roomNumber);
        topViewLand.setBackVisibility(View.VISIBLE);
        topViewLand.setFinishGameVisible(false);
        topViewLand.setSelectGameVisible(false);

        ConstraintLayout.LayoutParams chatParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 0);
        chatParams.bottomToTop = R.id.room_bottom_view;
        chatParams.topToBottom = R.id.cl_video;
        chatView.setLayoutParams(chatParams);
        chatView.normalTopMargin = DensityUtils.dp2px(this, 26);
        chatView.updateStyle();

        giftContainer.bringToFront();
        inputMsgView.bringToFront();
        clOpenMic.bringToFront();
    }

    /** 判断是否要显示横屏提示 */
    private void checkShowLandHint() {
        if (interceptShowAutoLandHint()) {
            return;
        }
        viewFullscreen.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (interceptShowAutoLandHint()) {
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
                startFullscreen();
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    private void startVideo() {
        if (binder != null) {
            if (isFullscreen) {
                binder.startVideo(roomInfoModel.streamId, videoViewLand);
            } else {
                binder.startVideo(roomInfoModel.streamId, videoView);
            }
        }
    }

    private void stopVideo() {
        if (binder != null && roomInfoModel != null) {
            binder.stopVideo(roomInfoModel.streamId);
        }
    }

    @Override
    protected void setListeners() {
        super.setListeners();
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
    }

    /** 开启全屏 */
    private void startFullscreen() {
        AppData.getInstance().setDanmakuLandHintCompleted(true);
        isFullscreen = true;
        stopVideo();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        viewPortrait.setVisibility(View.GONE);
        viewLandscape.setVisibility(View.VISIBLE);
        updateStatusBar();
        startVideo();
    }

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

    @Override
    protected void releaseService() {
        stopVideo();
        super.releaseService();
    }

}
