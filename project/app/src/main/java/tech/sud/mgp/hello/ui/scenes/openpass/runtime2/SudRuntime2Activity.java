package tech.sud.mgp.hello.ui.scenes.openpass.runtime2;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.LogUtils;
import com.gyf.immersionbar.ImmersionBar;

import java.util.Locale;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.service.room.resp.OpenPassGameInfo;
import tech.sud.mgp.hello.ui.scenes.base.model.GameLoadingProgressModel;

public class SudRuntime2Activity extends BaseActivity {

    private View mLayoutProgress;
    private TextView mTvProgress;
    private FrameLayout mGameContainer;
    private OpenPassGameInfo mOpenPassGameInfo;
    private TestRuntime2GameViewModel mGameViewModel = new TestRuntime2GameViewModel();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sud_runtime;
    }

    public static void start(Context context, OpenPassGameInfo info, String userId) {
        Intent intent = new Intent(context, SudRuntime2Activity.class);
        intent.putExtra("CocosGameInfo", info);
        intent.putExtra("userId", userId);
        context.startActivity(intent);
    }

    @Override
    protected boolean beforeSetContentView() {
        mOpenPassGameInfo = (OpenPassGameInfo) getIntent().getSerializableExtra("CocosGameInfo");
        mGameViewModel.userId = getIntent().getStringExtra("userId");
        return super.beforeSetContentView();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mGameContainer = findViewById(R.id.game_container);
        mLayoutProgress = findViewById(R.id.layout_progress);
        mTvProgress = findViewById(R.id.tv_progress);

        ViewUtils.setHeight(findViewById(R.id.top_bar), ImmersionBar.getStatusBarHeight(this));
    }

    @Override
    protected void initData() {
        super.initData();
        if (mOpenPassGameInfo == null) {
            return;
        }
        String gameId = mOpenPassGameInfo.gameId;
        String gameUrl = mOpenPassGameInfo.url;
        String gamePkgVersion = mOpenPassGameInfo.version;
        mGameViewModel.switchGame(this, gameId, gameUrl, gamePkgVersion);
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
                    LogUtils.d(mGameViewModel.CALC_TAG + "把游戏View添加到页面上 gameId:" + mOpenPassGameInfo.gameId);
                    mGameContainer.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                }
            }
        });
        findViewById(R.id.btn_load).setOnClickListener(v -> {
            mGameViewModel.switchGame(this, mOpenPassGameInfo.gameId, mOpenPassGameInfo.url, mOpenPassGameInfo.version);
        });
        findViewById(R.id.btn_destroy).setOnClickListener(v -> {
            mGameViewModel.destroyGame();
        });
        mGameViewModel.gameLoadingProgressLiveData.observe(this, new Observer<GameLoadingProgressModel>() {
            @Override
            public void onChanged(GameLoadingProgressModel model) {
                updateProgress(model);
            }
        });
    }

    private void updateProgress(GameLoadingProgressModel model) {
        if (model == null) {
            return;
        }
        if (model.stage == 3) {
            mLayoutProgress.setVisibility(View.GONE);
        } else {
            mLayoutProgress.setVisibility(View.VISIBLE);
            if (model.retCode == RetCode.SUCCESS) {
                mTvProgress.setText(String.format(Locale.US, "加载百分比为:%d%%", model.progress));
            } else {
                mTvProgress.setText(String.format(Locale.US, "加载失败，code：%d", model.retCode));
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGameViewModel.onStart();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LogUtils.d("onBackPressed");
    }
}
