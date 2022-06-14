package tech.sud.mgp.hello.ui.scenes.quiz.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.app.APPConfig;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ShapeUtils;
import tech.sud.mgp.hello.common.widget.dialog.SimpleChooseDialog;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.req.QuizBetReq;
import tech.sud.mgp.hello.service.room.resp.QuizGamePlayerResp;
import tech.sud.mgp.hello.ui.scenes.audio.activity.AbsAudioRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.RoomCmdModelUtils;
import tech.sud.mgp.hello.ui.scenes.quiz.viewmodel.QuizGameViewModel;
import tech.sud.mgp.hello.ui.scenes.quiz.widget.GuessIWinDialog;
import tech.sud.mgp.hello.ui.scenes.quiz.widget.QuizGuessDialog;

/**
 * 竞猜类场景
 */
public class QuizActivity extends AbsAudioRoomActivity<QuizGameViewModel> {

    private TextView tvGuess;
    private ConstraintLayout clGuessIWin;
    private ConstraintLayout viewAutoGuessIWin; // 已经开启了自动猜自己赢

    @Override
    protected QuizGameViewModel initGameViewModel() {
        return new QuizGameViewModel();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_quiz;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initWidget() {
        super.initWidget();
        clGuessIWin = findViewById(R.id.cl_guess_i_win);
        TextView tvGuessIWinCount = findViewById(R.id.tv_guess_i_win_count);

        // 增加猜输赢按钮
        int paddingHorizontal = DensityUtils.dp2px(this, 8);
        int textColor = Color.parseColor("#482500");
        int marginEnd = DensityUtils.dp2px(this, 11);
        tvGuess = createTopTextView(paddingHorizontal, textColor, marginEnd);
        tvGuess.setText(R.string.guess_win_or_lose);
        tvGuess.setBackgroundResource(R.drawable.shape_quiz_top_tv_bg);
        topView.addCustomView(tvGuess, (LinearLayout.LayoutParams) tvGuess.getLayoutParams());
        tvGuess.setVisibility(View.GONE);

        // 增加自动竞猜按钮
        createAutoGuessIWinView(marginEnd);

        tvGuessIWinCount.setText("x" + APPConfig.QUIZ_SINGLE_BET_COUNT);

        giftContainer.bringToFront();
        inputMsgView.bringToFront();
        clOpenMic.bringToFront();
    }

    private void createAutoGuessIWinView(int marginEnd) {
        viewAutoGuessIWin = new ConstraintLayout(this);
        viewAutoGuessIWin.setBackground(ShapeUtils.createShape(null, (float) DensityUtils.dp2px(30),
                null, GradientDrawable.RECTANGLE, null, Color.parseColor("#35c543")));
        int paddingHorizontal = DensityUtils.dp2px(this, 1);
        viewAutoGuessIWin.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);

        TextView tv = new TextView(this);
        tv.setId(View.generateViewId());
        tv.setText(R.string.auto_quiz);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(12);
        ConstraintLayout.LayoutParams tvParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        tvParams.setMarginStart(DensityUtils.dp2px(6));
        tvParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        tvParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        tvParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        viewAutoGuessIWin.addView(tv, tvParams);

        View viewIcon = new View(this);
        viewIcon.setBackgroundResource(R.drawable.ic_green_selected);
        int viewIconSize = DensityUtils.dp2px(this, 18);
        ConstraintLayout.LayoutParams viewParams = new ConstraintLayout.LayoutParams(viewIconSize, viewIconSize);
        viewParams.setMarginStart(DensityUtils.dp2px(4));
        viewParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        viewParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        viewParams.startToEnd = tv.getId();
        viewAutoGuessIWin.addView(viewIcon, viewParams);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                DensityUtils.dp2px(this, 20));
        params.setMarginEnd(marginEnd);
        topView.addCustomView(viewAutoGuessIWin, params);

