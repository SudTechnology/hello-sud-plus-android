package tech.sud.mgp.hello.ui.scenes.custom.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.ui.scenes.custom.dialog.CustomApiDialog;

/**
 * API流程view
 */
public class CustomDialogView extends ConstraintLayout {

    private TextView joinGameBtn, readyGameBtn, exitGameBtn,
            cancelReadyBtn, startGameBtn, escapeGameBtn, releaseGameBtn;
    private CustomApiDialog.OperationListener listener;

    public void setListener(CustomApiDialog.OperationListener listener) {
        this.listener = listener;
    }

    public CustomDialogView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CustomDialogView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomDialogView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_custom_dialog, this);
        joinGameBtn = findViewById(R.id.join_game_btn);
        readyGameBtn = findViewById(R.id.ready_game_btn);
        startGameBtn = findViewById(R.id.start_game_btn);
        exitGameBtn = findViewById(R.id.exit_game_btn);
        cancelReadyBtn = findViewById(R.id.cancel_ready_btn);
        escapeGameBtn = findViewById(R.id.escape_game_btn);
        releaseGameBtn = findViewById(R.id.release_game_btn);

        joinGameBtn.setOnClickListener(v -> {
            if (listener!=null){
                listener.operation(0);
            }
        });

        readyGameBtn.setOnClickListener(v -> {
            if (listener!=null){
                listener.operation(1);
            }
        });

        startGameBtn.setOnClickListener(v -> {
            if (listener!=null){
                listener.operation(2);
            }
        });

        exitGameBtn.setOnClickListener(v -> {
            if (listener!=null){
                listener.operation(3);
            }
        });

        cancelReadyBtn.setOnClickListener(v -> {
            if (listener!=null){
                listener.operation(4);
            }
        });

        escapeGameBtn.setOnClickListener(v -> {
            if (listener!=null){
                listener.operation(5);
            }
        });

        releaseGameBtn.setOnClickListener(v -> {
            if (listener!=null){
                listener.operation(6);
            }
        });
    }

}
