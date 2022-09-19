package tech.sud.mgp.hello.ui.main.nft.widget.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.ToastUtils;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.widget.view.CustomColorDrawable;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.GetAccountResp;
import tech.sud.mgp.hello.ui.common.utils.LifecycleUtils;
import tech.sud.mgp.hello.ui.main.nft.model.NftModel;
import tech.sud.mgp.hello.ui.main.nft.model.ZoneType;

/**
 * NFT详情弹窗
 */
public class NftDetailDialog extends BaseDialogFragment {

    private TextView tvUserName;
    private TextView tvUserId;
    private TextView tvCoin;
    private ImageView ivIcon;
    private TextView tvNftName;
    private TextView tvDesc;
    private TextView tvShowMore;
    private View viewShowMore;
    private View viewMoreArrow;
    private View viewAddress;
    private View viewTokenId;
    private TextView tvAddress;
    private TextView tvTokenId;
    private TextView tvStandard;
    private TextView tvDescTitle;
    private TextView tvAddressTitle;
    private TextView tvTokenIdTitle;
    private TextView tvStandardTitle;
    private TextView tvTitle;

    private NftModel nftModel;
    private boolean showFullDesc;
    private int descMaxLine;
    private int descScaleLine = 3;

    public static NftDetailDialog newInstance(NftModel nftModel) {
        Bundle args = new Bundle();
        args.putSerializable("NftModel", nftModel);
        NftDetailDialog fragment = new NftDetailDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            nftModel = (NftModel) arguments.getSerializable("NftModel");
        }
    }

    @Override
    protected boolean beforeSetContentView() {
        if (nftModel == null) {
            return true;
        }
        return super.beforeSetContentView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_nft_detail;
    }

    @Override
    protected int getWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getHeight() {
        return (int) (DensityUtils.getScreenHeight() * 0.75);
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
    protected void initWidget() {
        super.initWidget();
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserId = findViewById(R.id.tv_user_id);
        tvCoin = findViewById(R.id.tv_coin);
        ivIcon = findViewById(R.id.iv_icon);
        tvNftName = findViewById(R.id.tv_name);
        tvDesc = findViewById(R.id.tv_desc);
        tvShowMore = findViewById(R.id.tv_show_more);
        viewShowMore = findViewById(R.id.view_show_more);
        viewMoreArrow = findViewById(R.id.view_more_arrow);
        viewAddress = findViewById(R.id.view_address);
        tvAddress = findViewById(R.id.tv_address);
        viewTokenId = findViewById(R.id.view_token_id);
        tvTokenId = findViewById(R.id.tv_token_id);
        tvStandard = findViewById(R.id.tv_standard);
        tvDescTitle = findViewById(R.id.tv_desc_title);
        tvAddressTitle = findViewById(R.id.tv_address_title);
        tvTokenIdTitle = findViewById(R.id.tv_token_id_title);
        tvStandardTitle = findViewById(R.id.tv_standard_title);
        tvTitle = findViewById(R.id.tv_title);
    }

    @Override
    protected void initData() {
        super.initData();
        tvUserName.setText(HSUserInfo.nickName);
        tvUserId.setText(getString(R.string.user_id_content, HSUserInfo.userId + ""));
        setCoin();
        tvNftName.setText(nftModel.name);

        CustomColorDrawable drawable = new CustomColorDrawable();
        drawable.setRadius(DensityUtils.dp2px(6));
        drawable.setStartColor(Color.parseColor("#0d000000"));
        drawable.setEndColor(Color.parseColor("#1a000000"));
        ImageLoader.loadNftImage(ivIcon, nftModel.getShowUrl(), drawable);

        tvDesc.setText(nftModel.description);
        viewShowMore.setVisibility(View.GONE);

        tvAddress.setText(nftModel.contractAddress);
        tvTokenId.setText(nftModel.tokenId);
        tvStandard.setText(nftModel.tokenType);

        if (nftModel.zoneType == ZoneType.INTERNAL) {
            tvTitle.setText(R.string.internal_nft_title);
            tvDescTitle.setText(R.string.works_desc);
            tvAddressTitle.setText(R.string.address);
            tvTokenIdTitle.setText(R.string.cn_token_id);
            tvStandardTitle.setVisibility(View.GONE);
            tvStandard.setVisibility(View.GONE);
        }
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

    private void setCoin() {
        HomeRepository.getAccount(this, new RxCallback<GetAccountResp>() {
            @Override
            public void onNext(BaseResponse<GetAccountResp> t) {
                super.onNext(t);
                LifecycleUtils.safeLifecycle(fragment, () -> {
                    if (t.getRetCode() == RetCode.SUCCESS && t.getData() != null) {
                        String coin = t.getData().coin + "";
                        tvCoin.setText(coin);
                    }
                });
            }
        });
    }

}
