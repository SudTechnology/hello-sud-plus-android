package tech.sud.mgp.hello.ui.main.home;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseFragment;
import tech.sud.mgp.hello.common.event.LiveEventBusKey;
import tech.sud.mgp.hello.common.event.model.JumpRocketEvent;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.service.main.manager.HomeManager;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.repository.UserInfoRepository;
import tech.sud.mgp.hello.service.main.req.GameListReq;
import tech.sud.mgp.hello.service.main.resp.CreatRoomResp;
import tech.sud.mgp.hello.service.main.resp.GameListResp;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.GetBannerResp;
import tech.sud.mgp.hello.service.main.resp.QuizGameListResp;
import tech.sud.mgp.hello.service.main.resp.SceneModel;
import tech.sud.mgp.hello.service.main.resp.UserInfoResp;
import tech.sud.mgp.hello.service.room.resp.CrossAppModel;
import tech.sud.mgp.hello.ui.common.constant.RequestKey;
import tech.sud.mgp.hello.ui.common.utils.CompletedListener;
import tech.sud.mgp.hello.ui.common.utils.LifecycleUtils;
import tech.sud.mgp.hello.ui.main.base.constant.GameIdCons;
import tech.sud.mgp.hello.ui.main.base.constant.SceneType;
import tech.sud.mgp.hello.ui.main.base.widget.CreateTicketRoomDialog;
import tech.sud.mgp.hello.ui.main.base.widget.MainUserInfoView;
import tech.sud.mgp.hello.ui.main.home.helper.JumpHelper;
import tech.sud.mgp.hello.ui.main.home.manager.IndicatorHelper;
import tech.sud.mgp.hello.ui.main.home.model.MatchRoomModel;
import tech.sud.mgp.hello.ui.main.home.view.HomeBannerView;
import tech.sud.mgp.hello.ui.main.home.view.NewNestedScrollView;
import tech.sud.mgp.hello.ui.main.home.view.SceneTypeDialog;
import tech.sud.mgp.hello.ui.main.home.view.homeitem.CreatRoomClickListener;
import tech.sud.mgp.hello.ui.main.home.view.homeitem.GameItemListener;
import tech.sud.mgp.hello.ui.main.home.view.homeitem.HomeItemView;
import tech.sud.mgp.hello.ui.main.nft.model.BindWalletInfoModel;
import tech.sud.mgp.hello.ui.main.nft.model.NftModel;
import tech.sud.mgp.hello.ui.main.nft.viewmodel.CancelWearNftListener;
import tech.sud.mgp.hello.ui.main.nft.viewmodel.NFTViewModel;
import tech.sud.mgp.hello.ui.scenes.base.model.EnterRoomParams;
import tech.sud.mgp.hello.ui.scenes.base.utils.EnterRoomUtils;
import tech.sud.mgp.hello.ui.scenes.crossapp.widget.dialog.SelectMatchGameDialog;
import tech.sud.mgp.hello.ui.scenes.disco.activity.DiscoRankingActivity;
import tech.sud.mgp.hello.ui.scenes.league.activity.LeagueEntranceActivity;
import tech.sud.mgp.hello.ui.scenes.quiz.activity.MoreQuizActivity;
import tech.sud.mgp.hello.ui.scenes.ticket.activity.TicketLevelActivity;
import tech.sud.mgp.hello.ui.scenes.ticket.model.TicketLevelParams;

/**
 * 首页页面
 */
public class HomeFragment extends BaseFragment implements CreatRoomClickListener, GameItemListener {

    private EditText searchEt;
    private TextView goSearch;
    private HomeBannerView bannerView;
    private LinearLayout sceneLayout;
    private SmartRefreshLayout refreshLayout;
    private MagicIndicator magicIndicator;
    private IndicatorHelper helper;
    private NewNestedScrollView scrollView;
    private ImageView menuIv;
    private MainUserInfoView userInfoView;
    private final NFTViewModel nftViewModel = new NFTViewModel();
    private GameListResp mGameListResp;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        searchEt = mRootView.findViewById(R.id.search_et);
        goSearch = mRootView.findViewById(R.id.go_search);
        sceneLayout = mRootView.findViewById(R.id.scene_root);
        refreshLayout = mRootView.findViewById(R.id.refresh_layout);
        magicIndicator = mRootView.findViewById(R.id.magic_indicator);
        scrollView = mRootView.findViewById(R.id.scrollView);
        menuIv = mRootView.findViewById(R.id.menu_iv);
        userInfoView = mRootView.findViewById(R.id.user_info_view);
        bannerView = mRootView.findViewById(R.id.banner_view);
        refreshLayout.setEnableRefresh(true);
        refreshLayout.setEnableLoadMore(false);

