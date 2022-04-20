package tech.sud.mgp.hello.ui.scenes.orderentertainment.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.widget.dialog.SimpleChooseDialog;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.GameViewModel;

/**
 * 点单场景vm
 */
public class OrderViewModel extends GameViewModel {

    //1 邀请弹窗确定 2拒绝弹窗确定 3结束弹窗确定
    public MutableLiveData<Integer> dialogResult = new MutableLiveData<>();
    private SimpleChooseDialog inviteDialog;//邀请弹窗
    private SimpleChooseDialog operateDialog;//拒绝弹窗
    private SimpleChooseDialog finishDialog;//结束弹窗

    /** 邀请弹窗 */
    public void inviteDialog(Context context, String nickName, String gameName) {
        if (inviteDialog == null || !inviteDialog.isShowing()) {
            inviteDialog = new SimpleChooseDialog(context, context.getString(R.string.order_invite_conent, nickName, gameName),
                    context.getString(R.string.order_invite_left_btn),
                    context.getString(R.string.order_invite_right_btn));
            inviteDialog.setOnChooseListener(index -> {
                if (index == 1) {
                    dialogResult.postValue(1);
                }
                inviteDialog.dismiss();
                inviteDialog = null;
            });
            inviteDialog.show();
        }
    }

    /** 拒绝弹窗 */
    public void operateDialog(Context context, String nickName) {
        if (operateDialog == null || !operateDialog.isShowing()) {
            operateDialog = new SimpleChooseDialog(context, context.getString(R.string.order_result_conent, nickName),
                    context.getString(R.string.cancel), context.getString(R.string.confirm));
            operateDialog.setOnChooseListener(index -> {
                if (index == 1) {
                    dialogResult.postValue(2);
                }
                operateDialog.dismiss();
                operateDialog = null;
            });
            operateDialog.show();
        }
    }

    /** 结束弹窗 */
    public void finishDialog(Context context) {
        if (finishDialog == null || !finishDialog.isShowing()) {
            finishDialog = new SimpleChooseDialog(context, context.getString(R.string.order_finish_conent),
                    context.getString(R.string.order_finish_left_btn), context.getString(R.string.order_finish_right_btn));
            finishDialog.setOnChooseListener(index -> {
                if (index == 1) {
                    dialogResult.postValue(3);
                }
                finishDialog.dismiss();
                finishDialog = null;
            });
            finishDialog.show();
        }
    }

}