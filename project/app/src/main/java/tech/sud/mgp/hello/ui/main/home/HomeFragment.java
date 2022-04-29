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

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import net.lucode.hackware.magicindicator.MagicIndicator;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseFragment;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.service.main.manager.HomeManager;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.CreatRoomResp;
import tech.sud.mgp.hello.service.main.resp.GameListResp;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.SceneModel;
import tech.sud.mgp.hello.ui.common.constant.RequestKey;
import tech.sud.mgp.hello.ui.main.constant.SceneType;
import tech.sud.mgp.hello.ui.main.home.manager.IndicatorHelper;
import tech.sud.mgp.hello.ui.main.home.model.MatchRoomModel;
import tech.sud.mgp.hello.ui.main.home.view.CoinDialog;
import tech.sud.mgp.hello.ui.main.home.view.GameItemView;
import tech.sud.mgp.hello.ui.main.home.view.HomeRoomTypeView;
import tech.sud.mgp.hello.ui.main.home.view.NewNestedScrollView;
import tech.sud.mgp.hello.ui.main.home.view.SceneTypeDialog;
import tech.sud.mgp.hello.ui.main.widget.CreateTicketRoomDialog;
import tech.sud.mgp.hello.ui.scenes.base.utils.EnterRoomUtils;
import tech.sud.mgp.hello.ui.scenes.ticket.activity.TicketLevelActivity;
import tech.sud.mgp.hello.ui.scenes.ticket.model.TicketLevelParams;

/**
 * 首页页面
 */
public class HomeFragment extends BaseFragment implements HomeRoomTypeView.CreatRoomClickListener, GameItemView.GameItemListener {

    private EditText searchEt;
    private TextView goSearch;
    private LinearLayout sceneLayout;
    private TextView nameTv, useridTv;
    private ImageView headerIv;
    private SmartRefreshLayout refreshLayout;
    private MagicIndicator indicatorContainer;
    private IndicatorHelper helper;
    private NewNestedScrollView scrollView;
    private ImageView menuIv;

    public HomeFragment() {
    }

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
        nameTv = mRootView.findViewById(R.id.name_tv);
        useridTv = mRootView.findViewById(R.id.userid_tv);
        headerIv = mRootView.findViewById(R.id.header_iv);
        refreshLayout = mRootView.findViewById(R.id.refresh_layout);
        indicatorContainer = mRootView.findViewById(R.id.magic_indicator);
        scrollView = mRootView.findViewById(R.id.scrollView);
        menuIv = mRootView.findViewById(R.id.menu_iv);
        refreshLayout.setEnableRefresh(true);
        refreshLayout.setEnableLoadMore(false);
    }

    @Override
    protected void initData() {
        super.initData();
        nameTv.setText(HSUserInfo.nickName);
        useridTv.setText(getString(R.string.setting_userid, HSUserInfo.userId + ""));
        ImageLoader.loadImage(headerIv, HSUserInfo.avatar);
        loadList();
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
        headerIv.setOnClickListener(v -> {
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

    private void creatScene(GameListResp resp) {
        if (resp != null && resp.getSceneList().size() > 0) {
            Context context = getContext();
            if (context != null) {
                helper = new IndicatorHelper(indicatorContainer, resp.getSceneList(), scrollView);
                helper.init(context);
                helper.bind();

                sceneLayout.removeAllViews();
                for (int i = 0; i < resp.getSceneList().size(); i++) {
                    SceneModel model = resp.getSceneList().get(i);
                    HomeRoomTypeView sceneView = new HomeRoomTypeView(context);
                    sceneView.setGameItemListener(this);
                    sceneView.setCreatRoomClickListener(this);
                    sceneView.setData(model, HomeManager.getInstance().getSceneGame(model.getSceneId()));
                    sceneLayout.addView(sceneView);
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
            default:
                matchGame(sceneModel.getSceneId(), gameModel.getGameId());
                break;
        }
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
                    EnterRoomUtils.enterRoom(requireContext(), t.getData().roomId);
                }
            }
        });
    }

    private void loadList() {
        HomeRepository.gameList(this, new RxCallback<GameListResp>() {
            @Override
            public void onNext(BaseResponse<GameListResp> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS) {
                    HomeManager.getInstance().gameListResp = t.getData();
                    creatScene(t.getData());
                }
            }

            @Override
            public void onFinally() {
                super.onFinally();
                if (refreshLayout.isRefreshing()) {
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadMore();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onCreatRoomClick(SceneModel sceneModel) {
        //创建房间
        if (sceneModel != null) {
            switch (sceneModel.getSceneId()) {
                case SceneType.TICKET:
                    new CreateTicketRoomDialog().show(getChildFragmentManager(), null);
                    break;
                default:
                    creatRoom(sceneModel.getSceneId());
                    break;
            }
        }
    }

    private void creatRoom(Integer sceneType) {
        HomeRepository.creatRoom(sceneType, null, this, new RxCallback<CreatRoomResp>() {
            @Override
            public void onNext(BaseResponse<CreatRoomResp> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS) {
                    EnterRoomUtils.enterRoom(requireContext(), t.getData().roomId);
                }
            }
        });
    }

}