package tech.sud.mgp.hello.ui.room.audio.example.http.method;

import tech.sud.mgp.hello.common.http.retrofit.RetrofitManager;

public class AudioRequestMethodFactory {

    private static final AudioRequestMethod mAppRequestMethod = RetrofitManager.createMethod(AudioRequestMethod.class);

    public static AudioRequestMethod getMethod() {
        return mAppRequestMethod;
    }

}
