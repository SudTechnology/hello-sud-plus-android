package tech.sud.mgp.hello.login.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import tech.sud.mgp.common.utils.DensityUtils;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.login.listener.DialogSelectListener;

public class UserAgreementDialog extends DialogFragment {

    private TextView contentTv, rejectTv, agreeTv;
    private DialogSelectListener selectListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.dialog_useragreement, container, false);
        contentTv = layout.findViewById(R.id.content_tv);
        rejectTv = layout.findViewById(R.id.reject_tv);
        agreeTv = layout.findViewById(R.id.agree_tv);
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Window dialogWindow = getDialog().getWindow();
        dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogWindow.getDecorView().setBackgroundColor(Color.TRANSPARENT);
        dialogWindow.getAttributes().gravity = Gravity.CENTER;
        dialogWindow.getAttributes().width = DensityUtils.dp2px(getContext(), 296f);
        dialogWindow.getAttributes().height = DensityUtils.dp2px(getContext(), 245f);
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                return true;
            }
            return false;
        });
        init();
    }

    private void init() {
        contentText();
        rejectTv.setOnClickListener(v -> {
            if (this.selectListener != null) {
                this.selectListener.onSelectResult(false);
            }
        });
        agreeTv.setOnClickListener(v -> {
            if (this.selectListener != null) {
                this.selectListener.onSelectResult(true);
            }
            dismiss();
        });
    }

    private void contentText() {
        SpannableStringBuilder builder = new SpannableStringBuilder(getString(R.string.useragreement_content_part1));
        builder.append(getString(R.string.useragreement_content_part2), new ClickableSpan() {

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#1a1a1a"));
                ds.setFakeBoldText(true);
            }

            @Override
            public void onClick(@NonNull View widget) {
                if (UserAgreementDialog.this.selectListener != null) {
                    UserAgreementDialog.this.selectListener.onAgreementType(0);
                }
            }
        }, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(getString(R.string.useragreement_content_part3));
        builder.append(getString(R.string.useragreement_content_part4), new ClickableSpan() {

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#1a1a1a"));
                ds.setFakeBoldText(true);
            }

            @Override
            public void onClick(@NonNull View widget) {
                if (UserAgreementDialog.this.selectListener != null) {
                    UserAgreementDialog.this.selectListener.onAgreementType(1);
                }
            }
        }, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(getString(R.string.useragreement_content_part5));
        contentTv.setText(builder);
        contentTv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setDialogSelectListener(DialogSelectListener selectListener) {
        this.selectListener = selectListener;
    }
}
