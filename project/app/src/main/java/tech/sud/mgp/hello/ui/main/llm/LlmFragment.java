package tech.sud.mgp.hello.ui.main.llm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import net.lucode.hackware.magicindicator.MagicIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseFragment;
import tech.sud.mgp.hello.common.event.model.JumpRocketEvent;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.Base64Utils;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.common.utils.WavRecorder;
import tech.sud.mgp.hello.common.utils.permission.PermissionFragment;
import tech.sud.mgp.hello.common.utils.permission.SudPermissionUtils;
import tech.sud.mgp.hello.service.main.manager.HomeManager;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.repository.UserInfoRepository;
import tech.sud.mgp.hello.service.main.req.GameListReq;
import tech.sud.mgp.hello.service.main.resp.AiInfoModel;
import tech.sud.mgp.hello.service.main.resp.CreatRoomResp;
import tech.sud.mgp.hello.service.main.resp.GameListResp;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.GetAiCloneResp;
import tech.sud.mgp.hello.service.main.resp.QuizGameListResp;
import tech.sud.mgp.hello.service.main.resp.SaveAiCloneResp;
import tech.sud.mgp.hello.service.main.resp.SceneModel;
import tech.sud.mgp.hello.service.main.resp.UpdateAiCloneResp;
import tech.sud.mgp.hello.service.main.resp.UserInfoResp;
import tech.sud.mgp.hello.service.room.req.SaveAiCloneReq;
import tech.sud.mgp.hello.service.room.resp.CrossAppModel;
import tech.sud.mgp.hello.ui.common.constant.RequestKey;
import tech.sud.mgp.hello.ui.common.dialog.LoadingDialog;
import tech.sud.mgp.hello.ui.common.utils.CompletedListener;
import tech.sud.mgp.hello.ui.common.utils.LifecycleUtils;
import tech.sud.mgp.hello.ui.main.base.constant.GameIdCons;
import tech.sud.mgp.hello.ui.main.base.constant.SceneType;
import tech.sud.mgp.hello.ui.main.base.widget.CreateTicketRoomDialog;
import tech.sud.mgp.hello.ui.main.base.widget.MainUserInfoView;
import tech.sud.mgp.hello.ui.main.home.manager.IndicatorHelper;
import tech.sud.mgp.hello.ui.main.home.model.MatchRoomModel;
import tech.sud.mgp.hello.ui.main.home.view.NewNestedScrollView;
import tech.sud.mgp.hello.ui.main.home.view.SceneTypeDialog;
import tech.sud.mgp.hello.ui.main.home.view.homeitem.CreatRoomClickListener;
import tech.sud.mgp.hello.ui.main.home.view.homeitem.GameItemListener;
import tech.sud.mgp.hello.ui.main.home.view.homeitem.HomeItemView;
import tech.sud.mgp.hello.ui.main.llm.widget.CreateLlmView;
import tech.sud.mgp.hello.ui.main.llm.widget.VoiceRecordView;
import tech.sud.mgp.hello.ui.main.nft.model.BindWalletInfoModel;
import tech.sud.mgp.hello.ui.main.nft.model.NftModel;
import tech.sud.mgp.hello.ui.main.nft.viewmodel.CancelWearNftListener;
import tech.sud.mgp.hello.ui.main.nft.viewmodel.NFTViewModel;
import tech.sud.mgp.hello.ui.scenes.base.model.EnterRoomParams;
import tech.sud.mgp.hello.ui.scenes.base.utils.EnterRoomUtils;
import tech.sud.mgp.hello.ui.scenes.base.utils.SafeAudioPlayer;
import tech.sud.mgp.hello.ui.scenes.crossapp.widget.dialog.SelectMatchGameDialog;
import tech.sud.mgp.hello.ui.scenes.disco.activity.DiscoRankingActivity;
import tech.sud.mgp.hello.ui.scenes.league.activity.LeagueEntranceActivity;
import tech.sud.mgp.hello.ui.scenes.quiz.activity.MoreQuizActivity;
import tech.sud.mgp.hello.ui.scenes.ticket.activity.TicketLevelActivity;
import tech.sud.mgp.hello.ui.scenes.ticket.model.TicketLevelParams;

