package tech.sud.mgp.hello.ui.scenes.base.activity;

import android.view.View;
import android.widget.FrameLayout;

import androidx.lifecycle.Observer;

import tech.sud.mgp.core.SudMGP;
import tech.sud.mgp.hello.BuildConfig;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.ui.main.base.constant.GameIdCons;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppRocketGameViewModel;

/**
 * 带火箭动效的房间
 */
public abstract class BaseRocketRoomActivity<T extends AppGameViewModel> extends BaseRoomActivity<T> {

    private View viewRocketEntrance;
    protected FrameLayout rocketContainer;
    protected AppRocketGameViewModel rocketGameViewModel = new AppRocketGameViewModel();

    @Override
    protected void initWidget() {
        super.initWidget();
        viewRocketEntrance = findViewById(R.id.view_custom_rocket);
        rocketContainer = findViewById(R.id.rocket_container);
        if (BuildConfig.DEBUG) {
            SudMGP.getCfg().addEmbeddedMGPkg(GameIdCons.CUSTOM_ROCKET, "HelloWorld.rpk");
        }
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        viewRocketEntrance.setOnClickListener((v) -> {
            showCustomRocket();
        });
        rocketGameViewModel.gameViewLiveData.observe(this, new Observer<View>() {
            @Override
            public void onChanged(View view) {
                if (view == null) {
                    rocketContainer.removeAllViews();
                } else {
                    rocketContainer.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                }
            }
        });
    }

    /** 展示火箭 */
    protected void showCustomRocket() {
        rocketGameViewModel.switchGame(this, getGameRoomId(), GameIdCons.CUSTOM_ROCKET);
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

    @Override
    protected void bringToFrontViews() {
        giftContainer.bringToFront();
        rocketContainer.bringToFront();
        inputMsgView.bringToFront();
        clOpenMic.bringToFront();
    }

}
