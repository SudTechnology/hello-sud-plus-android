package tech.sud.mgp.hello.ui.main.settings.activity;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.common.utils.GlobalCache;
import tech.sud.mgp.hello.service.main.config.SudEnvConfig;
import tech.sud.mgp.hello.ui.login.DeveloperKitUtils;

/**
 * 切换 sud 环境
 */
public class ChangeSudEnvActivity extends BaseActivity {

    private final MyAdapter adapter = new MyAdapter();

    // 数据返回
    public final MutableLiveData<List<SudEnvConfig>> datasLiveData = new MutableLiveData<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_sud_env;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(null);
    }

    @Override
    protected void initData() {
        super.initData();
        getDatas();
    }

    // 获取数据
    public void getDatas() {
        List<SudEnvConfig> list = DeveloperKitUtils.getSudEnvConfigs();
        datasLiveData.setValue(list);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        datasLiveData.observe(this, new Observer<List<SudEnvConfig>>() {
            @Override
            public void onChanged(List<SudEnvConfig> baseRtcConfigs) {
                adapter.setList(baseRtcConfigs);
            }
        });
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                clickRtc(position);
            }
        });
    }

    private void clickRtc(int position) {
        SudEnvConfig item = adapter.getItem(position);
        AppData.getInstance().setSudEnvConfig(item);
        GlobalCache.getInstance().put(GlobalCache.SUD_ENV_CONFIG, item);
        adapter.notifyDataSetChanged();
    }

    public class MyAdapter extends BaseQuickAdapter<SudEnvConfig, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.item_option);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, SudEnvConfig model) {
            // 分割线
            int index = holder.getAdapterPosition();
            holder.setVisible(R.id.view_line, index > 0);

            // 名称
            holder.setText(R.id.tv_name, model.name);

            // 是否选中
            holder.setVisible(R.id.view_selected, model.equals(AppData.getInstance().getSudEnvConfig()));
        }
    }

}
