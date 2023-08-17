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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.service.main.manager.HomeManager;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.QuizGameListResp;
import tech.sud.mgp.hello.service.main.resp.SceneModel;

/**
 * 首页竖版弹幕场景下的itemView
 */
public class HomeVerticalDanmakuItemView extends ConstraintLayout implements IHomeChildItemView {

    private TextView sceneNameTv;
    private RecyclerView gameRecyclerview;

    private GameItemListener gameItemListener;
    public SceneModel sceneModel;
    private CreatRoomClickListener creatRoomClickListener;

    public HomeVerticalDanmakuItemView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HomeVerticalDanmakuItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeVerticalDanmakuItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_home_vertical_danmaku_item, this);
        sceneNameTv = findViewById(R.id.scene_name);
        gameRecyclerview = findViewById(R.id.more6_game_rv);
    }

    /** 设置数据 */
    @Override
    public void setData(SceneModel sceneModel, List<GameModel> datas, QuizGameListResp quizGameListResp) {
        this.sceneModel = sceneModel;

        // 场景名称
        if (!TextUtils.isEmpty(sceneModel.sceneName)) {
            sceneNameTv.setText(sceneModel.sceneName);
        }

        // 游戏列表数据
        setGameList(sceneModel, datas);
    }

    // 设置默认的游戏列表数据
    private void setGameList(SceneModel sceneModel, List<GameModel> datas) {
        List<GameModel> models = new ArrayList<>();
        if (datas != null && datas.size() > 0) {
            models.addAll(datas);
        } else { // 没有数据时，增加占位
            models.addAll(HomeManager.getInstance().getSceneEmptyGame(getContext()));
        }
        MyAdapter gameAdapter = new MyAdapter(models);
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

    private static class MyAdapter extends BaseQuickAdapter<GameModel, BaseViewHolder> {

        public MyAdapter(@Nullable List<GameModel> data) {
            super(R.layout.item_game_view, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, GameModel item) {
            ImageView iconView = helper.getView(R.id.game_icon);
            if (!TextUtils.isEmpty(item.homeGamePic)) {
                ImageLoader.loadGameCover(iconView, item.homeGamePic);
            } else {
                iconView.setImageResource(R.drawable.icon_game_empty);
            }
        }
    }

}
