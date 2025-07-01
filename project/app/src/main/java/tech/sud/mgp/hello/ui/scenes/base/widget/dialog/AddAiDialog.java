package tech.sud.mgp.hello.ui.scenes.base.widget.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialog;

public class AddAiDialog extends BaseDialog {

    private View mViewClose;
    private TextView mTvAddRobt;
    private TextView mTvDefaultTone;
    private TextView mTvCustomTone;
    private AddAiListener mAddAiListener;

    public AddAiDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_add_ai;
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected int getWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mViewClose = mRootView.findViewById(R.id.iv_close);
        mTvAddRobt = mRootView.findViewById(R.id.tv_add_robot);
        mTvDefaultTone = mRootView.findViewById(R.id.tv_default_tone);
        mTvCustomTone = mRootView.findViewById(R.id.tv_custom_tone);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        mViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mTvAddRobt.setOnClickListener(v -> {
            if (mAddAiListener != null) {
                mAddAiListener.onClickAddRobot();
            }
        });
        mTvDefaultTone.setOnClickListener(v -> {
            if (mAddAiListener != null) {
                mAddAiListener.onClickDefaultTone();
            }
        });
        mTvCustomTone.setOnClickListener(v -> {
            if (mAddAiListener != null) {
                mAddAiListener.onClickCustomTone();
            }
        });
    }

    public void setAddAiListener(AddAiListener addAiListener) {
        mAddAiListener = addAiListener;
    }

    public interface AddAiListener {
        void onClickAddRobot();

        void onClickDefaultTone();

        void onClickCustomTone();
    }

}
