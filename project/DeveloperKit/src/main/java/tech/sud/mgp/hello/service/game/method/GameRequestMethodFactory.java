package tech.sud.mgp.hello.service.game.method;

import tech.sud.mgp.hello.common.http.retrofit.RetrofitManager;

public class GameRequestMethodFactory {

    private static final GameRequestMethod method = RetrofitManager.createMethod(GameRequestMethod.class);

    public static GameRequestMethod getMethod() {
        return method;
    }

}