/**
 * 大模型分身页面
 */
public class LlmFragment extends BaseFragment implements CreatRoomClickListener, GameItemListener {

    private LinearLayout sceneLayout;
    private SmartRefreshLayout refreshLayout;
    private MagicIndicator magicIndicator;
    private IndicatorHelper helper;
    private NewNestedScrollView scrollView;
    private ImageView menuIv;
    private MainUserInfoView userInfoView;
    private final NFTViewModel nftViewModel = new NFTViewModel();
    private GameListResp mGameListResp;
    private MutableLiveData<JumpRocketEvent> mJumpRocketEventMutableLiveData = new MutableLiveData<>();
    private CreateLlmView mLlmView;
    private GetAiCloneResp mGetAiCloneResp;
    private WavRecorder mWavRecorder;
    private File mLocalWavFile;
    private SafeAudioPlayer mSafeAudioPlayer;
    private boolean isInitSwitchLlm; // 标识是否已经初始化了开关状态

    public static LlmFragment newInstance() {
        LlmFragment fragment = new LlmFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_llm;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        sceneLayout = mRootView.findViewById(R.id.scene_root);
        refreshLayout = mRootView.findViewById(R.id.refresh_layout);
        magicIndicator = mRootView.findViewById(R.id.magic_indicator);
        scrollView = mRootView.findViewById(R.id.scrollView);
        menuIv = mRootView.findViewById(R.id.menu_iv);
        userInfoView = mRootView.findViewById(R.id.user_info_view);
        mLlmView = mRootView.findViewById(R.id.create_llm_view);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadMore(false);

        userInfoView.setNftMask(R.drawable.ic_nft_mask_white);
        userInfoView.setViewWalletAddressArrowVisible(false);
        View viewStatusBar = findViewById(R.id.view_statusbar);
        ViewUtils.setHeight(viewStatusBar, ImmersionBar.getStatusBarHeight(requireContext()));
    }

    @Override
    protected void initData() {
        super.initData();
        mLlmView.showNormalStatus();
        if (mSafeAudioPlayer == null) {
            mSafeAudioPlayer = new SafeAudioPlayer(requireContext());
            mSafeAudioPlayer.setOnPlayListener(new SafeAudioPlayer.OnPlayListener() {
                @Override
                public void onStart() {
                    mLlmView.startVoiceAnim();
                }

                @Override
                public void onStop() {
                    mLlmView.stopVoiceAnim();
                }
            });
        }
    }

    private void refreshClone() {
        HomeRepository.getAiClone(this, new RxCallback<GetAiCloneResp>() {
            @Override
            public void onSuccess(GetAiCloneResp resp) {
                super.onSuccess(resp);
                setCloneInfo(resp);
            }
        });
    }

    private void setCloneInfo(GetAiCloneResp resp) {
        mGetAiCloneResp = resp;
        if (resp == null) {
            return;
        }
        mLlmView.setDatas(resp);

        AiInfoModel aiInfoModel = resp.aiInfo;
        boolean showVoicePlay = false;
        if (aiInfoModel == null) {
            if (!isInitSwitchLlm) {
                mLlmView.setSwitchCloned(false, false);
                isInitSwitchLlm = true;
            } else {
                mLlmView.setCannotOperateShow(true);
            }
        } else {
            if (!isInitSwitchLlm) {
                mLlmView.setSwitchCloned(aiInfoModel.status == 1, false);
                isInitSwitchLlm = true;
            } else {
                mLlmView.setCannotOperateShow(aiInfoModel.status != 1);
            }
            if (aiInfoModel.voiceStatus != 0) {
                showVoicePlay = true;
            }
        }
        String nickname = mLlmView.getNickName();
        if (TextUtils.isEmpty(nickname)) {
            if (aiInfoModel != null) {
                nickname = aiInfoModel.nickname;
            }
            if (TextUtils.isEmpty(nickname)) {
                nickname = HSUserInfo.nickName + "AI";
            }
            mLlmView.setNickName(nickname);
        }

        mLlmView.setReadAloud(resp.audioText);

        if (mLlmView.getVoiceModel() != 0) { // 编辑状态时，不刷新音频状态
            return;
        }
        if (showVoicePlay) {
            mLlmView.showReadyClonedStatus();
        } else {
            mLlmView.showNormalStatus();
        }
    }

