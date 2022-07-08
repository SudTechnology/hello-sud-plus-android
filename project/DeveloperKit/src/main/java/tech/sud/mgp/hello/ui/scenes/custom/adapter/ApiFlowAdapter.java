package tech.sud.mgp.hello.ui.scenes.custom.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.ui.scenes.custom.dialog.CustomApiHelpDialog;

public class ApiFlowAdapter extends BaseQuickAdapter<CustomApiHelpDialog.CustomApiModel, BaseViewHolder> {

    public ApiFlowAdapter() {
        super(R.layout.item_api_flow);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, CustomApiHelpDialog.CustomApiModel customApiModel) {
        baseViewHolder.setText(R.id.title_tv, customApiModel.apiName);
        baseViewHolder.setText(R.id.content_tv, customApiModel.apiState);
    }
}
