package tech.sud.mgp.hello.ui.main.home;

import android.content.Intent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseFragment;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.common.widget.view.SimpleTextWatcher;
import tech.sud.mgp.hello.service.main.repository.MainRepository;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.ui.main.home.view.HomeHeaderView;
import tech.sud.mgp.hello.ui.scenes.base.utils.EnterRoomUtils;
import tech.sud.mgp.hello.ui.scenes.custom.CustomConfigActivity;

public class HomeFragment extends BaseFragment {

    private final MyAdapter adapter = new MyAdapter();
    private final long roomId = 10000; // 默认使用的房间Id

    private EditText editText;
    private TextView tvEnter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        editText = findViewById(R.id.et_room_id);
        tvEnter = findViewById(R.id.tv_enter);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        editText.setHint(roomId + "");

        adapter.setHeaderView(getHeaderView());
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 3, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private View getHeaderView() {
        HomeHeaderView view = new HomeHeaderView(requireContext());
        view.setCustomConfigOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(), CustomConfigActivity.class));
            }
        });
        return view;
    }

    @Override
    protected void initData() {
        super.initData();
        List<GameModel> gameList = MainRepository.getGameList();
        adapter.setList(gameList);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                clickGame(position);
            }
        });
        editText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Long number = getInputNumber();
                if (number == null) {
                    tvEnter.setVisibility(View.GONE);
                } else {
                    tvEnter.setVisibility(View.VISIBLE);
                }
            }
        });
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                return !enterRoom();
            }
            return false;
        });
        tvEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterRoom();
            }
        });
    }

    /** 进入房间 */
    private boolean enterRoom() {
        Long number = getInputNumber();
        if (number == null) return false;
        // TODO: 2022/7/8 dd 
        return true;
    }

    /** 获取输入的房间号 */
    private Long getInputNumber() {
        String content = ViewUtils.getEditTextText(editText);
        if (content == null) return null;
        try {
            return Long.parseLong(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 点击了游戏 */
    private void clickGame(int position) {
        GameModel gameModel = adapter.getItem(position);
        EnterRoomUtils.enterRoom(requireContext(), roomId, gameModel.gameId);
    }

    private static class MyAdapter extends BaseQuickAdapter<GameModel, BaseViewHolder> {
        public MyAdapter() {
            super(R.layout.item_home_game);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, GameModel gameModel) {
            holder.setImageResource(R.id.iv_icon, gameModel.homeGamePicRes);
            holder.setText(R.id.tv_name, gameModel.gameName);
        }
    }

}
