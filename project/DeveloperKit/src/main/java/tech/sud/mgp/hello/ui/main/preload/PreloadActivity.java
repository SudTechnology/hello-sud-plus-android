package tech.sud.mgp.hello.ui.main.preload;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import tech.sud.mgp.core.ISudAPPD;
import tech.sud.mgp.core.ISudListenerInitSDK;
import tech.sud.mgp.core.ISudListenerPreloadMGPkg;
import tech.sud.mgp.core.PkgDownloadStatus;
import tech.sud.mgp.core.SudMGP;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.common.widget.dialog.SimpleChooseDialog;
import tech.sud.mgp.hello.service.main.config.SudConfig;
import tech.sud.mgp.hello.service.main.config.SudEnvConfig;
import tech.sud.mgp.hello.service.main.repository.MainRepository;
import tech.sud.mgp.hello.service.main.resp.GameModel;

/**
 * 预加载测试页面
 */
public class PreloadActivity extends BaseActivity {

    private PreloadGameViewModel gameViewModel = new PreloadGameViewModel();
    private PreloadProgressAdapter adapter = new PreloadProgressAdapter();

    private FrameLayout gameContainer;
    private Button btnPreload;
    private Button btnLoadGame;
    private Button btnOperateList;
    private Button btnCloseGame;
    private Button btnClearCache;
    private Button btnDownloadAll;
    private Button BtnDownloadAllReverse;
    private RecyclerView recyclerView;

