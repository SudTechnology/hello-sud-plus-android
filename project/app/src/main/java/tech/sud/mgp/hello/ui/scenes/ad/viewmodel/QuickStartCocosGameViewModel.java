package tech.sud.mgp.hello.ui.scenes.ad.viewmodel;

import android.view.View;

import androidx.lifecycle.MutableLiveData;

public class QuickStartCocosGameViewModel extends BaseCocosGameViewModel {

    public final MutableLiveData<View> gameViewLiveData = new MutableLiveData<>();

    @Override
    protected void onAddGameView(View gameView) {
        gameViewLiveData.setValue(gameView);
    }

    @Override
    protected void onRemoveGameView() {
        gameViewLiveData.setValue(null);
    }

}
