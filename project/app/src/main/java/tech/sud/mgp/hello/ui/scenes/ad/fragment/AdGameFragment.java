package tech.sud.mgp.hello.ui.scenes.ad.fragment;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.LogUtils;
import com.gyf.immersionbar.ImmersionBar;

import java.io.Serializable;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseFragment;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.room.resp.OpenPassGameInfo;
import tech.sud.mgp.hello.ui.main.base.constant.GameIdCons;
import tech.sud.mgp.hello.ui.main.base.constant.SceneType;
import tech.sud.mgp.hello.ui.main.home.model.MatchRoomModel;
import tech.sud.mgp.hello.ui.scenes.ad.activity.AdRoomActivity;
import tech.sud.mgp.hello.ui.scenes.ad.utils.SudGameStopwatch;
import tech.sud.mgp.hello.ui.scenes.ad.viewmodel.AppRuntime2GameViewModel;
import tech.sud.mgp.hello.ui.scenes.base.utils.EnterRoomUtils;

public class AdGameFragment extends BaseFragment {

    private int mPosition;
    private FrameLayout mGameContainer;
    private OpenPassGameInfo mOpenPassGameInfo;
    private AppRuntime2GameViewModel mGameViewModel = new AppRuntime2GameViewModel();

    private View mContainerClickPlay;
    private View mContainerGameFinish;
    private ImageView mIvCover;
    private ImageView mIvIcon;
    private TextView mTvGameName;
    private ImageView mIvBack;
    private ImageView mIvClosePlay;
    private View mContainerTime;
    private TextView mTvTime;
    private boolean isFirstFrameCompleted;
    private SudGameStopwatch mSudGameStopwatch = new SudGameStopwatch();

    public static AdGameFragment newInstance(int position, OpenPassGameInfo info) {
        Bundle args = new Bundle();
        args.putSerializable("CocosGameInfo", info);
        args.putInt("position", position);
        AdGameFragment fragment = new AdGameFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mPosition = arguments.getInt("position");
            Serializable cocosGameInfo = arguments.getSerializable("CocosGameInfo");
            if (cocosGameInfo instanceof OpenPassGameInfo) {
                mOpenPassGameInfo = (OpenPassGameInfo) cocosGameInfo;
            }
        }
        mGameViewModel.CALC_TAG = "CocosCalc" + mPosition + " ";
        LogUtils.d("position:" + mPosition + " onCreate");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_cocos_game;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        LogUtils.d("position:" + mPosition + " initWidget");
        mGameContainer = findViewById(R.id.game_container);
        mContainerClickPlay = findViewById(R.id.container_click_play);
        mContainerGameFinish = findViewById(R.id.container_finish);
        mIvCover = findViewById(R.id.iv_game_cover);
        mIvIcon = findViewById(R.id.iv_game_icon);
        mTvGameName = findViewById(R.id.tv_game_name);
        mIvBack = findViewById(R.id.iv_back);
        mIvClosePlay = findViewById(R.id.iv_close_play);
        mContainerTime = findViewById(R.id.container_time);
        mTvTime = findViewById(R.id.tv_time);

        setUserInputEnabled(true);

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
        mGameViewModel.switchGame(requireActivity(), gameId, gameUrl, gamePkgVersion);
        onStart();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        mContainerClickPlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        mContainerGameFinish.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

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
        mGameViewModel.gameMGCommonGameFinishLiveData.observe(this, this::onGameFinish);
        findViewById(R.id.tv_click_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 点击了试玩
                onClickPlayGame();
            }
        });
        findViewById(R.id.tv_operate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickGameOperate();
            }
        });
        findViewById(R.id.tv_play_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickPlayGame();
            }
        });
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().finish();
            }
        });
        mIvClosePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 点击了关闭试玩
                mIvBack.setVisibility(View.VISIBLE);
                mIvClosePlay.setVisibility(View.GONE);
                mContainerClickPlay.setVisibility(View.VISIBLE);
                mContainerGameFinish.setVisibility(View.GONE);
                setUserInputEnabled(true);
            }
        });
        mGameViewModel.gameStartedLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                onGameStarted();
            }
        });
        mGameViewModel.firstFrameLiveData.observe(this, o -> {
            isFirstFrameCompleted = true;
            stopTiming();
            mContainerTime.setVisibility(View.GONE);
            mContainerClickPlay.setVisibility(View.VISIBLE);
        });
        mSudGameStopwatch.setOnTimingListener(new SudGameStopwatch.OnTimingListener() {
            @Override
            public void onTiming(long millis) {
                mTvTime.setText(millis + "");
            }
        });
    }

    private void startTiming() {
        if (isFirstFrameCompleted) {
            return;
        }
        mSudGameStopwatch.start();
    }

    private void stopTiming() {
        mSudGameStopwatch.stop();
    }

    private void onGameStarted() {
        if (mPosition != 0) {
            return;
        }
        FragmentActivity activity = getActivity();
        if (activity instanceof AdRoomActivity) {
            AdRoomActivity adRoomActivity = (AdRoomActivity) activity;
            adRoomActivity.firstPageGameStarted();
        }
    }

    private void onClickPlayGame() {
        mIvBack.setVisibility(View.GONE);
        mIvClosePlay.setVisibility(View.VISIBLE);
        mContainerClickPlay.setVisibility(View.GONE);
        mContainerGameFinish.setVisibility(View.GONE);
        setUserInputEnabled(false);
    }

    private void onClickGameOperate() {
        HomeRepository.matchGame(SceneType.AUDIO, GameIdCons.MONSTER_CRUSH, null, this, new RxCallback<MatchRoomModel>() {
            @Override
            public void onNext(BaseResponse<MatchRoomModel> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS) {
                    EnterRoomUtils.enterRoom(null, t.getData().roomId);
                }
            }
        });
    }

    // 游戏结束了
    private void onGameFinish(String dataJson) {
        mIvBack.setVisibility(View.VISIBLE);
        mIvClosePlay.setVisibility(View.VISIBLE);
        mContainerGameFinish.setVisibility(View.VISIBLE);
        ImageLoader.loadImage(mIvCover, mOpenPassGameInfo.cover);
        ImageLoader.loadImage(mIvIcon, mOpenPassGameInfo.icon);
        mTvGameName.setText(mOpenPassGameInfo.name);
        setUserInputEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.d("position:" + mPosition + " onStart");
        startTiming();
        mGameViewModel.onStart();
        mGameViewModel.setMute(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d("position:" + mPosition + " onResume");
        mGameViewModel.onResume();
        mGameViewModel.setMute(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.d("position:" + mPosition + " onPause");
        mGameViewModel.onPause();
        mGameViewModel.setMute(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.d("position:" + mPosition + " onStop");
        stopTiming();
        mGameViewModel.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.d("position:" + mPosition + " onDestroyView");
        mGameViewModel.gameViewLiveData.removeObservers(this);
        mGameViewModel.destroyGame();
        isFirstFrameCompleted = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d("position:" + mPosition + " onDestroy");
    }

    private void setUserInputEnabled(boolean enabled) {
        FragmentActivity activity = getActivity();
        if (activity instanceof AdRoomActivity) {
            AdRoomActivity adRoomActivity = (AdRoomActivity) activity;
            adRoomActivity.setUserInputEnabled(enabled);
        }
    }

}
