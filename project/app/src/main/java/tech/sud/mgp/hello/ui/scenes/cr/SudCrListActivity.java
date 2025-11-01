package tech.sud.mgp.hello.ui.scenes.cr;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tech.sud.cr.core.ISudCrListenerUninitSDK;
import tech.sud.cr.core.SudCr;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.service.room.resp.CocosGameInfo;
import tech.sud.mgp.hello.ui.common.listener.SimpleTextWatcher;

public class SudCrListActivity extends BaseActivity {

    private List<CocosGameInfo> mDatas = new ArrayList<>();
    private MyAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private EditText mEtUserId;
    private String mUserId;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean isLoopSwitchGame;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test_sud_cr;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mEtUserId = findViewById(R.id.et_user_id);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void initData() {
        super.initData();
        mAdapter = new MyAdapter(mDatas);
        mRecyclerView.setAdapter(mAdapter);

        initList();
    }

    private void initList() {
        CocosGameInfo info = new CocosGameInfo();
        info.name = "游戏A";
        info.gameId = "sud.tech.test";
        info.version = "1.0.0";
        info.url = "http://test-runtime.cocos.com/cocos-runtime-demo/cpk/13/game.creator.cccshooter.13.cpk";
        mDatas.add(info);

        info = new CocosGameInfo();
        info.name = "游戏B(引擎分离2.3.1)";
        info.gameId = "game.creator.duang";
        info.version = "2.3.1";
        info.url = "http://test-runtime.cocos.com/cocos-runtime-demo/cpk/13/game.creator.duang-sheep.sp.13.cpk";
        mDatas.add(info);

        info = new CocosGameInfo();
        info.name = "游戏C(引擎分离3.8.7)";
        info.gameId = "game.creator.simple";
        info.version = "3.8.7";
        info.url = "http://test-runtime.cocos.com/cocos-runtime-demo/cpk/13/game.creator3d.simple-games.sp.13.cpk";
        mDatas.add(info);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                startCr(mAdapter.getItem(position));
            }
        });
        mEtUserId.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                mUserId = s == null ? null : s.toString();
            }
        });
        findViewById(R.id.btn_uninit_sdk).setOnClickListener(v -> {
            SudCr.uninitSDK(new ISudCrListenerUninitSDK() {
                @Override
                public void onSuccess() {
                    ToastUtils.showLong("uninitSDK 成功");
                }

                @Override
                public void onFailure(int code, String msg) {
                    ToastUtils.showLong("uninitSDK 失败：(" + code + ")" + msg);
                }
            });
        });
        findViewById(R.id.btn_start_loop).setOnClickListener(v -> {
            isLoopSwitchGame = true;
            startLoopGame();
        });
        findViewById(R.id.btn_stop_loop).setOnClickListener(v -> {
            isLoopSwitchGame = false;
            stopLoopGame();
            ToastUtils.showLong("关闭循环");
        });
    }

    private void startLoopGame() {
        stopLoopGame();
        int second = new Random().nextInt(20) + 1; // 1 - 20秒，随机开关
        ToastUtils.showLong("下一次开关时间:" + second + "秒");
        mHandler.postDelayed(mLoopSwitchGameTask, second * 1000);
    }

    private void stopLoopGame() {
        mHandler.removeCallbacks(mLoopSwitchGameTask);
    }

    private void startCr(CocosGameInfo item) {
        SudCrActivity.start(this, item, mUserId);
    }

    private class MyAdapter extends BaseQuickAdapter<CocosGameInfo, BaseViewHolder> {

        public MyAdapter(@Nullable List<CocosGameInfo> data) {
            super(R.layout.item_name_list, data);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder model, CocosGameInfo info) {
            model.setText(R.id.tv_title, info.name);
        }

    }

    private Runnable mLoopSwitchGameTask = new Runnable() {
        @Override
        public void run() {
            switchGame();
        }
    };

    private void switchGame() {
        if (!isLoopSwitchGame) {
            return;
        }
        Activity topActivity = ActivityUtils.getTopActivity();
        if (topActivity instanceof SudCrActivity) {
            topActivity.finish();
        } else {
            List<CocosGameInfo> list = mAdapter.getData();
            int size = list.size();
            if (size > 0) {
                int position = new Random().nextInt(size);
                CocosGameInfo cocosGameInfo = list.get(position);
                startCr(cocosGameInfo);
            }
        }
        startLoopGame();
    }

}
