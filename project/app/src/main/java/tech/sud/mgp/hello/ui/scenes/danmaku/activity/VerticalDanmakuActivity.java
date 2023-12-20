package tech.sud.mgp.hello.ui.scenes.danmaku.activity;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.service.room.resp.DanmakuListResp;
import tech.sud.mgp.hello.ui.scenes.base.interaction.base.activity.BaseInteractionRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.RoleType;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.utils.UserInfoConverter;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.RoomCmdModelUtils;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.danmaku.RoomCmdDanmakuTeamChangeModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;
import tech.sud.mgp.hello.ui.scenes.danmaku.widget.VerticalDanmakuListView;
import tech.sud.mgp.rtc.audio.core.MediaViewMode;

/**
 * 竖版弹幕场景房间
 */
public class VerticalDanmakuActivity extends BaseInteractionRoomActivity<AppGameViewModel> {

    private View videoView;
    private VerticalDanmakuListView danmakuListView;

    private View playingVideoView;

    private DanmakuListResp danmakuListResp;
    private View mViewArrowFold;
    private boolean isFoldWidget; // 当前是否收起了公屏等元素

    @Override
    protected AppGameViewModel initGameViewModel() {
        return new AppGameViewModel();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_vertical_danmaku;
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
        videoView = findViewById(R.id.video_view);
        danmakuListView = findViewById(R.id.danmaku_list_view);

        topView.setSelectGameVisible(false);

        bottomView.removeGotMicView();
        bottomView.removeMicStateView();

        ConstraintLayout.LayoutParams chatParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, DensityUtils.dp2px(169));
        chatParams.bottomToTop = R.id.danmaku_list_view;
        chatParams.bottomMargin = DensityUtils.dp2px(8);
        chatView.setLayoutParams(chatParams);

        bringToFrontViews();

        bottomView.setGiftBackground(R.drawable.ic_white_gift);
        bottomView.setInputBackground(R.drawable.shape_white_content_bg);

        mViewArrowFold = new View(this);
        updateFoldButton();
        LinearLayout.LayoutParams foldParams = new LinearLayout.LayoutParams(DensityUtils.dp2px(43), DensityUtils.dp2px(32));
        foldParams.setMarginStart(DensityUtils.dp2px(15));
        bottomView.addViewToLeft(mViewArrowFold, 0, foldParams);
    }

    private void updateFoldButton() {
        if (isFoldWidget) {
            mViewArrowFold.setBackgroundResource(R.drawable.ic_fold_arrow_right);
        } else {
            mViewArrowFold.setBackgroundResource(R.drawable.ic_fold_arrow_left);
        }
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
                danmakuListResp = resp;
                danmakuListView.setDatas(resp);
            }
        });
    }

    private void startVideo() {
        if (binder != null) {
            binder.startVideo(roomInfoModel.streamId, MediaViewMode.ASPECT_FILL, videoView);
            playingVideoView = videoView;
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
        danmakuListView.setDanmakuOnClickListener(danmakuOnClickListener);
        mViewArrowFold.setOnClickListener(v -> {
            isFoldWidget = !isFoldWidget;
            updateFoldButton();
            if (isFoldWidget) {
                chatView.setVisibility(View.GONE);
                danmakuListView.setVisibility(View.GONE);
                bottomView.setInputVisibility(View.GONE);
                bottomView.setGiftVisibility(View.GONE);
                topView.setVisibility(View.GONE);
            } else {
                chatView.setVisibility(View.VISIBLE);
                danmakuListView.setVisibility(View.VISIBLE);
                bottomView.setInputVisibility(View.VISIBLE);
                bottomView.setGiftVisibility(View.VISIBLE);
                topView.setVisibility(View.VISIBLE);
            }
        });
    }

    private VerticalDanmakuListView.DanmakuOnClickListener danmakuOnClickListener = new VerticalDanmakuListView.DanmakuOnClickListener() {
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
        List<UserInfo> toUserList = new ArrayList<>();
        toUserList.add(toUser);
        List<String> receiverList = new ArrayList<>();
        receiverList.add(toUser.userID);
        RoomRepository.sendGift(this, roomInfoModel.roomId, model.giftId, 1, 2, model.giftPrice, receiverList, new RxCallback<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);

                boolean isAllSeat = isAllSeat(toUserList);

                // 展示礼物
                GiftModel giftModel = new GiftModel();
                giftModel.giftId = model.giftId;
                giftModel.type = 1;
                giftModel.giftName = model.name;
                giftModel.giftUrl = model.giftUrl;
                giftModel.animationUrl = model.animationUrl;
                giftModel.giftPrice = model.giftPrice;
                showGift(giftModel, 1, RoomCmdModelUtils.getSendUser(), toUserList, isAllSeat);

                // 发送"送礼消息"
                if (binder != null) {
                    binder.sendGift(giftModel, 1, toUserList, isAllSeat);
                }
            }
        });
    }

    /** 快捷指令发弹幕 */
    private void fastDanmaku(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        // 发送公屏
        if (binder != null) {
            binder.sendPublicMsg(content);
        }
    }

    /** 发送了公屏 */
    @Override
    public void onSelfSendMsg(String msg) {
        super.onSelfSendMsg(msg);
        // 直接把消息发给后端，不过滤
        RoomRepository.sendDanmaku(this, roomInfoModel.roomId, msg, new RxCallback<>());
//        checkHitDanmaku(msg);
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

    @Override
    protected void updateStatusBar() {
        super.updateStatusBar();
        ImmersionBar.with(this).statusBarColor(R.color.transparent).fullScreen(false).hideBar(BarHide.FLAG_SHOW_BAR).init();
    }

    // region service回调
    @Override
    public void onRecoverCompleted() {
        super.onRecoverCompleted();
        startVideo();
    }

    @Override
    public void onEnterRoomSuccess() {
        super.onEnterRoomSuccess();
        startVideo();
    }
    // endregion service回调

    /** 释放资源 */
    @Override
    protected void releaseService() {
        stopVideo();
        super.releaseService();
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

    @Override
    public void onDanmakuMatch(RoomCmdDanmakuTeamChangeModel model) {
        super.onDanmakuMatch(model);
        refreshDanmakuList();
    }

    /** 拉流分辨率变化 */
    @Override
    public void onPlayerVideoSizeChanged(String streamID, int width, int height) {
        super.onPlayerVideoSizeChanged(streamID, width, height);
        // 根据分辨率，等比缩放显示到屏幕后的宽高来设置视频View的宽高
        if (width <= 0 || height <= 0) {
            return;
        }
        int appScreenWidth = DensityUtils.getAppScreenWidth();
        int appScreenHeight = DensityUtils.getAppScreenHeight();
        double widthRatio = appScreenWidth * 1.0 / width;
        double heightRatio = appScreenHeight * 1.0 / height;
        double ratio = Math.min(widthRatio, heightRatio);
        // 2023-11-11 修改为以宽度为准
        ratio = widthRatio;

        // 拿到等比缩放之后的宽高
        int scaledWidth = (int) (width * ratio);
        int scaledheight = (int) (height * ratio);
        if (videoView.getMeasuredWidth() != scaledWidth || videoView.getMeasuredHeight() != scaledheight) {
//            stopVideo();
            ViewGroup.LayoutParams params = videoView.getLayoutParams();
            params.width = scaledWidth;
            params.height = scaledheight;
            videoView.setLayoutParams(params);
//            startVideo();
        }
    }

}
