package tech.sud.mgp.hello.home.fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import tech.sud.mgp.common.base.BaseFragment;
import tech.sud.mgp.common.utils.ImageLoader;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.home.manager.HomeManager;
import tech.sud.mgp.hello.home.callback.GameItemCallback;
import tech.sud.mgp.hello.home.http.resp.GameListResp;
import tech.sud.mgp.hello.home.model.GameModel;
import tech.sud.mgp.hello.home.model.SceneModel;
import tech.sud.mgp.hello.home.view.HomeRoomTypeView;
import tech.sud.mgp.hello.utils.AppSharedPreferences;

public class IndexFragment extends BaseFragment implements GameItemCallback {

    private EditText searchEt;
    private ImageView goSearch;
    private LinearLayout sceneLayout;
    private TextView nameTv, useridTv;
    private ImageView headerIv;

    public IndexFragment() {
    }

    public static IndexFragment newInstance() {
        IndexFragment fragment = new IndexFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_index;
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
    }

    @Override
    protected void initData() {
        super.initData();
        nameTv.setText(AppSharedPreferences.getSP().getString(AppSharedPreferences.USER_NAME_KEY, ""));
        String userId = AppSharedPreferences.getSP().getLong(AppSharedPreferences.USER_ID_KEY, 0L) + "";
        useridTv.setText(userId);
        String header = AppSharedPreferences.getSP().getString(AppSharedPreferences.USER_HEAD_PORTRAIT_KEY, "");
        if (header.isEmpty()) {
            headerIv.setImageResource(R.mipmap.icon_logo);
        } else {
            ImageLoader.loadImage(headerIv, header);
        }

        //生成测试数据
        GameListResp resp = HomeManager.getInstance().testGameListResp();
        for (int i = 0; i < resp.getSceneList().size(); i++) {
            SceneModel model = resp.getSceneList().get(i);
            HomeRoomTypeView sceneView = new HomeRoomTypeView(requireContext());
            sceneView.setItemCallback(this);
            sceneView.setData(model, HomeManager.getInstance().testCreatGame());
            sceneLayout.addView(sceneView);
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

            }
            return false;
        });
    }

    @Override
    public void gameClick(GameModel model) {
        Log.i("gameClick::::", "gameClick" + model.getGameName());
    }
}