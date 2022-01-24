package tech.sud.mgp.hello.home.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import tech.sud.mgp.common.base.BaseFragment;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.utils.AppSharedPreferences;
import tech.sud.mgp.hello.utils.GlideImageLoader;

public class SetFragment extends BaseFragment implements View.OnClickListener {

    private LinearLayout sdkVersionLl, appVersionLl, userAgreementLl, userPrivacyLl;
    private TextView nameTv, useridTv, sdkVersionTv, appVersionTv;
    private ImageView headerIv;

    public SetFragment() {
    }

    public static SetFragment newInstance() {
        SetFragment fragment = new SetFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        sdkVersionLl = mRootView.findViewById(R.id.sdk_version_ll);
        appVersionLl = mRootView.findViewById(R.id.app_version_ll);
        userAgreementLl = mRootView.findViewById(R.id.user_agreement_ll);
        userPrivacyLl = mRootView.findViewById(R.id.user_privacy_ll);

        nameTv = mRootView.findViewById(R.id.name_tv);
        useridTv = mRootView.findViewById(R.id.userid_tv);
        sdkVersionTv = mRootView.findViewById(R.id.sdk_version_tv);
        appVersionTv = mRootView.findViewById(R.id.app_version_tv);
        headerIv = mRootView.findViewById(R.id.header_iv);
    }

    @Override
    protected void initData() {
        nameTv.setText(AppSharedPreferences.getSP().getString(AppSharedPreferences.USER_NAME_KEY, ""));
        String userId = AppSharedPreferences.getSP().getLong(AppSharedPreferences.USER_ID_KEY, 0L) + "";
        useridTv.setText(userId);
        String header = AppSharedPreferences.getSP().getString(AppSharedPreferences.USER_HEAD_PORTRAIT_KEY, "");
        if (header.isEmpty()) {
            headerIv.setImageResource(R.mipmap.icon_logo);
        } else {
            GlideImageLoader.loadImage(headerIv,header);
        }
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        sdkVersionLl.setOnClickListener(this);
        appVersionLl.setOnClickListener(this);
        userAgreementLl.setOnClickListener(this);
        userPrivacyLl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == sdkVersionLl) {

        } else if (v == appVersionLl) {

        } else if (v == userAgreementLl) {

        } else if (v == userPrivacyLl) {

        }
    }
}