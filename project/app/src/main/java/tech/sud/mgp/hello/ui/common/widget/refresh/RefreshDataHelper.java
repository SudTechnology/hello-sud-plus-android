package tech.sud.mgp.hello.ui.common.widget.refresh;

import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.List;

import tech.sud.mgp.hello.app.APPConfig;

/**
 * Description:配合{@see DTRefreshView}使用
 * 用于设置数据，及用户刷新或加载更多时触发获取数据回调
 */
public abstract class RefreshDataHelper<T> {

    private BaseQuickAdapter<T, BaseViewHolder> mAdapter;
    private int mPageSize = APPConfig.GLOBAL_PAGE_SIZE;
    private RefreshView mRefreshView;
    private boolean haveSetEmptyView = false; // 标记是否已经设置了emptyView
    private boolean isUseEmpty = false; // 标识是否使用空布局
    private StatusView mErrorView; // 发生错误时，展示的View
    private Integer mCurPageNumber; // IGNORE_PAGE_SIZE时，记录当前页码
    private boolean loadMoreEnable = true; // 设置是否可以加载更多
    private int firstPageNumber = 1; // 第一页的页码
    private RefreshDataModel refreshModel = RefreshDataModel.DEFAULT; // 数据刷新模式

    public RefreshDataHelper() {
        bindingRefreshView();
    }

    /** 设置刷新模式 */
    public void setRefreshDataModel(RefreshDataModel refreshModel) {
        this.refreshModel = refreshModel;
        if (refreshModel == RefreshDataModel.IGNORE_PAGE_SIZE) {
            mCurPageNumber = firstPageNumber;
        }
    }

    /**
     * 设置分页时，每页的大小。
     * 可以通过设置pageSize = Integer.MAX_VALUE 。这样将不会触发加载更多，达到一次性拉取所有数据的目的
     */
    public void setPageSize(int pageSize) {
        mPageSize = pageSize;
    }

