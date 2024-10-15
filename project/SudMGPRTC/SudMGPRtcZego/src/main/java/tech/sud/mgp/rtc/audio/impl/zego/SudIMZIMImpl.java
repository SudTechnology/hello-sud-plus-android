package tech.sud.mgp.rtc.audio.impl.zego;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;

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
import im.zego.zim.callback.ZIMRoomMemberQueriedCallback;
import im.zego.zim.entity.ZIMCommandMessage;
import im.zego.zim.entity.ZIMError;
import im.zego.zim.entity.ZIMMessage;
import im.zego.zim.entity.ZIMMessageSendConfig;
import im.zego.zim.entity.ZIMRoomAdvancedConfig;
import im.zego.zim.entity.ZIMRoomFullInfo;
import im.zego.zim.entity.ZIMRoomInfo;
import im.zego.zim.entity.ZIMRoomMemberQueryConfig;
import im.zego.zim.entity.ZIMTextMessage;
import im.zego.zim.entity.ZIMUserInfo;
import im.zego.zim.enums.ZIMConnectionEvent;
import im.zego.zim.enums.ZIMConnectionState;
import im.zego.zim.enums.ZIMErrorCode;
import im.zego.zim.enums.ZIMMessagePriority;
import im.zego.zim.enums.ZIMRoomEvent;
import im.zego.zim.enums.ZIMRoomState;
import tech.sud.mgp.sudmgpimcore.ISudIM;
import tech.sud.mgp.sudmgpimcore.listener.SendXCommandListener;
import tech.sud.mgp.sudmgpimcore.listener.SudIMListener;
import tech.sud.mgp.sudmgpimcore.listener.SudIMRoomLeftCallback;
import tech.sud.mgp.sudmgpimcore.model.SudIMConnectionEvent;
import tech.sud.mgp.sudmgpimcore.model.SudIMConnectionState;
import tech.sud.mgp.sudmgpimcore.model.SudIMError;
import tech.sud.mgp.sudmgpimcore.model.SudIMRoomFullInfo;
import tech.sud.mgp.sudmgpimcore.model.SudIMRoomState;

public class SudIMZIMImpl implements ISudIM {

    private static final String kTag = SudIMZIMImpl.class.toString();

    private ZIM zim;
    private OnReceiveRoomMessage receiveRoomMessageCallback;
    private String mRoomID;
    private SudIMListener mSudIMListener;

