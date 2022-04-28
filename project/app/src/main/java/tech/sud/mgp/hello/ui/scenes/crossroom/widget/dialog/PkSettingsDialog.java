package tech.sud.mgp.hello.ui.scenes.crossroom.widget.dialog;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.ArrayList;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.utils.DensityUtils;

/**
 * pk设置弹窗
 */
public class PkSettingsDialog extends BaseDialogFragment implements View.OnClickListener {

    private TextView tvClosePk;
    private TextView tvStartPk;
    private View viewContainerChange;
    private TextView tvCancel;
    private TextView tvChange;
    private RecyclerView recyclerView;

    private int selectedMinute = 5;
    private SettingsMode settingsMode = SettingsMode.START;
    private final MyAdapter adapter = new MyAdapter();

    private OnSelectedListener onSelectedListener;
    private View.OnClickListener closePkOnClickListener;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_pk_settings;
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
    protected int getHeight() {
        return DensityUtils.dp2px(360);
    }

    @Override
    protected void customStyle(Window window) {
        super.customStyle(window);
        window.setWindowAnimations(R.style.BottomToTopAnim);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        tvClosePk = findViewById(R.id.tv_close_pk);
        tvStartPk = findViewById(R.id.tv_start_pk);
        viewContainerChange = findViewById(R.id.container_change);
        tvCancel = findViewById(R.id.tv_cancel);
        tvChange = findViewById(R.id.tv_change);
        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        switch (settingsMode) {
            case CHANGE:
                viewContainerChange.setVisibility(View.VISIBLE);
                tvStartPk.setVisibility(View.GONE);
                break;
            case START:
                viewContainerChange.setVisibility(View.GONE);
                tvStartPk.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void initData() {
        super.initData();
        ArrayList<Integer> list = new ArrayList<>();
        list.add(5);
        list.add(15);
        list.add(30);
        list.add(60);
        adapter.setList(list);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        tvClosePk.setOnClickListener(this);
        tvStartPk.setOnClickListener(this);
        tvChange.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                selectedMinute = PkSettingsDialog.this.adapter.getItem(position);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }

    /** 设置关闭pk */
    public void setClosePkOnClickListener(View.OnClickListener closePkOnClickListener) {
        this.closePkOnClickListener = closePkOnClickListener;
    }

    /** 设置模式 */
    public void setSettingsMode(SettingsMode settingsMode) {
        this.settingsMode = settingsMode;
    }

    /** 设置当前选中的分钟数 */
    public void setSelectedMinute(int minute) {
        this.selectedMinute = minute;
    }

    @Override
    public void onClick(View v) {
        if (v == tvClosePk) {
            if (closePkOnClickListener != null) {
                closePkOnClickListener.onClick(v);
            }
            dismiss();
        } else if (v == tvStartPk) {
            if (onSelectedListener != null) {
                onSelectedListener.onSelected(selectedMinute, SettingsMode.START);
            }
            dismiss();
        } else if (v == tvChange) {
            if (onSelectedListener != null) {
                onSelectedListener.onSelected(selectedMinute, SettingsMode.START);
            }
            dismiss();
        } else if (v == tvCancel) {
            dismiss();
        }
    }

    public enum SettingsMode {
        CHANGE, // 修改pk时长模式
        START // 开始pk模式
    }

    public interface OnSelectedListener {
        void onSelected(int minute, SettingsMode mode);
    }

    private class MyAdapter extends BaseQuickAdapter<Integer, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.item_choose);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder baseViewHolder, Integer item) {
            // 分割线
            int index = getData().indexOf(item);
            baseViewHolder.setVisible(R.id.view_line, index > 0);

            // 名称
            baseViewHolder.setText(R.id.tv_name, getString(R.string.number_minute, item + ""));

            // 是否选中
            baseViewHolder.setVisible(R.id.view_selected, item == selectedMinute);
        }
    }

}
