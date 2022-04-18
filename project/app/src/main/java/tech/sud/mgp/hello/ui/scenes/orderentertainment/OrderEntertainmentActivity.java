package tech.sud.mgp.hello.ui.scenes.orderentertainment;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.ui.scenes.audio.activity.AbsAudioRoomActivity;
import tech.sud.mgp.hello.ui.scenes.orderentertainment.viewmodel.OrderViewModel;

/**
 * 点单娱乐类场景
 */
public class OrderEntertainmentActivity extends AbsAudioRoomActivity<OrderViewModel> {

    @Override
    protected OrderViewModel initGameViewModel() {
        return new OrderViewModel();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_entertainment;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
    }

    @Override
    protected void initData() {
        super.initData();
        
    }

    @Override
    protected void setListeners() {
        super.setListeners();
    }
}