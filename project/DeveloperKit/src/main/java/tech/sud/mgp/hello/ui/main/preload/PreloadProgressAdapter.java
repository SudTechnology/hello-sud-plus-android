package tech.sud.mgp.hello.ui.main.preload;

import android.annotation.SuppressLint;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.ui.scenes.common.gift.utils.FileUtils;

public class PreloadProgressAdapter extends BaseQuickAdapter<PreloadModel, BaseViewHolder> {

    public PreloadProgressAdapter() {
        super(R.layout.item_preload_progress);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void convert(@NonNull BaseViewHolder holder, PreloadModel model) {
        ImageView ivIcon = holder.getView(R.id.iv_icon);
        ivIcon.setImageResource(model.iconResId);

        holder.setText(R.id.tv_name, model.gameName);

        TextView tvStatus = holder.getView(R.id.tv_status);
        if (model.errorCode == 0) {
            StringBuilder sb = new StringBuilder();
            String status = getStatus(model);
            if (status != null) {
                sb.append(status);
                sb.append(FileUtils.formatFileSize(model.downloadedSize, 2, false));
                sb.append("/");
                sb.append(FileUtils.formatFileSize(model.totalSize, 2, false));
            }
            tvStatus.setText(sb.toString());
        } else {
            tvStatus.setText(model.errorMsg + "(" + model.errorCode + ")");
        }

        ProgressBar progressBar = holder.getView(R.id.progress_bar);
        progressBar.setProgress(model.progress);
    }

    private String getStatus(PreloadModel model) {
        if (model.status == null) {
            return null;
        }
        switch (model.status) {
            case PKG_DOWNLOAD_WAITING:
                return getContext().getString(R.string.waiting);
            case PKG_DOWNLOAD_STARTED:
                return getContext().getString(R.string.started);
            case PKG_DOWNLOAD_DOWNLOADING:
                return getContext().getString(R.string.downloading);
            case PKG_DOWNLOAD_PAUSE:
                return getContext().getString(R.string.paused);
            case PKG_DOWNLOAD_COMPLETED:
                return getContext().getString(R.string.completed);
            case PKG_DOWNLOAD_CANCELED:
                return getContext().getString(R.string.canceled);
        }
        return null;
    }

}
