package tech.sud.mgp.hello.ui.scenes.quiz.activity;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.req.QuizBetReq;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.QuizGameListResp;
import tech.sud.mgp.hello.ui.common.widget.refresh.RefreshDataHelper;
import tech.sud.mgp.hello.ui.common.widget.refresh.RefreshView;
import tech.sud.mgp.hello.ui.main.constant.SceneType;
import tech.sud.mgp.hello.ui.main.home.model.MatchRoomModel;
import tech.sud.mgp.hello.ui.scenes.base.utils.EnterRoomUtils;
import tech.sud.mgp.hello.ui.scenes.quiz.adapter.MoreQuizAdapter;
import tech.sud.mgp.hello.ui.scenes.quiz.model.QuizRoomPkModel;
import tech.sud.mgp.hello.ui.scenes.quiz.viewmodel.MoreQuizViewModel;
import tech.sud.mgp.hello.ui.scenes.quiz.widget.MoreQuizHeadView;
import tech.sud.mgp.hello.ui.scenes.quiz.widget.QuizBetDialog;

/**
 * 竞猜场景 更多活动
 */
public class MoreQuizActivity extends BaseActivity {

    private MoreQuizViewModel viewModel = new MoreQuizViewModel();

    private RefreshView refreshView;
    private RefreshDataHelper<GameModel> refreshDataHelper;
    private final MoreQuizAdapter adapter = new MoreQuizAdapter();
    private MoreQuizHeadView headView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_more_quiz;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        refreshView = findViewById(R.id.refresh_view);
        refreshView.setEnableRefresh(false);
        headView = new MoreQuizHeadView(this);
        initRefreshDataHelper();
        adapter.setHeaderWithEmptyEnable(true);
        adapter.setHeaderView(headView);
        RecyclerView recyclerView = refreshView.getRecyclerView();
        int paddingBottom = DensityUtils.dp2px(this, 16);
        recyclerView.setPadding(0, 0, 0, paddingBottom);
    }

    private void initRefreshDataHelper() {
        refreshDataHelper = new RefreshDataHelper<GameModel>() {
            @Override
            protected RefreshView getRefreshView() {
                return refreshView;
            }

            @Override
            protected RecyclerView.LayoutManager getLayoutManager() {
                return new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            }

            @Override
            protected BaseQuickAdapter<GameModel, BaseViewHolder> getAdapter() {
                return adapter;
            }

            @Override
            protected GetDataListener getDataListener() {
                return new GetDataListener() {
                    @Override
                    public void onGetData(int pageNumber, int pageSize) {
                        viewModel.getDatas(context, pageNumber, pageSize);
                    }
                };
            }
        };
        refreshDataHelper.setPageSize(Integer.MAX_VALUE);
    }

    @Override
    protected void initData() {
        super.initData();
        refreshDataHelper.initData();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        viewModel.dataSuccessLiveData.observe(this, new Observer<QuizGameListResp>() {
            @Override
            public void onChanged(QuizGameListResp resp) {
                if (resp == null) {
                    initRoomPkData(60 * 60);
                    refreshDataHelper.noPagingRespDatasSuccess(null);
                    headView.setQuizTitleVisible(false);
                } else {
                    initRoomPkData(resp.pkCountDownCycle);
                    List<GameModel> list = resp.quizGameInfoList;
                    refreshDataHelper.noPagingRespDatasSuccess(list);
                    headView.setQuizTitleVisible(list != null && list.size() > 0);
                }
            }
        });
        viewModel.dataFailedLiveData.observe(this, new Observer<QuizGameListResp>() {
            @Override
            public void onChanged(QuizGameListResp quizGameListResp) {
                refreshDataHelper.noPagingRespFailed();
            }
        });
        headView.setOnGuessListener(new MoreQuizHeadView.OnGuessListener() {
            @Override
            public void onGuess(QuizRoomPkModel.RoomPkInfoModel model) {
                showBetDialog(model);
            }
        });
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                itemClick(position);
            }
        });
    }

    private void itemClick(int position) {
        GameModel item = adapter.getItem(position);
        matchGame(item.gameId);
    }

    private void matchGame(Long gameId) {
        HomeRepository.matchGame(SceneType.QUIZ, gameId, null, this, new RxCallback<MatchRoomModel>() {
            @Override
            public void onNext(BaseResponse<MatchRoomModel> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS) {
                    EnterRoomUtils.enterRoom(null, t.getData().roomId);
                }
            }
        });
    }

    private void showBetDialog(QuizRoomPkModel.RoomPkInfoModel model) {
        QuizBetDialog dialog = QuizBetDialog.newInstance(model.roomName);
        dialog.setOnSelectedListener(new QuizBetDialog.OnSelectedListener() {
            @Override
            public void onSelected(long money) {
                model.betNumber += money;
                headView.notifyDataSetChanged();

                // 扣费
                ArrayList<Long> userIds = new ArrayList<>();
                userIds.add(model.roomId);
                HomeRepository.quizBet(null, QuizBetReq.QUIZ_TYPE_PK, money, userIds, new RxCallback<Object>() {
                });
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    private void initRoomPkData(int pkCountDownCycle) {
        List<QuizRoomPkModel> list = AppData.getInstance().getQuizRoomPkModelList();
        if (list == null || list.size() == 0) {
            list = new ArrayList<>();
            list.add(buildRoomPkModel1(pkCountDownCycle));
            list.add(buildRoomPkModel2(pkCountDownCycle));
        }
        headView.setDatas(list);
        AppData.getInstance().setQuizRoomPkModelList(list);
    }

    private QuizRoomPkModel buildRoomPkModel1(int pkCountDownCycle) {
        QuizRoomPkModel model = new QuizRoomPkModel();
        model.status = 0;
        model.countdownCycle = pkCountDownCycle;
        model.memberNumber = 30;
        model.winNumber = 0;
        model.jackpotCount = "50,000";

        model.leftInfo.icon = R.drawable.ic_avatar_7;
        model.leftInfo.roomId = 8721;
        model.leftInfo.roomName = getString(R.string.name_1);
        model.leftInfo.betNumber = 0;
        model.leftInfo.isWin = false;

        model.rightInfo.icon = R.drawable.ic_avatar_8;
        model.rightInfo.roomId = 8719;
        model.rightInfo.roomName = getString(R.string.name_2);
        model.rightInfo.betNumber = 0;
        model.rightInfo.isWin = false;

        return model;
    }

    private QuizRoomPkModel buildRoomPkModel2(int pkCountDownCycle) {
        QuizRoomPkModel model = new QuizRoomPkModel();
        model.status = 1;
        model.countdownCycle = pkCountDownCycle;
        model.memberNumber = 30;
        model.winNumber = 13;
        model.jackpotCount = "30,000";

        model.leftInfo.icon = R.drawable.ic_avatar_9;
        model.leftInfo.roomId = 8526;
        model.leftInfo.roomName = getString(R.string.name_3);
        model.leftInfo.betNumber = 100;
        model.leftInfo.isWin = true;

        model.rightInfo.icon = R.drawable.ic_avatar_10;
        model.rightInfo.roomId = 8329;
        model.rightInfo.roomName = getString(R.string.name_4);
        model.rightInfo.betNumber = 0;
        model.rightInfo.isWin = false;

        return model;
    }

}
