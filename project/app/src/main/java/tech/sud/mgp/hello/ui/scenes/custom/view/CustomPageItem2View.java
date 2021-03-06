package tech.sud.mgp.hello.ui.scenes.custom.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.ui.scenes.custom.model.ConfigItemModel;

/**
 * 配置Item样式2
 */
public class CustomPageItem2View extends ConstraintLayout {
    private TextView titleTv, subTitleTv, volumeSize;
    private SeekBar volumeProgress;
    private ConfigItemModel itemModel;
    private CustomPageItem1View.ModifyDataListener listener;

    public void setListener(CustomPageItem1View.ModifyDataListener listener) {
        this.listener = listener;
    }

    public CustomPageItem2View(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CustomPageItem2View(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomPageItem2View(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_config_page_item2, this);
        titleTv = findViewById(R.id.title_tv);
        subTitleTv = findViewById(R.id.subtitle_tv);
        volumeProgress = findViewById(R.id.volume_progress);
        volumeSize = findViewById(R.id.volume_size);
        volumeProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                volumeSize.setText(progress + "");
                itemModel.value = progress;
                if (listener != null) {
                    listener.onChange(itemModel);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void setData(ConfigItemModel model) {
        this.itemModel = model;
        titleTv.setText(itemModel.title);
        subTitleTv.setText(itemModel.subTitle);
        volumeProgress.setMax(100);
        volumeProgress.setProgress(itemModel.value);
        volumeSize.setText(itemModel.value + "");
    }

}
