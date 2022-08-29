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

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import net.lucode.hackware.magicindicator.MagicIndicator;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseFragment;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.service.main.manager.HomeManager;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.CreatRoomResp;
import tech.sud.mgp.hello.service.main.resp.GameListResp;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.QuizGameListResp;
import tech.sud.mgp.hello.service.main.resp.SceneModel;
import tech.sud.mgp.hello.ui.common.constant.RequestKey;
import tech.sud.mgp.hello.ui.common.utils.CompletedListener;
import tech.sud.mgp.hello.ui.common.utils.LifecycleUtils;
import tech.sud.mgp.hello.ui.main.base.constant.SceneType;
import tech.sud.mgp.hello.ui.main.base.widget.CreateTicketRoomDialog;
import tech.sud.mgp.hello.ui.main.base.widget.MainUserInfoView;
import tech.sud.mgp.hello.ui.main.home.manager.IndicatorHelper;
import tech.sud.mgp.hello.ui.main.home.model.MatchRoomModel;
import tech.sud.mgp.hello.ui.main.home.view.CoinDialog;
import tech.sud.mgp.hello.ui.main.home.view.NewNestedScrollView;
import tech.sud.mgp.hello.ui.main.home.view.SceneTypeDialog;
import tech.sud.mgp.hello.ui.main.home.view.homeitem.CreatRoomClickListener;
import tech.sud.mgp.hello.ui.main.home.view.homeitem.GameItemListener;
import tech.sud.mgp.hello.ui.main.home.view.homeitem.HomeItemView;
import tech.sud.mgp.hello.ui.scenes.base.utils.EnterRoomUtils;
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
    private LinearLayout sceneLayout;
    private SmartRefreshLayout refreshLayout;
    private MagicIndicator magicIndicator;
    private IndicatorHelper helper;
    private NewNestedScrollView scrollView;
    private ImageView menuIv;
    private MainUserInfoView userInfoView;

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
        refreshLayout.setEnableRefresh(true);
        refreshLayout.setEnableLoadMore(false);

        View viewStatusBar = findViewById(R.id.view_statusbar);
        ViewUtils.setHeight(viewStatusBar, ImmersionBar.getStatusBarHeight(requireContext()));
    }

    @Override
    protected void initData() {
        super.initData();
        loadList();
    }

    @Override
    public void onResume() {
        super.onResume();
        userInfoView.updateUserInfo();
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
        userInfoView.setAvatarOnClickListener(v -> {
            new CoinDialog().show(getChildFragmentManager(), null);
        });
        menuIv.setOnClickListener(v -> {
            if (helper != null) {
                SceneTypeDialog dialog = SceneTypeDialog.getInstance(helper.getSelectedIndex());
                dialog.listener = position -> helper.clickIndicator(position);
                dialog.show(getChildFragmentManager(), "SceneTypeDialog");
            }
        });
    }

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
                    view.setData(model, HomeManager.getInstance().getSceneGame(model.getSceneId()), quizGameListResp);
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
        HomeRepository.gameList(this, new RxCallback<GameListResp>() {
            @Override
            public void onNext(BaseResponse<GameListResp> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS) {
                    GameListResp gameListResp = t.getData();
                    HomeManager.getInstance().gameListResp = gameListResp;

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
                    EnterRoomUtils.enterRoom(null, t.getData().roomId);
                }
            }
        });
    }

    @Override
    public void onCreateRoomClick(SceneModel sceneModel, GameModel gameModel) {
        //创建房间
        if (sceneModel != null) {
            switch (sceneModel.getSceneId()) {
                case SceneType.TICKET:
                    new CreateTicketRoomDialog().show(getChildFragmentManager(), null);
                    break;
                default:
                    createRoom(sceneModel.getSceneId(), gameModel);
                    break;
            }
        }
    }

}