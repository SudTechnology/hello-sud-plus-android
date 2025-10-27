package tech.sud.mgp.hello.ui.scenes.cr;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.service.room.resp.CocosGameInfo;

public class TestSudCrListActivity extends BaseActivity {

    private List<CocosGameInfo> mDatas = new ArrayList<>();
    private MyAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test_sud_cr;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
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
    }

    private void startCr(CocosGameInfo item) {
        SudCrActivity.start(this, item);
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

}
