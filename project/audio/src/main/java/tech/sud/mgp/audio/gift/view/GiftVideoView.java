package tech.sud.mgp.audio.gift.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;

import com.ss.ugc.android.alpha_player.IMonitor;
import com.ss.ugc.android.alpha_player.IPlayerAction;
import com.ss.ugc.android.alpha_player.controller.IPlayerController;
import com.ss.ugc.android.alpha_player.controller.PlayerController;
import com.ss.ugc.android.alpha_player.model.AlphaVideoViewType;
import com.ss.ugc.android.alpha_player.model.Configuration;
import com.ss.ugc.android.alpha_player.model.DataSource;

import java.io.File;

import tech.sud.mgp.audio.R;
import tech.sud.mgp.audio.gift.callback.Mp4PlayErrorCallback;
import tech.sud.mgp.audio.gift.video.ExoPlayerImpl;
import tech.sud.mgp.common.utils.ImageLoader;

public class GiftVideoView extends ConstraintLayout {

    private ConstraintLayout mVideoContainer;
    private AppCompatImageView mGiftImage;
    private IPlayerController mPlayerController;
    private static String TAG = "GiftVideoView";

    public GiftVideoView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public GiftVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GiftVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.audio_view_video_gift, this);
        mVideoContainer = findViewById(R.id.video_view);
        mGiftImage = findViewById(R.id.gift_image_iv);
    }

    public void initPlayerController(Context context, LifecycleOwner owner, IPlayerAction playerAction, IMonitor monitor) {
        Configuration configuration = new Configuration(context, owner);
        configuration.setAlphaVideoViewType(AlphaVideoViewType.GL_TEXTURE_VIEW);
        mPlayerController = PlayerController.Companion.get(configuration, new ExoPlayerImpl(context));
        mPlayerController.setPlayerAction(playerAction);
        mPlayerController.setMonitor(monitor);

    }

    public void startVideoGift(String filePath, Mp4PlayErrorCallback playError) {
        if (TextUtils.isEmpty(filePath)) {
            playError.playError();
            return;
        }

        File file = new File(filePath);
        String portraitFileName = file.getName();
        int portraitScaleType = 2;
        String landscapeFileName = file.getName();
        int landscapeScaleType = 2;

        DataSource dataSource = new DataSource();
//        dataSource.setBaseDir(file.getParent());
        dataSource.setPortraitPath(portraitFileName, portraitScaleType);
        dataSource.setLandscapePath(landscapeFileName, landscapeScaleType);
//        dataSource.setLooping(false);
        startDataSource(dataSource, playError);
    }

    public void startDefaultImage(Boolean visibility) {
        if (mGiftImage != null) {
            if (visibility) {
                mGiftImage.setVisibility(View.VISIBLE);
            } else {
                mGiftImage.setVisibility(View.GONE);

            }
        }
    }

    public void showImage(String image) {
        if (mGiftImage != null) {
            ImageLoader.loadImage(mGiftImage, image);
        }
    }

    private void startDataSource(DataSource dataSource, Mp4PlayErrorCallback playError) {
        if (!dataSource.isValid()) {
            Log.e(TAG, "startDataSource: dataSource is invalid.");
            playError.playError();
            return;
        }
        try {
            mPlayerController.start(dataSource);
        } catch (Exception e) {
            playError.playError();
        }
    }

    public void attachView() {
        if (mPlayerController != null) {
            mPlayerController.attachAlphaView(mVideoContainer);
        }
    }

    public void detachView() {
        if (mPlayerController != null) {
            mPlayerController.detachAlphaView(mVideoContainer);
        }
    }

    public void releasePlayerController() {
        if (mPlayerController != null) {
            mPlayerController.detachAlphaView(mVideoContainer);
            mPlayerController.release();
        }
    }

}
