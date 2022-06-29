package tech.sud.mgp.hello.ui.scenes.disco.activity;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.widget.dialog.SimpleChooseDialog;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.GameListResp;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.ui.main.constant.GameIdCons;
import tech.sud.mgp.hello.ui.scenes.audio.activity.AbsAudioRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.constant.OperateMicType;
import tech.sud.mgp.hello.ui.scenes.base.model.RoleType;
import tech.sud.mgp.hello.ui.scenes.disco.viewmodel.DiscoGameViewModel;

/**
 * 蹦迪 场景
 */
public class DiscoActivity extends AbsAudioRoomActivity<DiscoGameViewModel> {

    private TextView tvStartDisco;
    private TextView tvCloseDisco;

    @Override
    protected DiscoGameViewModel initGameViewModel() {
        return new DiscoGameViewModel();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_disco;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        roomConfig.isShowGameNumber = false; // 不显示游戏人数

        // 开启蹦迪按钮
        int paddingHorizontal = DensityUtils.dp2px(this, 8);
        int textColor = Color.parseColor("#ffffff");
        int marginEnd = DensityUtils.dp2px(this, 11);
        tvStartDisco = createTopTextView(paddingHorizontal, textColor, marginEnd);
        tvStartDisco.setText(R.string.start_disco);
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                getResources().getIntArray(R.array.gradient_color_disco));
        tvStartDisco.setBackground(gradientDrawable);
        topView.addCustomView(tvStartDisco, (LinearLayout.LayoutParams) tvStartDisco.getLayoutParams());
        tvStartDisco.setVisibility(View.GONE);

        // 关闭蹦迪按钮
        tvCloseDisco = createTopTextView(paddingHorizontal, textColor, marginEnd);
        tvCloseDisco.setText(R.string.close_disco);
        tvCloseDisco.setBackgroundColor(Color.parseColor("#33ffffff"));
        topView.addCustomView(tvCloseDisco, (LinearLayout.LayoutParams) tvCloseDisco.getLayoutParams());
        tvCloseDisco.setVisibility(View.GONE);

        topView.setSelectGameVisible(false);
    }

    private TextView createTopTextView(int paddingHorizontal, int textColor, int marginEnd) {
        TextView tv = new TextView(this);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);
        tv.setTextSize(12);
        tv.setTextColor(textColor);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                DensityUtils.dp2px(this, 20));
        params.setMarginEnd(marginEnd);
        tv.setLayoutParams(params);
        return tv;
    }

    @Override
    protected void updatePageStyle() {
        super.updatePageStyle();
        if (roomInfoModel.roleType == RoleType.OWNER) {
            if (playingGameId > 0) {
                tvStartDisco.setVisibility(View.GONE);
                tvCloseDisco.setVisibility(View.VISIBLE);
            } else {
                tvStartDisco.setVisibility(View.VISIBLE);
                tvCloseDisco.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void initData() {
        super.initData();
        roomInfoModel.initGameId = roomInfoModel.gameId;
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        tvStartDisco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDisco();
            }
        });
        tvCloseDisco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDisco();
            }
        });
    }

    private void closeDisco() {
        SimpleChooseDialog dialog = new SimpleChooseDialog(this, getString(R.string.close_disco_title),
                getString(R.string.order_finish_left_text), getString(R.string.close));
        dialog.setOnChooseListener(new SimpleChooseDialog.OnChooseListener() {
            @Override
            public void onChoose(int index) {
                if (index == 1) {
                    gameViewModel.clearSite();
                    tvCloseDisco.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            intentSwitchGame(GameIdCons.NONE);
                        }
                    }, 500);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void startDisco() {
        if (roomInfoModel.initGameId > 0) {
            intentSwitchGame(roomInfoModel.initGameId);
            return;
        }
        HomeRepository.gameList(this, new RxCallback<GameListResp>() {
            @Override
            public void onSuccess(GameListResp gameListResp) {
                super.onSuccess(gameListResp);
                if (gameListResp != null) {
                    List<GameModel> gameList = gameListResp.getGameList(roomInfoModel.sceneType);
                    if (gameList != null && gameList.size() > 0) {
                        GameModel gameModel = gameList.get(0);
                        if (gameModel.gameId > 0) {
                            roomInfoModel.initGameId = gameModel.gameId;
                            startDisco();
                        }
                    }
                }
            }
        });
    }

    /** 空实现，业务不自动上麦 */
    @Override
    protected void businessAutoUpMic() {
    }

    @Override
    public void onMicLocationSwitchCompleted(int micIndex, boolean operate, OperateMicType type) {
        super.onMicLocationSwitchCompleted(micIndex, operate, type);
        if (operate) {
            gameViewModel.joinAnchor(null);
        }
    }

}