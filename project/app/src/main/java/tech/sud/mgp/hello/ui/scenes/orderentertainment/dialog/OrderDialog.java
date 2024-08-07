package tech.sud.mgp.hello.ui.scenes.orderentertainment.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.service.main.manager.HomeManager;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.GetAccountResp;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.orderentertainment.adapter.OrderGameAdapter;
import tech.sud.mgp.hello.ui.scenes.orderentertainment.adapter.OrderMicUserAdapter;
import tech.sud.mgp.hello.ui.scenes.orderentertainment.model.OrderGameModel;
import tech.sud.mgp.hello.ui.scenes.orderentertainment.model.OrderMicModel;

/**
 * 点单的dialog
 */
public class OrderDialog extends BaseDialogFragment {

    private TextView coinTv, selectedAllTv, totalCoinTv, totalUserTv, orderBtn;
    private RecyclerView anchorsRv, gamesRv;

    public boolean hasAnchor = false;//麦上是否有主播 false无主播样式
    private OrderMicUserAdapter userAdapter = new OrderMicUserAdapter();
    private List<OrderMicModel> mUsers = new ArrayList<>();

    private OrderGameAdapter gameAdapter = new OrderGameAdapter();
    private List<OrderGameModel> games = new ArrayList<>();
    private int sceneType;
    private int singlePrice = 200;//固定初始单价200
    private CreateClickListener listener;

    public static OrderDialog getInstance(int sceneType) {
        Bundle bundle = new Bundle();
        bundle.putInt("sceneType", sceneType);
        OrderDialog dialog = new OrderDialog();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            sceneType = arguments.getInt("sceneType");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_order;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        coinTv = mRootView.findViewById(R.id.coin_tv);
        selectedAllTv = mRootView.findViewById(R.id.select_all_tv);
        totalCoinTv = mRootView.findViewById(R.id.total_coin_tv);
        totalUserTv = mRootView.findViewById(R.id.total_user_tv);
        orderBtn = mRootView.findViewById(R.id.order_btn);
        anchorsRv = mRootView.findViewById(R.id.anchors_rv);
        gamesRv = mRootView.findViewById(R.id.games_rv);
        selectedAllTv.setSelected(false);
        initAnchorData();
        initUserRv();
        initGameRv();
        loadAccount();
        setOrderPrice(0, 0);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        selectedAllTv.setOnClickListener(v -> {
            boolean current = selectedAllTv.isSelected();
            selectedAll(!current);
            selectedAllTv.setSelected(!current);
        });
        orderBtn.setOnClickListener(v -> {
            if (!hasAnchor) return;
            List<UserInfo> anchors = findUsers();
            if (anchors.size() < 1) {
                ToastUtils.showLong(R.string.order_dialog_no_select_anchor);
                return;
            }
            OrderGameModel game = findGame();
            if (game == null) {
                ToastUtils.showLong(R.string.order_dialog_no_select_game);
                return;
            }
            if (listener != null) {
                listener.onCreateOrder(anchors, game);
            }
        });
    }

    private void initAnchorData() {
        hasAnchor = mUsers.size() > 0;
        if (!hasAnchor) {
            for (int i = 0; i < 8; i++) {
                OrderMicModel model = new OrderMicModel();
                model.isFake = true;
                mUsers.add(model);
            }
            selectedAllTv.setVisibility(View.GONE);
            orderBtn.setBackgroundColor(Color.parseColor("#33000000"));
        } else {
            selectedAllTv.setVisibility(View.VISIBLE);
            orderBtn.setBackgroundColor(Color.BLACK);
        }
        userAdapter.hasAnchor = hasAnchor;
    }

    private void initUserRv() {
        userAdapter.setList(mUsers);
        anchorsRv.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        anchorsRv.setAdapter(userAdapter);
        userAdapter.setOnItemClickListener((adapter, view, position) -> clickUser(position));
    }

    private void clickUser(int position) {
        if (mUsers.size() > 0 && hasAnchor) {
            int userCount = 0;
            int totalPrice = 0;
            OrderGameModel game = findGame();
            for (int i = 0; i < mUsers.size(); i++) {
                if (i == position) {
                    mUsers.get(i).checked = !mUsers.get(i).checked;
                }
                if (mUsers.get(i).checked) {
                    userCount++;
                    if (game != null) {
                        totalPrice += singlePrice;
                    }
                }
            }
            setOrderPrice(userCount, totalPrice);
            userAdapter.notifyDataSetChanged();
        }
    }

