package tech.sud.mgp.hello.ui.main.home.view.homeitem;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ClickUtils;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.QuizGameListResp;
import tech.sud.mgp.hello.service.main.resp.SceneModel;
import tech.sud.mgp.hello.ui.main.home.adapter.HomeQuizGameAdapter;

/**
 * 首页竞猜场景itemView
 */
public class HomeQuizItemView extends ConstraintLayout implements IHomeChildItemView {

    private TextView sceneNameTv, creatRoomTv;
    private ImageView sceneIv;
    private RecyclerView gameRecyclerview;
    private ConstraintLayout clMoreActivity;

    private GameItemListener gameItemListener;
    public SceneModel sceneModel;
    private ConstraintLayout createRoomBtn;
    private CreatRoomClickListener creatRoomClickListener;
    private boolean createEnable = false;

    public HomeQuizItemView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HomeQuizItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeQuizItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_home_quiz_item, this);
        sceneNameTv = findViewById(R.id.scene_name);
        sceneIv = findViewById(R.id.scene_img);
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
    }

    /** 设置"更多活动"的点击监听 */
    public void setMoreActivityOnClickListener(OnClickListener listener) {
        ClickUtils.applySingleDebouncing(clMoreActivity, listener);
    }

    /** 设置数据 */
    @Override
    public void setData(SceneModel sceneModel, List<GameModel> datas, QuizGameListResp quizGameListResp) {
        this.sceneModel = sceneModel;

        // 场景名称
        if (!TextUtils.isEmpty(sceneModel.sceneName)) {
            sceneNameTv.setText(sceneModel.sceneName);
        }

        // 创建房间的背景图
        if (TextUtils.isEmpty(sceneModel.sceneImageNew)) {
            sceneIv.setImageResource(R.drawable.icon_scene_default);
        } else {
            ImageLoader.loadSceneCover(sceneIv, sceneModel.sceneImageNew);
        }

        // 游戏列表数据
        setGameList(sceneModel, quizGameListResp);

        // 创建房间按钮是否可被点击
        if (createEnable) {
            creatRoomTv.setTextColor(Color.parseColor("#1a1a1a"));
        } else {
            creatRoomTv.setTextColor(Color.parseColor("#331a1a1a"));
        }
    }

    // 设置竞猜场景游戏列表数据
    private void setGameList(SceneModel sceneModel, QuizGameListResp quizGameListResp) {
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
        gameRecyclerview.setVisibility(View.VISIBLE);
        gameRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
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
