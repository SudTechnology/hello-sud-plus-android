package tech.sud.mgp.hello.ui.game;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.lifecycle.Observer;

import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.ui.game.widget.GameModeDialog;
import tech.sud.mgp.hello.ui.game.widget.GameRoomMoreDialog;
import tech.sud.mgp.hello.ui.game.widget.GameRoomTopView;

/**
 * 游戏页面
 */
public class GameActivity extends BaseActivity {

    private long roomId;
    private long gameId;

    private FrameLayout gameContainer;
    private GameRoomTopView topView;

    private final GameViewModel gameViewModel = new GameViewModel();

    /** 外部调用，打开游戏页面 */
    public static void start(Context context, long roomId, long gameId) {
        Intent intent = new Intent(context, GameActivity.class);
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
        gameContainer = findViewById(R.id.game_container);
        topView = findViewById(R.id.room_top_view);

        ViewUtils.addMarginTop(topView, ImmersionBar.getStatusBarHeight(this));

        topView.setName(getString(R.string.room_name));
        topView.setId(getString(R.string.room_number) + " " + roomId);
    }

    @Override
    protected void initData() {
        super.initData();
        // 调用此方法，加载对应的游戏
        gameViewModel.switchGame(this, roomId, gameId);
        updateStatusBar();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        // 在此处拿到游戏View，添加到Activity中去
        gameViewModel.gameViewLiveData.observe(this, new Observer<View>() {
            @Override
            public void onChanged(View view) {
                if (view == null) {
                    gameContainer.removeAllViews();
                } else {
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
                GameActivity.this.gameId = gameId;
                gameViewModel.switchGame(context, roomId, gameId);
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
