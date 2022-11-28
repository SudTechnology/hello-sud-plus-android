package tech.sud.mgp.hello.ui.main.home.view.homeitem;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.QuizGameListResp;
import tech.sud.mgp.hello.service.main.resp.SceneModel;

/**
 * 首页 跨域场景 下的itemView
 */
public class HomeCrossAppItemView extends ConstraintLayout implements IHomeChildItemView {

    private TextView sceneNameTv;
    private ImageView sceneIv;

    public SceneModel sceneModel;
    private ConstraintLayout containerSelectGame;
    private CreatRoomClickListener creatRoomClickListener;

    public HomeCrossAppItemView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HomeCrossAppItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeCrossAppItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_home_cross_app_item, this);
        sceneNameTv = findViewById(R.id.scene_name);
        sceneIv = findViewById(R.id.scene_img);
        containerSelectGame = findViewById(R.id.container_select_game);

        containerSelectGame.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (creatRoomClickListener != null) {
                    creatRoomClickListener.onCreateRoomClick(sceneModel, null);
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

        // 创建房间的背景图
        if (TextUtils.isEmpty(sceneModel.sceneImageNew)) {
            sceneIv.setImageResource(R.drawable.icon_scene_default);
        } else {
            ImageLoader.loadSceneCover(sceneIv, sceneModel.sceneImageNew);
        }

    }

    @Override
    public void setGameItemListener(GameItemListener gameItemListener) {
    }

    @Override
    public void setCreatRoomClickListener(CreatRoomClickListener creatRoomClickListener) {
        this.creatRoomClickListener = creatRoomClickListener;
    }

}
