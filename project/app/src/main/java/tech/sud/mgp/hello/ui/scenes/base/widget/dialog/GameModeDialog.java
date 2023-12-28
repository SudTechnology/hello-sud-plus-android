package tech.sud.mgp.hello.ui.scenes.base.widget.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.req.GameListReq;
import tech.sud.mgp.hello.service.main.resp.GameListResp;
import tech.sud.mgp.hello.service.main.resp.GameModel;

/**
 * 选择游戏模式的弹窗
 */
public class GameModeDialog extends BaseDialogFragment {

    private RecyclerView recyclerView;
    private final int spanCount = 4;
    private int sceneType;
    private final GameModeAdapter gameModeAdapter = new GameModeAdapter();
    private long playingGameId; // 当前正在玩的游戏id
    private SelectGameListener selectGameListener;
    private boolean isFinishGame; // 第一个按钮状态,true为结束游戏 false为关闭游戏

    public static GameModeDialog getInstance(int sceneType) {
        GameModeDialog dialog = new GameModeDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("sceneType", sceneType);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            sceneType = arguments.getInt("sceneType");
        }
    }

    /**
     * 设置当前正在玩的游戏id
     *
     * @param playingGameId
     */
    public void setPlayingGameId(long playingGameId) {
        this.playingGameId = playingGameId;
    }

    /**
     * 设置选中游戏模式的监听
     */
    public void setSelectGameListener(SelectGameListener listener) {
        this.selectGameListener = listener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_game_mode;
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected int getWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getHeight() {
        return (int) (DensityUtils.getAppScreenHeight() * 0.7);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        recyclerView = mRootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount, GridLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(gameModeAdapter);

        View headView = View.inflate(getContext(), R.layout.view_game_mode_head, null);
        gameModeAdapter.addHeaderView(headView);

        gameModeAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                GameModel gameModel = gameModeAdapter.getItem(position);
                selectGameMode(gameModel.gameId);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        LifecycleOwner lifecycleOwner = this;
        HomeRepository.gameListV2(lifecycleOwner, GameListReq.TAB_SCENE, new RxCallback<GameListResp>() {
            @Override
            public void onSuccess(GameListResp gameListResp) {
                super.onSuccess(gameListResp);
                if (gameListResp == null) {
                    return;
                }
                List<GameModel> gameList = gameListResp.getGameList(sceneType);
                if (gameList != null && gameList.size() > 0) {
                    gameModeAdapter.setList(gameList);
                    gameModeAdapter.addData(0, new GameModel()); // 添加一个关闭游戏选项
                }
            }

            @Override
            public void onFinally() {
                super.onFinally();
                if (gameModeAdapter.getData().size() == 0) {
                    HomeRepository.gameListV2(lifecycleOwner, GameListReq.TAB_GAME, new RxCallback<GameListResp>() {
                        @Override
                        public void onSuccess(GameListResp gameListResp) {
                            super.onSuccess(gameListResp);
                            if (gameListResp == null) {
                                return;
                            }
                            List<GameModel> gameList = gameListResp.getGameList(sceneType);
                            if (gameList != null && gameList.size() > 0) {
                                gameModeAdapter.setList(gameList);
                                gameModeAdapter.addData(0, new GameModel()); // 添加一个关闭游戏选项
                            }
                        }
                    });
                }
            }
        });
        HomeRepository.gameList(this, new RxCallback<GameListResp>() {
            @Override
            public void onSuccess(GameListResp gameListResp) {
                super.onSuccess(gameListResp);
                if (gameListResp != null) {
                    gameModeAdapter.setList(gameListResp.getGameList(sceneType));
                }
                gameModeAdapter.addData(0, new GameModel()); // 添加一个关闭游戏选项
            }
        });
    }

    @Override
    protected void customStyle(Window window) {
        super.customStyle(window);
        window.setWindowAnimations(R.style.BottomToTopAnim);
    }

    private void selectGameMode(long gameId) {
        dismiss();
        if (selectGameListener != null) {
            selectGameListener.onSelectGame(gameId, isFinishGame);
        }
    }

    /** 设置是否要显示结束游戏 */
    public void setFinishGame(boolean finishGame) {
        isFinishGame = finishGame;
        gameModeAdapter.notifyDataSetChanged();
    }

    private class GameModeAdapter extends BaseQuickAdapter<GameModel, BaseViewHolder> {

        public GameModeAdapter() {
            super(R.layout.item_game_mode);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder baseViewHolder, GameModel gameModel) {
            ImageView ivIcon = baseViewHolder.getView(R.id.item_game_mode_iv_icon);

            long gameId = gameModel.gameId;
            if (gameId == 0) {// 设置为关闭游戏的选项
                baseViewHolder.setVisible(R.id.item_game_mode_tv_gameing, false);
                if (isFinishGame) {
                    ImageLoader.loadDrawable(ivIcon, R.drawable.ic_finish_game);
                    baseViewHolder.setText(R.id.item_game_mode_tv_name, R.string.finish_game);
                } else {
                    ImageLoader.loadDrawable(ivIcon, R.drawable.ic_close_game);
                    baseViewHolder.setText(R.id.item_game_mode_tv_name, R.string.close_game);
                }
                return;
            }

            ImageLoader.loadImage(ivIcon, gameModel.gamePic);

            if (playingGameId == gameId) {
                baseViewHolder.setVisible(R.id.item_game_mode_tv_gameing, true);
            } else {
                baseViewHolder.setVisible(R.id.item_game_mode_tv_gameing, false);
            }

            baseViewHolder.setText(R.id.item_game_mode_tv_name, gameModel.gameName);
        }
    }

    public interface SelectGameListener {
        /**
         * 选择了游戏模式
         *
         * @param gameId       如果为0，则没有选择游戏，根据isFinishGame来判断是结束游戏还是关闭游戏
         * @param isFinishGame 当gameId为0时，是否是结束游戏
         */
        void onSelectGame(long gameId, boolean isFinishGame);
    }

}
