package tech.sud.mgp.hello.home.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.hello.R;

public class HomeRoomTypeView extends ConstraintLayout {
    public HomeRoomTypeView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HomeRoomTypeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeRoomTypeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        inflate(context, R.layout.view_home_roomtype,this);
    }

    public void setData(){

    }
}
