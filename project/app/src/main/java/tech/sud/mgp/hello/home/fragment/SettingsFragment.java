package tech.sud.mgp.hello.home.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import tech.sud.mgp.audio.gift.view.GiftEffectView;
import tech.sud.mgp.common.base.BaseFragment;
import tech.sud.mgp.common.utils.ImageLoader;
import tech.sud.mgp.core.SudMGP;
import tech.sud.mgp.hello.BuildConfig;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.agreement.UserAgreementActivity;
import tech.sud.mgp.hello.home.dialog.ContactDialog;
import tech.sud.mgp.hello.utils.AppSharedPreferences;

public class SettingsFragment extends BaseFragment implements View.OnClickListener {

    private LinearLayout sdkVersionLl, appVersionLl, userAgreementLl, userPrivacyLl, userContactLl;
    private TextView nameTv, useridTv, sdkVersionTv, appVersionTv;
    private ImageView headerIv;
    private GiftEffectView effectView;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
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
        userContactLl = mRootView.findViewById(R.id.user_contact_ll);

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
        useridTv.setText(getString(R.string.setting_userid1,userId));
        String header = AppSharedPreferences.getSP().getString(AppSharedPreferences.USER_HEAD_PORTRAIT_KEY, "");
        if (header.isEmpty()) {
            headerIv.setImageResource(R.mipmap.icon_logo);
        } else {
            ImageLoader.loadImage(headerIv, header);
        }
        sdkVersionTv.setText(getString(R.string.setting_version, SudMGP.getVersion()));
        appVersionTv.setText(getString(R.string.setting_version, BuildConfig.VERSION_NAME));
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        sdkVersionLl.setOnClickListener(this);
        appVersionLl.setOnClickListener(this);
        userAgreementLl.setOnClickListener(this);
        userPrivacyLl.setOnClickListener(this);
        userContactLl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == sdkVersionLl) {

        } else if (v == appVersionLl) {

        } else if (v == userAgreementLl) {
            Intent intent = new Intent(requireContext(), UserAgreementActivity.class);
            intent.putExtra(UserAgreementActivity.AGREEMENTTYPE, 0);
            startActivity(intent);
        } else if (v == userPrivacyLl) {
            Intent intent = new Intent(requireContext(), UserAgreementActivity.class);
            intent.putExtra(UserAgreementActivity.AGREEMENTTYPE, 1);
            startActivity(intent);
        } else if (v == userContactLl) {
            ContactDialog dialog = new ContactDialog();
            dialog.show(getFragmentManager(), "contact");
        }
    }
}