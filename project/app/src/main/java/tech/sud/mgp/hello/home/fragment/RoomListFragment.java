package tech.sud.mgp.hello.home.fragment;

import tech.sud.mgp.common.base.BaseFragment;
import tech.sud.mgp.hello.R;

public class RoomListFragment extends BaseFragment {

    public RoomListFragment() { }

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
    }

    @Override
    protected void setListeners() {
        super.setListeners();
    }
}