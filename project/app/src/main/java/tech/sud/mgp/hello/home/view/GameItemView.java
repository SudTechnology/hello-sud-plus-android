package tech.sud.mgp.hello.home.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.common.utils.ImageLoader;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.home.callback.GameItemCallback;
import tech.sud.mgp.hello.home.model.GameModel;

public class GameItemView extends ConstraintLayout {

    private ImageView gameIv;
    private TextView gameTv;
    private GameModel model;
    private GameItemCallback itemCallback;

    public void setItemCallback(GameItemCallback itemCallback) {
        this.itemCallback = itemCallback;
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

    public void setModel(GameModel model) {
        this.model = model;
        if (TextUtils.isEmpty(model.getGamePic())) {
            gameIv.setImageResource(R.mipmap.icon_logo);
        } else {
            ImageLoader.loadImage(gameIv, model.getGamePic());
        }

        if (TextUtils.isEmpty(model.getGameName())) {
            gameTv.setText("Game");
        } else {
            gameTv.setText(model.getGameName());
        }

        this.setOnClickListener(v -> {
            if (itemCallback != null) {
                itemCallback.gameClick(model);
            }
        });
    }

}