    private void selectedAll(boolean selected) {
        if (mUsers.size() > 0) {
            int userCount = 0;
            int totalPrice = 0;
            for (int i = 0; i < mUsers.size(); i++) {
                mUsers.get(i).checked = selected;
                if (mUsers.get(i).checked) {
                    userCount++;
                    totalPrice += singlePrice;
                }
            }
            setOrderPrice(userCount, totalPrice);
            userAdapter.notifyDataSetChanged();
        }
    }

    public void updateMicList(List<AudioRoomMicModel> mData, int selectedIndex) {
        mUsers.clear();
        if (mData.size() > 0) {
            for (int i = 0; i < mData.size(); i++) {
                AudioRoomMicModel micModel = mData.get(i);
                if (micModel.userId != 0 && micModel.userId != HSUserInfo.userId) {
                    OrderMicModel orderMicModel = new OrderMicModel();
                    orderMicModel.userInfo = micModel;
//                    orderMicModel.checked = i == selectedIndex;
                    orderMicModel.checked = false;
                    orderMicModel.indexMic = micModel.micIndex;
                    mUsers.add(orderMicModel);
                }
            }
        }
    }

    public void setCreateListener(CreateClickListener listener) {
        this.listener = listener;
    }

    private void initGameRv() {
        gamesRv.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        gamesRv.setAdapter(gameAdapter);
        gameAdapter.setOnItemClickListener((adapter, view, position) -> clickGame(position));
        refreshGameList();
    }

    private void refreshGameList() {
        games.clear();
        List<GameModel> models = HomeManager.getInstance().getSceneGame(sceneType);
        if (models != null && models.size() > 0) {
            for (int i = 0; i < models.size(); i++) {
                OrderGameModel model = new OrderGameModel();
                model.gameModel = models.get(i);
                model.checked = false;
                games.add(model);
            }
        }
        gameAdapter.setList(games);
    }

    private void clickGame(int position) {
        if (games.size() > 0) {
            for (int i = 0; i < games.size(); i++) {
                games.get(i).checked = i == position;
            }
            gameAdapter.notifyDataSetChanged();
        }
        updateInfo();
    }

    private void updateInfo() {
        if (mUsers.size() > 0 && hasAnchor) {
            int userCount = 0;
            int totalPrice = 0;
            OrderGameModel game = findGame();
            for (int i = 0; i < mUsers.size(); i++) {
                if (mUsers.get(i).checked) {
                    userCount++;
                    if (game != null) {
                        totalPrice += singlePrice;
                    }
                }
            }
            setOrderPrice(userCount, totalPrice);
        }
    }

    @Override
    protected int getWidth() {
        return DensityUtils.getAppScreenWidth();
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    //设置总价格显示,设置全选按钮状态
    private void setOrderPrice(int userCount, int total) {
        totalUserTv.setText(getString(R.string.order_selected_total, userCount));
        totalCoinTv.setText(total + "");
        selectedAllTv.setSelected(userCount == mUsers.size());
        if (selectedAllTv.isSelected()) {
            selectedAllTv.setText(R.string.cancel);
        } else {
            selectedAllTv.setText(R.string.audio_all);
        }
    }

    private void loadAccount() {
        HomeRepository.getAccount(this, new RxCallback<GetAccountResp>() {
            @Override
            public void onNext(BaseResponse<GetAccountResp> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS) {
                    coinTv.setText(t.getData().coin + "");
                }
            }
        });
    }

    /**
     * 遍历查找选中的主播
     * return 用户id集合
     */
    private List<UserInfo> findUsers() {
        List<UserInfo> userList = new ArrayList<>();
        if (mUsers.size() > 0) {
            for (int i = 0; i < mUsers.size(); i++) {
                OrderMicModel micUser = mUsers.get(i);
                if (micUser.checked && micUser.userInfo.userId != 0 && micUser.userInfo.userId != HSUserInfo.userId) {
                    UserInfo userInfo = new UserInfo();
                    userInfo.userID = micUser.userInfo.userId + "";
                    userInfo.name = micUser.userInfo.nickName;
                    userList.add(userInfo);
                }
            }
        }
        return userList;
    }

    /**
     * 遍历查找选中的游戏
     * return 游戏id
     */
    private OrderGameModel findGame() {
        OrderGameModel game = null;
        if (games.size() > 0) {
            for (int i = 0; i < games.size(); i++) {
                if (games.get(i).checked) {
                    game = games.get(i);
                }
            }
        }
        return game;
    }

    public interface CreateClickListener {
        void onCreateOrder(List<UserInfo> userList, OrderGameModel game);
    }
}
