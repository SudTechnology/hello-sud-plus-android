package tech.sud.mgp.hello.ui.common.widget.refresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.scwang.smart.drawable.ProgressDrawable;
import com.scwang.smart.refresh.classics.ArrowDrawable;
import com.scwang.smart.refresh.classics.ClassicsAbstract;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;

import tech.sud.mgp.hello.R;

public class CustomHeaderView extends ClassicsAbstract<ClassicsHeader> implements RefreshHeader {

    public CustomHeaderView(Context context) {
        this(context, null);
    }

    public CustomHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);

        mFinishDuration = 100;

        View.inflate(context, R.layout.view_custom_header, this);

        final View thisView = this;
        final View arrowView = mArrowView = thisView.findViewById(R.id.srl_classics_arrow);
        final View progressView = mProgressView = thisView.findViewById(R.id.srl_classics_progress);

        mTitleText = new TextView(context);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ClassicsHeader);

        if (ta.hasValue(R.styleable.ClassicsHeader_srlDrawableArrow)) {
            mArrowView.setImageDrawable(ta.getDrawable(R.styleable.ClassicsHeader_srlDrawableArrow));
        } else if (mArrowView.getDrawable() == null) {
            mArrowDrawable = new ArrowDrawable();
            mArrowDrawable.setColor(0xff666666);
            mArrowView.setImageDrawable(mArrowDrawable);
        }

        if (ta.hasValue(R.styleable.ClassicsHeader_srlDrawableProgress)) {
            mProgressView.setImageDrawable(ta.getDrawable(R.styleable.ClassicsHeader_srlDrawableProgress));
        } else if (mProgressView.getDrawable() == null) {
            mProgressDrawable = new ProgressDrawable();
            mProgressDrawable.setColor(0xff666666);
            mProgressView.setImageDrawable(mProgressDrawable);
        }

        ta.recycle();

        progressView.animate().setInterpolator(null);

        if (thisView.isInEditMode()) {
            arrowView.setVisibility(GONE);
        } else {
            progressView.setVisibility(GONE);
        }

    }
    //</editor-fold>

    //<editor-fold desc="RefreshHeader">
    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        final View progressView = mProgressView;
        Drawable drawable = mProgressView.getDrawable();
        if (drawable instanceof Animatable) {
            if (((Animatable) drawable).isRunning()) {
                ((Animatable) drawable).stop();
            }
        } else {
            progressView.animate().rotation(0).setDuration(0);
        }
        progressView.setVisibility(View.GONE);
        return mFinishDuration; // 延迟xxx毫秒之后再弹回
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        final View arrowView = mArrowView;
        switch (newState) {
            case None:
            case PullDownToRefresh:
                arrowView.setVisibility(VISIBLE);
                arrowView.animate().rotation(0);
                break;
            case Refreshing:
            case RefreshReleased:
                arrowView.setVisibility(GONE);
                break;
            case ReleaseToRefresh:
                arrowView.animate().rotation(180);
                break;
            case ReleaseToTwoLevel:
                arrowView.animate().rotation(0);
                break;
            case Loading:
                arrowView.setVisibility(GONE);
                break;
        }
    }
    //</editor-fold>

}
