package tech.sud.mgp.hello.ui.scenes.custom.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialog;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.ui.scenes.custom.model.ConfigItemModel;

/**
 * 配置选项弹窗
 */
public class CustomConfigSelectDialog extends BaseDialog {

    private RecyclerView selectRv;
    private SelectedAdapter adapter = new SelectedAdapter();
    private List<ConfigItemModel.OptionListBean> datas;
    private SelectedListener listener;

    public CustomConfigSelectDialog(@NonNull Context context, List<ConfigItemModel.OptionListBean> datas, SelectedListener listener) {
        super(context);
        this.datas = datas;
        this.listener = listener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_custom_config_select;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        selectRv = mRootView.findViewById(R.id.select_rv);
        adapter.setList(datas);
        selectRv.setLayoutManager(new LinearLayoutManager(getContext()));
        selectRv.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (listener != null) {
                    listener.selected(position);
                }
                dismiss();
            }
        });
    }

    @Override
    protected int getWidth() {
        return DensityUtils.getAppScreenWidth();
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    public interface SelectedListener {
        void selected(int position);
    }

    public static class SelectedAdapter extends BaseQuickAdapter<ConfigItemModel.OptionListBean, BaseViewHolder> {

        public SelectedAdapter() {
            super(R.layout.item_selected_custom_config);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder baseViewHolder, ConfigItemModel.OptionListBean optionListBean) {
            ImageView stateIv = baseViewHolder.getView(R.id.state_iv);
            baseViewHolder.setText(R.id.title_tv, optionListBean.title);
            if (optionListBean.isSeleted) {
                stateIv.setVisibility(View.VISIBLE);
            } else {
                stateIv.setVisibility(View.INVISIBLE);
            }
        }
    }

}