        userInfoView.setNftMask(R.drawable.ic_nft_mask_white);
        userInfoView.setViewWalletAddressArrowVisible(false);
        View viewStatusBar = findViewById(R.id.view_statusbar);
        ViewUtils.setHeight(viewStatusBar, ImmersionBar.getStatusBarHeight(requireContext()));
    }

    @Override
    protected void initData() {
        super.initData();
        loadList();
    }

    @Override
    public void onPause() {
        super.onPause();
        bannerView.stopChangeTask();
    }

    @Override
    public void onResume() {
        super.onResume();
        userInfoView.updateUserInfo();
        updateNftHeader();
        getBanner();
        bannerView.startChangeTask();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LiveEventBus.<JumpRocketEvent>get(LiveEventBusKey.KEY_JUMP_ROCKET).removeObserver(jumpRocketObserver);
    }

    private void getBanner() {
        HomeRepository.getBanner(this, new RxCallback<GetBannerResp>() {
            @Override
            public void onSuccess(GetBannerResp getBannerResp) {
                super.onSuccess(getBannerResp);
                LifecycleUtils.safeLifecycle(mFragment, () -> {
                    updateBannerInfo(getBannerResp);
                });
            }
        });
    }

    private void updateBannerInfo(GetBannerResp resp) {
        if (resp == null || resp.bannerInfoList == null || resp.bannerInfoList.size() == 0) {
            bannerView.setVisibility(View.GONE);
            return;
        }
        bannerView.setVisibility(View.VISIBLE);
        bannerView.setBannerInfo(resp);
    }

    /** 更新nft头像 */
    private void updateNftHeader() {
        List<Long> userIdList = new ArrayList<>();
        userIdList.add(HSUserInfo.userId);
        UserInfoRepository.getUserInfoList(this, userIdList, new UserInfoRepository.UserInfoResultListener() {
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
        searchEt.setOnFocusChangeListener((v, hasFocus) -> {
            String keyword = searchEt.getText().toString();
            if (keyword.length() > 0) {
                goSearch.setVisibility(View.VISIBLE);
            } else {
                goSearch.setVisibility(View.GONE);
            }
        });
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    goSearch.setVisibility(View.VISIBLE);
                } else {
                    goSearch.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchEt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                enterRoom();
            }
            return false;
        });
        goSearch.setOnClickListener(v -> enterRoom());
        refreshLayout.setOnRefreshListener(refreshLayout -> loadList());
        menuIv.setOnClickListener(v -> {
            if (helper != null) {
                SceneTypeDialog dialog = SceneTypeDialog.getInstance(helper.getSelectedIndex());
                dialog.mGameListResp = mGameListResp;
                dialog.listener = position -> helper.clickIndicator(position);
                dialog.show(getChildFragmentManager(), "SceneTypeDialog");
            }
        });
        bannerView.setOnPagerClickListener(new HomeBannerView.OnPagerClickListener() {
            @Override
            public void onPagerClick(GetBannerResp.BannerModel model) {
                JumpHelper.jump(model);
            }
        });
        LiveEventBus.<JumpRocketEvent>get(LiveEventBusKey.KEY_JUMP_ROCKET).observe(this, jumpRocketObserver);
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

    private void enterRoom() {
        try {
            String roomIdString = searchEt.getText().toString().trim();
            if (!TextUtils.isEmpty(roomIdString)) {
                long roomId = Long.parseLong(roomIdString);
                EnterRoomUtils.enterRoom(requireContext(), roomId);
            }
            KeyboardUtils.hideSoftInput(searchEt);
            searchEt.setText("");
            searchEt.clearFocus();
        } catch (Exception e) {
            ToastUtils.showShort(getString(R.string.search_room_error));
        }
    }

    private void createScene(GameListResp resp, QuizGameListResp quizGameListResp) {
        if (resp != null && resp.sceneList.size() > 0) {
            Context context = getContext();
            if (context != null) {
                helper = new IndicatorHelper(magicIndicator, resp.sceneList, scrollView);
                helper.init(context);
                helper.bind();

                sceneLayout.removeAllViews();
                for (int i = 0; i < resp.sceneList.size(); i++) {
                    SceneModel model = resp.sceneList.get(i);
                    HomeItemView view = new HomeItemView(context);
                    view.setData(model, HomeManager.getInstance().getSceneGame(resp, model.getSceneId()), quizGameListResp);
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
                }
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
        HomeRepository.gameListV2(this, GameListReq.TAB_SCENE, new RxCallback<GameListResp>() {
            @Override
            public void onNext(BaseResponse<GameListResp> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS) {
                    GameListResp gameListResp = t.getData();
                    mGameListResp = gameListResp;
                    HomeManager.getInstance().mGameListRespTabScene = gameListResp;

                    // 2，竞猜游戏列表
                    HomeRepository.quizGameList(HomeFragment.this, new RxCallback<QuizGameListResp>() {
                        @Override
                        public void onSuccess(QuizGameListResp quizGameListResp) {
                            super.onSuccess(quizGameListResp);
                            safeCreateScene(gameListResp, quizGameListResp);
                        }

                        @Override
                        public void onFinally() {
                            super.onFinally();
                            loadCompleted();
                        }
                    });
                } else {
                    loadCompleted();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
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
                    CreateTicketRoomDialog.newInstance(GameListReq.TAB_SCENE).show(getChildFragmentManager(), null);
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