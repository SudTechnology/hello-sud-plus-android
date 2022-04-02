package tech.sud.mgp.hello.ui.main.home;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ToastUtils;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialog;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.utils.AppSharedPreferences;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.utils.ResponseUtils;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.GetAccountResp;

public class CoinDialog extends BaseDialog {

    private ImageView headerView;
    private TextView nameView, idView, coinView;

    public CoinDialog(@NonNull Context context) {
        super(context);
    }

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

        nameView.setText(AppSharedPreferences.getSP().getString(AppSharedPreferences.USER_NAME_KEY, ""));
        String userId = AppSharedPreferences.getSP().getLong(AppSharedPreferences.USER_ID_KEY, 0L) + "";
        idView.setText(getContext().getString(R.string.setting_userid, userId));
        String header = AppSharedPreferences.getSP().getString(AppSharedPreferences.USER_HEAD_PORTRAIT_KEY, "");
        if (header.isEmpty()) {
            headerView.setImageResource(R.drawable.icon_logo);
        } else {
            ImageLoader.loadImage(headerView, header);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        loadAccount();
    }

    @Override
    protected void setListeners() {
        super.setListeners();

    }

    @Override
    protected int getWidth() {
        return DensityUtils.dp2px(getContext(), 296);
    }

    @Override
    protected int getHeight() {
        return DensityUtils.dp2px(getContext(), 214);
    }

    private void loadAccount() {
        HomeRepository.getAccount(null, new RxCallback<GetAccountResp>() {
            @Override
            public void onNext(BaseResponse<GetAccountResp> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS) {
                    coinView.setText(t.getData().coin + "");
                } else {
                    ToastUtils.showShort(ResponseUtils.conver(t));
                }
            }
        });
    }
}
