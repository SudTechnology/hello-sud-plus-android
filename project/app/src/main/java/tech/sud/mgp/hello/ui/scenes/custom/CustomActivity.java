package tech.sud.mgp.hello.ui.scenes.custom;

import android.view.View;
import android.widget.TextView;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.ui.scenes.audio.activity.AbsAudioRoomActivity;
import tech.sud.mgp.hello.ui.scenes.custom.viewmodel.CustomViewModel;

/**
 * custom场景
 */
public class CustomActivity extends AbsAudioRoomActivity<CustomViewModel> {
    private TextView apiTv;

    @Override
    protected CustomViewModel initGameViewModel() {
        return new CustomViewModel();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_custom;
    }


    @Override
    protected void initWidget() {
        super.initWidget();
        apiTv = findViewById(R.id.api_btn);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        apiTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
