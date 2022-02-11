package tech.sud.mgp.audio.example.widget.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.audio.R;
import tech.sud.mgp.common.base.BaseDialog;

/**
 * 底部弹出的按钮弹窗
 */
public class BottomOptionDialog extends BaseDialog {

    private RecyclerView recyclerView;
    private TextView tvCancel;

    private MyAdapter adapter = new MyAdapter();

    public BottomOptionDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.audio_dialog_bottom_option;
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected int getWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    /**
     * 添加一个选项
     *
     * @param key
     * @param name
     */
    public void addOption(int key, String name) {
        BottomOptionModel model = new BottomOptionModel();
        model.key = key;
        model.name = name;
        adapter.addData(model);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        recyclerView = mRootView.findViewById(R.id.recycler_view);
        tvCancel = mRootView.findViewById(R.id.tv_cancel);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /**
     * 设置点击按钮监听
     *
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        adapter.setOnItemClickListener(new com.chad.library.adapter.base.listener.OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                BottomOptionModel item = BottomOptionDialog.this.adapter.getItem(position);
                listener.onItemClick(item);
            }
        });
    }

    /**
     * 菜单模型
     */
    public static class BottomOptionModel {
        public int key; // 用于标记
        public String name; // 展示的名称
    }

    public interface OnItemClickListener {
        void onItemClick(BottomOptionModel model);
    }

    private static class MyAdapter extends BaseQuickAdapter<BottomOptionModel, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.audio_item_bottom_option);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder baseViewHolder, BottomOptionModel bottomOptionModel) {
            baseViewHolder.setText(R.id.item_bottom_option_tv_name, bottomOptionModel.name);
        }
    }

}
