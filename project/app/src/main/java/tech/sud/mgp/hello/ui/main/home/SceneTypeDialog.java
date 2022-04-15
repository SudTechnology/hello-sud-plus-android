package tech.sud.mgp.hello.ui.main.home;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.utils.ResponseUtils;
import tech.sud.mgp.hello.service.main.manager.HomeManager;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.GetAccountResp;
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
        return DensityUtils.getScreenWidth();
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
