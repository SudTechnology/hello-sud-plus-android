package tech.sud.mgp.hello.common.base;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public abstract class BaseFragmentStateAdapter<T> extends FragmentStateAdapter {

    private List<T> datas;

    public BaseFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public BaseFragmentStateAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public BaseFragmentStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    public void setDatas(List<T> list) {
        datas = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (datas != null) {
            return datas.size();
        }
        return 0;
    }
}
