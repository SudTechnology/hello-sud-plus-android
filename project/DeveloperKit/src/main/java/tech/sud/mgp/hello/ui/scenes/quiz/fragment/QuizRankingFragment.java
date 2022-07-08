package tech.sud.mgp.hello.ui.scenes.quiz.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseFragment;
import tech.sud.mgp.hello.ui.scenes.quiz.widget.QuizRankingTopView;

/**
 * 竞猜排行榜页面
 */
public class QuizRankingFragment extends BaseFragment {

    private int firstIndex;
    private int secondIndex;

    private QuizRankingTopView rankingTopView1;
    private QuizRankingTopView rankingTopView2;
    private QuizRankingTopView rankingTopView3;
    private TextView tvWinCount1;
    private TextView tvWinCount2;
    private TextView tvWinCount3;
    private RecyclerView recyclerView;

    private MyAdapter adapter = new MyAdapter();

    public static QuizRankingFragment newInstance(int firstIndex, int secondIndex) {
        QuizRankingFragment fragment = new QuizRankingFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("firstIndex", firstIndex);
        bundle.putInt("secondIndex", secondIndex);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            firstIndex = arguments.getInt("firstIndex", 0);
            secondIndex = arguments.getInt("secondIndex", 0);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_quiz_ranking;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        recyclerView = findViewById(R.id.recycler_view);
        rankingTopView1 = findViewById(R.id.ranking_top_view_1);
        rankingTopView2 = findViewById(R.id.ranking_top_view_2);
        rankingTopView3 = findViewById(R.id.ranking_top_view_3);
        tvWinCount1 = findViewById(R.id.tv_win_count_1);
        tvWinCount2 = findViewById(R.id.tv_win_count_2);
        tvWinCount3 = findViewById(R.id.tv_win_count_3);

        View topBar = findViewById(R.id.top_bar);
        int statusBarHeight = ImmersionBar.getStatusBarHeight(this);
        topBar.setPadding(topBar.getPaddingLeft(), statusBarHeight, topBar.getPaddingRight(), topBar.getPaddingBottom());

        rankingTopView1.setRankingIcon(R.drawable.ic_ranking_1);
        rankingTopView2.setRankingIcon(R.drawable.ic_ranking_2);
        rankingTopView3.setRankingIcon(R.drawable.ic_ranking_3);

        rankingTopView1.setBorderColor(Color.parseColor("#ffd324"));
        rankingTopView2.setBorderColor(Color.parseColor("#dedfec"));
        rankingTopView3.setBorderColor(Color.parseColor("#ffc877"));

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        super.initData();
        List<RankingModel> list;
        if (firstIndex == 0) {
            String title = getString(R.string.quiz_king);
            String winCause = getString(R.string.quiz_win);
            list = getDatas(title, winCause);
        } else {
            String title = getString(R.string.game_master);
            String winCause = getString(R.string.win_victory);
            list = getDatas(title, winCause);
            // 把排名反过来即可
            reverseDatas(list);
        }
        setTopViewData(rankingTopView1, tvWinCount1, list.remove(0));
        setTopViewData(rankingTopView2, tvWinCount2, list.remove(0));
        setTopViewData(rankingTopView3, tvWinCount3, list.remove(0));
        adapter.setList(list);
    }

    private void reverseDatas(List<RankingModel> list) {
        List<Integer> winCountList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            winCountList.add(list.get(i).winCount);
        }
        Collections.reverse(list);
        for (int i = 0; i < list.size(); i++) {
            RankingModel model = list.get(i);
            model.ranking = i + 1;
            model.winCount = winCountList.get(i);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setTopViewData(QuizRankingTopView view, TextView tvWinCount, RankingModel model) {
        view.setIcon(model.icon);
        view.setName(model.name);
        view.setTitle(model.title);
        tvWinCount.setText(model.winCount + "");
    }

    private List<RankingModel> getDatas(String title, String winCause) {
        List<RankingModel> list = new ArrayList<>();
        list.add(buildRankingModel(1, R.drawable.ic_avatar_1, getString(R.string.name_5), title, 8763, winCause));
        list.add(buildRankingModel(2, R.drawable.ic_avatar_2, getString(R.string.name_6), title, 8598, winCause));
        list.add(buildRankingModel(3, R.drawable.ic_avatar_3, getString(R.string.name_7), title, 8267, winCause));
        list.add(buildRankingModel(4, R.drawable.ic_avatar_4, getString(R.string.name_8), title, 7862, winCause));
        list.add(buildRankingModel(5, R.drawable.ic_avatar_5, getString(R.string.name_9), title, 7623, winCause));
        list.add(buildRankingModel(6, R.drawable.ic_avatar_6, getString(R.string.name_10), title, 6592, winCause));
        list.add(buildRankingModel(7, R.drawable.ic_avatar_7, getString(R.string.name_11), title, 5689, winCause));
        list.add(buildRankingModel(8, R.drawable.ic_avatar_8, getString(R.string.name_12), title, 4369, winCause));
        list.add(buildRankingModel(9, R.drawable.ic_avatar_9, getString(R.string.name_13), title, 3321, winCause));
        list.add(buildRankingModel(10, R.drawable.ic_avatar_10, getString(R.string.name_14), title, 3114, winCause));
        return list;
    }

    private RankingModel buildRankingModel(int ranking, int icon, String name, String title, int winCount, String winCause) {
        RankingModel model = new RankingModel();
        model.ranking = ranking;
        model.icon = icon;
        model.name = name;
        model.title = title;
        model.winCount = winCount;
        model.winCause = winCause;
        return model;
    }

    private static class RankingModel {
        public int ranking;
        public int icon;
        public String name;
        public String title;
        public int winCount;
        public String winCause;
    }

    private static class MyAdapter extends BaseQuickAdapter<RankingModel, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.item_quiz_ranking);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, RankingModel model) {
            holder.setText(R.id.tv_ranking, model.ranking + "");

            ImageView ivIcon = holder.getView(R.id.iv_icon);
            ivIcon.setImageResource(model.icon);

            holder.setText(R.id.tv_name, model.name);
            holder.setText(R.id.tv_title, model.title);
            holder.setText(R.id.tv_win_count, model.winCount + "");
            holder.setText(R.id.tv_win_cause, model.winCause);
        }

    }
}
