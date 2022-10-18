package tech.sud.mgp.hello.ui.main.nft.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.utils.ImageUtils;
import tech.sud.mgp.hello.common.widget.view.CustomColorDrawable;
import tech.sud.mgp.hello.common.widget.view.YStretchDrawable;
import tech.sud.mgp.hello.ui.main.nft.model.BindWalletInfoModel;
import tech.sud.mgp.hello.ui.main.nft.model.NftListResultModel;
import tech.sud.mgp.hello.ui.main.nft.model.NftModel;
import tech.sud.mgp.hello.ui.main.nft.model.WalletChainInfo;
import tech.sud.mgp.hello.ui.main.nft.model.ZoneType;
import tech.sud.nft.core.model.resp.SudNFTGetWalletListModel.WalletInfo;

/**
 * 绑定nft信息View
 */
public class WalletInfoView extends ConstraintLayout {

    private View viewChain;
    private ImageView ivChainIcon;
    private TextView tvChainName;
    private TextView tvCount;
    private ImageView ivNft1;
    private ImageView ivNft2;
    private ImageView ivNft3;
    private TextView tvEmpty;
    private TextView tvTitle;

    public WalletInfoView(@NonNull Context context) {
        this(context, null);
    }

    public WalletInfoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WalletInfoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        inflate(context, R.layout.view_wallet_info, this);
        View rootView = findViewById(R.id.root_view);
        Bitmap bitmap = ImageUtils.getBitmap(R.drawable.ic_wallet_bg, DensityUtils.getScreenWidth(), DensityUtils.getScreenHeight());
        rootView.setBackground(new YStretchDrawable(bitmap));

        viewChain = findViewById(R.id.view_chain);
        ivChainIcon = findViewById(R.id.iv_chain_icon);
        tvChainName = findViewById(R.id.tv_chain_name);
        tvCount = findViewById(R.id.tv_nft_count);
        ivNft1 = findViewById(R.id.iv_nft_1);
        ivNft2 = findViewById(R.id.iv_nft_2);
        ivNft3 = findViewById(R.id.iv_nft_3);
        tvEmpty = findViewById(R.id.tv_empty);
        tvTitle = findViewById(R.id.tv_title);
    }

    /** 设置当前链信息 */
    public void setChainInfo(BindWalletInfoModel model, List<WalletInfo> walletList) {
        if (model == null) {
            viewChain.setVisibility(View.GONE);
            return;
        }
        int zoneType = model.getZoneType();
        WalletChainInfo chainInfo = model.getChainInfo();
        if (zoneType == ZoneType.OVERSEAS) { // 国外
            setOverseasChainInfo(chainInfo);
        } else if (zoneType == ZoneType.INTERNAL) { // 国内
            viewChain.setVisibility(View.VISIBLE);

            WalletInfo bindWallet = getBindWallet(model.walletType, walletList);
            if (bindWallet == null) {
                ImageLoader.loadImage(ivChainIcon, null);
                tvChainName.setText("");
            } else {
                ImageLoader.loadImage(ivChainIcon, bindWallet.icon);
                tvChainName.setText(bindWallet.name);
            }
        } else {
            viewChain.setVisibility(View.GONE);
        }
    }

    private WalletInfo getBindWallet(long walletType, List<WalletInfo> walletList) {
        if (walletList != null) {
            for (WalletInfo walletInfo : walletList) {
                if (walletInfo.type == walletType) {
                    return walletInfo;
                }
            }
        }
        return null;
    }

    private void setOverseasChainInfo(WalletChainInfo chainInfo) {
        if (chainInfo == null) {
            viewChain.setVisibility(View.GONE);
        } else {
            viewChain.setVisibility(View.VISIBLE);
            ImageLoader.loadImage(ivChainIcon, chainInfo.icon);
            tvChainName.setText(chainInfo.name);
        }
    }

    /** 设置链点击事件 */
    public void setChainOnClickListener(OnClickListener listener) {
        viewChain.setOnClickListener(listener);
    }

    /** 设置nft列表 */
    @SuppressLint("SetTextI18n")
    public void setDatas(NftListResultModel model) {
        if (model != null) {
            tvCount.setText(model.totalCount + "");
        }
        if (model == null || model.list == null || model.list.size() == 0) {
            ivNft1.setVisibility(View.INVISIBLE);
            ivNft2.setVisibility(View.INVISIBLE);
            ivNft3.setVisibility(View.INVISIBLE);
            tvEmpty.setVisibility(View.VISIBLE);
            return;
        }
        ivNft1.setVisibility(View.INVISIBLE);
        ivNft2.setVisibility(View.INVISIBLE);
        ivNft3.setVisibility(View.INVISIBLE);

        tvEmpty.setVisibility(View.GONE);
        for (int i = 0; i < model.list.size(); i++) {
            NftModel nftModel = model.list.get(i);
            switch (i) {
                case 0:
                    ivNft1.setVisibility(View.VISIBLE);
                    loadNftImage(ivNft1, nftModel);
                    break;
                case 1:
                    ivNft2.setVisibility(View.VISIBLE);
                    loadNftImage(ivNft2, nftModel);
                    break;
                case 2:
                    ivNft3.setVisibility(View.VISIBLE);
                    loadNftImage(ivNft3, nftModel);
                    break;
            }
        }
    }

    /** 设置绑定钱包信息 */
    public void setBindWallet(BindWalletInfoModel model) {
        if (model != null && model.getZoneType() == ZoneType.INTERNAL) {
            tvEmpty.setText(R.string.no_digital_collection);
            tvTitle.setText(R.string.my_digital_collection);
        } else {
            tvEmpty.setText(R.string.nft_empty);
            tvTitle.setText(R.string.owned_nft);
        }
    }

    private void loadNftImage(ImageView imageView, NftModel nftModel) {
        String url;
        if (nftModel == null) {
            url = null;
        } else {
            url = nftModel.getShowUrl();
        }
        CustomColorDrawable drawable = new CustomColorDrawable();
        drawable.setRadius(DensityUtils.dp2px(8));
        drawable.setStartColor(Color.parseColor("#1affffff"));
        drawable.setEndColor(Color.parseColor("#29ffffff"));

        ImageLoader.loadNftImage(imageView, url, drawable);
    }

}
