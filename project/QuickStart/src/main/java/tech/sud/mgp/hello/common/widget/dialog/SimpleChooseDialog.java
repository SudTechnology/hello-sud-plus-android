package tech.sud.mgp.hello.common.widget.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialog;
import tech.sud.mgp.hello.common.utils.DensityUtils;

/**
 * 左右两个按钮及顶部一个标题的选择弹窗
 */
public class SimpleChooseDialog extends BaseDialog {

    private TextView mTvTitle;
    private TextView mTvLeft;
    private TextView mTvRight;

    private String mTitle;
    private String mLeftText;
    private String mRightText;

    private OnChooseListener mOnChooseListener;

    public SimpleChooseDialog(@NonNull Context context) {
        super(context);
    }

    public SimpleChooseDialog(@NonNull Context context, String title) {
        super(context);
        mTitle = title;
    }

    public SimpleChooseDialog(@NonNull Context context, String title, String leftText, String rightText) {
        super(context);
        mTitle = title;
        mLeftText = leftText;
        mRightText = rightText;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_simple_choose;
    }

    @Override
    protected int getWidth() {
        return DensityUtils.dp2px(getContext(), 296);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTvTitle = mRootView.findViewById(R.id.tv_title);
        mTvLeft = mRootView.findViewById(R.id.tv_left);
        mTvRight = mRootView.findViewById(R.id.tv_right);

        if (mTitle != null) {
            mTvTitle.setText(mTitle);
        }
        if (mLeftText != null) {
            mTvLeft.setText(mLeftText);
        }
        if (mRightText != null) {
            mTvRight.setText(mRightText);
        }
    }

    /** 设置左边按钮文本 */
    public void setLeftText(String text) {
        mLeftText = text;
        if (mTvLeft != null) {
            mTvLeft.setText(mLeftText);
        }
    }

    /** 设置右边按钮文本 */
    public void setRightText(String text) {
        mRightText = text;
        if (mTvRight != null) {
            mTvRight.setText(mRightText);
        }
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        mTvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnChooseListener listener = mOnChooseListener;
                if (listener != null) {
                    listener.onChoose(0);
                }
            }
        });
        mTvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnChooseListener listener = mOnChooseListener;
                if (listener != null) {
                    listener.onChoose(1);
                }
            }
        });
    }

    public void setOnChooseListener(OnChooseListener listener) {
        mOnChooseListener = listener;
    }

    public interface OnChooseListener {
        /**
         * 按下了某个按钮
         *
         * @param index 0左边按钮，1右边按钮
         */
        void onChoose(int index);
    }

}
