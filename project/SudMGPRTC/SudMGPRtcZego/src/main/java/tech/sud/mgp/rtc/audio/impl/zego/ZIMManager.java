package tech.sud.mgp.rtc.audio.impl.zego;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import im.zego.zim.ZIM;
import im.zego.zim.callback.ZIMEventHandler;
import im.zego.zim.callback.ZIMLoggedInCallback;
import im.zego.zim.callback.ZIMMessageSentCallback;
import im.zego.zim.callback.ZIMRoomEnteredCallback;
import im.zego.zim.callback.ZIMRoomJoinedCallback;
import im.zego.zim.callback.ZIMRoomLeftCallback;
import im.zego.zim.entity.ZIMCommandMessage;
import im.zego.zim.entity.ZIMError;
import im.zego.zim.entity.ZIMMessage;
import im.zego.zim.entity.ZIMMessageSendConfig;
import im.zego.zim.entity.ZIMRoomAdvancedConfig;
import im.zego.zim.entity.ZIMRoomFullInfo;
import im.zego.zim.entity.ZIMRoomInfo;
import im.zego.zim.entity.ZIMTextMessage;
import im.zego.zim.entity.ZIMUserInfo;
import im.zego.zim.enums.ZIMErrorCode;
import im.zego.zim.enums.ZIMMessagePriority;
import im.zego.zim.enums.ZIMRoomEvent;
import im.zego.zim.enums.ZIMRoomState;
import tech.sud.mgp.rtc.audio.core.ISudAudioEngine;

public class ZIMManager {

    private static final String kTag = ZIMManager.class.toString();

    private static ZIMManager zimManager;

    private ZIM zim;

    private OnReceiveRoomMessage receiveRoomMessageCallback;

    private String mRoomID;

    public static ZIMManager sharedInstance() {
        if (zimManager == null) {
            synchronized (ZIMManager.class) {
                if (zimManager == null) {
                    zimManager = new ZIMManager();
                }
            }
        }

        return zimManager;
    }

    public void create(long appID, Application application) {
        if (zim != null) {
            destroy();
        }

        zim = ZIM.create(appID, application);

        zim.setEventHandler(new ZIMEventHandler() {
            @Override
            public void onError(ZIM zim, ZIMError errorInfo) {
                super.onError(zim, errorInfo);
                Log.i(kTag, "onError: " + errorInfo.code);
            }

            @Override
            public void onReceiveRoomMessage(ZIM zim, ArrayList<ZIMMessage> messageList, String fromRoomID) {
                for (ZIMMessage zimMessage : messageList) {
                    if (zimMessage instanceof ZIMTextMessage) {
                        ZIMTextMessage zimTextMessage = (ZIMTextMessage) zimMessage;
                        if (receiveRoomMessageCallback != null && mRoomID.equals(fromRoomID)) {
                            receiveRoomMessageCallback.onReceiveRoomMessage(fromRoomID, zimTextMessage.getSenderUserID(), zimTextMessage.message);
                        }
                    } else if (zimMessage instanceof ZIMCommandMessage) {
                        ZIMCommandMessage zimCommandMessage = (ZIMCommandMessage) zimMessage;
                        if (receiveRoomMessageCallback != null && mRoomID.equals(fromRoomID)) {
                            String command = new String(zimCommandMessage.message);
                            receiveRoomMessageCallback.onReceiveRoomMessage(fromRoomID, zimCommandMessage.getSenderUserID(), command);
                        }
                    }
                }
            }

            public void onRoomStateChanged(ZIM zim, ZIMRoomState state, ZIMRoomEvent event, JSONObject extendedData, String roomID) {
                Log.i(kTag, "onRoomStateChanged: " + state);
            }
        });
    }

    public void destroy() {
        if (zim == null) {
            return;
        }

        zim.destroy();
        zim = null;
        mRoomID = null;
    }

