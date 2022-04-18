package tech.sud.mgp.hello.ui.main.home.view;

import android.widget.ImageView;
import android.widget.TextView;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.GetAccountResp;

/**
 * 显示金币余额弹窗
 */
public class CoinDialog extends BaseDialogFragment {

    private ImageView headerView;
    private TextView nameView, idView, coinView;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_coin;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        headerView = mRootView.findViewById(R.id.header_view);
        nameView = mRootView.findViewById(R.id.name_view);
        idView = mRootView.findViewById(R.id.id_view);
        coinView = mRootView.findViewById(R.id.coin_view);

        nameView.setText(HSUserInfo.nickName);
        idView.setText(getString(R.string.setting_userid, HSUserInfo.userId + ""));
        ImageLoader.loadImage(headerView, HSUserInfo.avatar);
    }

    @Override
    protected void initData() {
        super.initData();
        loadAccount();
    }

    @Override
    protected int getWidth() {
        return DensityUtils.dp2px(requireContext(), 296);
    }

    @Override
    protected int getHeight() {
        return DensityUtils.dp2px(requireContext(), 214);
    }

    private void loadAccount() {
        HomeRepository.getAccount(this, new RxCallback<GetAccountResp>() {
            @Override
            public void onNext(BaseResponse<GetAccountResp> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS) {
                    coinView.setText(t.getData().coin + "");
                }
            }
        });
    }
}
