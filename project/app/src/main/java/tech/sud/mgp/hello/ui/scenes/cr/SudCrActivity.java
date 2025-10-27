package tech.sud.mgp.hello.ui.scenes.cr;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.LogUtils;
import com.gyf.immersionbar.ImmersionBar;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.service.room.resp.CocosGameInfo;
import tech.sud.mgp.hello.ui.scenes.ad.viewmodel.QuickStartCocosGameViewModel;

public class SudCrActivity extends BaseActivity {

    private FrameLayout mGameContainer;
    private CocosGameInfo mCocosGameInfo;
    private QuickStartCocosGameViewModel mGameViewModel = new QuickStartCocosGameViewModel();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sud_cr;
    }
    
    public static void start(Context context, CocosGameInfo info) {
        Intent intent = new Intent(context, SudCrActivity.class);
        intent.putExtra("CocosGameInfo", info);
        context.startActivity(intent);
    }

    @Override
    protected boolean beforeSetContentView() {
        mCocosGameInfo = (CocosGameInfo) getIntent().getSerializableExtra("CocosGameInfo");
        return super.beforeSetContentView();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mGameContainer = findViewById(R.id.game_container);

        ViewUtils.setHeight(findViewById(R.id.top_bar), ImmersionBar.getStatusBarHeight(this));
    }

    @Override
    protected void initData() {
        super.initData();
        if (mCocosGameInfo == null) {
            return;
        }
        String userId = HSUserInfo.userId + "";
        String gameId = mCocosGameInfo.gameId;
        String gameUrl = mCocosGameInfo.url;
        String gamePkgVersion = mCocosGameInfo.version;
        mGameViewModel.startGame(this, userId, gameId, gameUrl, gamePkgVersion);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        mGameViewModel.gameViewLiveData.observe(this, new Observer<View>() {
            @Override
            public void onChanged(View view) {
                if (view == null) {
                    mGameContainer.removeAllViews();
                } else {
                    LogUtils.d(mGameViewModel.CALC_TAG + "把游戏View添加到页面上 gameId:" + mCocosGameInfo.gameId);
                    mGameContainer.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                }
            }
        });
        findViewById(R.id.btn_load).setOnClickListener(v -> {
            mGameViewModel.startGame(this, HSUserInfo.userId + "", mCocosGameInfo.gameId, mCocosGameInfo.url, mCocosGameInfo.version);
        });
        findViewById(R.id.btn_destroy).setOnClickListener(v -> {
            mGameViewModel.destroyGame();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mGameViewModel.onStart();
        mGameViewModel.setMute(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mGameViewModel.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGameViewModel.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGameViewModel.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGameViewModel.destroyGame();
    }

}
