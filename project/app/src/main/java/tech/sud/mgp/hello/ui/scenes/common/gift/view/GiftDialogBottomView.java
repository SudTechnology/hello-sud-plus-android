package tech.sud.mgp.hello.ui.scenes.common.gift.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.ui.scenes.common.gift.listener.PresentClickListener;

@SuppressLint("SetTextI18n")
public class GiftDialogBottomView extends ConstraintLayout {

    private TextView presentTv, sendGiftCountTv;
    private ImageView arrowIv;
    private TextView tvBalance;
    public PresentClickListener presentClickListener;
    private int mGiftCount = 1;
    private TextView mTvAddCoin;

    public GiftDialogBottomView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public GiftDialogBottomView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GiftDialogBottomView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        setListener();
    }


    private void init(Context context) {
        inflate(context, R.layout.view_gift_send_bottom, this);
        presentTv = findViewById(R.id.present_tv);
        sendGiftCountTv = findViewById(R.id.send_gift_count_tv);
        arrowIv = findViewById(R.id.send_gift_bottom_arrow_iv);
        tvBalance = findViewById(R.id.tv_balance);
        mTvAddCoin = findViewById(R.id.tv_add_coin);
        setGiftCount(mGiftCount);
    }

    private void setListener() {
        presentTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (presentClickListener != null) {
                    presentClickListener.onPresent(mGiftCount);
                }
            }
        });
        sendGiftCountTv.setOnClickListener(v -> showSelectGiftPop());
        arrowIv.setOnClickListener(v -> showSelectGiftPop());
    }

    private void showSelectGiftPop() {
        View contnetView = View.inflate(getContext(), R.layout.popup_gift_count, null);
        LinearLayout viewRoot = contnetView.findViewById(R.id.view_root);

        int popupWindowWidth = DensityUtils.dp2px(getContext(), 105);
        int popupWindowHeight = DensityUtils.dp2px(getContext(), 167);
        PopupWindow popupWindow = new PopupWindow(contnetView, popupWindowWidth, popupWindowHeight);

        popupWindow.setOutsideTouchable(true); //设置点击外部区域可以取消popupWindow
        popupWindow.setFocusable(true); // 返回键取消popupwindow

        addGiftCountItem(popupWindow, viewRoot, 1314);
        addGiftCountItem(popupWindow, viewRoot, 520);
        addGiftCountItem(popupWindow, viewRoot, 99);
        addGiftCountItem(popupWindow, viewRoot, 1);

        int xoff = -(popupWindowWidth / 2);
        int yoff = -(popupWindowHeight + DensityUtils.dp2px(getContext(), 36));
        popupWindow.showAsDropDown(arrowIv, xoff, yoff);
    }

    private void addGiftCountItem(PopupWindow popupWindow, LinearLayout viewRoot, int count) {
        TextView tv = new TextView(getContext());
        tv.setTextSize(12);
        tv.setTextColor(Color.BLACK);
        tv.setText("x" + count);
        tv.setGravity(Gravity.CENTER);
        int paddingVertical = DensityUtils.dp2px(getContext(), 8);
        tv.setPadding(0, paddingVertical, 0, paddingVertical);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = DensityUtils.dp2px(getContext(), 2);
        viewRoot.addView(tv, params);
        tv.setOnClickListener(v -> {
            setGiftCount(count);
            popupWindow.dismiss();
        });
    }

    /** 设置余额 */
    public void setBalance(String text) {
        tvBalance.setText(text);
    }

    private void setGiftCount(int count) {
        mGiftCount = count;
        sendGiftCountTv.setText("x " + count);
    }

    public int getGiftCount() {
        return mGiftCount;
    }

    public void setAddCoinOnClickListener(OnClickListener listener) {
        mTvAddCoin.setOnClickListener(listener);
    }

}
