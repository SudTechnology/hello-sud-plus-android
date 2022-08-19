package tech.sud.mgp.hello.ui.main.settings.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gyf.immersionbar.ImmersionBar;

import java.io.Serializable;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.utils.ShapeUtils;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.common.widget.view.CustomColorDrawable;
import tech.sud.mgp.hello.ui.main.nft.model.NftModel;
import tech.sud.mgp.hello.ui.main.nft.viewmodel.NFTViewModel;

/**
 * nft详情页
 */
public class NftDetailActivity extends BaseActivity {

    private NftModel nftModel;
    private ImageView ivIcon;
    private TextView tvName;
    private View viewAddress;
    private TextView tvAddress;
    private TextView tvTokenId;
    private TextView tvStandard;
    private TextView tvOperate;
    private View viewOperate;

    private final NFTViewModel viewModel = new NFTViewModel();

    private boolean nftLoadSuccess; // 标识NFT图片是否加载成功了

    public static void start(Context context, NftModel nftModel) {
        Intent intent = new Intent(context, NftDetailActivity.class);
        intent.putExtra("NftModel", nftModel);
        context.startActivity(intent);
    }

    @Override
    protected boolean beforeSetContentView() {
        Serializable nftModelSeri = getIntent().getSerializableExtra("NftModel");
        if (nftModelSeri instanceof NftModel) {
            nftModel = (NftModel) nftModelSeri;
        } else {
            return true;
        }
        return super.beforeSetContentView();
    }

    @Override
    protected void setStatusBar() {
        ImmersionBar.with(this).statusBarColor(R.color.transparent).statusBarDarkFont(false).init();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_nft_detail;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        ivIcon = findViewById(R.id.iv_icon);
        tvName = findViewById(R.id.tv_name);
        viewAddress = findViewById(R.id.view_address);
        tvAddress = findViewById(R.id.tv_address);
        tvTokenId = findViewById(R.id.tv_token_id);
        tvStandard = findViewById(R.id.tv_standard);
        tvOperate = findViewById(R.id.tv_operate);
        viewOperate = findViewById(R.id.view_operate);

        View topBar = findViewById(R.id.top_bar);
        ViewUtils.addMarginTop(topBar, ImmersionBar.getStatusBarHeight(this));
    }

    @Override
    protected void initData() {
        super.initData();
        viewModel.initData(this);

        CustomColorDrawable drawable = new CustomColorDrawable();
        drawable.setStartColor(Color.parseColor("#0d000000"));
        drawable.setEndColor(Color.parseColor("#1a000000"));
        ImageLoader.loadNftImage(ivIcon, nftModel.coverUrl, drawable, new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                nftLoadSuccess = true;
                ThreadUtils.runOnUiThread(() -> {
                    updateWearInfo();
                });
                return false;
            }
        });

        tvName.setText(nftModel.name);
        tvAddress.setText(nftModel.contractAddress);
        tvTokenId.setText(nftModel.tokenId);
        tvStandard.setText(nftModel.tokenType);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        viewAddress.setOnClickListener(v -> {
            CharSequence address = tvAddress.getText();
            if (address == null) {
                return;
            }
            ClipboardUtils.copyText(address);
            ToastUtils.showShort(R.string.address_copied);
        });
        viewModel.bindWalletInfoMutableLiveData.observe(this, bindWalletInfoModel -> updateWearInfo());
        tvOperate.setOnClickListener(v -> {
            clickWear();
        });
        viewModel.wearNftChangeLiveData.observe(this, o -> updateWearInfo());
    }

    private void clickWear() {
        NftModel wearNft = viewModel.getWearNft();
        if (wearNft == null || !wearNft.equals(nftModel)) {
            viewModel.wearNft(nftModel);
        } else {
            viewModel.cancelWearNft();
        }
    }

    private void updateWearInfo() {
        // 已经穿戴了该NFT，提供取消穿戴按钮
        NftModel wearNft = viewModel.getWearNft();
        if (wearNft != null && wearNft.equals(nftModel)) {
            viewOperate.setVisibility(View.VISIBLE);
            tvOperate.setBackground(ShapeUtils.createShape(DensityUtils.dp2px(1), null, null,
                    GradientDrawable.RECTANGLE, Color.BLACK, null));
            tvOperate.setTextColor(Color.BLACK);
            tvOperate.setText(R.string.cancel_wear);
            return;
        }

        // 判断是否可以穿戴该NFT
        if (TextUtils.isEmpty(nftModel.coverUrl) || nftModel.fileType != NftModel.FILE_TYPE_IMAGE || !nftLoadSuccess) {
            viewOperate.setVisibility(View.GONE);
            return;
        }

        if (wearNft == null || !wearNft.equals(nftModel)) {
            viewOperate.setVisibility(View.VISIBLE);
            tvOperate.setBackground(ShapeUtils.createShape(null, null, null,
                    GradientDrawable.RECTANGLE, null, Color.BLACK));
            tvOperate.setTextColor(Color.WHITE);
            tvOperate.setText(R.string.wear);
        }
    }

}
