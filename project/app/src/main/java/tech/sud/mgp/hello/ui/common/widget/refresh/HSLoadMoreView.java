package tech.sud.mgp.hello.ui.common.widget.refresh;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.loadmore.BaseLoadMoreView;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.hello.R;

/**
 * Describe:  自定义加载更多的View
 */
public class HSLoadMoreView extends BaseLoadMoreView {
    @NonNull
    @Override
    public View getLoadComplete(@NonNull BaseViewHolder holder) {
        return holder.findView(R.id.load_more_load_complete_view);
    }

    @NonNull
    @Override
    public View getLoadEndView(@NonNull BaseViewHolder holder) {
        return holder.findView(R.id.load_more_load_end_view);
    }

    @NonNull
    @Override
    public View getLoadFailView(@NonNull BaseViewHolder holder) {
        return holder.findView(R.id.load_more_load_fail_view);
    }

    @NonNull
    @Override
    public View getLoadingView(@NonNull BaseViewHolder holder) {
        return holder.findView(R.id.load_more_loading_view);
    }

    @NonNull
    @Override
    public View getRootView(@NonNull ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.view_load_more, parent, false);
    }
}
