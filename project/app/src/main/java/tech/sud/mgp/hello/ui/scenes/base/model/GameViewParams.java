package tech.sud.mgp.hello.ui.scenes.base.model;

import android.view.View;

public class GameViewParams {

    public View gameView;
    public Float scale;
    public int loadType;

    public GameViewParams() {
    }

    public GameViewParams(View gameView) {
        this.gameView = gameView;
    }

    public GameViewParams(View gameView, Float scale, int loadType) {
        this.gameView = gameView;
        this.scale = scale;
        this.loadType = loadType;
    }

    public GameViewParams(int loadType) {
        this.loadType = loadType;
    }
}
