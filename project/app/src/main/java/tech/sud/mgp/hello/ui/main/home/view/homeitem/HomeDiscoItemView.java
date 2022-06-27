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

import com.blankj.utilcode.util.ClickUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.WebPUtils;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.QuizGameListResp;
import tech.sud.mgp.hello.service.main.resp.SceneModel;

/**
 * 首页蹦迪场景itemView
 */
public class HomeDiscoItemView extends ConstraintLayout implements IHomeChildItemView {

    private TextView sceneNameTv;
    private RecyclerView recyclerView;

    private GameItemListener gameItemListener;
    private CreatRoomClickListener creatRoomClickListener;
    public SceneModel sceneModel;

    public HomeDiscoItemView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HomeDiscoItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeDiscoItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_home_disco_item, this);
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
        gameAdapter.addChildClickViewIds(R.id.tv_create, R.id.tv_join);
        gameAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                GameModel model = gameAdapter.getItem(position);
                if (view.getId() == R.id.tv_create) { // 点击创建房间
                    if (creatRoomClickListener != null) {
                        creatRoomClickListener.onCreateRoomClick(sceneModel, model);
                    }
                } else if (view.getId() == R.id.tv_join) { // 点击了立即加入
                    if (gameItemListener != null && model.gameId != -1) {
                        gameItemListener.onGameClick(sceneModel, model);
                    }
                }
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
            super(R.layout.item_home_disco);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, GameModel model) {
            ImageView ivIcon = holder.getView(R.id.iv_icon);
            WebPUtils.loadWebP(ivIcon, model.homeGamePic, -1);
        }
    }

    @Override
    public void setGameItemListener(GameItemListener gameItemListener) {
        this.gameItemListener = gameItemListener;
    }

    @Override
    public void setCreatRoomClickListener(CreatRoomClickListener creatRoomClickListener) {
        this.creatRoomClickListener = creatRoomClickListener;
    }

    /** 设置"更多活动"的点击监听 */
    public void setMoreActivityOnClickListener(OnClickListener listener) {
        ClickUtils.applySingleDebouncing(findViewById(R.id.view_ranking), listener);
    }

}
