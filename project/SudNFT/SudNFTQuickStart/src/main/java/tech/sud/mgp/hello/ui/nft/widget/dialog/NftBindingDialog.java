package tech.sud.mgp.hello.ui.nft.widget.dialog;

import android.view.View;
import android.widget.TextView;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.listener.CompletedListener;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.LifecycleUtils;
import tech.sud.mgp.hello.common.widget.dialog.TitleInfoDialog;
import tech.sud.mgp.hello.ui.nft.viewmodel.NFTViewModel;
import tech.sud.nft.core.listener.ISudNFTListenerBindWallet;
import tech.sud.nft.core.model.resp.SudNFTBindWalletEvent;
import tech.sud.nft.core.model.resp.SudNFTBindWalletModel;
import tech.sud.nft.core.model.resp.SudNFTBindWalletStage;
import tech.sud.nft.core.model.resp.SudNFTGetWalletListModel;

/**
 * NFT绑定弹窗
 */
public class NftBindingDialog extends BaseDialogFragment {

    private View viewClose;
    private View viewStatus;
    private TextView tvStatus;
    public NFTViewModel viewModel;
    public SudNFTGetWalletListModel.WalletInfo walletInfo;
    private onBindSuccessListener onBindSuccessListener;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_nft_binding;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        viewClose = findViewById(R.id.iv_close);
        viewStatus = findViewById(R.id.view_status);
        tvStatus = findViewById(R.id.tv_status);
    }

    @Override
    protected int getWidth() {
        return DensityUtils.dp2px(271);
    }

    @Override
    protected int getHeight() {
        return DensityUtils.dp2px(141);
    }

    @Override
    protected void initData() {
        super.initData();
        if (viewModel == null || walletInfo == null) {
            dismiss();
            return;
        }
        viewModel.bindWallet(requireContext(), walletInfo, new ISudNFTListenerBindWallet() {
            @Override
            public void onSuccess(SudNFTBindWalletModel model) {
                LifecycleUtils.safeLifecycle(NftBindingDialog.this, new CompletedListener() {
                    @Override
                    public void onCompleted() {
                        processOnSuccess(model);
                    }
                });
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                LifecycleUtils.safeLifecycle(NftBindingDialog.this, new CompletedListener() {
                    @Override
                    public void onCompleted() {
                        processOnFailure(retCode, retMsg);
                    }
                });
            }

            @Override
            public void onBindStageList(List<SudNFTBindWalletStage> list) {
                LifecycleUtils.safeLifecycle(NftBindingDialog.this, new CompletedListener() {
                    @Override
                    public void onCompleted() {
                        processOnBindStageList(list);
                    }
                });
            }

            @Override
            public void onBindStageEvent(SudNFTBindWalletStage stage, SudNFTBindWalletEvent event) {
                LifecycleUtils.safeLifecycle(NftBindingDialog.this, new CompletedListener() {
                    @Override
                    public void onCompleted() {
                        processOnBindStageEvent(stage, event);
                    }
                });
            }
        });
    }

    private void processOnSuccess(SudNFTBindWalletModel model) {
        if (onBindSuccessListener != null) {
            onBindSuccessListener.onBindSuccess();
        }
        tvStatus.postDelayed(this::dismissAllowingStateLoss, 3000);
    }

    public void setOnBindSuccessListener(NftBindingDialog.onBindSuccessListener onBindSuccessListener) {
        this.onBindSuccessListener = onBindSuccessListener;
    }

    public interface onBindSuccessListener {
        void onBindSuccess();
    }

    private void processOnBindStageList(List<SudNFTBindWalletStage> list) {
    }

    private void processOnFailure(int retCode, String retMsg) {
        dismiss();
        showBindWalletFailedDialog(retCode, retMsg);
    }

    private void processOnBindStageEvent(SudNFTBindWalletStage stage, SudNFTBindWalletEvent event) {
        if (event == null) {
            return;
        }
        switch (event) {
            case CONNECT_START:
                viewStatus.setBackgroundResource(R.drawable.ic_connecting);
                tvStatus.setText(R.string.wallet_connecting);
                break;
            case SIGN_END:
                viewClose.setVisibility(View.GONE);
                viewStatus.setBackgroundResource(R.drawable.ic_sign_success);
                tvStatus.setText(R.string.sign_success);
                break;
            default:
                viewStatus.setBackgroundResource(R.drawable.ic_connected);
                tvStatus.setText(R.string.waiting_sign);
                break;
        }
    }

    private void showBindWalletFailedDialog(int code, String msg) {
        String info = msg + "(" + code + ")";
        TitleInfoDialog dialog = new TitleInfoDialog(requireContext(), getString(R.string.connect_failed), info);
        dialog.show();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        viewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    protected boolean canceledOnTouchOutside() {
        return false;
    }

    @Override
    protected boolean cancelable() {
        return false;
    }

}
