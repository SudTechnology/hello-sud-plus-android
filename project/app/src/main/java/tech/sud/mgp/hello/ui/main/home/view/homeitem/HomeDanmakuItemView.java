package tech.sud.mgp.hello.ui.main.home.view.homeitem;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.WebPUtils;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.QuizGameListResp;
import tech.sud.mgp.hello.service.main.resp.SceneModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.listener.PlayResultListener;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.PlayResult;

/**
 * 首页竞猜场景itemView
 */
public class HomeDanmakuItemView extends ConstraintLayout implements IHomeChildItemView {

    private TextView sceneNameTv;
    private ImageView ivKingWarAnim;

    private GameItemListener gameItemListener;
    public SceneModel sceneModel;

    public HomeDanmakuItemView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HomeDanmakuItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeDanmakuItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_home_danmaku_item, this);
        sceneNameTv = findViewById(R.id.scene_name);
        ivKingWarAnim = findViewById(R.id.iv_king_war_anim);
    }

    /** 设置数据 */
    @Override
    public void setData(SceneModel sceneModel, List<GameModel> datas, QuizGameListResp quizGameListResp) {
        this.sceneModel = sceneModel;

        // 场景名称
        if (!TextUtils.isEmpty(sceneModel.sceneName)) {
            sceneNameTv.setText(sceneModel.sceneName);
        }

        // 王者战争
        WebPUtils.loadWebp(ivKingWarAnim, R.drawable.ic_home_danmaku, -1, new PlayResultListener() {
            @Override
            public void onResult(PlayResult result) {

            }
        });
    }

    @Override
    public void setGameItemListener(GameItemListener gameItemListener) {
        this.gameItemListener = gameItemListener;
    }

    @Override
    public void setCreatRoomClickListener(CreatRoomClickListener creatRoomClickListener) {
    }
}