    private void bindingRefreshView() {
        mRefreshView = getRefreshView();
        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        mAdapter = getAdapter();
        GetDataListener subscription = getDataListener();
        mRefreshView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                subscription.onGetData(firstPageNumber, mPageSize);
            }
        });
        mRefreshView.getRecyclerView().setLayoutManager(layoutManager);
        mRefreshView.getRecyclerView().setAdapter(mAdapter);

        mAdapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                subscription.onGetData(getLoadMorePageNumber(), mPageSize);
            }
        });
    }

    /**
     * 成功获取数据后，调用此方法进行刷新，这是不需要分页(一次拿全部数据)时调用的方法
     */
    public void noPagingRespDatasSuccess(List<T> datas) {
        respDatasSuccess(firstPageNumber, mPageSize, datas);
    }

    /** 成功获取数据后，调用此方法进行刷新。分页数据 */
    public void respDatasSuccess(ListModel<T> listModel) {
        int pageSize;
        if (refreshModel == RefreshDataModel.IGNORE_PAGE_SIZE) {
            pageSize = 1;
        } else if (listModel == null) {
            pageSize = mPageSize;
        } else {
            pageSize = listModel.pageSize;
        }
        int pageNumber;
        List<T> datas;
        if (listModel == null) {
            pageNumber = firstPageNumber;
            datas = null;
        } else {
            pageNumber = listModel.pageNumber;
            datas = listModel.datas;
        }
        respDatasSuccess(pageNumber, pageSize, datas);
    }

    private void respDatasSuccess(int pageNumber, int pageSize, List<T> datas) {
        RecyclerView recyclerView = mRefreshView.getRecyclerView();
        if (recyclerView == null) return;
        BaseQuickAdapter<T, BaseViewHolder> adapter = mAdapter;
        int backSize;
        if (datas == null) {
            backSize = 0;
        } else {
            backSize = datas.size();
        }
        if (pageNumber == firstPageNumber) {
            if (mErrorView != null) {
                mErrorView = null;
            }
            if (recyclerView.isComputingLayout()) {
                recyclerView.post(() -> {
                    firstSetList(adapter, backSize, pageSize, datas);
                });
            } else {
                firstSetList(adapter, backSize, pageSize, datas);
            }
        } else {
            if (pageNumber != getLoadMorePageNumber()) return;
            if (datas == null) return;
            if (recyclerView.isComputingLayout()) {
                recyclerView.post(() -> {
                    loadMoreAddData(adapter, recyclerView, pageNumber, backSize, pageSize, datas);
                });
            } else {
                loadMoreAddData(adapter, recyclerView, pageNumber, backSize, pageSize, datas);
            }
        }
    }

    private void loadMoreAddData(BaseQuickAdapter<T, BaseViewHolder> adapter, RecyclerView recyclerView,
                                 int pageNumber, int backSize, int pageSize, List<T> backDatas) {
        List<T> data = adapter.getData();
        switch (refreshModel) {
            case DEFAULT:
                int freeSize;
                if (pageSize > 0) {
                    freeSize = data.size() % pageSize;
                } else {
                    freeSize = 0;
                }
                if (freeSize > 0) { // 处理非完整页数据的加载，比如每页加载30条数据，当前已持有29条数据的加载更多
                    if (backSize > freeSize) {
                        adapter.addData(backDatas.subList(freeSize, backSize));
                    }
                } else {
                    adapter.addData(backDatas);
                }
                if (backSize < pageSize) {
                    adapter.getLoadMoreModule().loadMoreEnd();
                } else {
                    adapter.getLoadMoreModule().loadMoreComplete();
                }
                break;
            case IGNORE_PAGE_SIZE:
                adapter.addData(backDatas);
                if (mCurPageNumber != null) {
                    mCurPageNumber = mCurPageNumber + 1;
                }
                if (backSize < pageSize) {
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            boolean isFullScreen = isFullScreen(recyclerView.getLayoutManager(), adapter);
                            adapter.getLoadMoreModule().loadMoreEnd(!isFullScreen);
                        }
                    }, 50);
                } else {
                    adapter.getLoadMoreModule().loadMoreComplete();
                }
                break;
        }
    }

    private boolean isFullScreen(RecyclerView.LayoutManager manager, BaseQuickAdapter<T, BaseViewHolder> adapter) {
        if (manager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) manager;
            if ((linearLayoutManager.findLastCompletelyVisibleItemPosition() + 1) != adapter.getItemCount() ||
                    linearLayoutManager.findFirstCompletelyVisibleItemPosition() != 0
            ) {
                return true;
            }
        } else if (manager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) manager;
            int[] positions = new int[staggeredGridLayoutManager.getSpanCount()];
            staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(positions);
            int pos = getTheBiggestNumber(positions) + 1;
            if (pos != adapter.getItemCount()) {
                return true;
            }
        }
        return false;
    }

    private int getTheBiggestNumber(int[] numbers) {
        int tmp = -1;
        if (numbers == null || numbers.length == 0) {
            return tmp;
        }
        for (int number : numbers) {
            if (number > tmp) {
                tmp = number;
            }
        }
        return tmp;
    }

    private void firstSetList(BaseQuickAdapter<T, BaseViewHolder> adapter, int backSize, int pageSize, List<T> datas) {
        adapter.setList(datas);
        if (loadMoreEnable) {
            adapter.getLoadMoreModule().setEnableLoadMore(backSize >= pageSize);
        } else {
            adapter.getLoadMoreModule().setEnableLoadMore(false);
        }
        mRefreshView.finishRefresh();
        if (isUseEmpty && !haveSetEmptyView) {
            View emptyView = getEmptyView();
            if (emptyView != null) {
                haveSetEmptyView = true;
                adapter.setEmptyView(emptyView);
            }
        }
        if (refreshModel == RefreshDataModel.IGNORE_PAGE_SIZE) {
            mCurPageNumber = firstPageNumber;
        }
    }

    /** 可以重写此方法，返回在数据为空时的一个view */
    protected View getEmptyView() {
        return null;
    }

    /** 设置是否使用空布局 */
    public void setUseEmpty(boolean useEmpty) {
        this.isUseEmpty = useEmpty;
        if (mAdapter != null) {
            mAdapter.setUseEmpty(useEmpty);
        }
        if (!useEmpty) {
            if (mAdapter != null) {
                FrameLayout emptyLayout = mAdapter.getEmptyLayout();
                if (emptyLayout != null) {
                    emptyLayout.removeAllViews();
                }
            }
            haveSetEmptyView = false;
        }
    }

    /** 获取数据失败，调用此方法，不使用分页的情况下 */
    public void noPagingRespFailed() {
        respDatasFailed(firstPageNumber);
    }

    /** 获取数据失败，调用此方法，使用分页的情况下 */
    public void respDatasFailed(ListModel<T> listModel) {
        int pageNumber;
        if (listModel == null) {
            pageNumber = firstPageNumber;
        } else {
            pageNumber = listModel.pageNumber;
        }
        respDatasFailed(pageNumber);
    }

    private void respDatasFailed(int pageNumber) {
        BaseQuickAdapter<T, BaseViewHolder> adapter = mAdapter;
        if (adapter == null) return;
        if (pageNumber == firstPageNumber) {
            mRefreshView.finishRefresh(false);
            //处理错误的View
            if (mErrorView == null) {
                StatusView errorView = getErrorView();
                if (errorView != null) {
                    adapter.setList(null);
                    adapter.setEmptyView(errorView);
                    haveSetEmptyView = false;
                    errorView.setRefreshListener(new StatusView.RefreshListener() {
                        @Override
                        public void onRefresh() {
                            errorView.setRefreshEnable(false);
                            initData();
                        }
                    });
                    mErrorView = errorView;
                }
            } else {
                mErrorView.setRefreshEnable(true);
            }
        } else {
            if (pageNumber != getLoadMorePageNumber()) return;
            adapter.getLoadMoreModule().loadMoreFail();
        }
    }

    /** 初始化数据，会回调数据获取方法，拿到第一页的数据 */
    public void initData() {
        getDataListener().onGetData(firstPageNumber, mPageSize);
    }

    /** 是否正在刷新，也可以相当于说是否是用户手机刷新中 */
    public boolean isRefreshing() {
        if (mRefreshView == null) {
            return false;
        }
        return mRefreshView.isRefreshing();
    }

    /** 获取加载更多传递的页码 */
    public int getLoadMorePageNumber() {
        switch (refreshModel) {
            case IGNORE_PAGE_SIZE:
                return getCurrentPageNumber() + 1;
            default:
                return getCurrentPageNumber();
        }
    }

    /** 获取当前的页码 */
    private int getCurrentPageNumber() {
        switch (refreshModel) {
            case DEFAULT:
                if (mAdapter != null) {
                    return calcPageNumber(mAdapter);
                }
                break;
            case IGNORE_PAGE_SIZE:
                if (mCurPageNumber == null) {
                    return firstPageNumber;
                } else {
                    return mCurPageNumber;
                }
        }
        return 0;
    }

    /** 通过Adapter里面的数据，计算当前的页码，可覆写 */
    public int calcPageNumber(BaseQuickAdapter<T, BaseViewHolder> adapter) {
        return adapter.getData().size() / mPageSize + 1;
    }

    /** 可以重写此方法，返回在数据获取失败时展示的view */
    public StatusView getErrorView() {
        return null;
    }

    protected abstract RefreshView getRefreshView();

    protected abstract RecyclerView.LayoutManager getLayoutManager();

    protected abstract BaseQuickAdapter<T, BaseViewHolder> getAdapter();

    /**
     * 回调此方法时，表明需要数据
     * 数据返回成功或者不成功调用responseDatasSuccess或者responseDatasFailed方法
     */
    protected abstract GetDataListener getDataListener();

    public interface GetDataListener {
        void onGetData(int pageNumber, int pageSize);
    }

    public enum RefreshDataModel {
        /** 默认分页,pageNubmer及pageSize按正常计算 */
        DEFAULT,

        /**
         * 忽略pageSize，加载更多时页码加1。
         * 通俗点来讲就是，当前页只要有一条数据返回，那么都会触发加载更多。
         * 而{DEFAULT}模式下，如果pagwSize = 30，那么只返回了一条数据，那么后续将不会继续加载更多，也就是执行"到底了"的逻辑
         */
        IGNORE_PAGE_SIZE,
    }

}