        viewAutoGuessIWin.setVisibility(View.GONE);
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
    protected void setListeners() {
        super.setListeners();
        gameViewModel.selfIsInLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isIn) {
                updateGuessState();
            }
        });
        gameViewModel.gameLoadingCompletedLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isCompleted) {
                updateGuessState();
            }
        });
        gameViewModel.gameStateChangedLiveData.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer gameState) {
                updateGuessState();
                checkAutoGuessIWin(gameState);
            }
        });
        tvGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickGuess();
            }
        });
        clGuessIWin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickGussIWin();
            }
        });
        viewAutoGuessIWin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 游戏进行中时，不可以关闭自动猜自己赢
                int gameState = gameViewModel.getGameState();
                if (gameState == SudMGPMGState.MGCommonGameState.LOADING || gameState == SudMGPMGState.MGCommonGameState.PLAYING) {
                    ToastUtils.showShort(R.string.game_playing_close_quiz_warn);
                    return;
                }
                SimpleChooseDialog dialog = new SimpleChooseDialog(context, getString(R.string.close_auto_guess_i_win_title),
                        getString(R.string.order_finish_left_text), getString(R.string.confirm_close));
                dialog.setOnChooseListener(new SimpleChooseDialog.OnChooseListener() {
                    @Override
                    public void onChoose(int index) {
                        dialog.dismiss();
                        if (index == 1) {
                            closeAutoGuessIWin();
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    /**
     * 检查是否要执行自动猜自己赢
     * 开启了自动猜自己赢并且游戏状态是在loading状态
     */
    private void checkAutoGuessIWin(Integer gameState) {
        if (AppData.getInstance().isQuizAutoGuessIWin() && gameState == SudMGPMGState.MGCommonGameState.LOADING) {
            HomeRepository.quizBet(context, QuizBetReq.QUIZ_TYPE_GAME, APPConfig.QUIZ_SINGLE_BET_COUNT, HSUserInfo.userId, new RxCallback<>());
            return;
        }
    }

    private void clickGussIWin() {
        // 游戏进行中时，不可以开启自动猜自己赢
        int gameState = gameViewModel.getGameState();
        if (gameState == SudMGPMGState.MGCommonGameState.LOADING || gameState == SudMGPMGState.MGCommonGameState.PLAYING) {
            ToastUtils.showShort(R.string.game_playing_start_quiz_warn);
            return;
        }
        GuessIWinDialog dialog = new GuessIWinDialog();
        dialog.setStartNowOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                // 游戏进行中时，不可以开启自动猜自己赢
                int gameState = gameViewModel.getGameState();
                if (gameState == SudMGPMGState.MGCommonGameState.LOADING || gameState == SudMGPMGState.MGCommonGameState.PLAYING) {
                    ToastUtils.showShort(R.string.game_playing_start_quiz_warn);
                    return;
                }
                startAutoGuessIWin();
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    /** 开启自动猜自己赢 */
    private void startAutoGuessIWin() {
        HomeRepository.quizBet(context, QuizBetReq.QUIZ_TYPE_GAME, APPConfig.QUIZ_SINGLE_BET_COUNT, HSUserInfo.userId, new RxCallback<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                AppData.getInstance().setQuizAutoGuessIWin(true);
                updateGuessState();

                // 发送信令通知其他人
                if (binder != null) {
                    List<UserInfo> recUser = new ArrayList<>();
                    recUser.add(RoomCmdModelUtils.getSendUser());
                    binder.notifyQuizBet(recUser);
                }
            }
        });
    }

    /** 关闭自动猜自己赢 */
    private void closeAutoGuessIWin() {
        viewAutoGuessIWin.setVisibility(View.GONE);
        AppData.getInstance().setQuizAutoGuessIWin(false);
        updateGuessState();
    }

    /** 点击了顶部猜输赢按钮 */
    private void clickGuess() {
        QuizGuessDialog dialog = QuizGuessDialog.newInstance(roomInfoModel.roomId, gameViewModel.getPlayers());
        dialog.setBetListener(new QuizGuessDialog.BetListener() {
            @Override
            public void onSelected(List<QuizGamePlayerResp.Player> list) {
                betPlayer(list);
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    /** 给玩家下注 */
    private void betPlayer(List<QuizGamePlayerResp.Player> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        List<UserInfo> recUser = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();
        for (QuizGamePlayerResp.Player player : list) {
            userIds.add(player.userId);

            UserInfo userInfo = new UserInfo();
            userInfo.userID = player.userId + "";
            userInfo.name = player.nickname;
            recUser.add(userInfo);
        }
        HomeRepository.quizBet(context, QuizBetReq.QUIZ_TYPE_GAME, APPConfig.QUIZ_SINGLE_BET_COUNT, userIds, new RxCallback<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                // 发送信令通知其他人
                if (binder != null) {
                    binder.notifyQuizBet(recUser);
                }
                ToastUtils.showLong(R.string.bet_completed);
            }
        });
    }

    /** 根据当前状态，判断是否显示猜输赢及猜自己赢 */
    private void updateGuessState() {
        if (playingGameId == 0 || !gameViewModel.gameLoadingCompleted()) {
            tvGuess.setVisibility(View.GONE);
            clGuessIWin.setVisibility(View.GONE);
            viewAutoGuessIWin.setVisibility(View.GONE);
            return;
        }
        // 游戏已加载完成，游戏未开始，才判断是显示猜自己赢还是猜别的玩家赢
        if (gameViewModel.isSelfInGame()) { // 自己是玩家
            tvGuess.setVisibility(View.GONE);
            if (AppData.getInstance().isQuizAutoGuessIWin()) {
                viewAutoGuessIWin.setVisibility(View.VISIBLE);
                clGuessIWin.setVisibility(View.GONE);
            } else {
                viewAutoGuessIWin.setVisibility(View.GONE);
                clGuessIWin.setVisibility(View.VISIBLE);
            }
        } else {
            tvGuess.setVisibility(View.VISIBLE);
            clGuessIWin.setVisibility(View.GONE);
            viewAutoGuessIWin.setVisibility(View.GONE);
        }
    }

}