package tech.sud.mgp.hello.ui.main.home;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseFragment;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.utils.AppSharedPreferences;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.utils.ResponseUtils;
import tech.sud.mgp.hello.service.main.manager.HomeManager;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.CreatRoomResp;
import tech.sud.mgp.hello.service.main.resp.GameListResp;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.SceneModel;
import tech.sud.mgp.hello.ui.scenes.base.utils.EnterRoomUtils;

public class HomeFragment extends BaseFragment implements HomeRoomTypeView.CreatRoomClickListener, GameItemView.GameItemListener {

    private EditText searchEt;
    private TextView goSearch;
    private LinearLayout sceneLayout;
    private TextView nameTv, useridTv;
    private ImageView headerIv;
    private SwipeRefreshLayout refreshLayout;

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
    }

    @Override
    protected void initData() {
        super.initData();
        nameTv.setText(AppSharedPreferences.getSP().getString(AppSharedPreferences.USER_NAME_KEY, ""));
        String userId = AppSharedPreferences.getSP().getLong(AppSharedPreferences.USER_ID_KEY, 0L) + "";
        useridTv.setText(getString(R.string.setting_userid, userId));
        String header = AppSharedPreferences.getSP().getString(AppSharedPreferences.USER_HEAD_PORTRAIT_KEY, "");
        if (header.isEmpty()) {
            headerIv.setImageResource(R.drawable.icon_logo);
        } else {
            ImageLoader.loadImage(headerIv, header);
        }
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
        refreshLayout.setOnRefreshListener(this::loadList);
    }

    private void enterRoom() {
        try {
            String roomIdString = searchEt.getText().toString().trim();
            if (!TextUtils.isEmpty(roomIdString)) {
                Long roomId = Long.parseLong(roomIdString);
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
            sceneLayout.removeAllViews();
            for (int i = 0; i < resp.getSceneList().size(); i++) {
                SceneModel model = resp.getSceneList().get(i);
                HomeRoomTypeView sceneView = new HomeRoomTypeView(requireContext());
                sceneView.setGameItemListener(this);
                sceneView.setCreatRoomClickListener(this);
                sceneView.setData(model, HomeManager.getInstance().getSceneGame(model));
                sceneLayout.addView(sceneView);
            }
        }
    }

    @Override
    public void onGameClick(SceneModel sceneModel, GameModel gameModel) {
        matchGame(sceneModel.getSceneId(), gameModel.getGameId());
    }

    private void matchGame(int sceneId, Long gameId) {
        HomeRepository.matchGame(sceneId, gameId, null, this, new RxCallback<MatchRoomModel>() {
            @Override
            public void onNext(BaseResponse<MatchRoomModel> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS) {
                    EnterRoomUtils.enterRoom(requireContext(), t.getData().roomId);
                } else {
                    ToastUtils.showLong(ResponseUtils.conver(t));
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
                    HomeManager.getInstance().updateGameList(t.getData());
                    creatScene(t.getData());
                } else {
                    ToastUtils.showShort(ResponseUtils.conver(t));
                }
            }

            @Override
            public void onComplete() {
                super.onComplete();
                if (refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadList();
    }

    @Override
    public void onCreatRoomClick(SceneModel sceneModel) {
        //创建房间
        if (sceneModel != null) {
            creatRoom(sceneModel.getSceneId());
        }
    }

    private void creatRoom(Integer sceneType) {
        HomeRepository.creatRoom(sceneType, this, new RxCallback<CreatRoomResp>() {
            @Override
            public void onNext(BaseResponse<CreatRoomResp> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS) {
                    EnterRoomUtils.enterRoom(requireContext(), t.getData().roomId);
                } else {
                    ToastUtils.showShort(ResponseUtils.conver(t));
                }
            }
        });
    }

}