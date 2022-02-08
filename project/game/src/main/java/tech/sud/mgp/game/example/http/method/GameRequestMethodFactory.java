package tech.sud.mgp.game.example.http.method;

import tech.sud.mgp.common.http.retrofit.RetrofitManager;

public class GameRequestMethodFactory {

    private static final GameRequestMethod method = RetrofitManager.createMethod(GameRequestMethod.class);

    public static GameRequestMethod getMethod() {
        return method;
    }

}
