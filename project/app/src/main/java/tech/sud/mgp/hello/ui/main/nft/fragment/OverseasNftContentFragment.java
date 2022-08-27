package tech.sud.mgp.hello.ui.main.nft.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.io.Serializable;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseFragment;
import tech.sud.mgp.hello.ui.main.nft.model.NftModel;

/**
 * 详情页中，展示国外钱包内容
 */
public class OverseasNftContentFragment extends BaseFragment {

    private TextView tvName;
    private View viewAddress;
    private View viewTokenId;
    private TextView tvAddress;
    private TextView tvTokenId;
    private TextView tvStandard;

    private NftModel nftModel;

    public static OverseasNftContentFragment newInstance(NftModel nftModel) {
        Bundle args = new Bundle();
        args.putSerializable("NftModel", nftModel);
        OverseasNftContentFragment fragment = new OverseasNftContentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            Serializable nftModelSeri = arguments.getSerializable("NftModel");
            if (nftModelSeri instanceof NftModel) {
                nftModel = (NftModel) nftModelSeri;
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_overseas_nft_content;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        tvName = findViewById(R.id.tv_name);
        viewAddress = findViewById(R.id.view_address);
        tvAddress = findViewById(R.id.tv_address);
        viewTokenId = findViewById(R.id.view_token_id);
        tvTokenId = findViewById(R.id.tv_token_id);
        tvStandard = findViewById(R.id.tv_standard);
    }

    @Override
    protected void initData() {
        super.initData();
        if (nftModel == null) {
            return;
        }
        tvName.setText(nftModel.name);
        tvAddress.setText(nftModel.contractAddress);
        tvTokenId.setText(nftModel.tokenId);
        tvStandard.setText(nftModel.tokenType);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        viewAddress.setOnClickListener(v -> {
            CharSequence text = tvAddress.getText();
            if (text == null) {
                return;
            }
            ClipboardUtils.copyText(text);
            ToastUtils.showShort(R.string.copy_success);
        });
        viewTokenId.setOnClickListener(v -> {
            CharSequence text = tvTokenId.getText();
            if (text == null) {
                return;
            }
            ClipboardUtils.copyText(text);
            ToastUtils.showShort(R.string.copy_success);
        });
    }

}
