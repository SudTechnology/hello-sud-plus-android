package tech.sud.mgp.hello.ui.scenes.crossapp.widget.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseProviderMultiAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.common.widget.adapter.EmptyProvider;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.CrossAppGameListResp;
import tech.sud.mgp.hello.service.main.resp.GameModel;

/**
 * 跨域，选择匹配游戏弹窗
 */
public class SelectMatchGameDialog extends BaseDialogFragment {

    public static final int MODE_MATCH = 0; // 匹配模式
    public static final int MODE_CHANGE = 1; // 修改游戏模式

    private int mode;
    private List<GameModel> selectedList = new ArrayList<>();
    private MyAdapter adapter = new MyAdapter();

    private OnSingleMatchListener onSingleMatchListener;
    private OnTeamMatchListener onTeamMatchListener;
    private OnSelectedListener onSelectedListener;

    private TextView tvSingleMatch;
    private TextView tvTeamMatch;

    public static SelectMatchGameDialog newInstance(int mode) {
        Bundle args = new Bundle();
        args.putInt("mode", mode);
        SelectMatchGameDialog fragment = new SelectMatchGameDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mode = arguments.getInt("mode");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_select_match_game;
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
        return (int) (DensityUtils.getScreenHeight() * 0.75);
    }

    @Override
    protected void customStyle(Window window) {
        super.customStyle(window);
        window.setWindowAnimations(R.style.BottomToTopAnim);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        tvSingleMatch = findViewById(R.id.tv_single_match);
        tvTeamMatch = findViewById(R.id.tv_team_match);

        int spanCount = 4;
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), spanCount);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                Object item = adapter.getItem(position);
                if (item instanceof String) {
                    return spanCount;
                }
                return 1;
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(null);

        if (mode == MODE_MATCH) {
            tvSingleMatch.setVisibility(View.VISIBLE);
            tvTeamMatch.setVisibility(View.VISIBLE);
        } else {
            tvSingleMatch.setVisibility(View.GONE);
            tvTeamMatch.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        getGameList();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> quickAdapter, @NonNull View view, int position) {
                Object item = adapter.getItem(position);
                if (item instanceof GameModel) {
                    GameModel gameModel = (GameModel) item;
                    if (mode == MODE_MATCH) {
                        selectedList.clear();
                        selectedList.add(gameModel);
                        adapter.notifyDataSetChanged();
                    } else {
                        if (onSelectedListener != null) {
                            onSelectedListener.onSelected(gameModel);
                        }
                    }
                }
            }
        });
        tvSingleMatch.setOnClickListener((v) -> {
            if (selectedList.size() == 0 || onSingleMatchListener == null) {
                return;
            }
            GameModel gameModel = selectedList.get(0);
            onSingleMatchListener.onSingleMatch(gameModel);
        });
        tvTeamMatch.setOnClickListener((v) -> {
            if (selectedList.size() == 0 || onTeamMatchListener == null) {
                return;
            }
            GameModel gameModel = selectedList.get(0);
            onTeamMatchListener.onTeamMatch(gameModel);
        });
    }

    private void getGameList() {
        HomeRepository.crossAppGameList(this, new RxCallback<CrossAppGameListResp>() {
            @Override
            public void onSuccess(CrossAppGameListResp resp) {
                super.onSuccess(resp);
                adapter.setList(wrapList(resp));
            }
        });
    }

    private List<Object> wrapList(CrossAppGameListResp resp) {
        if (resp == null) {
            return null;
        }
        List<Object> list = new ArrayList<>();
        if (resp.hotGameList != null && resp.hotGameList.size() > 0) {
            list.add(getString(R.string.hot));
            list.addAll(resp.hotGameList);
            if (selectedList.size() == 0) {
                selectedList.add(resp.hotGameList.get(0));
            }
        }
        if (resp.allGameList != null && resp.allGameList.size() > 0) {
            list.add(getString(R.string.more));
            list.addAll(resp.allGameList);
            if (selectedList.size() == 0) {
                selectedList.add(resp.allGameList.get(0));
            }
        }
        return list;
    }

    public void setOnSingleMatchListener(OnSingleMatchListener onSingleMatchListener) {
        this.onSingleMatchListener = onSingleMatchListener;
    }

    public void setOnTeamMatchListener(OnTeamMatchListener onTeamMatchListener) {
        this.onTeamMatchListener = onTeamMatchListener;
    }

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }

    /**
     * {@link SelectMatchGameDialog#MODE_MATCH}
     * 此模式下才有此监听
     */
    public interface OnSingleMatchListener {
        void onSingleMatch(GameModel gameModel);
    }

    /**
     * {@link SelectMatchGameDialog#MODE_MATCH}
     * 此模式下才有此监听
     */
    public interface OnTeamMatchListener {
        void onTeamMatch(GameModel gameModel);
    }

    /**
     * {@link SelectMatchGameDialog#MODE_CHANGE}
     * 此模式下才有此监听
     */
    public interface OnSelectedListener {
        void onSelected(GameModel gameModel);
    }

    private class MyAdapter extends BaseProviderMultiAdapter<Object> {

        public static final int TYPE_TITLE = 1;
        public static final int TYPE_GAME = 2;

        public MyAdapter() {
            addItemProvider(new MyTitleProvider());
            addItemProvider(new MyGameProvider());
            addItemProvider(new EmptyProvider());
        }

        @Override
        protected int getItemType(@NonNull List<?> list, int i) {
            Object o = list.get(i);
            if (o instanceof String) {
                return TYPE_TITLE;
            }
            if (o instanceof GameModel) {
                return TYPE_GAME;
            }
            return EmptyProvider.TYPE_EMPTY;
        }

    }

    private class MyTitleProvider extends BaseItemProvider<Object> {
        @Override
        public int getItemViewType() {
            return MyAdapter.TYPE_TITLE;
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_cross_app_title;
        }

        @Override
        public void convert(@NonNull BaseViewHolder holder, Object o) {
            String item = (String) o;
            TextView tvName = holder.getView(R.id.tv_name);
            tvName.setText(item);

            // 设置间距
            int position = adapter.getData().indexOf(o);
            if (position > 0) {
                ViewUtils.setMarginTop(tvName, DensityUtils.dp2px(30));
            } else {
                ViewUtils.setMarginTop(tvName, 0);
            }
        }
    }

    private class MyGameProvider extends BaseItemProvider<Object> {

        @Override
        public int getItemViewType() {
            return MyAdapter.TYPE_GAME;
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_cross_app_game;
        }

        @Override
        public void convert(@NonNull BaseViewHolder holder, Object o) {
            GameModel item = (GameModel) o;
            ImageView ivIcon = holder.getView(R.id.iv_icon);
            ImageLoader.loadImage(ivIcon, item.gamePic);

            holder.setText(R.id.tv_name, item.gameName);

            if (mode == MODE_MATCH) {
                holder.setVisible(R.id.view_selected, isSelected(item));
            } else {
                holder.setVisible(R.id.view_selected, false);
            }
        }
    }

    private boolean isSelected(GameModel item) {
        if (selectedList.contains(item)) {
            return true;
        }
        return false;
    }

}
