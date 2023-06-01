package tech.sud.mgp.hello.ui.main.settings.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.BuildConfig;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;

public class DebugActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private Myadapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_debug;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        recyclerView = findViewById(R.id.recycler_view);
    }

    @Override
    protected void initData() {
        super.initData();
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new Myadapter();
        recyclerView.setAdapter(adapter);

        initInfo();
    }

    private void initInfo() {
        List<String> list = new ArrayList<>();
        list.add("APPLICATION_ID：" + BuildConfig.APPLICATION_ID);
        list.add("BuildType：" + BuildConfig.BUILD_TYPE);
        list.add("FLAVOR：" + BuildConfig.FLAVOR);
        list.add("VERSION_CODE：" + BuildConfig.VERSION_CODE);
        list.add("VERSION_NAME：" + BuildConfig.VERSION_NAME);
        list.add("CHANNEL：" + BuildConfig.CHANNEL);
        list.add("baseUrl：" + BuildConfig.baseUrl);
        list.add("baseUrlConfig：" + BuildConfig.baseUrlConfig);
        list.add("buglyAppId：" + BuildConfig.buglyAppId);
        list.add("gameBaseUrl：" + BuildConfig.gameBaseUrl);
        list.add("gameIsTestEnv：" + BuildConfig.gameIsTestEnv);
        list.add("interactBaseUrl：" + BuildConfig.interactBaseUrl);
        list.add("mgpEnv：" + BuildConfig.mgpEnv);
        list.add("nftEnv：" + BuildConfig.nftEnv);
        list.add("nftSwitch：" + BuildConfig.nftSwitch);
        adapter.setList(list);
    }

    private class Myadapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public Myadapter() {
            super(R.layout.item_text);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, String s) {
            holder.setText(R.id.tv_info, s);
        }
    }

}
