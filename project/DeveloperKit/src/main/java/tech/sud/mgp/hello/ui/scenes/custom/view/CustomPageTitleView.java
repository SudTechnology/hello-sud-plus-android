package tech.sud.mgp.hello.ui.scenes.custom.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.ui.scenes.custom.model.ConfigItemModel;

/**
 * 配置页标题样式
 */
public class CustomPageTitleView extends ConstraintLayout {
    private TextView configTypeTv;

    public CustomPageTitleView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CustomPageTitleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomPageTitleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_custom_page_title, this);
        configTypeTv = findViewById(R.id.config_type_name);
    }

    public void setData(ConfigItemModel model) {
        configTypeTv.setText(model.title);
    }
}