    private String gameRoomId = "999";
    private long playingGameId;
    private List<GameModel> gameModelList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_preload;
    }

    @Override
    protected void setStatusBar() {
        updateStatusBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatusBar();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            updateStatusBar();
        }
    }

    protected void updateStatusBar() {
        ImmersionBar.with(this).statusBarDarkFont(true).statusBarColor(R.color.transparent).fullScreen(false).hideBar(BarHide.FLAG_SHOW_BAR).init();
//        if (playingGameId > 0) { // 玩着游戏
//            ImmersionBar.with(this).statusBarColor(R.color.transparent).fullScreen(true).hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR).init();
//        } else {
//            ImmersionBar.with(this).statusBarDarkFont(true).statusBarColor(R.color.transparent).fullScreen(false).hideBar(BarHide.FLAG_SHOW_BAR).init();
//        }
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        gameContainer = findViewById(R.id.game_container);
        btnPreload = findViewById(R.id.btn_preload);
        btnLoadGame = findViewById(R.id.btn_load_game);
        btnOperateList = findViewById(R.id.btn_operate_list);
        recyclerView = findViewById(R.id.recycler_view);
        btnCloseGame = findViewById(R.id.btn_close_game);
        btnClearCache = findViewById(R.id.btn_clear_cache);
        btnDownloadAll = findViewById(R.id.btn_download_all);
        BtnDownloadAllReverse = findViewById(R.id.btn_download_all_reverse);
        View viewTopBtn = findViewById(R.id.view_top_btn);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(null);

        ViewUtils.addMarginTop(viewTopBtn, ImmersionBar.getStatusBarHeight(this));

        initSdk();
    }

    @Override
    protected void initData() {
        super.initData();
        gameModelList = MainRepository.getGameList();
        for (GameModel gameModel : gameModelList) {
            addPreloadModel(gameModel.gameId);
        }
    }

    private void initSdk() {
        String appId = null;
        String appKey = null;
        SudConfig sudConfig = AppData.getInstance().getSudConfig();
        if (sudConfig != null) {
            appId = sudConfig.appId;
            appKey = sudConfig.appKey;
        }
        if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(appKey)) {
            ToastUtils.showLong("SudConfig is empty");
            return;
        }

        initEnv();

        // 初始化sdk
        SudMGP.initSDK(this, appId, appKey, isTestEnv(), new ISudListenerInitSDK() {
            @Override
            public void onSuccess() {
                ToastUtils.showShort("initSDK onSuccess");
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                ToastUtils.showShort("initSDK onFailure:" + errMsg + "(" + errCode + ")");
            }
        });
    }

    private void initEnv() {
        SudEnvConfig config = AppData.getInstance().getSudEnvConfig();
        if (config != null) {
            ISudAPPD.e(config.env);
            if (config.env == 4) {
                ISudAPPD.d();
            }
        }
    }

    private boolean isTestEnv() {
        SudEnvConfig config = AppData.getInstance().getSudEnvConfig();
        if (config != null) {
            return config.isTestEnv;
        }
        return false;
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        setClickListener();
        setGameListener();
    }

    private void setClickListener() {
        btnPreload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPreload();
            }
        });
        btnLoadGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLoadGame();
            }
        });
        btnCloseGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchGame(0);
            }
        });
        btnClearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.launchAppDetailsSettings();
            }
        });
        btnDownloadAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preloadAll();
            }
        });
        BtnDownloadAllReverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preloadAllReverse();
            }
        });
        adapter.addChildClickViewIds(R.id.tv_load_game, R.id.tv_start, R.id.tv_cancel, R.id.tv_pause, R.id.tv_resume);
        adapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                itemChildClick(view, position);
            }
        });
        btnOperateList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerView.getVisibility() == View.VISIBLE) {
                    recyclerView.setVisibility(View.GONE);
                    btnOperateList.setText(R.string.show_list);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    btnOperateList.setText(R.string.hide_list);
                }
            }
        });
    }

    // 预加载全部
    private void preloadAll() {
        List<Long> mgIdList = new ArrayList<>();
        for (PreloadModel item : adapter.getData()) {
            mgIdList.add(item.gameId);
        }
        SudMGP.preloadMGPkgList(this, mgIdList, iSudListenerGamePkgPreload);
    }

    // 预加载全部，倒序
    private void preloadAllReverse() {
        List<Long> mgIdList = new ArrayList<>();
        for (PreloadModel item : adapter.getData()) {
            mgIdList.add(item.gameId);
        }
        Collections.reverse(mgIdList);
        SudMGP.preloadMGPkgList(this, mgIdList, iSudListenerGamePkgPreload);
    }

    private void itemChildClick(View view, int position) {
        int id = view.getId();
        PreloadModel item = adapter.getItem(position);
        long gameId = item.gameId;
        if (id == R.id.tv_load_game) {
            switchGame(gameId);
            return;
        }
        if (id == R.id.tv_start) {
            onPreload(gameId);
            return;
        }
        if (id == R.id.tv_pause) {
//            SudMGP.pausePreloadMGPkgList(getMGIdList(gameId));
            return;
        }
        if (id == R.id.tv_resume) {
//            SudMGP.resumePreloadMGPkgList(getMGIdList(gameId));
            return;
        }
        if (id == R.id.tv_cancel) {
            SudMGP.cancelPreloadMGPkgList(getMGIdList(gameId));
            return;
        }
    }

    private List<Long> getMGIdList(Long... mgId) {
        return Arrays.asList(mgId);
    }

    private void setGameListener() {
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
    }

    // 点击了加载游戏
    private void onClickLoadGame() {
        GameModeDialog dialog = new GameModeDialog();
        dialog.setSelectGameListener(new GameModeDialog.SelectGameListener() {
            @Override
            public void onSelectGame(long gameId) {
                switchGame(gameId);
                dialog.dismiss();
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    // 点击预加载
    private void onClickPreload() {
        GameModeDialog dialog = new GameModeDialog();
        dialog.setSelectGameListener(new GameModeDialog.SelectGameListener() {
            @Override
            public void onSelectGame(long gameId) {
                onPreload(gameId);
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    // 预加载游戏
    private void onPreload(long gameId) {
        PreloadModel preloadModel = getPreloadModel(gameId);
        if (preloadModel == null) {
            preloadModel = addPreloadModel(gameId);
        }
        SudMGP.preloadMGPkgList(this, getMGIdList(gameId), iSudListenerGamePkgPreload);
    }

    private PreloadModel addPreloadModel(long gameId) {
        PreloadModel preloadModel = new PreloadModel();
        preloadModel.gameId = gameId;

        GameModel gameModel = findGameModel(gameId);
        if (gameModel != null) {
            preloadModel.iconResId = gameModel.gamePicRes;
            preloadModel.gameName = gameModel.gameName;
        }

        adapter.addData(preloadModel);
        return preloadModel;
    }

    private GameModel findGameModel(long gameId) {
        for (GameModel gameModel : gameModelList) {
            if (gameModel.gameId == gameId) {
                return gameModel;
            }
        }
        return null;
    }

    private PreloadModel getPreloadModel(long gameId) {
        for (PreloadModel model : adapter.getData()) {
            if (gameId == model.gameId) {
                return model;
            }
        }
        return null;
    }

    private void switchGame(long gameId) {
        playingGameId = gameId;
        gameViewModel.switchGame(this, gameRoomId, gameId, null);
        updateStatusBar();
    }

    // 预加载进度
    private final ISudListenerPreloadMGPkg iSudListenerGamePkgPreload = new ISudListenerPreloadMGPkg() {
        @Override
        public void onPreloadSuccess(long mgId) {
            LogUtils.d("onPreloadSuccess:" + mgId);
            PreloadModel preloadModel = getPreloadModel(mgId);
            if (preloadModel == null) {
                preloadModel = addPreloadModel(mgId);
            }
            preloadModel.status = PkgDownloadStatus.PKG_DOWNLOAD_COMPLETED;
            preloadModel.progress = 100;
            updateItem(preloadModel);
        }

        @Override
        public void onPreloadFailure(long mgId, int errorCode, String errorMsg) {
            LogUtils.d("onPreloadFailure:" + mgId + "  errorCode:" + errorCode + "  errorMsg:" + errorMsg);
            PreloadModel preloadModel = getPreloadModel(mgId);
            if (preloadModel == null) {
                preloadModel = addPreloadModel(mgId);
            }
            preloadModel.errorCode = errorCode;
            preloadModel.errorMsg = errorMsg;
            updateItem(preloadModel);
        }

        @Override
        public void onPreloadStatus(long mgId, long downloadedSize, long totalSize, PkgDownloadStatus status) {
            LogUtils.d("onPreloadStatus:" + mgId + "  status:" + status + "  downloadedSize:" + downloadedSize + "  totalSize:" + totalSize);
            PreloadModel preloadModel = getPreloadModel(mgId);
            if (preloadModel == null) {
                preloadModel = addPreloadModel(mgId);
            }
            preloadModel.errorCode = 0;
            preloadModel.downloadedSize = downloadedSize;
            preloadModel.totalSize = totalSize;
            preloadModel.status = status;
            preloadModel.progress = (int) (downloadedSize * 1.0f / totalSize * 100);
            updateItem(preloadModel);
        }
    };

    private void updateItem(PreloadModel model) {
        int position = adapter.getData().indexOf(model);
        if (position >= 0) {
            adapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onBackPressed() {
        intentBack();
    }

    private void intentBack() {
        SimpleChooseDialog dialog = new SimpleChooseDialog(this,
                getString(R.string.audio_close_room_title),
                getString(R.string.audio_cancle),
                getString(R.string.confirm));
        dialog.show();
        dialog.setOnChooseListener(new SimpleChooseDialog.OnChooseListener() {
            @Override
            public void onChoose(int index) {
                dialog.dismiss();
                if (index == 1) {
                    finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameViewModel.onDestroy();
    }

}