    private String randomStr(List<String> list) {
        if (list == null || list.size() == 0) {
            return null;
        }
        int position = new Random().nextInt(list.size());
        return list.get(position);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadList();
        userInfoView.updateUserInfo();
        updateNftHeader();
        refreshClone();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        mJumpRocketEventMutableLiveData.removeObservers(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(JumpRocketEvent event) {
        mJumpRocketEventMutableLiveData.setValue(event);
    }

    private final Observer<JumpRocketEvent> jumpRocketObserver = new Observer<JumpRocketEvent>() {
        @Override
        public void onChanged(JumpRocketEvent jumpRocketEvent) {
            if (jumpRocketEvent.isConsume) {
                return;
            }
            createRoom(SceneType.AUDIO, null);
        }
    };

    /** 更新nft头像 */
    private void updateNftHeader() {
        List<Long> userIdList = new ArrayList<>();
        userIdList.add(HSUserInfo.userId);
        UserInfoRepository.getUserInfoList(this, userIdList, new UserInfoRepository.UserInfoListResultListener() {
            @Override
            public void userInfoList(List<UserInfoResp> userInfos) {
                BindWalletInfoModel bindWalletInfoModel = nftViewModel.getBindWalletInfoByCache();
                if (userInfos == null || userInfos.size() == 0 || bindWalletInfoModel == null) {
                    return;
                }
                NftModel wearNft = bindWalletInfoModel.getWearNft();
                if (wearNft == null) {
                    return;
                }
                UserInfoResp userInfoResp = userInfos.get(0);
                if (userInfoResp.headerType != 1 || !Objects.equals(userInfoResp.headerNftToken, wearNft.detailsToken)) {
                    nftViewModel.cancelWearNft(new CancelWearNftListener() {
                        @Override
                        public void onSuccess() {
                            LifecycleUtils.safeLifecycle(mFragment, () -> {
                                userInfoView.updateUserInfo();
                            });
                        }

                        @Override
                        public void onFailure(int retCode, String retMsg) {
                            LifecycleUtils.safeLifecycle(mFragment, () -> {
                                nftViewModel.clearWearNft();
                                userInfoView.updateUserInfo();
                            });
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        refreshLayout.setOnRefreshListener(refreshLayout -> loadList());
        menuIv.setOnClickListener(v -> {
            if (helper != null) {
                SceneTypeDialog dialog = SceneTypeDialog.getInstance(helper.getSelectedIndex());
                dialog.mGameListResp = mGameListResp;
                dialog.listener = position -> helper.clickIndicator(position);
                dialog.show(getChildFragmentManager(), "SceneTypeDialog");
            }
        });
        EventBus.getDefault().register(this);
        mJumpRocketEventMutableLiveData.observe(this, jumpRocketObserver);

        setLlmListeners();
    }

    private void setLlmListeners() {
        mLlmView.setSwitchListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                HomeRepository.updateAiClone(LlmFragment.this, isChecked ? 1 : 0, new RxCallback<UpdateAiCloneResp>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        isInitSwitchLlm = false;
                        refreshClone();
                    }
                });
                mLlmView.setCannotOperateShow(!isChecked);
                if (!isChecked) {
                    ToastUtils.showShort(R.string.close_clone_hint);
                }
            }
        });

        mLlmView.setOnRecordListener(new VoiceRecordView.OnRecordListener() {
            @Override
            public void onStart() {
                startRecord();
            }

            @Override
            public void onStop() {
                stopRecord();
            }
        });

        mLlmView.setCancelOnClickListener(v -> {
            mLlmView.showNormalStatus();
            setCloneInfo(mGetAiCloneResp);
        });

        mLlmView.setSubmitOnClickListener(v -> {
            onClickSubmit();
        });

        mLlmView.setLlmVoiceOnClickListener(v -> {
            onClickPlayVoice();
        });

        mLlmView.setTrialListeningOnClickListener(v -> {
            onClickPlayVoice();
        });
    }

    private void onClickPlayVoice() {
        if (mLocalWavFile == null) {
            onClickLlmVoice();
        } else {
            mSafeAudioPlayer.playFromPath(mLocalWavFile.getAbsolutePath());
        }
    }

    private void onClickLlmVoice() {
        // 点击了提交上去的分身音色
        if (mGetAiCloneResp == null) {
            LogUtils.d("没有拿到ai信息");
            return;
        }
        AiInfoModel aiInfo = mGetAiCloneResp.aiInfo;
        if (aiInfo == null) {
            LogUtils.d("没有拿到ai信息");
            return;
        }
        if (aiInfo.voiceStatus == 1) {
            ToastUtils.showShort(R.string.voice_training);
            refreshClone();
        } else if (aiInfo.voiceStatus == 3) {
            ToastUtils.showShort(R.string.voice_training_fail);
            refreshClone();
        } else {
            String demoAudioData = aiInfo.demoAudioData;
            if (TextUtils.isEmpty(demoAudioData)) {
                LogUtils.d("没有可试听的音色");
                ToastUtils.showShort(R.string.voice_training);
                refreshClone();
            } else {
                mSafeAudioPlayer.playFromBytes(Base64Utils.base64Decode(demoAudioData), "mp3");
            }
        }
    }

    private void onClickSubmit() {
        if (mLocalWavFile == null || !mLocalWavFile.exists()) {
            LogUtils.d("本地没有录音文件");
            return;
        }
        if (mGetAiCloneResp == null) {
            LogUtils.d("没有拿到ai信息");
            return;
        }
        LoadingDialog loadingDialog = new LoadingDialog(getString(R.string.clone_being_created));
        loadingDialog.show(getChildFragmentManager(), null);
        byte[] voiceBytes = FileIOUtils.readFile2BytesByStream(mLocalWavFile);
        String voiceBase64 = Base64Utils.base64Encode(voiceBytes);
        SaveAiCloneReq req = new SaveAiCloneReq();
        req.nickname = mLlmView.getNickName();
        req.birthday = "1998-05-08";
        req.bloodType = randomStr(mGetAiCloneResp.bloodTypeOptions);
        req.mbti = randomStr(mGetAiCloneResp.mbtiOptions);
        req.personality = mLlmView.getPersonality();
        req.languageStyle = mLlmView.getLanguageStyle();
        req.languageDetailStyle = mLlmView.getLanguageDetailStyle();
        req.audioData = voiceBase64;
        req.audioFormat = "wav";
        HomeRepository.saveAiClone(this, req, new RxCallback<SaveAiCloneResp>() {
            @Override
            public void onSuccess(SaveAiCloneResp saveAiCloneResp) {
                super.onSuccess(saveAiCloneResp);
                mLocalWavFile = null;
                mLlmView.showReadyClonedStatus();
                refreshClone();
            }

            @Override
            public void onFinally() {
                super.onFinally();
                loadingDialog.dismiss();
            }
        });
    }

    private void startRecord() {
        Context context = requireContext();
        SudPermissionUtils.requirePermission(requireContext(), getChildFragmentManager(), new String[]{Manifest.permission.RECORD_AUDIO}, new PermissionFragment.OnPermissionListener() {
            @Override
            public void onPermission(boolean success) {
                if (!success) {
                    return;
                }
                startRecordPermissionDenied(context);
            }
        });
    }

    private void startRecordPermissionDenied(Context context) {
        File file = new File(context.getFilesDir(), "HelloSud" + File.separator + "audio" + File.separator + "voiceTemp.wav");
        int minSeconds = 5;
        int maxSeconds = 15;
        mLocalWavFile = null;
        mWavRecorder = new WavRecorder(file, maxSeconds);
        mWavRecorder.startRecording();
        mWavRecorder.setOnRecordListener((durationSeconds, saveFile) -> {
            if (durationSeconds < minSeconds) {
                ToastUtils.showShort(getString(R.string.voice_warn_min_seconds, minSeconds + ""));
                return;
            }
            mLocalWavFile = saveFile;
            mLlmView.showReadyRecordStatus();
        });
    }

    private void stopRecord() {
        if (mWavRecorder == null) {
            return;
        }
        mWavRecorder.stopRecording();
        mWavRecorder = null;
    }

    private void createScene(GameListResp resp, QuizGameListResp quizGameListResp) {
        if (resp != null && resp.sceneList != null && resp.sceneList.size() > 0) {
            Context context = getContext();
            if (context != null) {
                helper = new IndicatorHelper(magicIndicator, resp.sceneList, scrollView);
                helper.init(context);
                helper.bind();

                sceneLayout.removeAllViews();
                for (int i = 0; i < resp.sceneList.size(); i++) {
                    SceneModel model = resp.sceneList.get(i);
                    HomeItemView view = new HomeItemView(context);
                    view.setData(GameListReq.TAB_LLM, model, HomeManager.getInstance().getSceneGame(resp, model.getSceneId()), quizGameListResp);
                    view.setGameItemListener(this);
                    view.setCreatRoomClickListener(this);
                    view.setMoreActivityOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (model.sceneId) {
                                case SceneType.QUIZ:
                                    startActivity(new Intent(context, MoreQuizActivity.class));
                                    break;
                                case SceneType.DISCO:
                                    startActivity(new Intent(context, DiscoRankingActivity.class));
                                    break;
                            }
                        }
                    });
                    sceneLayout.addView(view);
                    // TODO: 2025/6/24 先只使用一个scene
                    break;
                }
                sceneLayout.postDelayed(() -> helper.selectSceneType(resp.defaultSceneId), 500);

            }
        }
    }

    @Override
    public void onGameClick(SceneModel sceneModel, GameModel gameModel) {
        switch (sceneModel.getSceneId()) {
            case SceneType.TICKET: // 门票制场景
                startTicketLevelActivity(sceneModel, gameModel);
                break;
            case SceneType.LEAGUE: // 联赛场景
                clickLeagueGame(sceneModel, gameModel);
                break;
            default:
                matchGame(sceneModel.getSceneId(), gameModel.gameId);
                break;
        }
    }

    // 点击了联赛场景的
    private void clickLeagueGame(SceneModel sceneModel, GameModel gameModel) {
        Intent intent = new Intent(requireContext(), LeagueEntranceActivity.class);
        intent.putExtra(RequestKey.KEY_GAME_MODEL, gameModel);
        startActivity(intent);
    }

    // 打开场景等级选择页面
    private void startTicketLevelActivity(SceneModel sceneModel, GameModel gameModel) {
        TicketLevelParams params = new TicketLevelParams();
        params.sceneType = sceneModel.getSceneId();
        params.gameId = gameModel.gameId;
        params.gameName = gameModel.gameName;
        Intent intent = new Intent(requireContext(), TicketLevelActivity.class);
        intent.putExtra(RequestKey.TICKET_LEVEL_PARAMS, params);
        startActivity(intent);
    }

    private void matchGame(int sceneId, Long gameId) {
        HomeRepository.matchGame(sceneId, gameId, null, this, new RxCallback<MatchRoomModel>() {
            @Override
            public void onNext(BaseResponse<MatchRoomModel> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS) {
                    EnterRoomUtils.enterRoom(null, t.getData().roomId);
                }
            }
        });
    }

