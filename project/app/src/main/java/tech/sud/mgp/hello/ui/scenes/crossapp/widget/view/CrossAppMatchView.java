package tech.sud.mgp.hello.ui.scenes.crossapp.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.service.room.model.CrossAppMatchStatus;
import tech.sud.mgp.hello.service.room.resp.CrossAppModel;

/**
 * 跨域 匹配状态View
 */
public class CrossAppMatchView extends ConstraintLayout {

    private TextView tvTitle;
    private TextView tvNumber;
    private TextView tvStatus;
    private TextView tvCancelMatch;
    private TextView tvChangeGame;
    private TextView tvAnewMatch;
    private View viewOb;

    public CrossAppMatchView(@NonNull Context context) {
        this(context, null);
    }

    public CrossAppMatchView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CrossAppMatchView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CrossAppMatchView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        setClipChildren(false);
        inflate(getContext(), R.layout.view_cross_app_match, this);
        tvTitle = findViewById(R.id.match_tv_title);
        tvNumber = findViewById(R.id.match_tv_number);
        tvStatus = findViewById(R.id.match_tv_status);
        tvCancelMatch = findViewById(R.id.match_tv_cancel_match);
        tvChangeGame = findViewById(R.id.match_tv_change_game);
        tvAnewMatch = findViewById(R.id.match_tv_anew_match);
        viewOb = findViewById(R.id.match_container_ob);
    }

    public void setCrossAppModel(CrossAppModel model) {
        switch (model.matchStatus) {
            case CrossAppMatchStatus.MATCHING:
                break;
            case CrossAppMatchStatus.MATCH_SUCCESS:
                break;
            case CrossAppMatchStatus.MATCH_FAILED:
                break;
        }
    }

}
