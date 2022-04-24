package tech.sud.mgp.hello.ui.scenes.custom.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.ui.scenes.custom.dialog.CustomConfigSelectDialog;
import tech.sud.mgp.hello.ui.scenes.custom.model.ConfigItemModel;

/**
 * 配置item样式1
 */
public class CustomPageItem1View extends ConstraintLayout {
    private TextView titleTv, subTitleTv, currentStateTv;
    private ConfigItemModel itemModel;

    public CustomPageItem1View(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CustomPageItem1View(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomPageItem1View(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_config_page_item1, this);
        titleTv = findViewById(R.id.title_tv);
        subTitleTv = findViewById(R.id.subtitle_tv);
        currentStateTv = findViewById(R.id.current_state_tv);
        currentStateTv.setOnClickListener(v -> {
            if (itemModel.optionList.size() > 0) {
                CustomConfigSelectDialog dialog = new CustomConfigSelectDialog(getContext(), itemModel.optionList, position -> {
                    switchValue(itemModel.optionList.get(position).title, position);
                });
                dialog.show();
            }
        });
    }

    public void setData(ConfigItemModel model) {
        this.itemModel = model;
        titleTv.setText(itemModel.title);
        subTitleTv.setText(itemModel.subTitle);
        if (itemModel.optionList.size() > 0) {
            for (ConfigItemModel.OptionListBean bean : itemModel.optionList) {
                if (bean.isSeleted) {
                    currentStateTv.setText(bean.title);
                    break;
                }
            }
        }
    }

    public void switchValue(String title, int position) {
        if (itemModel.optionList.size() > 0) {
            for (ConfigItemModel.OptionListBean bean : itemModel.optionList) {
                bean.isSeleted = bean.title.equals(title);
                if (bean.isSeleted) {
                    currentStateTv.setText(bean.title);
                    if (itemModel.type == 0) {
                        itemModel.value = position;
                    } else if (itemModel.type == 1) {
                        itemModel.hide = (position == 1);
                    } else if (itemModel.type == 2) {
                        itemModel.custom = (position == 1);
                    }
                }
            }
        }
    }

}
