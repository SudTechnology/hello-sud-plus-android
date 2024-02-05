package tech.sud.mgp.hello.ui.main.home.view;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.BarUtils;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.service.main.resp.GameListResp;
import tech.sud.mgp.hello.service.main.resp.SceneModel;
import tech.sud.mgp.hello.ui.main.home.adapter.SceneAdapter;

/**
 * 分类展开弹窗
 */
public class SceneTypeDialog extends BaseDialogFragment {

    private RecyclerView sceneRv;
    private View emptyView;
    private int selected = 0;
    private final SceneAdapter adapter = new SceneAdapter();
    private final List<DialogSceneModel> models = new ArrayList<>();
    public SelectedSceneListener listener;
    public GameListResp mGameListResp;

    public static SceneTypeDialog getInstance(int selected) {
        Bundle bundle = new Bundle();
        bundle.putInt("selectedIndex", selected);
        SceneTypeDialog dialog = new SceneTypeDialog();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_scene_type;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.LightStatusBarDialogTheme);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        sceneRv = mRootView.findViewById(R.id.scene_rv);
        emptyView = mRootView.findViewById(R.id.empty_view);
    }

    @Override
    protected void initData() {
        super.initData();
        Bundle arguments = getArguments();
        if (arguments != null) {
            selected = arguments.getInt("selectedIndex", 0);
        }
        List<SceneModel> sceneList = mGameListResp == null ? null : mGameListResp.sceneList;
        if (sceneList != null && sceneList.size() > 0) {
            for (int i = 0; i < sceneList.size(); i++) {
                DialogSceneModel model = new DialogSceneModel();
                model.selected = (i == selected);
                model.model = sceneList.get(i);
                models.add(model);
            }
        }
        adapter.setList(models);
        sceneRv.setLayoutManager(new GridLayoutManager(getContext(), 2));
        sceneRv.setAdapter(adapter);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        adapter.setOnItemClickListener((adapter, view, position) -> refreshList(position));
        emptyView.setOnClickListener(v -> dismiss());
    }

    @Override
    protected int getWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getHeight() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getGravity() {
        return Gravity.TOP;
    }

    private void refreshList(int position) {
        if (listener != null) {
            listener.selectedScene(position);
        }
        dismiss();
    }

    @Override
    protected void customStyle(Window window) {
        super.customStyle(window);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.y = BarUtils.getStatusBarHeight();
        window.setDimAmount(0f);
        window.setAttributes(attributes);
    }

    public static class DialogSceneModel {
        public SceneModel model;
        public boolean selected;
    }

    public interface SelectedSceneListener {
        void selectedScene(int position);
    }

}
