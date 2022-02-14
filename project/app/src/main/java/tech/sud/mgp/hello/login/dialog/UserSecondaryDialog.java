package tech.sud.mgp.hello.login.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.login.listener.DialogSecondaryListener;

public class UserSecondaryDialog extends DialogFragment {

    private TextView contentTv, rejectTv, agreeTv;
    private DialogSecondaryListener secondaryListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.dialog_user_secondary, container, false);
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
        dialogWindow.getAttributes().height = DensityUtils.dp2px(getContext(), 277f);
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
        rejectTv.setOnClickListener(v -> {
            if (this.secondaryListener != null) {
                this.secondaryListener.onSecondaryResult(false);
            }
            dismiss();
        });
        agreeTv.setOnClickListener(v -> {
            if (this.secondaryListener != null) {
                this.secondaryListener.onSecondaryResult(true);
            }
            dismiss();
        });
    }

    public void setDialogSecondaryListener(DialogSecondaryListener secondaryListener) {
        this.secondaryListener = secondaryListener;
    }
}
