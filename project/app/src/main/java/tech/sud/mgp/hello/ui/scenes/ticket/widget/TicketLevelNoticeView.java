package tech.sud.mgp.hello.ui.scenes.ticket.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 门票等级选择页面的通知栏
 */
public class TicketLevelNoticeView extends androidx.appcompat.widget.AppCompatTextView {

    private String[] datas;
    private int position;
    private long changeDuration = 3000; // 切换的间隔

    public TicketLevelNoticeView(@NonNull Context context) {
        this(context, null);
    }

    public TicketLevelNoticeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TicketLevelNoticeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDatas(String[] datas) {
        this.datas = datas;
        setNotice();
        startChangeTask();
    }

    private void startChangeTask() {
        removeCallbacks(changeTask);
        postDelayed(changeTask, changeDuration);
    }

    private Runnable changeTask = new Runnable() {
        @Override
        public void run() {
            setNotice();
            startChangeTask();
        }
    };

    private void setNotice() {
        setText(findNotice());
        position++;
    }

    private String findNotice() {
        if (datas == null || datas.length == 0) return null;
        if (position >= datas.length) {
            position = 0;
        }
        return datas[position];
    }

}
