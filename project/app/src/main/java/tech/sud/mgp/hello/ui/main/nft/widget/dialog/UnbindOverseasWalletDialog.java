package tech.sud.mgp.hello.ui.main.nft.widget.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.Serializable;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.nft.core.model.resp.SudNFTGetWalletListModel;

public class UnbindOverseasWalletDialog extends BaseDialogFragment {

    private TextView tvTitle;
    private TextView tvUnbind;
    private TextView tvCancel;
    private SudNFTGetWalletListModel.WalletInfo walletInfo;
    private OnUnbindListener onUnbindListener;

    public static UnbindOverseasWalletDialog newInstance(SudNFTGetWalletListModel.WalletInfo walletInfo) {
        Bundle args = new Bundle();
        args.putSerializable("WalletInfo", walletInfo);
        UnbindOverseasWalletDialog fragment = new UnbindOverseasWalletDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            Serializable walletInfoSeri = arguments.getSerializable("WalletInfo");
            if (walletInfoSeri instanceof SudNFTGetWalletListModel.WalletInfo) {
                walletInfo = (SudNFTGetWalletListModel.WalletInfo) walletInfoSeri;
            }
        }
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected void customStyle(Window window) {
        super.customStyle(window);
        window.setWindowAnimations(R.style.BottomToTopAnim);
    }

    @Override
    protected boolean beforeSetContentView() {
        if (walletInfo == null) {
            return true;
        }
        return super.beforeSetContentView();
    }

    @Override
    protected int getWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_unbind_overseas_wallet;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        tvTitle = findViewById(R.id.tv_title);
        tvUnbind = findViewById(R.id.tv_unbind);
        tvCancel = findViewById(R.id.tv_cancel);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        tvUnbind.setOnClickListener(v -> {
            dismiss();
            if (onUnbindListener != null) {
                onUnbindListener.onUnbind();
            }
        });
        tvCancel.setOnClickListener(v -> {
            dismiss();
        });
    }

    public void setOnUnbindListener(OnUnbindListener onUnbindListener) {
        this.onUnbindListener = onUnbindListener;
    }

    public interface OnUnbindListener {
        void onUnbind();
    }

}
