package tech.sud.mgp.hello.ui.main.home.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.service.main.manager.HomeManager;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.SceneModel;
import tech.sud.mgp.hello.ui.main.constant.SceneType;
import tech.sud.mgp.hello.ui.main.home.adapter.GameAdapter;
import tech.sud.mgp.hello.ui.scenes.custom.CustomConfigActivity;

public class HomeRoomTypeView extends ConstraintLayout {
    private TextView sceneNameTv, creatRoomTv;
    private ImageView sceneIv, customConfigIv;
    private RecyclerView gameRecyclerview;

    private GameAdapter gameAdapter;
    private GameItemView.GameItemListener gameItemListener;
    public SceneModel sceneModel;
    private ConstraintLayout creatRoomBtn;
    private CreatRoomClickListener creatRoomClickListener;
    private boolean creatEnable = false;

    public void setGameItemListener(GameItemView.GameItemListener gameItemListener) {
        this.gameItemListener = gameItemListener;
    }

    public void setCreatRoomClickListener(CreatRoomClickListener creatRoomClickListener) {
        this.creatRoomClickListener = creatRoomClickListener;
    }

    public HomeRoomTypeView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HomeRoomTypeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeRoomTypeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_home_roomtype, this);
        sceneNameTv = findViewById(R.id.scene_name);
        sceneIv = findViewById(R.id.scene_img);
        customConfigIv = findViewById(R.id.custom_config_iv);
        gameRecyclerview = findViewById(R.id.more6_game_rv);
        creatRoomBtn = findViewById(R.id.creat_room_btn);
        creatRoomTv = findViewById(R.id.creat_room_tv);
        creatRoomBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (creatRoomClickListener != null && creatEnable) {
                    creatRoomClickListener.onCreatRoomClick(sceneModel);
                }
            }
        });

        customConfigIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sceneModel.getSceneId() == SceneType.CUSTOM_SCENE) {
                    getContext().startActivity(new Intent(getContext(), CustomConfigActivity.class));
                }
            }
        });
    }

    public void setData(SceneModel sceneModel, List<GameModel> datas) {
        this.sceneModel = sceneModel;
        if (!TextUtils.isEmpty(sceneModel.getSceneName())) {
            sceneNameTv.setText(sceneModel.getSceneName());
        }
        if (sceneModel.getSceneId() == SceneType.CUSTOM_SCENE) {
            customConfigIv.setVisibility(View.VISIBLE);
        } else {
            customConfigIv.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(sceneModel.getSceneImageNew())) {
            ImageLoader.loadSceneCover(sceneIv, sceneModel.getSceneImageNew());
        } else {
            sceneIv.setImageResource(R.drawable.icon_scene_default);
        }
        List<GameModel> models = new ArrayList<>();
        if (datas != null && datas.size() > 0) {
            creatEnable = true;
            models.addAll(datas);
        } else {
            creatEnable = false;
            models.addAll(HomeManager.getInstance().getSceneEmptyGame(getContext(), sceneModel));
        }
        gameAdapter = new GameAdapter(models);
        gameAdapter.setOnItemClickListener((adapter, view, position) -> {
            GameModel model = models.get(position);
            if (gameItemListener != null && model.gameId != -1) {
                gameItemListener.onGameClick(sceneModel, model);
            }
        });
        gameRecyclerview.setHasFixedSize(true);//解决ScrollView嵌套RecyclerView滑动卡顿和加载不全的问题
        gameRecyclerview.setNestedScrollingEnabled(false);
        gameRecyclerview.setVisibility(View.VISIBLE);
        gameRecyclerview.setLayoutManager(new GridLayoutManager(getContext(), 3));
        gameRecyclerview.setAdapter(gameAdapter);
        if (creatEnable) {
            //创建房间可点击状态
            creatRoomTv.setTextColor(Color.parseColor("#1a1a1a"));
        } else {
            //创建房间不可点击状态
            creatRoomTv.setTextColor(Color.parseColor("#331a1a1a"));
        }
    }

//    private void addGameView(SceneModel sceneModel, GameModel gameModel) {
//        GameItemView gameItemView = new GameItemView(getContext());
//        gameItemView.setGameItemListener(gameItemListener);
//        gameItemView.setModel(sceneModel, gameModel);
//        gridLayout.addView(gameItemView);
//    }

    public interface CreatRoomClickListener {
        void onCreatRoomClick(SceneModel sceneModel);
    }

}
