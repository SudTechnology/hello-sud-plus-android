package tech.sud.mgp.hello.ui.nft.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.io.Serializable;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseFragment;
import tech.sud.mgp.hello.ui.nft.model.NftModel;
import tech.sud.mgp.hello.ui.nft.model.ZoneType;

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
    private TextView tvDesc;
    private TextView tvShowMore;
    private View viewShowMore;
    private View viewMoreArrow;
    private NftModel nftModel;

    private boolean showFullDesc;
    private int descMaxLine;
    private int descScaleLine = 3;

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
        tvDesc = findViewById(R.id.tv_desc);
        tvShowMore = findViewById(R.id.tv_show_more);
        viewShowMore = findViewById(R.id.view_show_more);
        viewMoreArrow = findViewById(R.id.view_more_arrow);
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
        tvDesc.setText(nftModel.description);
        viewShowMore.setVisibility(View.GONE);
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
        viewShowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFullDesc = !showFullDesc;
                updateShowMore();
            }
        });

        tvDesc.setMaxLines(Integer.MAX_VALUE); // 因为一开始我们不知道内容又多少，所以要设置足够的行数
        tvDesc.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                // 这个监听的回调是异步的，在监听完以后一定要把绘制监听移除，不然这个会一直回调，导致界面错乱
                tvDesc.getViewTreeObserver().removeOnPreDrawListener(this);
                descMaxLine = tvDesc.getLineCount();
                updateShowMore();
                return true;
            }
        });
    }

    private void updateShowMore() {
        if (showFullDesc) { // 当前是全部展示
            tvDesc.setMaxLines(Integer.MAX_VALUE);
            viewMoreArrow.setRotation(180);
            if (nftModel.zoneType == ZoneType.OVERSEAS) {
                tvShowMore.setText(R.string.see_less);
            } else {
                tvShowMore.setText(R.string.pack_up);
            }
        } else {
            tvDesc.setMaxLines(descScaleLine);
            viewMoreArrow.setRotation(0);
            if (nftModel.zoneType == ZoneType.OVERSEAS) {
                tvShowMore.setText(R.string.see_more);
            } else {
                tvShowMore.setText(R.string.spread);
            }
        }
        if (descMaxLine > descScaleLine) {
            viewShowMore.setVisibility(View.VISIBLE);
        } else {
            viewShowMore.setVisibility(View.GONE);
        }
    }
}
