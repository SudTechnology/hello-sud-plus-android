package tech.sud.mgp.hello.ui.room.audio.example.widget.view.mic;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tech.sud.mgp.hello.ui.room.audio.example.model.AudioRoomMicModel;

public abstract class BaseMicItemView extends FrameLayout {

    public BaseMicItemView(@NonNull Context context) {
        this(context, null);
    }

    public BaseMicItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseMicItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public abstract void convert(int position, AudioRoomMicModel item);

    public abstract void startSoundLevel();

    public abstract void stopSoundLevel();

}