package tech.sud.mgp.hello.home.fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tech.sud.mgp.common.base.BaseFragment;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.home.model.GameModel;
import tech.sud.mgp.hello.home.model.SceneModel;
import tech.sud.mgp.hello.home.view.HomeRoomTypeView;
import tech.sud.mgp.hello.home.view.HomeTabView;
import tech.sud.mgp.hello.utils.AppSharedPreferences;
import tech.sud.mgp.hello.utils.GlideImageLoader;

public class IndexFragment extends BaseFragment {

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
            GlideImageLoader.loadImage(headerIv, header);
        }
        //生成测试数据
//        for (int i = 0; i < 4; i++) {
//            SceneModel model = new SceneModel();
//            model.setSceneId(i);
//            model.setSceneName("场景" + i);
//            HomeRoomTypeView sceneView = new HomeRoomTypeView(requireContext());
//            sceneView.setData(model, creatGame());
//            sceneView.addView(sceneView);
//        }
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

    private List<GameModel> creatGame() {
        List<GameModel> lists = new ArrayList<>();
        int total = new Random().nextInt(20) + 1;
        for (int i = 0; i < total; i++) {
            GameModel gameModel = new GameModel();
            gameModel.setGameId(i);
            gameModel.setGameName("GameName" + i);
            lists.add(gameModel);
        }
        return lists;
    }
}