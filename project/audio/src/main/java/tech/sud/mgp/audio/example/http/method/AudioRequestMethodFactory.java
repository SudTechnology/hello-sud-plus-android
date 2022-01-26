package tech.sud.mgp.audio.example.http.method;

import tech.sud.mgp.common.http.retrofit.RetrofitManager;

public class AudioRequestMethodFactory {

    private static final AudioRequestMethod mAppRequestMethod = RetrofitManager.createMethod(AudioRequestMethod.class);

    public static AudioRequestMethod getMethod() {
        return mAppRequestMethod;
    }

}
