package tech.sud.mgp.hello.ui.main.room;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gyf.immersionbar.ImmersionBar;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import tech.sud.mgp.hello.BuildConfig;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.app.FlavorChannel;
import tech.sud.mgp.hello.common.base.BaseFragment;
import tech.sud.mgp.hello.common.base.BaseFragmentStateAdapter;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.service.main.repository.UserInfoRepository;
import tech.sud.mgp.hello.service.main.resp.UserInfoResp;
import tech.sud.mgp.hello.ui.common.utils.LifecycleUtils;
import tech.sud.mgp.hello.ui.common.widget.ScaleTransitionPagerTitleView;
import tech.sud.mgp.hello.ui.common.widget.ViewPager2Helper;
import tech.sud.mgp.hello.ui.main.base.widget.MainUserInfoView;
import tech.sud.mgp.hello.ui.main.discover.DiscoverFragment;
import tech.sud.mgp.hello.ui.main.nft.model.BindWalletInfoModel;
import tech.sud.mgp.hello.ui.main.nft.model.NftModel;
import tech.sud.mgp.hello.ui.main.nft.viewmodel.CancelWearNftListener;
import tech.sud.mgp.hello.ui.main.nft.viewmodel.NFTViewModel;
import tech.sud.mgp.hello.ui.main.roomlist.RoomListFragment;
import tech.sud.mgp.hello.ui.scenes.base.utils.EnterRoomUtils;

/**
 * 房间页面
 */
public class RoomFragment extends BaseFragment {

    private EditText searchEt;
    private TextView goSearch;
    private MainUserInfoView userInfoView;
    private MagicIndicator magicIndicator;
    private ViewPager2 viewPager;

    private final List<String> tabs = new ArrayList<>();
    private final NFTViewModel nftViewModel = new NFTViewModel();

    public static RoomFragment newInstance() {
        return new RoomFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_room;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        searchEt = mRootView.findViewById(R.id.search_et);
        goSearch = mRootView.findViewById(R.id.go_search);
        userInfoView = mRootView.findViewById(R.id.user_info_view);
        magicIndicator = findViewById(R.id.magic_indicator);
        viewPager = findViewById(R.id.view_pager2);

        userInfoView.setNftMask(R.drawable.ic_nft_mask_white);
        userInfoView.setViewWalletAddressArrowVisible(false);
        ViewUtils.addMarginTop(findViewById(R.id.index_header), ImmersionBar.getStatusBarHeight(requireContext()));
    }

    @Override
    protected void initData() {
        super.initData();
        if (FlavorChannel.OVERSEA.equals(BuildConfig.CHANNEL)) {
            tabs.add(getString(R.string.room_list));
//            tabs.add(getString(R.string.cross_domain_connect));
            magicIndicator.setVisibility(View.GONE);
        } else {
            tabs.add(getString(R.string.room_list));
            tabs.add(getString(R.string.cross_domain_connect));
        }
        initMagicIndicator();
        initViewPager();
    }

    private void initMagicIndicator() {
        CommonNavigator commonNavigator = new CommonNavigator(requireContext());
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return tabs.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                ScaleTransitionPagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
                simplePagerTitleView.setMinScale(1);
                simplePagerTitleView.setText(tabs.get(index));
                simplePagerTitleView.setTextSize(18);
                simplePagerTitleView.setNormalColor(Color.parseColor("#666666"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#1a1a1a"));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setColors(Color.WHITE);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineWidth(DensityUtils.dp2px(context, 18));
                indicator.setLineHeight(DensityUtils.dp2px(context, 4));
                indicator.setColors(Color.parseColor("#000000"));
//                indicator.setYOffset(DensityUtils.dp2px(context, 5));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPager2Helper.bind(magicIndicator, viewPager);
    }

    private void initViewPager() {
        MyAdapter adapter = new MyAdapter(this);
        viewPager.setAdapter(adapter);
        adapter.setDatas(tabs);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        searchEt.setOnFocusChangeListener((v, hasFocus) -> {
            String keyword = searchEt.getText().toString();
            if (keyword.length() > 0) {
                goSearch.setVisibility(View.VISIBLE);
            } else {
                goSearch.setVisibility(View.GONE);
            }
        });
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    goSearch.setVisibility(View.VISIBLE);
                } else {
                    goSearch.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchEt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                enterRoom();
            }
            return false;
        });
        goSearch.setOnClickListener(v -> enterRoom());
    }

    private void enterRoom() {
        try {
            String roomIdString = searchEt.getText().toString().trim();
            if (!TextUtils.isEmpty(roomIdString)) {
                long roomId = Long.parseLong(roomIdString);
                EnterRoomUtils.enterRoom(requireContext(), roomId);
            }
            KeyboardUtils.hideSoftInput(searchEt);
            searchEt.setText("");
            searchEt.clearFocus();
        } catch (Exception e) {
            ToastUtils.showShort(getString(R.string.search_room_error));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        userInfoView.updateUserInfo();
        updateNftHeader();
    }

    /** 更新nft头像 */
    private void updateNftHeader() {
        List<Long> userIdList = new ArrayList<>();
        userIdList.add(HSUserInfo.userId);
        UserInfoRepository.getUserInfoList(this, userIdList, new UserInfoRepository.UserInfoResultListener() {
            @Override
            public void userInfoList(List<UserInfoResp> userInfos) {
                BindWalletInfoModel bindWalletInfoModel = nftViewModel.getBindWalletInfoByCache();
                if (userInfos == null || userInfos.size() == 0 || bindWalletInfoModel == null) {
                    return;
                }
                NftModel wearNft = bindWalletInfoModel.getWearNft();
                if (wearNft == null) {
                    return;
                }
                UserInfoResp userInfoResp = userInfos.get(0);
                if (userInfoResp.headerType != 1 || !Objects.equals(userInfoResp.headerNftToken, wearNft.detailsToken)) {
                    nftViewModel.cancelWearNft(new CancelWearNftListener() {
                        @Override
                        public void onSuccess() {
                            LifecycleUtils.safeLifecycle(mFragment, () -> {
                                userInfoView.updateUserInfo();
                            });
                        }

                        @Override
                        public void onFailure(int retCode, String retMsg) {
                            LifecycleUtils.safeLifecycle(mFragment, () -> {
                                nftViewModel.clearWearNft();
                                userInfoView.updateUserInfo();
                            });
                        }
                    });
                }
            }
        });
    }

    private static class MyAdapter extends BaseFragmentStateAdapter<String> {

        public MyAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return RoomListFragment.newInstance();
                case 1:
                default:
                    return new DiscoverFragment();
            }
        }
    }
}