    public void joinRoom(String roomID, String userID, String userName, String token) {
        if (zim == null) {
            return;
        }

        Log.i(kTag, "roomID = " + roomID);

        ZIMUserInfo zimUserInfo = new ZIMUserInfo();
        zimUserInfo.userID = userID;
        zimUserInfo.userName = userName;
        zim.login(zimUserInfo, token, new ZIMLoggedInCallback() {
            @Override
            public void onLoggedIn(ZIMError errorInfo) {
                if (errorInfo.code == ZIMErrorCode.SUCCESS || errorInfo.code == ZIMErrorCode.USER_HAS_ALREADY_LOGGED) {

                    ZIMRoomInfo zimRoomInfo = new ZIMRoomInfo();
                    zimRoomInfo.roomID = roomID;
                    zimRoomInfo.roomName = roomID;

                    ZIMRoomAdvancedConfig zimRoomAdvancedConfig = new ZIMRoomAdvancedConfig();
                    zimRoomAdvancedConfig.roomAttributes = new HashMap<String, String>();

                    zim.enterRoom(zimRoomInfo, zimRoomAdvancedConfig, new ZIMRoomEnteredCallback() {
                        @Override
                        public void onRoomEntered(ZIMRoomFullInfo roomInfo, ZIMError errorInfo) {
                            Log.i(kTag, "enterRoom: " + errorInfo.code);
                            if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                                mRoomID = roomID;
                            }
                        }
                    });
                }
            }
        });
    }

    public void leaveRoom() {
        if (zim == null || mRoomID == null) {
            return;
        }

        zim.leaveRoom(mRoomID, new ZIMRoomLeftCallback() {
            @Override
            public void onRoomLeft(String roomID, ZIMError errorInfo) {
                if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                    mRoomID = null;
                }
            }
        });
    }

    public interface OnReceiveRoomMessage {
        void onReceiveRoomMessage(String roomID, String senderUserID, String message);
    }

    public void setReceiveRoomMessageCallback(OnReceiveRoomMessage callback) {
        receiveRoomMessageCallback = callback;
    }

    public void sendXRoomCommand(String roomID, String command, ISudAudioEngine.SendCommandListener listener) {
        if (zim == null) {
            return;
        }

        ZIMTextMessage zimMessage = new ZIMTextMessage();
        zimMessage.message = command;
        ZIMMessageSendConfig config = new ZIMMessageSendConfig();
        config.priority = ZIMMessagePriority.LOW;

        if (mRoomID != null && !mRoomID.equals(roomID)) {
            zim.joinRoom(roomID, new ZIMRoomJoinedCallback() {
                @Override
                public void onRoomJoined(ZIMRoomFullInfo roomInfo, ZIMError errorInfo) {
                    if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                        zim.sendRoomMessage(zimMessage, roomID, config, new ZIMMessageSentCallback() {
                            @Override
                            public void onMessageSent(ZIMMessage message, ZIMError errorInfo) {
                                Log.i(kTag, "sendRoomMessage: " + errorInfo.code);

                                int errorCode = errorInfo.code.value();
                                zim.leaveRoom(roomID, new ZIMRoomLeftCallback() {
                                    @Override
                                    public void onRoomLeft(String roomID, ZIMError errorInfo) {
                                        if (listener != null) {
                                            listener.onResult(errorCode);
                                        }
                                    }
                                });
                            }
                        });
                    } else {
                        if (listener != null) {
                            listener.onResult(errorInfo.code.value());
                        }
                    }
                }
            });
        } else {
            zim.sendRoomMessage(zimMessage, roomID, config, new ZIMMessageSentCallback() {
                @Override
                public void onMessageSent(ZIMMessage message, ZIMError errorInfo) {
                    Log.i(kTag, "sendRoomMessage: " + errorInfo.code);
                    if (listener != null) {
                        listener.onResult(errorInfo.code.value());
                    }
                }
            });
        }
    }
}