    private void loadList() {
        // 1，游戏列表
        HomeRepository.gameListV2(this, GameListReq.TAB_LLM, new RxCallback<GameListResp>() {
            @Override
            public void onNext(BaseResponse<GameListResp> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS) {
                    GameListResp gameListResp = t.getData();
                    mGameListResp = gameListResp;
                    HomeManager.getInstance().mGameListRespTabGame = gameListResp;
                    safeCreateScene(gameListResp, null);
                }
            }

            @Override
            public void onFinally() {
                super.onFinally();
                loadCompleted();
            }
        });
    }

    private void safeCreateScene(GameListResp resp, QuizGameListResp quizGameListResp) {
        LifecycleUtils.safeLifecycle(this, new CompletedListener() {
            @Override
            public void onCompleted() {
                createScene(resp, quizGameListResp);
            }
        });
    }

    private void loadCompleted() {
        if (refreshLayout.isRefreshing()) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore();
        }
    }

    private void createRoom(Integer sceneType, GameModel gameModel) {
        createRoom(sceneType, gameModel, null);
    }

    private void createRoom(Integer sceneType, GameModel gameModel, EnterRoomParams params) {
        Long gameId;
        if (gameModel == null) {
            gameId = null;
        } else {
            gameId = gameModel.gameId;
        }
        HomeRepository.creatRoom(sceneType, gameId, null, this, new RxCallback<CreatRoomResp>() {
            @Override
            public void onNext(BaseResponse<CreatRoomResp> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS) {
                    long roomId = t.getData().roomId;
                    if (params == null) {
                        EnterRoomUtils.enterRoom(null, roomId);
                    } else {
                        params.roomId = roomId;
                        EnterRoomUtils.enterRoom(null, params);
                    }
                }
            }
        });
    }

    @Override
    public void onCreateRoomClick(SceneModel sceneModel, GameModel gameModel) {
        // 创建房间
        if (sceneModel != null) {
            switch (sceneModel.getSceneId()) {
                case SceneType.TICKET:
                    CreateTicketRoomDialog.newInstance(GameListReq.TAB_LLM).show(getChildFragmentManager(), null);
                    break;
                case SceneType.CROSS_APP_MATCH:
                    showCrossAppMatchGameDialog(sceneModel);
                    break;
                default:
                    createRoom(sceneModel.getSceneId(), gameModel);
                    break;
            }
        }
    }

    private void showCrossAppMatchGameDialog(SceneModel sceneModel) {
        SelectMatchGameDialog dialog = SelectMatchGameDialog.newInstance(SelectMatchGameDialog.MODE_MATCH, GameIdCons.NONE);
        EnterRoomParams enterRoomParams = new EnterRoomParams();
        enterRoomParams.crossAppModel = new CrossAppModel();
        dialog.setOnSingleMatchListener((model) -> {
            enterRoomParams.crossAppModel.matchGameId = model.gameId;
            enterRoomParams.crossAppModel.enterType = CrossAppModel.SINGLE_MATCH;
            createRoom(sceneModel.getSceneId(), null, enterRoomParams);
        });
        dialog.setOnTeamMatchListener((model) -> {
            enterRoomParams.crossAppModel.matchGameId = model.gameId;
            enterRoomParams.crossAppModel.enterType = CrossAppModel.TEAM_MATCH;
            createRoom(sceneModel.getSceneId(), null, enterRoomParams);
        });
        dialog.show(getChildFragmentManager(), null);
    }

}
