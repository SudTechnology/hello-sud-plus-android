package tech.sud.mgp.sudmgpimcore.listener;

import tech.sud.mgp.sudmgpimcore.model.SudIMError;

public interface SudIMRoomLeftCallback {

    void onRoomLeft(String roomID, SudIMError errorInfo);

}
