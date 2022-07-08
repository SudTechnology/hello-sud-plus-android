package tech.sud.mgp.hello.service.room.method;

import tech.sud.mgp.hello.common.http.retrofit.RetrofitManager;

public class AudioRequestMethodFactory {

    private static final AudioRequestMethod mAppRequestMethod = RetrofitManager.createMethod(AudioRequestMethod.class);

    public static AudioRequestMethod getMethod() {
        return mAppRequestMethod;
    }

}
