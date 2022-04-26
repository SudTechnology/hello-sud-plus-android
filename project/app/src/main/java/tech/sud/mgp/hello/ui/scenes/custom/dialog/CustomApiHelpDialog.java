package tech.sud.mgp.hello.ui.scenes.custom.dialog;

import android.view.Gravity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.ui.scenes.custom.adapter.ApiFlowAdapter;

/**
 * custom 帮助弹窗
 */
public class CustomApiHelpDialog extends BaseDialogFragment {

    private RecyclerView recyclerView;
    private ApiFlowAdapter adapter = new ApiFlowAdapter();
    private List<CustomApiModel> models = new ArrayList<>();


    public static CustomApiHelpDialog getInstance() {
        return new CustomApiHelpDialog();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_custom_api_help;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        String[] titles = {
                getString(R.string.game_api_in_title),
                getString(R.string.game_api_ready_title),
                getString(R.string.game_api_ready_title_cancle),
                getString(R.string.game_api_in_title_false),
                getString(R.string.game_api_playing_title),
                getString(R.string.game_api_playing_title_false),
                getString(R.string.game_api_end_title_false)};
        String[] states = {
                getString(R.string.game_api_in_state),
                getString(R.string.game_api_ready_state),
                getString(R.string.game_api_ready_state_cancle),
                getString(R.string.game_api_in_state_false),
                getString(R.string.game_api_playing_state),
                getString(R.string.game_api_playing_state_false),
                getString(R.string.game_api_end_state_false)};
        recyclerView = mRootView.findViewById(R.id.recycler_view);
        for (int i = 0; i < titles.length; i++) {
            CustomApiModel apiModel = new CustomApiModel();
            apiModel.apiName = titles[i] + ":";
            apiModel.apiState = states[i];
            models.add(apiModel);
        }
        adapter.setList(models);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected int getWidth() {
        return DensityUtils.getAppScreenWidth();
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    public class CustomApiModel {
        public String apiName;
        public String apiState;
    }
}
