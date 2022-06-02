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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ClickUtils;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.service.main.manager.HomeManager;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.QuizGameListResp;
import tech.sud.mgp.hello.service.main.resp.SceneModel;
import tech.sud.mgp.hello.ui.main.constant.SceneType;
import tech.sud.mgp.hello.ui.main.home.adapter.GameAdapter;
import tech.sud.mgp.hello.ui.main.home.adapter.HomeQuizGameAdapter;
import tech.sud.mgp.hello.ui.scenes.custom.CustomConfigActivity;

public class HomeRoomTypeView extends ConstraintLayout {

    private TextView sceneNameTv, creatRoomTv;
    private ImageView sceneIv, customConfigIv;
    private RecyclerView gameRecyclerview;
    private ConstraintLayout clMoreActivity;

    private GameItemView.GameItemListener gameItemListener;
    public SceneModel sceneModel;
    private ConstraintLayout createRoomBtn;
    private CreatRoomClickListener creatRoomClickListener;
    private boolean createEnable = false;

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
        createRoomBtn = findViewById(R.id.creat_room_btn);
        creatRoomTv = findViewById(R.id.creat_room_tv);
        clMoreActivity = findViewById(R.id.cl_more_activity);

        createRoomBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (creatRoomClickListener != null && createEnable) {
                    creatRoomClickListener.onCreateRoomClick(sceneModel);
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

    /** 设置"更多活动"的点击监听 */
    public void setMoreActivityOnClickListener(OnClickListener listener) {
        ClickUtils.applySingleDebouncing(clMoreActivity, listener);
    }

    /** 设置数据 */
    public void setData(SceneModel sceneModel, List<GameModel> datas, QuizGameListResp quizGameListResp) {
        this.sceneModel = sceneModel;

        // 场景名称
        if (!TextUtils.isEmpty(sceneModel.sceneName)) {
            sceneNameTv.setText(sceneModel.sceneName);
        }

        // 自定义场景的设置按钮
        if (sceneModel.getSceneId() == SceneType.CUSTOM_SCENE) {
            customConfigIv.setVisibility(View.VISIBLE);
        } else {
            customConfigIv.setVisibility(View.GONE);
        }

        // 创建房间的背景图
        if (TextUtils.isEmpty(sceneModel.sceneImageNew)) {
            sceneIv.setImageResource(R.drawable.icon_scene_default);
        } else {
            ImageLoader.loadSceneCover(sceneIv, sceneModel.sceneImageNew);
        }

        // 游戏列表数据
        if (sceneModel.getSceneId() == SceneType.QUIZ) { // 竞猜场景
            setQuizGameList(sceneModel, quizGameListResp);
        } else {
            setNormalGameList(sceneModel, datas);
        }

        // 创建房间按钮是否可被点击
        if (createEnable) {
            creatRoomTv.setTextColor(Color.parseColor("#1a1a1a"));
        } else {
            creatRoomTv.setTextColor(Color.parseColor("#331a1a1a"));
        }

        // 是否展示更多活动
        if (sceneModel.getSceneId() == SceneType.QUIZ) { // 竞猜场景
            clMoreActivity.setVisibility(View.VISIBLE);
        } else {
            clMoreActivity.setVisibility(View.GONE);
        }
    }

    // 设置竞猜场景游戏列表数据
    private void setQuizGameList(SceneModel sceneModel, QuizGameListResp quizGameListResp) {
        List<GameModel> models = new ArrayList<>();
        List<GameModel> datas = quizGameListResp.quizGameInfoList;
        if (datas != null && datas.size() > 0) {
            createEnable = true;
            if (datas.size() >= 5) {
                models.addAll(datas.subList(0, 5));
            } else {
                models.addAll(datas);
            }
        } else {
            createEnable = false;
        }
        HomeQuizGameAdapter gameAdapter = new HomeQuizGameAdapter(models);
        gameAdapter.setOnItemClickListener((adapter, view, position) -> {
            GameModel model = models.get(position);
            if (gameItemListener != null && model.gameId != -1) {
                gameItemListener.onGameClick(sceneModel, model);
            }
        });
        int paddingHorizontal = DensityUtils.dp2px(getContext(), 7);
        gameRecyclerview.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);
//        gameRecyclerview.setNestedScrollingEnabled(false);
        gameRecyclerview.setVisibility(View.VISIBLE);
        gameRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        gameRecyclerview.setAdapter(gameAdapter);
    }

    // 设置默认的游戏列表数据
    private void setNormalGameList(SceneModel sceneModel, List<GameModel> datas) {
        List<GameModel> models = new ArrayList<>();
        if (datas != null && datas.size() > 0) {
            createEnable = true;
            models.addAll(datas);
        } else { // 没有数据时，增加占位
            createEnable = false;
            models.addAll(HomeManager.getInstance().getSceneEmptyGame(getContext()));
        }
        GameAdapter gameAdapter = new GameAdapter(models);
        gameAdapter.setOnItemClickListener((adapter, view, position) -> {
            GameModel model = models.get(position);
            if (gameItemListener != null && model.gameId != -1) {
                gameItemListener.onGameClick(sceneModel, model);
            }
        });
        gameRecyclerview.setNestedScrollingEnabled(false);
        gameRecyclerview.setVisibility(View.VISIBLE);
        gameRecyclerview.setLayoutManager(new GridLayoutManager(getContext(), 3));
        gameRecyclerview.setAdapter(gameAdapter);
    }

    public interface CreatRoomClickListener {
        void onCreateRoomClick(SceneModel sceneModel);
    }

}
