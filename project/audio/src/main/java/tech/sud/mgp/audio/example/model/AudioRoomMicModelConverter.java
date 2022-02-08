package tech.sud.mgp.audio.example.model;

import tech.sud.mgp.audio.example.http.response.RoomMicResp;

public class AudioRoomMicModelConverter {

    public static AudioRoomMicModel conver(RoomMicResp resp) {
        if (resp == null) return null;
        AudioRoomMicModel model = new AudioRoomMicModel();
        model.userId = resp.userId;
        model.micIndex = resp.micIndex;
        model.roleType = resp.roleType;
        model.streamId = resp.streamId;
        return model;
    }

}
