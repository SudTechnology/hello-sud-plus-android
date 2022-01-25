package tech.sud.mgp.hello.home.fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.common.base.BaseFragment;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.home.manager.HomeManager;
import tech.sud.mgp.hello.home.adapter.RoomListAdapter;
import tech.sud.mgp.hello.home.model.RoomItemModel;
import tech.sud.mgp.hello.utils.AppSharedPreferences;
import tech.sud.mgp.hello.utils.GlideImageLoader;

public class RoomListFragment extends BaseFragment {

    private EditText searchEt;
    private ImageView goSearch;
    private TextView nameTv, useridTv;
    private ImageView headerIv;
    private RecyclerView roomRecyclerView;
    private RoomListAdapter adapter;
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
    }

    @Override
    protected void initData() {
        super.initData();
        datas.addAll(HomeManager.getInstance().testCreatRoom());
        adapter = new RoomListAdapter(datas);
        roomRecyclerView.setAdapter(adapter);
        roomRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        nameTv.setText(AppSharedPreferences.getSP().getString(AppSharedPreferences.USER_NAME_KEY, ""));
        String userId = AppSharedPreferences.getSP().getLong(AppSharedPreferences.USER_ID_KEY, 0L) + "";
        useridTv.setText(userId);
        String header = AppSharedPreferences.getSP().getString(AppSharedPreferences.USER_HEAD_PORTRAIT_KEY, "");
        if (header.isEmpty()) {
            headerIv.setImageResource(R.mipmap.icon_logo);
        } else {
            GlideImageLoader.loadImage(headerIv, header);
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

            }
            return false;
        });
        adapter.setOnItemClickListener((adapter, view, position) -> {

        });
    }

}