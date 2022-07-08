package tech.sud.mgp.hello.ui.common.widget;

import androidx.viewpager2.widget.ViewPager2;

import net.lucode.hackware.magicindicator.MagicIndicator;

public class ViewPager2Helper {

    public static void bind(MagicIndicator magicIndicator, ViewPager2 viewPager2) {
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                magicIndicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                magicIndicator.onPageScrollStateChanged(state);
            }
        });
    }

}
