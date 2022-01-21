package tech.sud.mgp.hello.home.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.hello.R;

public class HomeTabView extends LinearLayout {
    public HomeTabView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HomeTabView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeTabView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){

    }

    public void setData(String tabText, @DrawableRes int iconid){
//        ImageView icon = new ImageView(getContext());
//        LayoutParams params = new LayoutParams()
//        TextView text = new TextView(getContext());
//        text.setText(tabText);
//        text.setTextSize(10);
//
//        if (isSelected()){
//
//        }
//        text.setTextColor();
    }


}
