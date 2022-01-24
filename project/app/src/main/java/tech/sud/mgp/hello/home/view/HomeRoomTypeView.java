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

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.home.GameAdapter;
import tech.sud.mgp.hello.home.model.GameModel;
import tech.sud.mgp.hello.home.model.SceneModel;
import tech.sud.mgp.hello.utils.GlideImageLoader;

public class HomeRoomTypeView extends ConstraintLayout {
    private TextView sceneNameTv;
    private ImageView sceneIv;
    private GridLayout gridLayout;
    private RecyclerView gameRecyclerview;
    private GameAdapter gameAdapter;

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
    }

    public void setData(SceneModel model, List<GameModel> datas) {
        if (!TextUtils.isEmpty(model.getSceneName())) {
            sceneNameTv.setText(model.getSceneName());
        }
        if (!TextUtils.isEmpty(model.getSceneImage())) {
            GlideImageLoader.loadImage(sceneIv, model.getSceneImage());
        }else {
            sceneIv.setImageResource(R.mipmap.icon_audio_room);
        }
        if (datas != null && datas.size() > 0) {
            List<GameModel> models = new ArrayList<>();
            for (int i = 0; i < datas.size(); i++) {
                if (i > 6) {
                    models.add(datas.get(i));
                } else {
                    addGameView(datas.get(i));
                }
            }
            if (models.size() > 0) {
                gameAdapter = new GameAdapter(models);
                gameRecyclerview.setLayoutManager(new GridLayoutManager(getContext(), 4));
                gameRecyclerview.setAdapter(gameAdapter);
                gameRecyclerview.setVisibility(View.VISIBLE);
            } else {
                gameRecyclerview.setVisibility(View.GONE);
            }
        }
    }

    private void addGameView(GameModel gameModel) {
        GameItemView gameItemView = new GameItemView(getContext());
        gameItemView.setModel(gameModel);
        gridLayout.addView(gameItemView);
    }
}
