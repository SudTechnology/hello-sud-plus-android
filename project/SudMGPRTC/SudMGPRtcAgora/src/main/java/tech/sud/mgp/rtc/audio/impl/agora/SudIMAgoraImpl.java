package tech.sud.mgp.rtc.audio.impl.agora;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;

import java.util.ArrayList;
import java.util.List;

import io.agora.rtm.ErrorInfo;
import io.agora.rtm.MessageEvent;
import io.agora.rtm.PresenceEvent;
import io.agora.rtm.PublishOptions;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmConfig;
import io.agora.rtm.RtmConstants;
import io.agora.rtm.RtmEventListener;
import io.agora.rtm.SnapshotInfo;
import io.agora.rtm.SubscribeOptions;
import io.agora.rtm.UserState;
import tech.sud.mgp.sudmgpimcore.ISudIM;
import tech.sud.mgp.sudmgpimcore.listener.SendXCommandListener;
import tech.sud.mgp.sudmgpimcore.listener.SudIMListener;
import tech.sud.mgp.sudmgpimcore.listener.SudIMRoomLeftCallback;

public class SudIMAgoraImpl implements ISudIM {

    private RtmClient mRtmClient;
    private SudIMListener mSudIMListener;
    private String mRoomId;
    private String mToken;
    private String mUserId;
    private boolean isLoginSuccess;
    private List<CommandModel> mCommandModelList = new ArrayList<>();
    private List<String> userIdList = new ArrayList<>();

    @Override
    public void init(Context context, String appId, String userId, SudIMListener imListener) {
        mSudIMListener = imListener;
        try {
            RtmConfig config = new RtmConfig.Builder(appId, userId)
                    .eventListener(rtmClientListener)
                    .build();
            mRtmClient = RtmClient.create(config);
        } catch (Exception e) {
            e.printStackTrace();
            log("agora im init error:" + e);
        }
    }

    @Override
    public void joinRoom(String roomID, String userID, String userName, String token) {
        if (mRtmClient == null) {
            return;
        }
        mRoomId = roomID;
        mToken = token;
        mUserId = userID;
        // 登录Agora RTM 系统
        login(token, userID, new IAgoraImLoginListener() {
            @Override
            public void onSuccess() {
                isLoginSuccess = true;
                sendMessageCacheList();

                SubscribeOptions options = new SubscribeOptions();
                mRtmClient.subscribe(roomID, options, new ResultCallback<Void>() {
                    @Override
                    public void onSuccess(Void responseInfo) {
                        log("agora im subscribe onSuccess:" + roomID);
                    }

                    @Override
                    public void onFailure(ErrorInfo errorInfo) {
                        int code = getErrorInfoCode(errorInfo);
                        log("agora im login join error code:" + code + " roomId:" + roomID);
                    }
                });
            }

            @Override
            public void onFailure(int code) {
                log("agora im login failure code:" + code);
            }
        });
    }

    // 将队列中的消息发送出去
    private void sendMessageCacheList() {
        if (!isLoginSuccess) {
            return;
        }
        for (CommandModel model : mCommandModelList) {
            sendXRoomCommand(model);
        }
    }

