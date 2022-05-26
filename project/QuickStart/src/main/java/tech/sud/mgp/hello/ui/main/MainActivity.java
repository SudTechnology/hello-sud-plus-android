package tech.sud.mgp.hello.ui.main;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.widget.view.round.RoundedImageView;
import tech.sud.mgp.hello.service.MainRepository;

/**
 * 主页
 */
public class MainActivity extends BaseActivity {

    private final MyAdapter adapter = new MyAdapter();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        adapter.setHeaderView(getHeaderView());
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private View getHeaderView() {
        LinearLayout container = new LinearLayout(this);

        RoundedImageView iv = new RoundedImageView(this);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setImageResource(R.drawable.ic_quick_start);
        int radius = DensityUtils.dp2px(this, 8);
        iv.setCornerRadius(radius, radius, 0, 0);

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.topMargin = DensityUtils.dp2px(this, 8);
        int marginHorizontal = DensityUtils.dp2px(this, 5);
        params.setMarginStart(marginHorizontal);
        params.setMarginEnd(marginHorizontal);
        container.addView(iv, params);

        return container;
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
    }

    private void clickGame(int position) {
        GameModel model = adapter.getItem(position);
        
    }

    private static class MyAdapter extends BaseQuickAdapter<GameModel, BaseViewHolder> {
        public MyAdapter() {
            super(R.layout.item_home_game);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, GameModel gameModel) {
            holder.setImageResource(R.id.iv_icon, gameModel.gamePic);
            holder.setText(R.id.tv_name, gameModel.gameName);
        }
    }

}
