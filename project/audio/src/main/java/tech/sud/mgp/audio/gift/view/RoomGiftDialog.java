package tech.sud.mgp.audio.gift.view;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ScreenUtils;

import java.util.List;

import tech.sud.mgp.audio.R;
import tech.sud.mgp.audio.gift.adapter.GiftListAdapter;
import tech.sud.mgp.audio.gift.manager.GiftHelper;
import tech.sud.mgp.audio.gift.model.GiftModel;
import tech.sud.mgp.common.base.BaseDialogFragment;

public class RoomGiftDialog extends BaseDialogFragment {

    private GiftDialogTopView topView;
    private GiftDialogBottomView bottomView;
    private RecyclerView giftRv;
    private GiftListAdapter giftListAdapter;
    private List<GiftModel> gifts = GiftHelper.getInstance().creatGifts();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.audio_dialog_soft_input);
    }

    @Override
    protected void customStyle(Window window) {
        super.customStyle(window);
        window.setDimAmount(0f);
        window.setWindowAnimations(R.style.DtBottomToTopAnim);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.audio_dialog_gift_send_room;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        topView = mRootView.findViewById(R.id.gift_top_view);
        bottomView = mRootView.findViewById(R.id.gift_bottom_view);
        giftRv = mRootView.findViewById(R.id.gift_data_rv);
    }

    @Override
    protected void initData() {
        super.initData();
        giftListAdapter = new GiftListAdapter(gifts);
        giftRv.setAdapter(giftListAdapter);
        giftRv.setLayoutManager(new GridLayoutManager(requireContext(), 4));
        giftListAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (gifts.size() > 0) {
                for (int i = 0; i < gifts.size(); i++) {
                    if (i == position) {
                        gifts.get(position).checkState = true;
                    } else {
                        gifts.get(position).checkState = false;
                    }
                }
                giftListAdapter.setList(gifts);
            }
        });

        if (GiftHelper.getInstance().testIsMic) {
            topView.setInMic(GiftHelper.getInstance().testMicsUser());
        } else {
            topView.setMicOut(GiftHelper.getInstance().testCreatUserInfo());
        }
    }

    @Override
    protected void setListeners() {
        super.setListeners();
    }

    @Override
    protected int getWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getHeight() {
        return ScreenUtils.getScreenHeight() - 1;
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