    @Override
    public void init(Context context, String appId, String userId, SudIMListener imListener) {
        long appID = 0;
        try {
            appID = Long.parseLong(appId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        create(appID, Utils.getApp(), imListener);
        setReceiveRoomMessageCallback(new SudIMZIMImpl.OnReceiveRoomMessage() {
            @Override
            public void onReceiveRoomMessage(String roomID, String senderUserID, String message) {
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SudIMListener listener = imListener;
                        if (listener != null) {
                            listener.onRecvXRoomCommand(roomID, senderUserID, message);
                        }
                    }
                });
            }
        });
    }

    private void create(long appID, Application application, SudIMListener imListener) {
        if (zim != null) {
            destroy();
        }
        mSudIMListener = imListener;
        zim = ZIM.create(appID, application);

        zim.setEventHandler(new ZIMEventHandler() {
            @Override
            public void onConnectionStateChanged(ZIM zim, ZIMConnectionState state, ZIMConnectionEvent event, JSONObject extendedData) {
                super.onConnectionStateChanged(zim, state, event, extendedData);
                if (imListener != null) {
                    imListener.onConnectionStateChanged(convertSudIMConnectionState(state), convertSudIMConnectionEvent(event), extendedData);
                }
            }

            @Override
            public void onError(ZIM zim, ZIMError errorInfo) {
                super.onError(zim, errorInfo);
                Log.i(kTag, "onError: " + errorInfo.code);
                if (imListener != null) {
                    imListener.onError(convertSudIMError(errorInfo));
                }
            }

            @Override
            public void onReceiveRoomMessage(ZIM zim, ArrayList<ZIMMessage> messageList, String fromRoomID) {
                for (ZIMMessage zimMessage : messageList) {
                    if (zimMessage instanceof ZIMTextMessage) {
                        ZIMTextMessage zimTextMessage = (ZIMTextMessage) zimMessage;
                        if (receiveRoomMessageCallback != null && mRoomID != null && mRoomID.equals(fromRoomID)) {
                            receiveRoomMessageCallback.onReceiveRoomMessage(fromRoomID, zimTextMessage.getSenderUserID(), zimTextMessage.message);
                        }
                    } else if (zimMessage instanceof ZIMCommandMessage) {
                        ZIMCommandMessage zimCommandMessage = (ZIMCommandMessage) zimMessage;
                        if (receiveRoomMessageCallback != null && mRoomID != null && mRoomID.equals(fromRoomID)) {
                            String command = new String(zimCommandMessage.message);
                            receiveRoomMessageCallback.onReceiveRoomMessage(fromRoomID, zimCommandMessage.getSenderUserID(), command);
                        }
                    }
                }
            }

            public void onRoomStateChanged(ZIM zim, ZIMRoomState state, ZIMRoomEvent event, JSONObject extendedData, String roomID) {
                Log.i(kTag, "onRoomStateChanged: " + state);
                if (imListener != null) {
                    imListener.onRoomStateChanged(convertSudIMRoomState(state), roomID);
                }
            }
        });
    }

    @Override
    public void destroy() {
        mSudIMListener = null;
        setReceiveRoomMessageCallback(null);

        if (zim == null) {
            return;
        }

        zim.destroy();
        zim = null;
        mRoomID = null;
    }

    @Override
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
                if (mSudIMListener != null) {
                    mSudIMListener.onLoggedIn(convertSudIMError(errorInfo));
                }
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
                            if (mSudIMListener != null) {
                                mSudIMListener.onRoomEntered(convertSudIMRoomFullInfo(roomInfo), convertSudIMError(errorInfo));
                            }
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

    @Override
    public void leaveRoom(String roomId, SudIMRoomLeftCallback callback) {
        if (zim == null || roomId == null) {
            return;
        }

        zim.leaveRoom(roomId, new ZIMRoomLeftCallback() {
            @Override
            public void onRoomLeft(String roomID, ZIMError errorInfo) {
                if (callback != null) {
                    callback.onRoomLeft(roomID, convertSudIMError(errorInfo));
                }
            }
        });
    }

    public void queryRoomMemberList(String roomID, ZIMRoomMemberQueryConfig config, ZIMRoomMemberQueriedCallback callback) {
        if (zim != null) {
            zim.queryRoomMemberList(roomID, config, callback);
        }
    }

    public interface OnReceiveRoomMessage {
        void onReceiveRoomMessage(String roomID, String senderUserID, String message);
    }

    private void setReceiveRoomMessageCallback(OnReceiveRoomMessage callback) {
        receiveRoomMessageCallback = callback;
    }

    @Override
    public void sendXRoomCommand(String roomID, String command, SendXCommandListener listener) {
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
                            public void onMessageAttached(ZIMMessage message) {

                            }

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
                public void onMessageAttached(ZIMMessage message) {
                }

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

    @Override
    public void checkConnected(String roomId, String userId) {
        if (roomId == null || userId == null) {
            return;
        }
        ZIMRoomMemberQueryConfig config = new ZIMRoomMemberQueryConfig();
        config.count = 10;
        queryRoomMemberList(roomId, config, new ZIMRoomMemberQueriedCallback() {
            @Override
            public void onRoomMemberQueried(String roomID, ArrayList<ZIMUserInfo> memberList, String nextFlag, ZIMError errorInfo) {
                LogUtils.file("zim:onRoomMemberQueried:" + errorInfo.code + " msg:" + errorInfo.message);
                if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                    boolean existsZim = selfExistsZim(memberList);
                    LogUtils.file("zim:onRoomMemberQueried-existsZim:" + existsZim);
                    if (!existsZim) {
                        // 不存在此房间内，离开房间再次进入该房间
                        if (mSudIMListener != null) {
                            mSudIMListener.onRejoinRoom(roomID, userId);
                        }
                    }
                }
            }

            private boolean selfExistsZim(ArrayList<ZIMUserInfo> memberList) {
                if (memberList != null) {
                    for (ZIMUserInfo zimUserInfo : memberList) {
                        if (zimUserInfo != null && userId.equals(zimUserInfo.userID)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    private SudIMConnectionState convertSudIMConnectionState(ZIMConnectionState state) {
        if (state == null) {
            return SudIMConnectionState.UNKNOWN;
        }
        return SudIMConnectionState.getIMConnectionState(state.value());
    }

    private SudIMConnectionEvent convertSudIMConnectionEvent(ZIMConnectionEvent state) {
        if (state == null) {
            return SudIMConnectionEvent.UNKNOWN;
        }
        return SudIMConnectionEvent.getIMConnectionEvent(state.value());
    }

    private SudIMError convertSudIMError(ZIMError state) {
        SudIMError sudIMError = new SudIMError();
        if (state == null) {
            return sudIMError;
        }
        sudIMError.code = state.code == null ? -1 : state.code.value();
        sudIMError.message = state.message;
        return sudIMError;
    }

    private SudIMRoomState convertSudIMRoomState(ZIMRoomState state) {
        if (state == null) {
            return SudIMRoomState.UNKNOWN;
        }
        return SudIMRoomState.getIMRoomState(state.value());
    }

    private SudIMRoomFullInfo convertSudIMRoomFullInfo(ZIMRoomFullInfo state) {
        SudIMRoomFullInfo sudIMRoomFullInfo = new SudIMRoomFullInfo();
        if (state == null || state.baseInfo == null) {
            return null;
        }
        sudIMRoomFullInfo.roomID = state.baseInfo.roomID;
        sudIMRoomFullInfo.roomName = state.baseInfo.roomName;
        return sudIMRoomFullInfo;
    }
}
