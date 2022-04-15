package tech.sud.mgp.hello.ui.scenes.ticket.widget;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ShapeUtils;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.GetAccountResp;
import tech.sud.mgp.hello.ui.main.constant.GameLevel;

/**
 * 门票房间加入游戏确认弹窗
 */
public class JoinTicketConfirmDialog extends BaseDialogFragment implements View.OnClickListener {

    private int gameLevel;

    private ImageView ivNoRemind;
    private TextView tvBalance;
    private View.OnClickListener clickConfirmListener;

    public static JoinTicketConfirmDialog getInstance(int gameLevel) {
        JoinTicketConfirmDialog dialog = new JoinTicketConfirmDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("gameLevel", gameLevel);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            gameLevel = arguments.getInt("gameLevel");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_join_ticket_confirm;
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected int getWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getHeight() {
        return DensityUtils.dp2px(222);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        ivNoRemind = findViewById(R.id.iv_no_remind);
        tvBalance = findViewById(R.id.tv_balance);
        View containerBalance = findViewById(R.id.container_balance);
        int radius = DensityUtils.dp2px(requireContext(), 50);
        containerBalance.setBackground(ShapeUtils.createShape(null, null,
                new float[]{radius, radius, 0, 0, 0, 0, radius, radius},
                GradientDrawable.RECTANGLE, null,
                Color.parseColor("#4d000000")));
    }

    @Override
    protected void initData() {
        super.initData();
        TextView tvAward = findViewById(R.id.tv_award);
        tvAward.setText(getString(R.string.high_gold_coin_award, getMaxAward()));
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.join_game_confirm, getConsume()));

        getBalance();
    }

    private void getBalance() {
        HomeRepository.getAccount(this, new RxCallback<GetAccountResp>() {
            @Override
            public void onSuccess(GetAccountResp getAccountResp) {
                super.onSuccess(getAccountResp);
                if (getAccountResp != null) {
                    tvBalance.setText(getAccountResp.coin + "");
                }
            }
        });
    }

    // 获取消费的金币数量
    private int getConsume() {
        switch (gameLevel) {
            case GameLevel.PRIMARY:
                return 2;
            case GameLevel.MIDDLE:
                return 5;
            case GameLevel.HIGH:
                return 10;
        }
        return 0;
    }

    // 获取最高奖励
    private int getMaxAward() {
        switch (gameLevel) {
            case GameLevel.PRIMARY:
                return 20;
            case GameLevel.MIDDLE:
                return 250;
            case GameLevel.HIGH:
                return 900;
        }
        return 0;
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        findViewById(R.id.tv_cancel).setOnClickListener(this);
        findViewById(R.id.tv_confirm).setOnClickListener(this);
        findViewById(R.id.container_no_remind).setOnClickListener(this);
    }

    @Override
    protected void customStyle(Window window) {
        super.customStyle(window);
        window.setWindowAnimations(R.style.BottomToTopAnim);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_cancel) {
            dismiss();
        } else if (id == R.id.tv_confirm) {
            confirmJoin(v);
        } else if (id == R.id.container_no_remind) {
            clickNoRemind();
        }
    }

    private void clickNoRemind() {
        ivNoRemind.setSelected(!ivNoRemind.isSelected());
        AppData.getInstance().setJoinTicketNoRemind(ivNoRemind.isSelected());
    }

    private void confirmJoin(View v) {
        dismiss();
        View.OnClickListener listener = clickConfirmListener;
        if (listener != null) {
            listener.onClick(v);
        }
    }

    public void setClickConfirmListener(View.OnClickListener listener) {
        clickConfirmListener = listener;
    }

}
