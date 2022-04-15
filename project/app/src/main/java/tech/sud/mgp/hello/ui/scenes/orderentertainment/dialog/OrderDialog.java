package tech.sud.mgp.hello.ui.scenes.orderentertainment.dialog;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
/**
 * 点单的dialog
 * */
public class OrderDialog extends BaseDialogFragment {

    private TextView coinTv,selectedAllTv,totalCoinTv,orderBtn;
    private RecyclerView anchorsRv,gamesRv;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_order;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        coinTv = mRootView.findViewById(R.id.coin_tv);
        selectedAllTv = mRootView.findViewById(R.id.select_all_tv);
        totalCoinTv = mRootView.findViewById(R.id.total_coin_tv);
        orderBtn = mRootView.findViewById(R.id.order_btn);
        anchorsRv = mRootView.findViewById(R.id.anchors_rv);
        gamesRv = mRootView.findViewById(R.id.games_rv);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
    }

    @Override
    protected int getWidth() {
        return super.getWidth();
    }

    @Override
    protected int getHeight() {
        return super.getHeight();
    }

    @Override
    protected int getGravity() {
        return super.getGravity();
    }
}
