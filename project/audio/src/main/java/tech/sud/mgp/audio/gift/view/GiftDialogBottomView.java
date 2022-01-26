package tech.sud.mgp.audio.gift.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.audio.R;
import tech.sud.mgp.audio.gift.callback.PresentClickCallback;

public class GiftDialogBottomView extends ConstraintLayout {

    private TextView presentTv,sendGiftCountTv;
    private ImageView arrowIv;
    private PresentClickCallback callback;

    public GiftDialogBottomView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public GiftDialogBottomView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GiftDialogBottomView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        inflate(context, R.layout.audio_view_gift_send_bottom,this);
        presentTv = findViewById(R.id.present_tv);
        sendGiftCountTv = findViewById(R.id.send_gift_count_tv);
        arrowIv = findViewById(R.id.send_gift_bottom_arrow_iv);
        presentTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback!=null){
                    callback.present(1);
                }
            }
        });
    }
}
