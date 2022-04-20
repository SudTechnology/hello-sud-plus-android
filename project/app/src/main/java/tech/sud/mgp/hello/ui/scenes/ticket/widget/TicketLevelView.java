package tech.sud.mgp.hello.ui.scenes.ticket.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.AnimUtils;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.ui.main.constant.GameLevel;

/**
 * 选择等级的View
 */
public class TicketLevelView extends ConstraintLayout {

    private View containerMain;
    private View viewHot;
    private View viewGold;
    private TextView tvWinAward;
    private FrameLayout containerAvatar;
    private TextView tvCountPeople;
    private TextView tvJoin;

    private int ticketLevel;

    public TicketLevelView(@NonNull Context context) {
        this(context, null);
    }

    public TicketLevelView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TicketLevelView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        inflate(getContext(), R.layout.view_ticket_level, this);
        containerMain = findViewById(R.id.container_main);
        viewHot = findViewById(R.id.view_hot);
        viewGold = findViewById(R.id.view_gold);
        tvWinAward = findViewById(R.id.tv_win_award);
        containerAvatar = findViewById(R.id.container_avatar);
        tvCountPeople = findViewById(R.id.tv_count_people);
        tvJoin = findViewById(R.id.tv_join);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TicketLevelView, defStyleAttr, 0);
        ticketLevel = typedArray.getInt(R.styleable.TicketLevelView_tlv_level, GameLevel.PRIMARY);
        typedArray.recycle();

        initLevelData();
    }

    // 初始化不同等级下的数据
    private void initLevelData() {
        switch (ticketLevel) {
            case GameLevel.PRIMARY:
                containerMain.setBackgroundResource(R.drawable.ic_ticket_level_small);
                viewHot.setVisibility(View.GONE);
                viewGold.setBackgroundResource(R.drawable.ic_ticket_level_gold_small);
                tvWinAward.setText(getContext().getString(R.string.win_multiple_award, 10));
                tvCountPeople.setText(getContext().getString(R.string.count_people_play, "87367"));
                tvJoin.setBackgroundResource(R.drawable.ic_ticket_level_join_bg_small);
                break;
            case GameLevel.MIDDLE:
                containerMain.setBackgroundResource(R.drawable.ic_ticket_level_middle);
                viewHot.setVisibility(View.VISIBLE);
                viewGold.setBackgroundResource(R.drawable.ic_ticket_level_gold_middle);
                tvWinAward.setText(getContext().getString(R.string.win_multiple_award, 50));
                tvCountPeople.setText(getContext().getString(R.string.count_people_play, "85787"));
                tvJoin.setBackgroundResource(R.drawable.ic_ticket_level_join_bg_middle);
                break;
            case GameLevel.HIGH:
                containerMain.setBackgroundResource(R.drawable.ic_ticket_level_high);
                viewHot.setVisibility(View.VISIBLE);
                viewGold.setBackgroundResource(R.drawable.ic_ticket_level_gold_high);
                tvWinAward.setText(getContext().getString(R.string.win_multiple_award, 90));
                tvCountPeople.setText(getContext().getString(R.string.count_people_play, "98759"));
                tvJoin.setBackgroundResource(R.drawable.ic_ticket_level_join_bg_high);
                break;
        }
        int avatarSize = DensityUtils.dp2px(getContext(), 24);
        addAvatars(containerAvatar, avatarSize);
        AnimUtils.breathe(tvJoin);
    }

    private void addAvatars(FrameLayout container, int avatarSize) {
        for (int i = 0; i < 4; i++) {
            View view = new View(getContext());
            switch (i) {
                case 0:
                    view.setBackgroundResource(R.drawable.ic_ticket_avatar_1);
                    break;
                case 1:
                    view.setBackgroundResource(R.drawable.ic_ticket_avatar_2);
                    break;
                case 2:
                    view.setBackgroundResource(R.drawable.ic_ticket_avatar_3);
                    break;
                case 3:
                    view.setBackgroundResource(R.drawable.ic_ticket_avatar_4);
                    break;
            }
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(avatarSize, avatarSize);
            params.setMarginStart(DensityUtils.dp2px(getContext(), 16) * i);
            container.addView(view, 0, params);
        }
    }

}
