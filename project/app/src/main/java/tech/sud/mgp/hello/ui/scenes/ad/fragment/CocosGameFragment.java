package tech.sud.mgp.hello.ui.scenes.ad.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.LogUtils;

import java.io.Serializable;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseFragment;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.service.room.resp.CocosGameInfo;
import tech.sud.mgp.hello.ui.scenes.ad.viewmodel.QuickStartCocosGameViewModel;

public class CocosGameFragment extends BaseFragment {

    private int mPosition;
    private FrameLayout mGameContainer;
    private CocosGameInfo mCocosGameInfo;
    private QuickStartCocosGameViewModel mGameViewModel = new QuickStartCocosGameViewModel();

    public static CocosGameFragment newInstance(int position, CocosGameInfo info) {
        Bundle args = new Bundle();
        args.putSerializable("CocosGameInfo", info);
        args.putInt("position", position);
        CocosGameFragment fragment = new CocosGameFragment();
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
            if (cocosGameInfo instanceof CocosGameInfo) {
                mCocosGameInfo = (CocosGameInfo) cocosGameInfo;
            }
        }
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
        mGameViewModel.startGame(requireActivity(), userId, gameId, gameUrl, gamePkgVersion);
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
                    mGameContainer.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                }
            }
        });
        mGameViewModel.gameMGCommonGameFinishLiveData.observe(this, this::onGameFinish);
    }

    private void onGameFinish(String dataJson) {
        // TODO: 2025/10/13 游戏结束了
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.d("position:" + mPosition + " onStart");
        mGameViewModel.setMute(true);
        mGameViewModel.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d("position:" + mPosition + " onResume");
        mGameViewModel.setMute(false);
        mGameViewModel.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.d("position:" + mPosition + " onPause");
        mGameViewModel.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.d("position:" + mPosition + " onStop");
        mGameViewModel.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.d("position:" + mPosition + " onDestroyView");
        mGameViewModel.gameViewLiveData.removeObservers(this);
        mGameViewModel.destroyGame();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d("position:" + mPosition + " onDestroy");
    }

}
