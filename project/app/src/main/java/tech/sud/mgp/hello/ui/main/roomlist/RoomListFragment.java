package tech.sud.mgp.hello.ui.main.roomlist;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseFragment;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.service.main.manager.HomeManager;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.RoomListResp;
import tech.sud.mgp.hello.ui.scenes.base.utils.EnterRoomUtils;

public class RoomListFragment extends BaseFragment {

    private TextView emptyTv;
    private RecyclerView roomRecyclerView;
    private RoomListAdapter adapter;
    private SmartRefreshLayout refreshLayout;

    public static RoomListFragment newInstance() {
        return new RoomListFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_room_list;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        roomRecyclerView = mRootView.findViewById(R.id.room_rv);
        refreshLayout = mRootView.findViewById(R.id.room_refresh_layout);
        emptyTv = mRootView.findViewById(R.id.empty_tv);
        refreshLayout.setEnableRefresh(true);
        refreshLayout.setEnableLoadMore(false);
    }

    @Override
    protected void initData() {
        super.initData();
        adapter = new RoomListAdapter();
        roomRecyclerView.setAdapter(adapter);
        roomRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        adapter.setOnItemClickListener((adapter, view, position) -> {
            EnterRoomUtils.enterRoom(requireContext(), this.adapter.getItem(position).getRoomId());
        });
        refreshLayout.setOnRefreshListener(refreshLayout -> loadList());
    }

    private void loadList() {
        HomeRepository.roomList(this, null, new RxCallback<RoomListResp>() {
            @Override
            public void onNext(BaseResponse<RoomListResp> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS) {
                    HomeManager.getInstance().roomListResp = t.getData();
                    RoomListResp data = t.getData();
                    if (data == null) {
                        adapter.setList(null);
                    } else {
                        adapter.setList(data.getRoomInfoList());
                    }
                    if (adapter.getData().size() == 0) {
                        emptyTv.setVisibility(View.VISIBLE);
                    } else {
                        emptyTv.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFinally() {
                super.onFinally();
                if (refreshLayout.isRefreshing()) {
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadMore();
                }
            }

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadList();
    }

}