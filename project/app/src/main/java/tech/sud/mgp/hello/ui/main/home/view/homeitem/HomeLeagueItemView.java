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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.QuizGameListResp;
import tech.sud.mgp.hello.service.main.resp.SceneModel;

/**
 * 首页联赛场景itemView
 */
public class HomeLeagueItemView extends ConstraintLayout implements IHomeChildItemView {

    private TextView sceneNameTv;
    private RecyclerView recyclerView;

    private GameItemListener gameItemListener;
    public SceneModel sceneModel;

    public HomeLeagueItemView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HomeLeagueItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeLeagueItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_home_league_item, this);
        sceneNameTv = findViewById(R.id.scene_name);
        recyclerView = findViewById(R.id.recycler_view);
    }

    /** 设置数据 */
    @Override
    public void setData(SceneModel sceneModel, List<GameModel> datas, QuizGameListResp quizGameListResp) {
        this.sceneModel = sceneModel;

        // 场景名称
        if (!TextUtils.isEmpty(sceneModel.sceneName)) {
            sceneNameTv.setText(sceneModel.sceneName);
        }

        // 加载游戏列表
        setGameList(sceneModel, datas);
    }

    // 设置默认的游戏列表数据
    private void setGameList(SceneModel sceneModel, List<GameModel> datas) {
        MyAdapter gameAdapter = new MyAdapter();
        gameAdapter.setOnItemClickListener((adapter, view, position) -> {
            GameModel model = gameAdapter.getItem(position);
            if (gameItemListener != null && model.gameId != -1) {
                gameItemListener.onGameClick(sceneModel, model);
            }
        });
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(gameAdapter);
        gameAdapter.setList(datas);

        if (datas == null || datas.size() == 0) {
            recyclerView.setVisibility(View.GONE);
        }
    }

    private static class MyAdapter extends BaseQuickAdapter<GameModel, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.item_home_league);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, GameModel model) {
            ImageView ivIcon = holder.getView(R.id.iv_icon);
            ImageLoader.loadAvatar(ivIcon, model.homeGamePic);
            holder.setText(R.id.tv_name, model.gameName);
        }
    }

    @Override
    public void setGameItemListener(GameItemListener gameItemListener) {
        this.gameItemListener = gameItemListener;
    }

    @Override
    public void setCreatRoomClickListener(CreatRoomClickListener creatRoomClickListener) {
    }
}
