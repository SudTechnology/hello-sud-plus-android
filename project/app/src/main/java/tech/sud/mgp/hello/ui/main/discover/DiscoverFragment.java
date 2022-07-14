package tech.sud.mgp.hello.ui.main.discover;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseFragment;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.ui.common.widget.EmptyDataView;
import tech.sud.mgp.hello.ui.common.widget.refresh.RefreshDataHelper;
import tech.sud.mgp.hello.ui.common.widget.refresh.RefreshView;
import tech.sud.mgp.hello.ui.main.home.view.CoinDialog;
import tech.sud.mgp.hello.ui.scenes.base.utils.EnterRoomUtils;

public class DiscoverFragment extends BaseFragment {

    private EditText searchEt;
    private TextView goSearch;
    private TextView nameTv, useridTv;
    private ImageView headerIv;
    private RefreshView refreshView;
    private RefreshDataHelper<DiscoverRoomModel> refreshDataHelper;
    private DiscoverRoomAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_discover;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        searchEt = mRootView.findViewById(R.id.search_et);
        goSearch = mRootView.findViewById(R.id.go_search);
        nameTv = mRootView.findViewById(R.id.name_tv);
        useridTv = mRootView.findViewById(R.id.userid_tv);
        headerIv = mRootView.findViewById(R.id.header_iv);
        refreshView = mRootView.findViewById(R.id.refresh_view);
        initRefreshDataHelper();
        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = refreshView.getRecyclerView();
        int paddingHorizontal = DensityUtils.dp2px(9);
        recyclerView.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);
    }

    private void initRefreshDataHelper() {
        adapter = new DiscoverRoomAdapter();
        refreshDataHelper = new RefreshDataHelper<DiscoverRoomModel>() {
            @Override
            protected RefreshView getRefreshView() {
                return refreshView;
            }

            @Override
            protected RecyclerView.LayoutManager getLayoutManager() {
                return new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false);
            }

            @Override
            protected BaseQuickAdapter<DiscoverRoomModel, BaseViewHolder> getAdapter() {
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
//                view.setText(getString(R.string.empty_room_match));
                return view;
            }
        };
    }

    /** 获取数据 */
    private void getRoomList(int pageNumber, int pageSize) {
        // TODO: 2022/7/14 需要数据
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        nameTv.setText(HSUserInfo.nickName);
        useridTv.setText(getString(R.string.setting_userid, HSUserInfo.userId + ""));
        ImageLoader.loadImage(headerIv, HSUserInfo.avatar);
        searchEt.setOnFocusChangeListener((v, hasFocus) -> {
            String keyword = searchEt.getText().toString();
            if (keyword.length() > 0) {
                goSearch.setVisibility(View.VISIBLE);
            } else {
                goSearch.setVisibility(View.GONE);
            }
        });
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    goSearch.setVisibility(View.VISIBLE);
                } else {
                    goSearch.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchEt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                enterRoom();
            }
            return false;
        });
        goSearch.setOnClickListener(v -> enterRoom());
        headerIv.setOnClickListener(v -> {
            new CoinDialog().show(getChildFragmentManager(), null);
        });
    }

    private void enterRoom() {
        try {
            String roomIdString = searchEt.getText().toString().trim();
            if (!TextUtils.isEmpty(roomIdString)) {
                long roomId = Long.parseLong(roomIdString);
                EnterRoomUtils.enterRoom(requireContext(), roomId);
            }
            KeyboardUtils.hideSoftInput(searchEt);
            searchEt.setText("");
            searchEt.clearFocus();
        } catch (Exception e) {
            ToastUtils.showShort(getString(R.string.search_room_error));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshDataHelper.initData();
    }

}