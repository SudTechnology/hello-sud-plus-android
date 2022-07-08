package tech.sud.mgp.hello.ui.common.widget.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;

/**
 * Description:自定义的列表控件，带下拉刷新及上拉加载更多的功能
 */
public class RefreshView extends SmartRefreshLayout {

    private RecyclerView recyclerView;

    public RefreshView(Context context) {
        this(context, null);
    }

    public RefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr();
        initHeadView();
        initRecyclerView();
    }

    private void initAttr() {
        setEnableOverScrollDrag(true); // 采用越界拖动
    }

    private void initHeadView() {
        CustomHeaderView headerView = new CustomHeaderView(getContext());
        addView(headerView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void initRecyclerView() {
        recyclerView = new RecyclerView(getContext());
        recyclerView.setClipToPadding(false);
        recyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
        RecyclerView.ItemAnimator itemAnimator = recyclerView.getItemAnimator();
        if (itemAnimator != null) {
            itemAnimator.setChangeDuration(0);
        }
        setRefreshContent(recyclerView);
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
}
