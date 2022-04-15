package tech.sud.mgp.hello.ui.main.home.adapter;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.ui.main.home.SceneTypeDialog;

public class SceneAdapter extends BaseQuickAdapter<SceneTypeDialog.DialogSceneModel, BaseViewHolder> {

    public SceneAdapter() {
        super(R.layout.item_scene);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, SceneTypeDialog.DialogSceneModel sceneModel) {
        TextView nameTv = baseViewHolder.getView(R.id.scene_name_tv);
        nameTv.setText(sceneModel.model.getSceneName());
        nameTv.setSelected(sceneModel.selected);
    }
}
