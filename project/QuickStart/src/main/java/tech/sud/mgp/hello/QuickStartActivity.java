package tech.sud.mgp.hello;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.Utils;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

import tech.sud.mgp.SudMGPWrapper.model.GameConfigModel;
import tech.sud.mgp.SudMGPWrapper.model.GameViewInfoModel;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.SystemUtils;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.ui.game.widget.GameModeDialog;
import tech.sud.mgp.hello.ui.game.widget.GameRoomMoreDialog;
import tech.sud.mgp.hello.ui.game.widget.GameRoomTopView;

/**
 * 游戏页面
 */
public class QuickStartActivity extends BaseActivity {

    private long roomId;
    private long gameId;

    private GameRoomTopView topView;

    private final QuickStartGameViewModel gameViewModel = new QuickStartGameViewModel();

    /** 外部调用，打开游戏页面 */
    public static void start(Context context, long roomId, long gameId) {
        Intent intent = new Intent(context, QuickStartActivity.class);
        intent.putExtra("roomId", roomId);
        intent.putExtra("gameId", gameId);
        context.startActivity(intent);
    }

    @Override
    protected void setStatusBar() {
        updateStatusBar();
    }

    @Override
    protected boolean beforeSetContentView() {
        roomId = getIntent().getLongExtra("roomId", 0);
        gameId = getIntent().getLongExtra("gameId", 0);
        return super.beforeSetContentView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_game;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        topView = findViewById(R.id.room_top_view);

        ViewUtils.addMarginTop(topView, ImmersionBar.getStatusBarHeight(this));

        topView.setName(getString(R.string.room_name));
        topView.setId(getString(R.string.room_number) + " " + roomId);

        // 设置游戏安全操作区域
        GameViewInfoModel.GameViewRectModel gameViewRectModel = new GameViewInfoModel.GameViewRectModel();
        gameViewRectModel.left = 0;
        gameViewRectModel.top = DensityUtils.dp2px(Utils.getApp(), 54) + BarUtils.getStatusBarHeight();
        gameViewRectModel.right = 0;
        gameViewRectModel.bottom = DensityUtils.dp2px(Utils.getApp(), 54);
        gameViewModel.gameViewRectModel = gameViewRectModel;

        // 游戏配置
        GameConfigModel gameConfigModel = gameViewModel.getGameConfigModel();
        gameConfigModel.ui.ping.hide = false; // 配置不隐藏ping值

        // 语言代码
        gameViewModel.languageCode = SystemUtils.getLanguageCode(this);
    }

    @Override
    protected void initData() {
        super.initData();
        // 调用此方法，加载对应的游戏，开发者可根据业务决定什么时候加载游戏。
        gameViewModel.switchGame(this, roomId + "", gameId);
        updateStatusBar();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        FrameLayout gameContainer = findViewById(R.id.game_container); // 获取游戏View容器
        gameViewModel.gameViewLiveData.observe(this, new Observer<View>() {
            @Override
            public void onChanged(View view) {
                if (view == null) { // 在关闭游戏时，把游戏View给移除
                    gameContainer.removeAllViews();
                } else { // 把游戏View添加到容器内
                    gameContainer.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                }
            }
        });

        // 选择游戏的点击监听
        topView.setSelectGameClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGameModeDialog();
            }
        });

        // 更多按钮的点击监听
        topView.setMoreOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreDialog();
            }
        });
    }

    private void showMoreDialog() {
        GameRoomMoreDialog dialog = new GameRoomMoreDialog();
        dialog.setExitOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    private void showGameModeDialog() {
        GameModeDialog dialog = new GameModeDialog();
        dialog.setPlayingGameId(gameId);
        dialog.setSelectGameListener(new GameModeDialog.SelectGameListener() {
            @Override
            public void onSelectGame(long gameId) {
                QuickStartActivity.this.gameId = gameId;
                gameViewModel.switchGame(context, roomId + "", gameId);
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    private void updateStatusBar() {
        if (gameId > 0) { // 玩着游戏
            ImmersionBar.with(this).statusBarColor(R.color.transparent).hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR).init();
        } else {
            ImmersionBar.with(this).statusBarColor(R.color.transparent).hideBar(BarHide.FLAG_SHOW_BAR).init();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            updateStatusBar();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatusBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameViewModel.onDestroy();
    }

}
