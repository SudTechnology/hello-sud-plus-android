package tech.sud.mgp.hello.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.home.fragment.IndexFragment;
import tech.sud.mgp.hello.home.fragment.RoomListFragment;
import tech.sud.mgp.hello.home.fragment.SetFragment;

public class HomeActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private List<String> tabs = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tabs.add(getString(R.string.tabs_index));
        tabs.add(getString(R.string.tabs_room));
        tabs.add(getString(R.string.tabs_set));
        viewPager = findViewById(R.id.viewPager);
        HomeTabAdapter adapter = new HomeTabAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(tabs.size());
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    class HomeTabAdapter extends FragmentStateAdapter {
        public HomeTabAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: {
                    return IndexFragment.newInstance();
                }
                case 1: {
                    return RoomListFragment.newInstance();
                }
                default: {
                    return SetFragment.newInstance();
                }
            }
        }

        @Override
        public int getItemCount() {
            return tabs.size();
        }
    }
}