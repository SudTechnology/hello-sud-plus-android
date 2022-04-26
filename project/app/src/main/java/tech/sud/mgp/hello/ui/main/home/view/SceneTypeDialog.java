package tech.sud.mgp.hello.ui.main.home.view;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.service.main.manager.HomeManager;
import tech.sud.mgp.hello.service.main.resp.SceneModel;
import tech.sud.mgp.hello.ui.main.home.adapter.SceneAdapter;

/**
 * 分类展开弹窗
 */
public class SceneTypeDialog extends BaseDialogFragment {

    private TextView dialogTitleTtv;
    private RecyclerView sceneRv;
    private int selected = 0;
    private SceneAdapter adapter = new SceneAdapter();
    private List<DialogSceneModel> models = new ArrayList<>();
    public SelectedSceneListener listener;

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
    protected void initWidget() {
        super.initWidget();
        dialogTitleTtv = mRootView.findViewById(R.id.dialog_title_tv);
        sceneRv = mRootView.findViewById(R.id.scene_rv);
    }

    @Override
    protected void initData() {
        super.initData();
        selected = getArguments().getInt("selectedIndex", 0);
        List<SceneModel> sceneList = HomeManager.getInstance().gameListResp.sceneList;
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
    }

    @Override
    protected int getWidth() {
        return DensityUtils.getAppScreenWidth();
    }

    @Override
    protected int getGravity() {
        return Gravity.TOP;
    }

    private void refreshList(int position) {
//        for (int i = 0; i < models.size(); i++) {
//            models.get(i).selected = (i == position);
//        }
//        adapter.notifyDataSetChanged();

        if (listener != null) {
            listener.selectedScene(position);
        }
        dismiss();
    }

    public class DialogSceneModel {
        public SceneModel model;
        public boolean selected;
    }

    public interface SelectedSceneListener {
        public void selectedScene(int position);
    }

}
