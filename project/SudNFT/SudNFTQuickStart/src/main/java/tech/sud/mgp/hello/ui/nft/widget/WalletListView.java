package tech.sud.mgp.hello.ui.nft.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.hello.R;

/**
 * nft列表View
 */
public class WalletListView extends ConstraintLayout {

    private TextView tvOverseas;
    private TextView tvInternal;

    public WalletListView(@NonNull Context context) {
        this(context, null);
    }

    public WalletListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WalletListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        inflate(context, R.layout.view_wallet_list, this);
        tvOverseas = findViewById(R.id.tv_overseas);
        tvInternal = findViewById(R.id.tv_internal);
    }

    public void setOverseasOnClickListener(OnClickListener listener) {
        tvOverseas.setOnClickListener(listener);
    }

    public void setInternalOnClickListener(OnClickListener listener) {
        tvInternal.setOnClickListener(listener);
    }

}
