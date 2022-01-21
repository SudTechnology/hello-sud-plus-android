package tech.sud.mgp.hello.home.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import tech.sud.mgp.hello.R;

public class RoomListFragment extends Fragment {

    public RoomListFragment() { }

    public static RoomListFragment newInstance() {
        RoomListFragment fragment = new RoomListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_index, container, false);
    }
}