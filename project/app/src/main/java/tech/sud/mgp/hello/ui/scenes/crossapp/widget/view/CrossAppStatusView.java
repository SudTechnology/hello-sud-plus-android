package tech.sud.mgp.hello.ui.scenes.crossapp.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.hello.service.room.model.CrossAppMatchStatus;
import tech.sud.mgp.hello.service.room.resp.CrossAppModel;

/**
 * 跨域 显示当前状态的View
 */
public class CrossAppStatusView extends ConstraintLayout {

    private CrossAppTeamView crossAppTeamView = new CrossAppTeamView(getContext());
    private CrossAppMatchView crossAppMatchView = new CrossAppMatchView(getContext());

    public CrossAppStatusView(@NonNull Context context) {
        this(context, null);
    }

    public CrossAppStatusView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CrossAppStatusView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CrossAppStatusView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        addView(crossAppTeamView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(crossAppMatchView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    /** 设置数据 */
    public void setCrossAppModel(CrossAppModel model) {
        if (model == null) {
            return;
        }
        switch (model.matchStatus) {
            case CrossAppMatchStatus.TEAM:
                crossAppTeamView.setVisibility(View.VISIBLE);
                crossAppMatchView.setVisibility(View.GONE);
                crossAppTeamView.setCrossAppModel(model);
                break;
            case CrossAppMatchStatus.MATCHING:
            case CrossAppMatchStatus.MATCH_SUCCESS:
            case CrossAppMatchStatus.MATCH_FAILED:
                crossAppTeamView.setVisibility(View.GONE);
                crossAppMatchView.setVisibility(View.VISIBLE);
                crossAppMatchView.setCrossAppModel(model);
                break;
        }
    }

    /** 点击车位监听 */
    public void setOnClickStallListener(CrossAppStallView.OnClickStallListener onClickStallListener) {
        crossAppTeamView.setOnClickStallListener(onClickStallListener);
    }

    public void setExitTeamOnClickListener(OnClickListener listener) {
        crossAppTeamView.setExitTeamOnClickListener(listener);
    }

    public void setTeamFastMatchOnClickListener(OnClickListener listener) {
        crossAppTeamView.setTeamFastMatchOnClickListener(listener);
    }

    public void setJoinTeamOnClickListener(OnClickListener listener) {
        crossAppTeamView.setJoinTeamOnClickListener(listener);
    }

    public void setCancelMatchOnClickListener(OnClickListener listener) {
        crossAppMatchView.setCancelMatchOnClickListener(listener);
    }

    public void setChangeGameOnClickListener(OnClickListener listener) {
        crossAppMatchView.setChangeGameOnClickListener(listener);
    }

    public void setAnewMatchOnClickListener(OnClickListener listener) {
        crossAppMatchView.setAnewMatchOnClickListener(listener);
    }

}
