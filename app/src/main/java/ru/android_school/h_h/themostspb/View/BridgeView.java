package ru.android_school.h_h.themostspb.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import ru.android_school.h_h.themostspb.R;

public class BridgeView extends View {

    private String name;
    private String times;
    private int divorseState;
    private boolean notificationState;

    public static final int BRIDGE_CONNECT=0,
                            BRIDGE_SOON=1,
                            BRIDGE_DIVORSE=2;

    public BridgeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BridgeView,0,0);
        try{
            name = ta.getString(R.styleable.BridgeView_name);
            times = ta.getString(R.styleable.BridgeView_times);
            divorseState = ta.getInt(R.styleable.BridgeView_divorseState,0);
            notificationState = ta.getBoolean(R.styleable.BridgeView_isNotified,false);
        } finally {
            ta.recycle();
        }
    }

    //Паддинг этой вьюхи - 16dp по всем измерениямм. Размер иконки моста - 48x48dp, размер иконки уведомления - 24x24dp,
    //её отступы сверху и снизу - 8 dp. размер текста - 16sp для имени и l4sp для времени.
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int pixelsIndp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                1,
                getResources().getDisplayMetrics());

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setName(String name){
        this.name = name;
        invalidate();
        requestLayout();
    }

    public void setTimes(String[] intervals){
        times = "";
        for (int i=0;i<intervals.length/2;i++){
            times+=intervals[2*i]+":"+intervals[2*i+1];
            if (intervals.length-i>1){
                times+="\t";
            }
        }
        invalidate();
        requestLayout();
    }

    public void setDivorseState(int divorseState){
        this.divorseState = divorseState;
        invalidate();
    }

    public void setNotificationState(boolean notificationState){
        this.notificationState = notificationState;
        invalidate();
    }
}
