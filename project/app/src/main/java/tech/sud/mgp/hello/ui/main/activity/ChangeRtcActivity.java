package tech.sud.mgp.hello.ui.main.activity;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.ui.main.model.ChangeRtcViewModel;
import tech.sud.mgp.hello.ui.main.model.config.BaseRtcConfig;

/**
 * 切换rtc服务商页面
 */
public class ChangeRtcActivity extends BaseActivity {

    private final MyAdapter adapter = new MyAdapter();
    private final ChangeRtcViewModel viewModel = new ChangeRtcViewModel();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_rtc;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        super.initData();
        viewModel.getRtcList();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        viewModel.rtcDatasLiveData.observe(this, new Observer<List<BaseRtcConfig>>() {
            @Override
            public void onChanged(List<BaseRtcConfig> baseRtcConfigs) {
                adapter.setList(baseRtcConfigs);
            }
        });
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                BaseRtcConfig item = ChangeRtcActivity.this.adapter.getItem(position);
                viewModel.setRtcConfig(item);
                adapter.notifyDataSetChanged();
            }
        });
        findViewById(R.id.view_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public class MyAdapter extends BaseQuickAdapter<BaseRtcConfig, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.item_change_rtc);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder baseViewHolder, BaseRtcConfig baseRtcConfig) {
            // 分割线
            int index = getData().indexOf(baseRtcConfig);
            baseViewHolder.setVisible(R.id.view_line, index > 0);

            // 名称
            baseViewHolder.setText(R.id.tv_name, baseRtcConfig.desc);

            // 是否选中
            baseViewHolder.setVisible(R.id.view_selected, viewModel.isSelectRtcConfig(baseRtcConfig));
        }
    }

}
