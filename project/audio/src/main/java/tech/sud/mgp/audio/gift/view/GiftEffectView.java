package tech.sud.mgp.audio.gift.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.airbnb.lottie.LottieAnimationView;
import com.opensource.svgaplayer.SVGAImageView;

import tech.sud.mgp.audio.R;
import tech.sud.mgp.audio.gift.manager.GiftDisplayManager;
import tech.sud.mgp.audio.gift.manager.stategy.GiftJsonModel;
import tech.sud.mgp.audio.gift.manager.stategy.GiftJsonStrategy;
import tech.sud.mgp.audio.gift.manager.stategy.GiftMp4Model;
import tech.sud.mgp.audio.gift.manager.stategy.GiftMp4Strategy;
import tech.sud.mgp.audio.gift.manager.stategy.GiftSVGAModel;
import tech.sud.mgp.audio.gift.manager.stategy.GiftSVGAStrategy;
import tech.sud.mgp.audio.gift.model.GiftModel;

public class GiftEffectView extends ConstraintLayout implements LifecycleObserver {

    private LifecycleOwner lifecycleOwner;
    private ConstraintLayout aContainer;
    private ConstraintLayout bContainer;
    private ConstraintLayout cContainer;
    private GiftDisplayManager giftDisplayManager;

    public GiftEffectView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public GiftEffectView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GiftEffectView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.audio_view_gift_effect, this);
        aContainer = findViewById(R.id.gift_a_container);
        bContainer = findViewById(R.id.gift_b_container);
        cContainer = findViewById(R.id.gift_c_container);
        giftDisplayManager = new GiftDisplayManager();
    }

    public void showEffect(GiftModel giftModel) {
        switch (giftModel.animationType) {
            case SVGA: {
                playSVGA(giftModel);
            }
            case MP4: {
                playMp4(giftModel);
            }
            case JSON: {
                playJson(giftModel);
            }
            default: {
                showImage(giftModel);
            }
        }
    }

    private void playSVGA(GiftModel giftModel) {
        GiftSVGAStrategy strategy = new GiftSVGAStrategy();
        GiftSVGAModel svgaModel = new GiftSVGAModel();
        SVGAImageView svgaImageView = creatSVGAImageView();
        svgaModel.setSvgaView(svgaImageView);
        svgaModel.setPath(giftModel.path);

        aContainer.addView(svgaImageView);
        svgaImageView.setVisibility(View.VISIBLE);

        svgaModel.setCallback(result -> {
            switch (result) {
                case START: {

                }
                case PLAYEND: {
                    aContainer.post(() -> {
                        aContainer.removeView(svgaImageView);
                    });
                }
                case PLAYERROR: {
                    aContainer.post(() -> {
                        aContainer.removeView(svgaImageView);
                    });
                }
            }
        });
        giftDisplayManager.showEffect(svgaModel, strategy);
    }

    private void playMp4(GiftModel giftModel) {
        GiftMp4Strategy strategy = new GiftMp4Strategy();
        GiftMp4Model model = new GiftMp4Model();
        GiftVideoView giftVideoView = creatGiftVideoView();
        model.setMp4View(giftVideoView);
        model.setLifecycleOwner(lifecycleOwner);
        model.setPath(giftModel.path);

        aContainer.addView(giftVideoView);
        giftVideoView.setVisibility(View.VISIBLE);

        model.setCallback(result -> {
            switch (result) {
                case START: {

                }
                case PLAYEND: {
                    aContainer.post(() -> {
                        aContainer.removeView(giftVideoView);
                        giftVideoView.detachView();
                        giftVideoView.releasePlayerController();
                    });
                }
                case PLAYERROR: {
                    aContainer.post(() -> {
                        aContainer.removeView(giftVideoView);
                        giftVideoView.detachView();
                        giftVideoView.releasePlayerController();
                    });
                }
            }
        });
        giftDisplayManager.showEffect(model, strategy);
    }

    private void playJson(GiftModel giftModel) {
        GiftJsonStrategy strategy = new GiftJsonStrategy();
        GiftJsonModel model = new GiftJsonModel();
        LottieAnimationView lottieAnimationView = creatLottieAnimationView();
        model.setLottieAnimationView(lottieAnimationView);
        model.setPath(giftModel.path);

        aContainer.addView(lottieAnimationView);
        lottieAnimationView.setVisibility(View.VISIBLE);

        model.setCallback(result -> {
            switch (result) {
                case START: {

                }
                case PLAYEND: {
                    aContainer.post(() -> {
                        aContainer.removeView(lottieAnimationView);
                    });
                }
                case PLAYERROR: {
                    aContainer.post(() -> {
                        aContainer.removeView(lottieAnimationView);
                        lottieAnimationView.cancelAnimation();
                    });
                }
            }
        });
        giftDisplayManager.showEffect(model, strategy);
    }

    private void showImage(GiftModel giftModel) {
        ImageView imageView = creatImageView();
        aContainer.addView(imageView);
        imageView.setVisibility(View.VISIBLE);
        giftDisplayManager.loadDefualtImageInImageView(giftModel.path, true, imageView, result -> {
            switch (result) {
                case START: {

                }
                case PLAYEND: {
                    aContainer.post(() -> {
                        aContainer.removeView(imageView);
                    });
                }
                case PLAYERROR: {
                    aContainer.post(() -> {
                        aContainer.removeView(imageView);
                    });
                }
            }
        });
    }

    private SVGAImageView creatSVGAImageView() {
        SVGAImageView svgaImageView = new SVGAImageView(getContext());
        svgaImageView.setLayoutParams(new LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
        svgaImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        svgaImageView.setLoops(1);
        return svgaImageView;
    }

    private GiftVideoView creatGiftVideoView() {
        GiftVideoView mp4View = new GiftVideoView(getContext());
        mp4View.setLayoutParams(new LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
        return mp4View;
    }

    private AppCompatImageView creatImageView() {
        AppCompatImageView imageView = new AppCompatImageView(getContext());
        imageView.setLayoutParams(new LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }

    private LottieAnimationView creatLottieAnimationView() {
        LottieAnimationView iconView = new LottieAnimationView(getContext());
        iconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iconView.setLayoutParams(new LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
        iconView.setRepeatCount(0);
        iconView.setFrame(1);
        return iconView;
    }

    public void addLifecycleObserver(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
        lifecycleOwner.getLifecycle().addObserver(this);
    }

    public void onDestory() {
        this.lifecycleOwner.getLifecycle().removeObserver(this);
    }
}