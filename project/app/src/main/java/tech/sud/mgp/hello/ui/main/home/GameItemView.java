package tech.sud.mgp.hello.ui.main.home;

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

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.SceneModel;

public class GameItemView extends ConstraintLayout {

    private ImageView gameIv;
    private TextView gameTv;
    private SceneModel sceneModel;
    private GameModel gameModel;
    private GameItemListener gameItemListener;

    public void setGameItemListener(GameItemListener gameItemListener) {
        this.gameItemListener = gameItemListener;
    }

    public GameItemView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public GameItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.item_game_view, this);
        gameIv = findViewById(R.id.game_icon);
        gameTv = findViewById(R.id.game_name);
    }

    public void setModel(SceneModel sceneModel, GameModel gameModel) {
        this.sceneModel = sceneModel;
        this.gameModel = gameModel;
        if (TextUtils.isEmpty(gameModel.getGamePic())) {
            gameIv.setImageResource(R.drawable.icon_empty_game);
        } else {
            ImageLoader.loadImage(gameIv, gameModel.getGamePic());
        }

        gameTv.setText(gameModel.getGameName());

        this.setOnClickListener(v -> {
            if (gameItemListener != null && gameModel.getGameId() != -1) {
                gameItemListener.onGameClick(sceneModel, gameModel);
            }
        });
    }

    public interface GameItemListener {
        void onGameClick(SceneModel sceneModel, GameModel gameModel);
    }

}