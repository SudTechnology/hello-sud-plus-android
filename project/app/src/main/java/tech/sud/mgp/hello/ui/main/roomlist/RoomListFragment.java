package tech.sud.mgp.hello.ui.main.roomlist;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseFragment;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.utils.GlobalSP;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.utils.ResponseUtils;
import tech.sud.mgp.hello.service.main.manager.HomeManager;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.RoomListResp;
import tech.sud.mgp.hello.ui.main.home.CoinDialog;
import tech.sud.mgp.hello.ui.main.home.RoomItemModel;
import tech.sud.mgp.hello.ui.scenes.base.utils.EnterRoomUtils;

public class RoomListFragment extends BaseFragment {

    private EditText searchEt;
    private TextView goSearch, emptyTv;
    private TextView nameTv, useridTv;
    private ImageView headerIv;
    private RecyclerView roomRecyclerView;
    private RoomListAdapter adapter;
    private SwipeRefreshLayout roomRefreshLayout;
    private List<RoomItemModel> datas = new ArrayList<>();

    public RoomListFragment() {
    }

    public static RoomListFragment newInstance() {
        RoomListFragment fragment = new RoomListFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_room;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        searchEt = mRootView.findViewById(R.id.search_et);
        goSearch = mRootView.findViewById(R.id.go_search);
        nameTv = mRootView.findViewById(R.id.name_tv);
        useridTv = mRootView.findViewById(R.id.userid_tv);
        headerIv = mRootView.findViewById(R.id.header_iv);
        roomRecyclerView = mRootView.findViewById(R.id.room_rv);
        roomRefreshLayout = mRootView.findViewById(R.id.room_refresh_layout);
        emptyTv = mRootView.findViewById(R.id.empty_tv);
    }

    @Override
    protected void initData() {
        super.initData();
        adapter = new RoomListAdapter(datas);
        roomRecyclerView.setAdapter(adapter);
        roomRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        nameTv.setText(GlobalSP.getSP().getString(GlobalSP.USER_NAME_KEY, ""));
        String userId = GlobalSP.getSP().getLong(GlobalSP.USER_ID_KEY, 0L) + "";
        useridTv.setText(getString(R.string.setting_userid, userId));
        String header = GlobalSP.getSP().getString(GlobalSP.USER_HEAD_PORTRAIT_KEY, "");
        if (header.isEmpty()) {
            headerIv.setImageResource(R.drawable.icon_logo);
        } else {
            ImageLoader.loadImage(headerIv, header);
        }
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
        adapter.setOnItemClickListener((adapter, view, position) -> {
            EnterRoomUtils.enterRoom(requireContext(), datas.get(position).getRoomId());
        });
        goSearch.setOnClickListener(v -> enterRoom());
        roomRefreshLayout.setOnRefreshListener(this::loadList);

        headerIv.setOnClickListener(v -> {
            CoinDialog dialog = new CoinDialog(requireContext());
            dialog.show();
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

    private void loadList() {
        HomeRepository.roomList(this, new RxCallback<RoomListResp>() {
            @Override
            public void onNext(BaseResponse<RoomListResp> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS) {
                    HomeManager.getInstance().updateRoomList(t.getData());
                    datas.clear();
                    if (t.getData() != null && t.getData().getRoomInfoList() != null) {
                        datas.addAll(t.getData().getRoomInfoList());
                    }
                    adapter.setList(datas);
                    if (datas.size() == 0) {
                        emptyTv.setVisibility(View.VISIBLE);
                    } else {
                        emptyTv.setVisibility(View.GONE);
                    }
                } else {
                    ToastUtils.showShort(ResponseUtils.conver(t));
                }
            }

            @Override
            public void onFinally() {
                super.onFinally();
                if (roomRefreshLayout.isRefreshing()) {
                    roomRefreshLayout.setRefreshing(false);
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