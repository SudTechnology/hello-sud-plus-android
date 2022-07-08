package tech.sud.mgp.hello.ui.main.home.view.homeitem;

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
import tech.sud.mgp.hello.service.main.resp.QuizGameListResp;
import tech.sud.mgp.hello.service.main.resp.SceneModel;
import tech.sud.mgp.hello.ui.main.constant.SceneType;
import tech.sud.mgp.hello.ui.main.home.adapter.GameAdapter;
import tech.sud.mgp.hello.ui.scenes.custom.CustomConfigActivity;

/**
 * 首页通用场景下的itemView
 */
public class HomeNormalItemView extends ConstraintLayout implements IHomeChildItemView {

    private TextView sceneNameTv, creatRoomTv;
    private ImageView sceneIv, customConfigIv;
    private RecyclerView gameRecyclerview;

    private GameItemListener gameItemListener;
    public SceneModel sceneModel;
    private ConstraintLayout createRoomBtn;
    private CreatRoomClickListener creatRoomClickListener;
    private boolean createEnable = false;

    public HomeNormalItemView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HomeNormalItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeNormalItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_home_normal_item, this);
        sceneNameTv = findViewById(R.id.scene_name);
        sceneIv = findViewById(R.id.scene_img);
        customConfigIv = findViewById(R.id.custom_config_iv);
        gameRecyclerview = findViewById(R.id.more6_game_rv);
        createRoomBtn = findViewById(R.id.creat_room_btn);
        creatRoomTv = findViewById(R.id.creat_room_tv);

        createRoomBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (creatRoomClickListener != null && createEnable) {
                    creatRoomClickListener.onCreateRoomClick(sceneModel, null);
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

    /** 设置数据 */
    @Override
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
        setGameList(sceneModel, datas);

        // 创建房间按钮是否可被点击
        if (createEnable) {
            creatRoomTv.setTextColor(Color.parseColor("#1a1a1a"));
        } else {
            creatRoomTv.setTextColor(Color.parseColor("#331a1a1a"));
        }
    }

    // 设置默认的游戏列表数据
    private void setGameList(SceneModel sceneModel, List<GameModel> datas) {
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

    @Override
    public void setGameItemListener(GameItemListener gameItemListener) {
        this.gameItemListener = gameItemListener;
    }

    @Override
    public void setCreatRoomClickListener(CreatRoomClickListener creatRoomClickListener) {
        this.creatRoomClickListener = creatRoomClickListener;
    }

}
