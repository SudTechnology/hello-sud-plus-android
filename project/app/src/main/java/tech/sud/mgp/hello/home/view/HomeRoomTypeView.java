package tech.sud.mgp.hello.home.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.common.http.use.resp.GameModel;
import tech.sud.mgp.hello.common.http.use.resp.SceneModel;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.home.adapter.GameAdapter;
import tech.sud.mgp.hello.home.listener.GameItemListener;

public class HomeRoomTypeView extends ConstraintLayout {
    private TextView sceneNameTv;
    private ImageView sceneIv;
    private GridLayout gridLayout;
    private RecyclerView gameRecyclerview;
    private TextView emptyTv;
    private GameAdapter gameAdapter;
    private GameItemListener gameItemListener;
    private SceneModel sceneModel;

    public void setGameItemListener(GameItemListener gameItemListener) {
        this.gameItemListener = gameItemListener;
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
        gridLayout = findViewById(R.id.gridlayout);
        gameRecyclerview = findViewById(R.id.more6_game_rv);
        emptyTv = findViewById(R.id.empty_tv);
    }

    public void setData(SceneModel sceneModel, List<GameModel> datas) {
        this.sceneModel = sceneModel;
        if (!TextUtils.isEmpty(sceneModel.getSceneName())) {
            sceneNameTv.setText(sceneModel.getSceneName());
        }
        if (!TextUtils.isEmpty(sceneModel.getSceneImage())) {
            ImageLoader.loadImage(sceneIv, sceneModel.getSceneImage());
        } else {
            sceneIv.setImageResource(R.mipmap.icon_audio_room);
        }
        if (datas != null && datas.size() > 0) {
            emptyTv.setVisibility(View.GONE);
            List<GameModel> models = new ArrayList<>();
            for (int i = 0; i < datas.size(); i++) {
                if (i > 5) {
                    models.add(datas.get(i));
                } else {
                    addGameView(sceneModel, datas.get(i));
                }
            }
            if (models.size() > 0) {
                gameAdapter = new GameAdapter(models);
                gameAdapter.setOnItemClickListener((adapter, view, position) -> {
                    if (gameItemListener != null) {
                        gameItemListener.onGameClick(sceneModel, models.get(position));
                    }
                });
                gameRecyclerview.setLayoutManager(new GridLayoutManager(getContext(), 4));
                gameRecyclerview.setAdapter(gameAdapter);
                gameRecyclerview.setVisibility(View.VISIBLE);
            } else {
                gameRecyclerview.setVisibility(View.GONE);
            }
        } else {
            emptyTv.setVisibility(View.VISIBLE);
        }
    }

    private void addGameView(SceneModel sceneModel, GameModel gameModel) {
        GameItemView gameItemView = new GameItemView(getContext());
        gameItemView.setGameItemListener(gameItemListener);
        gameItemView.setModel(sceneModel, gameModel);
        gridLayout.addView(gameItemView);
    }
}
