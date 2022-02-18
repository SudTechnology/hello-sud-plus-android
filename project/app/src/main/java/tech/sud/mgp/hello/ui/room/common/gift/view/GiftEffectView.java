package tech.sud.mgp.hello.ui.room.common.gift.view;

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

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.ui.room.common.gift.manager.GiftDisplayManager;
import tech.sud.mgp.hello.ui.room.common.gift.manager.stategy.lottie.GiftJsonModel;
import tech.sud.mgp.hello.ui.room.common.gift.manager.stategy.lottie.GiftJsonStrategy;
import tech.sud.mgp.hello.ui.room.common.gift.manager.stategy.mp4.GiftMp4Model;
import tech.sud.mgp.hello.ui.room.common.gift.manager.stategy.mp4.GiftMp4Strategy;
import tech.sud.mgp.hello.ui.room.common.gift.manager.stategy.svga.GiftSVGAModel;
import tech.sud.mgp.hello.ui.room.common.gift.manager.stategy.svga.GiftSVGAStrategy;
import tech.sud.mgp.hello.ui.room.common.gift.manager.stategy.webp.GiftWebpModel;
import tech.sud.mgp.hello.ui.room.common.gift.manager.stategy.webp.GiftWebpStrategy;
import tech.sud.mgp.hello.ui.room.common.gift.model.GiftModel;

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
        inflate(context, R.layout.view_gift_effect, this);
        aContainer = findViewById(R.id.gift_a_container);
        bContainer = findViewById(R.id.gift_b_container);
        cContainer = findViewById(R.id.gift_c_container);
        giftDisplayManager = new GiftDisplayManager(context);
    }

    public void showEffect(GiftModel giftModel) {
        switch (giftModel.animationType) {
            case SVGA: {
                playSVGA(giftModel);
                break;
            }
            case MP4: {
                playMp4(giftModel);
                break;
            }
            case JSON: {
                playJson(giftModel);
                break;
            }
            case WEBP: {
                playWebp(giftModel);
                break;
            }
            default: {
                showImage(giftModel);
                break;
            }
        }
    }

    private void playSVGA(GiftModel giftModel) {
        GiftSVGAStrategy strategy = new GiftSVGAStrategy();
        GiftSVGAModel svgaModel = new GiftSVGAModel();
        SVGAImageView svgaImageView = creatSVGAImageView();
        svgaModel.setSvgaView(svgaImageView);
        svgaModel.setResId(giftModel.resId);

        aContainer.addView(svgaImageView);
        svgaImageView.setVisibility(View.VISIBLE);

        svgaModel.setPlayResultListener(result -> {
            switch (result) {
                case START: {
                    break;
                }
                //END和ERROR状态根据具体业务需求去处理，这里资源文件一定存在所以统一处理了
                case PLAYEND:
                case PLAYERROR: {
                    aContainer.post(() -> {
                        aContainer.removeView(svgaImageView);
                    });
                    break;
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
        model.setResId(giftModel.resId);
        model.setPath(giftModel.path);

        aContainer.addView(giftVideoView);
        giftVideoView.setVisibility(View.VISIBLE);

        model.setPlayResultListener(result -> {
            switch (result) {
                case START: {
                    break;
                }
                case PLAYERROR:
                    showImage(giftModel);
                    break;
                case PLAYEND: {
                    aContainer.post(() -> {
                        aContainer.removeView(giftVideoView);
                        giftVideoView.detachView();
                        giftVideoView.releasePlayerController();
                    });
                    break;
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

        model.setPlayResultListener(result -> {
            switch (result) {
                case START: {
                    break;
                }
                case PLAYEND:
                case PLAYERROR: {
                    aContainer.post(() -> {
                        aContainer.removeView(lottieAnimationView);
                        lottieAnimationView.cancelAnimation();
                    });
                    break;
                }
            }
        });
        giftDisplayManager.showEffect(model, strategy);
    }


    private void playWebp(GiftModel giftModel) {
        GiftWebpStrategy strategy = new GiftWebpStrategy();
        GiftWebpModel model = new GiftWebpModel();
        ImageView imageView = creatImageView();
        model.imageView = imageView;
        model.setResId(giftModel.resId);

        aContainer.addView(imageView);
        imageView.setVisibility(View.VISIBLE);

        model.setPlayResultListener(result -> {
            switch (result) {
                case START: {
                    break;
                }
                case PLAYERROR:
                    showImage(giftModel);
                    break;
                case PLAYEND: {
                    aContainer.post(() -> {
                        aContainer.removeView(imageView);
                    });
                    break;
                }
            }
        });
        giftDisplayManager.showEffect(model, strategy);
    }

    private void showImage(GiftModel giftModel) {
        ImageView imageView = creatImageView();
        aContainer.addView(imageView);
        imageView.setVisibility(View.VISIBLE);
        giftDisplayManager.loadDefualtImageInImageView(giftModel.giftImage, true, imageView, result -> {
            switch (result) {
                case START: {
                    break;
                }
                case PLAYEND:
                case PLAYERROR: {
                    aContainer.post(() -> {
                        aContainer.removeView(imageView);
                    });
                    break;
                }
            }
        });
    }

    private SVGAImageView creatSVGAImageView() {
        SVGAImageView svgaImageView = new SVGAImageView(getContext());
        svgaImageView.setLayoutParams(new LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
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
        return imageView;
    }

    private LottieAnimationView creatLottieAnimationView() {
        LottieAnimationView iconView = new LottieAnimationView(getContext());
        iconView.setLayoutParams(new LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
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