package tech.sud.mgp.hello.home.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.utils.AppSharedPreferences;

public class SetFragment extends Fragment implements View.OnClickListener{

    private LinearLayout sdkVersionLl,appVersionLl,userAgreementLl,userPrivacyLl;
    private TextView nameTv,useridTv,sdkVersionTv,appVersionTv;
    private ImageView headerIv;

    public SetFragment() { }

    public static SetFragment newInstance() {
        SetFragment fragment = new SetFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_set, container, false);
        sdkVersionLl = root.findViewById(R.id.sdk_version_ll);
        appVersionLl = root.findViewById(R.id.app_version_ll);
        userAgreementLl = root.findViewById(R.id.user_agreement_ll);
        userPrivacyLl =root.findViewById(R.id.user_privacy_ll);

        nameTv =root.findViewById(R.id.name_tv);
        useridTv = root.findViewById(R.id.userid_tv);
        sdkVersionTv = root.findViewById(R.id.sdk_version_tv);
        appVersionTv = root.findViewById(R.id.app_version_tv);
        headerIv = root.findViewById(R.id.header_iv);
        initData();
        setListener();
        return root;
    }

    private void initData(){
        nameTv.setText(AppSharedPreferences.getSP().getString(AppSharedPreferences.USER_NAME_KEY,""));
        String userId = AppSharedPreferences.getSP().getLong(AppSharedPreferences.USER_ID_KEY,0L)+"";
        useridTv.setText(userId);
        String header = AppSharedPreferences.getSP().getString(AppSharedPreferences.USER_HEAD_PORTRAIT_KEY,"");
        if (header.isEmpty()){
            headerIv.setImageResource(R.mipmap.icon_logo);
        }else {
            Glide.with(this).load(header).into(headerIv);
        }
    }

    private void setListener(){
        sdkVersionLl.setOnClickListener(this);
        appVersionLl.setOnClickListener(this);
        userAgreementLl.setOnClickListener(this);
        userPrivacyLl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == sdkVersionLl){

        }else if (v == appVersionLl){

        }else if (v == userAgreementLl){

        }else if (v == userPrivacyLl){

        }
    }
}