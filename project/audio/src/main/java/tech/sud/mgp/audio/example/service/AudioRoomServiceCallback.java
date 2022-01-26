package tech.sud.mgp.audio.example.service;

import java.util.List;

import tech.sud.mgp.audio.example.model.AudioRoomMicModel;

public interface AudioRoomServiceCallback {

    /**
     * 麦位数据
     *
     * @param list 麦位列表
     */
    void setMicList(List<AudioRoomMicModel> list);

}
