package tech.sud.mgp.hello.ui.scenes.common.gift.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.service.room.resp.GiftListResp;

public class GiftDetailsView extends ConstraintLayout {

    private View mViewEmpty;
    private View mViewMain;
    private ImageView mIvBg;
    private TextView mTvTitle;
    private TextView mTvDescription;
    private OnLayoutListener mOnLayoutListener;

    public GiftDetailsView(@NonNull Context context) {
        this(context, null);
    }

    public GiftDetailsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GiftDetailsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        setListeners();
    }

    private void setListeners() {
        mViewMain.setOnClickListener(v -> {
        });
    }

    private void initView() {
        inflate(getContext(), R.layout.view_gift_details, this);
        mViewEmpty = findViewById(R.id.view_empty);
        mViewMain = findViewById(R.id.cl_main);
        mIvBg = findViewById(R.id.iv_bg);
        mTvTitle = findViewById(R.id.tv_title);
        mTvDescription = findViewById(R.id.tv_description);
    }

    public void setData(GiftListResp.Details model) {
        if (model == null) {
            return;
        }
        ImageLoader.loadImage(mIvBg, model.backgroundUrl);
        mTvTitle.setText(model.title);
        mTvDescription.setText(model.description);
        int color;
        try {
            color = Color.parseColor(model.textColor);
        } catch (Exception e) {
            e.printStackTrace();
            color = Color.parseColor("#705100");
        }
        mTvTitle.setTextColor(color);
        mTvDescription.setTextColor(color);
    }

    public void setEmptyOnClickListener(OnClickListener listener) {
        mViewEmpty.setOnClickListener(listener);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mOnLayoutListener != null) {
            mOnLayoutListener.onLayout(mViewMain.getTop());
        }
    }

    public void setOnLayoutListener(OnLayoutListener onLayoutListener) {
        mOnLayoutListener = onLayoutListener;
    }

    public interface OnLayoutListener {
        void onLayout(int cardTop);
    }

}
