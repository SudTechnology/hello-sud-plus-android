package tech.sud.mgp.hello.ui.scenes.crossroom.widget.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.RoomListResp;
import tech.sud.mgp.hello.ui.common.widget.EmptyDataView;
import tech.sud.mgp.hello.ui.common.widget.refresh.RefreshDataHelper;
import tech.sud.mgp.hello.ui.common.widget.refresh.RefreshView;
import tech.sud.mgp.hello.ui.main.base.constant.SceneType;
import tech.sud.mgp.hello.ui.main.home.model.RoomItemModel;
import tech.sud.mgp.hello.ui.main.roomlist.RoomListAdapter;

/**
 * 邀请pk弹窗
 */
public class InvitePkDialog extends BaseDialogFragment {

    private RefreshView refreshView;
    private RefreshDataHelper<RoomItemModel> refreshDataHelper;
    private final RoomListAdapter adapter = new RoomListAdapter(R.string.invite_pk);
    private OnSelectedRoomListener listener;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_invite_pk;
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
        return DensityUtils.dp2px(388);
    }

    @Override
    protected void customStyle(Window window) {
        super.customStyle(window);
        window.setWindowAnimations(R.style.BottomToTopAnim);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        refreshView = findViewById(R.id.refresh_view);
        int paddingHorizontal = DensityUtils.dp2px(requireContext(), 16);
        refreshView.getRecyclerView().setPadding(paddingHorizontal, 0, paddingHorizontal, 0);
        initRefreshDataHelper();
    }

    private void initRefreshDataHelper() {
        refreshDataHelper = new RefreshDataHelper<RoomItemModel>() {
            @Override
            protected RefreshView getRefreshView() {
                return refreshView;
            }

            @Override
            protected RecyclerView.LayoutManager getLayoutManager() {
                return new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            }

            @Override
            protected BaseQuickAdapter<RoomItemModel, BaseViewHolder> getAdapter() {
                return adapter;
            }

            @Override
            protected GetDataListener getDataListener() {
                return new GetDataListener() {
                    @Override
                    public void onGetData(int pageNumber, int pageSize) {
                        getRoomList(pageNumber, pageSize);
                    }
                };
            }

            @Override
            protected View getEmptyView() {
                Context context = getContext();
                if (context == null) return null;
                EmptyDataView view = new EmptyDataView(context);
                view.setText(getString(R.string.empty_room_match));
                return view;
            }
        };
        refreshDataHelper.setPageSize(Integer.MAX_VALUE);
    }

    @Override
    protected void initData() {
        super.initData();
        refreshDataHelper.initData();
    }

    private void getRoomList(int pageNumber, int pageSize) {
        HomeRepository.roomList(this, SceneType.CROSS_ROOM, new RxCallback<RoomListResp>() {
            @Override
            public void onNext(BaseResponse<RoomListResp> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS) {
                    RoomListResp data = t.getData();
                    if (data == null) {
                        refreshDataHelper.noPagingRespDatasSuccess(null);
                    } else {
                        refreshDataHelper.noPagingRespDatasSuccess(data.getRoomInfoList());
                    }
                } else {
                    refreshDataHelper.noPagingRespFailed();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                refreshDataHelper.noPagingRespFailed();
            }
        });
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (listener != null) {
                    listener.onSelected(InvitePkDialog.this.adapter.getItem(position));
                }
            }
        });
    }

    /** 设置选择房间的监听器 */
    public void setOnSelectedRoomListener(OnSelectedRoomListener listener) {
        this.listener = listener;
    }

    public interface OnSelectedRoomListener {
        void onSelected(RoomItemModel model);
    }

}
