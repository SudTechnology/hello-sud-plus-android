package tech.sud.mgp.hello.ui.scenes.base.activity;

import android.view.View;
import android.widget.FrameLayout;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppRocketGameViewModel;

/**
 * 带火箭动效的房间
 */
public abstract class BaseRocketRoomActivity<T extends AppRocketGameViewModel> extends BaseRoomActivity<T> {

    private View viewRocketEntrance;
    protected FrameLayout rocketContainer;

    @Override
    protected void initWidget() {
        super.initWidget();
        viewRocketEntrance = findViewById(R.id.view_custom_rocket);
        rocketContainer = findViewById(R.id.rocket_container);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        viewRocketEntrance.setOnClickListener((v) -> {
            showCustomRocket();
        });
    }

    /** 展示火箭 */
    protected void showCustomRocket() {

    }

    @Override
    protected void updatePageStyle() {
        super.updatePageStyle();
        if (playingGameId > 0) {
            viewRocketEntrance.setVisibility(View.GONE);
        } else {
            viewRocketEntrance.setVisibility(View.VISIBLE);
        }
    }

}