    private void login(String token, String userId, IAgoraImLoginListener listener) {
        if (mRtmClient == null) {
            if (listener != null) {
                listener.onFailure(-1);
            }
            return;
        }
        log("agora login ");
        mRtmClient.login(token, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                log("agora im login success ");
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                log("agora im login error:" + errorInfo);
                int code = errorInfo == null || errorInfo.getErrorCode() == null ? -1 : RtmConstants.RtmErrorCode.getValue(errorInfo.getErrorCode());
                if (listener != null) {
                    listener.onFailure(code);
                }
            }
        });
    }

    @Override
    public void leaveRoom(String roomId, SudIMRoomLeftCallback callback) {
        if (mRtmClient == null) {
            return;
        }
        mRtmClient.unsubscribe(roomId, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                log("agora im unsubscribe success:" + " roomId:" + roomId);
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                log("agora im unsubscribe error code:" + getErrorInfoCode(errorInfo) + " roomId:" + roomId);
            }
        });
    }

    @Override
    public void sendXRoomCommand(String roomID, String command, SendXCommandListener listener) {
        if (!isLoginSuccess) {
            CommandModel model = new CommandModel();
            model.roomId = roomID;
            model.command = command;
            model.listener = listener;
            mCommandModelList.add(model);
            return;
        }
        sendMessage(roomID, command, listener);
    }

    private void sendXRoomCommand(CommandModel model) {
        if (model == null) {
            return;
        }
        sendMessage(model.roomId, model.command, model.listener);
    }

    private class CommandModel {
        public String roomId;
        public String command;
        public SendXCommandListener listener;
    }

    private int getErrorInfoCode(ErrorInfo errorInfo) {
        return errorInfo == null || errorInfo.getErrorCode() == null ? -1 : RtmConstants.RtmErrorCode.getValue(errorInfo.getErrorCode());
    }

    private void sendMessage(String roomId, String command, SendXCommandListener listener) {
        if (mRtmClient == null || roomId == null) {
            if (listener != null) {
                listener.onResult(-1);
            }
            return;
        }
        PublishOptions options = new PublishOptions();
        options.setCustomType("");
        mRtmClient.publish(roomId, command, options, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                String text = "Message sent to channel " + roomId + " : " + command + "\n";
                log(text);
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                String text = "Message fails to send to channel " + roomId + " Error: " + errorInfo + "\n";
                log(text);
            }
        });
    }

    @Override
    public void checkConnected(String roomId, String userId) {
    }

    @Override
    public void destroy() {
        if (mRtmClient != null) {
            isLoginSuccess = false;
            mRtmClient.logout(new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void responseInfo) {
                    log("agora logout success");
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    log("agora logout onFailre" + errorInfo);
                }
            });
            RtmClient.release();
            mRtmClient = null;
        }
    }

    // 云信令回调
    // 新版sdk暂无此数量回调
//    private final RtmChannelListener rtmChannelListener = new SimpleRtmChannelListener() {
//        @Override
//        public void onMemberCountUpdated(int memberCount) {
//            ThreadUtils.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (mSudIMListener != null) {
//                        mSudIMListener.onRoomOnlineUserCountUpdate(memberCount);
//                    }
//                }
//            });
//        }
//    };

    /** 声网RTM客户端监听 */
    private final RtmEventListener rtmClientListener = new RtmEventListener() {
        @Override
        public void onMessageEvent(MessageEvent event) {
            if (event == null) {
                return;
            }
            String message = event.getMessage() == null || event.getMessage().getData() == null ? null : event.getMessage().getData().toString();
            RtmEventListener.super.onMessageEvent(event);
            log("收到消息：" + message);
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mSudIMListener != null) {
                        mSudIMListener.onRecvXRoomCommand(event.getChannelName(), event.getPublisherId(), message);
                    }
                }
            });
        }

        @Override
        public void onPresenceEvent(PresenceEvent event) {
            RtmEventListener.super.onPresenceEvent(event);
            log("收到onPresenceEvent :" + event);
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (event == null) {
                        return;
                    }
                    String userId = event.getPublisherId();
                    if (event.getEventType() == RtmConstants.RtmPresenceEventType.REMOTE_JOIN) {
                        if (!userIdList.contains(userId)) {
                            userIdList.add(userId);
                        }
                    } else if (event.getEventType() == RtmConstants.RtmPresenceEventType.REMOTE_LEAVE) {
                        userIdList.remove(event.getPublisherId());
                    } else if (event.getEventType() == RtmConstants.RtmPresenceEventType.SNAPSHOT) {
                        SnapshotInfo snapshot;
                        if ((snapshot = event.getSnapshot()) != null && snapshot.getUserStateList() != null) {
                            ArrayList<UserState> userStateList = snapshot.getUserStateList();
                            for (UserState userState : userStateList) {
                                if (!userIdList.contains(userState.getUserId())) {
                                    userIdList.add(userState.getUserId());
                                }
                            }
                        }
                    }

                    if (mSudIMListener != null) {
                        mSudIMListener.onRoomOnlineUserCountUpdate(userIdList.size());
                    }
                }
            });
        }
    };

    private interface IAgoraImLoginListener {
        void onSuccess();

        void onFailure(int code);
    }

    private void log(String msg) {
        LogUtils.d(msg);
        LogUtils.file(msg);
    }

}
