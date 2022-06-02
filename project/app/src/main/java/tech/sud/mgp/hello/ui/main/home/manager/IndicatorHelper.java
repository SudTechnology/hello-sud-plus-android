package tech.sud.mgp.hello.ui.main.home.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.LogUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView;

import java.util.List;

import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.service.main.resp.SceneModel;
import tech.sud.mgp.hello.ui.main.home.view.HomeRoomTypeView;
import tech.sud.mgp.hello.ui.main.home.view.NewNestedScrollView;

/**
 * 管理indicator和Scrollview的联动效果
 */
public class IndicatorHelper {

    private MagicIndicator indicator;
    private List<SceneModel> sceneList;
    private NewNestedScrollView nestedScrollView;
    private int nestedScrollViewTop;//NestScrollview到顶部的距离
    private int selectedIndex = 0;
    private int optionType = 1; // 1用户点击，2用户滚动

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public IndicatorHelper(MagicIndicator indicator, List<SceneModel> sceneList, NewNestedScrollView nestedScrollView) {
        this.indicator = indicator;
        this.sceneList = sceneList;
        this.nestedScrollView = nestedScrollView;
    }

    /** 绑定：点击indicator，滚动NestScrollview */
    public IndicatorHelper init(Context context) {
        CommonNavigator navigator = new CommonNavigator(context);
        navigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return sceneList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                SceneModel model = sceneList.get(index);
                CommonPagerTitleView pagerTitleView = new CommonPagerTitleView(context);
                ColorTransitionPagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setText(model.sceneName);

                simplePagerTitleView.setTextSize(16);
                simplePagerTitleView.setOnClickListener(v -> clickIndicator(index));
                pagerTitleView.setContentView(simplePagerTitleView);
                pagerTitleViewSetListener(pagerTitleView, simplePagerTitleView);
                return pagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setColors(Color.parseColor("#000000"));
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineHeight(DensityUtils.dp2px(context, 4f));
                indicator.setLineWidth(DensityUtils.dp2px(context, 18f));
                indicator.setYOffset(DensityUtils.dp2px(context, 4f));
                indicator.setRoundRadius(DensityUtils.dp2px(context, 1));
                return indicator;
            }
        });
        indicator.setNavigator(navigator);
        return this;
    }

    /** 绑定：滚动NestScrollView，选中indicator */
    public void bind() {
        nestedScrollView.setViewTouchListener(event -> {
            LogUtils.i("=====", "setViewTouchListener = event=" + event.toString());
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                optionType = 2;
                LogUtils.i("=====", "setOnTouchListener = optionType=" + optionType);
            }
        });
        nestedScrollView.setScrollStateChangeListener(new NewNestedScrollView.ScrollStateChangeListener() {
            @Override
            public void onScrollChange(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                LogUtils.i("=====", "setScrollStateChangeListener = optionType=" + optionType);
                LogUtils.i("=====", "scrollX=" + scrollX + " scrollY=" + scrollY + " oldScrollX=" + oldScrollX + "oldScrollY=" + oldScrollY);
//                findView(scrollY + nestedScrollView.getMeasuredHeight());
                if (optionType == 2) {
                    findView(scrollY);
                }
            }

            @Override
            public void onScrollDirection(NewNestedScrollView.ScrollDirection direction) {

            }
        });
    }

    /** 用户点击了Indicator */
    public void clickIndicator(int index) {
        if (index == selectedIndex) {
            LogUtils.i("=====", "当前index 没有变化=" + selectedIndex);
            return;
        }
        optionType = 1;
        LogUtils.i("=====", "clickIndicator = optionType=" + optionType);
        LinearLayout childView = (LinearLayout) nestedScrollView.getChildAt(0);
        View sceneView = childView.getChildAt(index);
        if (nestedScrollViewTop == 0) {
            scrollViewTop();
        }
        int[] intArray1 = locationInScreen(sceneView);
        int distance = intArray1[1] - nestedScrollViewTop;
        nestedScrollView.fling(distance);
        nestedScrollView.smoothScrollBy(0, distance);
        LogUtils.i("=====", "distance=" + distance);
        selectedIndex = index;
        indicator.onPageSelected(index);
        indicator.onPageScrolled(index, 0.0F, 0);
    }

    /** 用户滚动了nestscrollview */
    private void scroll(int index) {
        if (index == selectedIndex) {
            LogUtils.i("=====", "当前index 没有变化=" + selectedIndex);
            return;
        }
        selectedIndex = index;
        indicator.onPageSelected(index);
        indicator.onPageScrolled(index, 0.0F, 0);
    }

    /** 查找1坐标最接近的view */
    private HomeRoomTypeView findView(int locationY) {
        LinearLayout childView = (LinearLayout) nestedScrollView.getChildAt(0);
        int count = childView.getChildCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                View sceneView = childView.getChildAt(i);
                if (sceneView instanceof HomeRoomTypeView) {
                    float sceneViewY = sceneView.getY();
                    float sceneViewBottomY = sceneView.getY() + sceneView.getHeight();
                    if (sceneViewY < locationY && sceneViewBottomY > locationY) {
                        HomeRoomTypeView homeRoomTypeView = (HomeRoomTypeView) sceneView;
                        LogUtils.i("=====", "IDLE findView=" + homeRoomTypeView.sceneModel.sceneName);
                        scroll(i);
                        return homeRoomTypeView;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 计算scrollView距离屏幕顶部的距离
     */
    private void scrollViewTop() {
        int[] intArray = new int[2];
        nestedScrollView.getLocationOnScreen(intArray);
        nestedScrollViewTop = intArray[1];
    }

    /**
     * 计算View在屏幕中的坐标
     */
    private int[] locationInScreen(View view) {
        int[] intArray = new int[2];
        view.getLocationOnScreen(intArray);
        LogUtils.i("===== intArray[0]=" + intArray[0] + "intArray[1]=" + intArray[1]);
        return intArray;
    }

    /**
     * 计算View在父亲view中的坐标
     */
    private int[] locationInParent(View view) {
        int[] intArray = new int[2];
        view.getLocationInWindow(intArray);
        LogUtils.i("===== intArray[0]=" + intArray[0] + "intArray[1]=" + intArray[1]);
        return intArray;
    }

    /**
     * indicator 选中操作
     */
    private void pagerTitleViewSetListener(CommonPagerTitleView pagerTitleView, ColorTransitionPagerTitleView simplePagerTitleView) {
        pagerTitleView.setOnPagerTitleChangeListener(new CommonPagerTitleView.OnPagerTitleChangeListener() {

            @Override
            public void onSelected(int index, int totalCount) {
                simplePagerTitleView.setTextColor(Color.parseColor("#1a1a1a"));
            }

            @Override
            public void onDeselected(int index, int totalCount) {
                simplePagerTitleView.setTextColor(Color.parseColor("#666666"));
            }

            @Override
            public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
                simplePagerTitleView.setTextColor(Color.parseColor("#666666"));
            }

            @Override
            public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
                simplePagerTitleView.setTextColor(Color.parseColor("#1a1a1a"));
            }
        });
    }

}
