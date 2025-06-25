package tech.sud.mgp.hello.common.widget.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialog;

/**
 * 底部弹出的按钮弹窗
 */
public class BottomItemOptionDialog extends BaseDialog {

    private RecyclerView recyclerView;

    private MyAdapter adapter = new MyAdapter();

    public BottomItemOptionDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_bottom_item_option;
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
    protected void initWidget() {
        super.initWidget();
        recyclerView = mRootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
    }

    public void setList(List<ItemModel> list) {
        adapter.setList(list);
    }

    public void addItem(ItemModel model) {
        adapter.addData(model);
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
                ItemModel item = BottomItemOptionDialog.this.adapter.getItem(position);
                listener.onItemClick(item);
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(ItemModel model);
    }

    private static class MyAdapter extends BaseQuickAdapter<ItemModel, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.item_bottom_item_option);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder baseViewHolder, ItemModel model) {
            baseViewHolder.setText(R.id.tv_info, model.info);
            baseViewHolder.setVisible(R.id.view_selected, model.isSelected);
        }
    }

    public static class ItemModel {
        public String info;
        public boolean isSelected;
    }

}
