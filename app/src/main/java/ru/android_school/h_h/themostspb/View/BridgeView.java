package ru.android_school.h_h.themostspb.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import ru.android_school.h_h.themostspb.Model.BridgeManager;
import ru.android_school.h_h.themostspb.R;

public class BridgeView extends View {

    private String name;
    private String times;
    private int divorseState;
    private boolean notificationState;

    private float padding,
            divorseIconSide,
            notificationIconSide,
            notificationIconVerticalPadding,
            nameFontSize,
            timesFontSize;

    private float maximumTextWidth;


    private TextPaint namePaint,
            timesPaint;

    private float getDimensionsInDp(int dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics());
    }

    private float getDimensionsInSp(int sp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sp,
                getResources().getDisplayMetrics());
    }

    private void init() {
        padding = getDimensionsInDp(16);
        divorseIconSide = getDimensionsInDp(40);
        notificationIconSide = getDimensionsInDp(24);
        notificationIconVerticalPadding = getDimensionsInDp(8);

        nameFontSize = getDimensionsInSp(16);
        timesFontSize = getDimensionsInSp(14);

        namePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        namePaint.setColor(Color.BLACK);
        namePaint.setTextSize(nameFontSize);
        namePaint.setStyle(Paint.Style.FILL);
        namePaint.setTextAlign(Paint.Align.LEFT);
        namePaint.setLinearText(true);

        timesPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        timesPaint.setColor(Color.parseColor("#6e6e6e"));
        timesPaint.setTextSize(timesFontSize);
        timesPaint.setStyle(Paint.Style.FILL);
        timesPaint.setTextAlign(Paint.Align.LEFT);
        timesPaint.setLinearText(true);
    }

    public BridgeView(Context context) {
        super(context);
        name = "";
        times = "";
        divorseState = BridgeManager.BRIDGE_CONNECT;
        notificationState = false;
        init();
    }

    public BridgeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BridgeView, 0, 0);
        init();
        try {
            name = ta.getString(R.styleable.BridgeView_name);
            times = ta.getString(R.styleable.BridgeView_times);
            divorseState = ta.getInt(R.styleable.BridgeView_divorseState, 0);
            notificationState = ta.getBoolean(R.styleable.BridgeView_isNotified, false);
        } finally {
            ta.recycle();
        }
    }

    //Паддинг этой вьюхи - 16dp по всем измерениямм. Размер иконки моста - 48x48dp, размер иконки уведомления - 24x24dp,
    //её отступы сверху и снизу - 8 dp. размер текста - 16sp для имени и l4sp для времени.
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setPadding((int) padding, (int) padding, (int) padding, (int) padding);
        int requiredMinimalWidth = getPaddingLeft() + (int) (divorseIconSide + notificationIconSide) + getPaddingRight();
        int requiredMinimalHeight = getPaddingTop() + (int) divorseIconSide + getPaddingBottom();
        int finalWidthSpec = resolveSizeAndState(requiredMinimalWidth, widthMeasureSpec, 0);
        int finalHeightSpec = resolveSizeAndState(requiredMinimalHeight, heightMeasureSpec, 0);
        maximumTextWidth = MeasureSpec.getSize(finalWidthSpec) - padding * 2 - divorseIconSide - notificationIconSide;
        setMeasuredDimension(finalWidthSpec, finalHeightSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Drawable divorseIcon;
        switch (divorseState) {
            case (BridgeManager.BRIDGE_SOON):
                divorseIcon = getResources().getDrawable(R.drawable.ic_bridge_soon);
                break;
            case (BridgeManager.BRIDGE_DIVORSE):
                divorseIcon = getResources().getDrawable(R.drawable.ic_bridge_divorse);
                break;
            default:
                divorseIcon = getResources().getDrawable(R.drawable.ic_bridge_connect);
                break;
        }
        divorseIcon.setBounds((int) padding,
                (int) padding,
                (int) (divorseIconSide + padding),
                (int) (divorseIconSide + padding));
        divorseIcon.draw(canvas);

        Drawable notificationIcon;
        if (notificationState) {
            notificationIcon = getResources().getDrawable(R.drawable.ic_bell_on);
        } else {
            notificationIcon = getResources().getDrawable(R.drawable.ic_bell_off);
        }
        notificationIcon.setBounds(getWidth() - (int) (notificationIconSide + padding),
                (int) (notificationIconVerticalPadding + padding),
                getWidth() - (int) padding,
                getHeight() - (int) (notificationIconVerticalPadding + padding));
        notificationIcon.draw(canvas);

        int textYStartPosition = (getHeight() - (int) (nameFontSize - timesFontSize)) / 2;

        if (name != null) {
            CharSequence ellipsizedName = TextUtils.ellipsize(name, namePaint, maximumTextWidth, TextUtils.TruncateAt.END);
            canvas.drawText(ellipsizedName,
                    0,
                    ellipsizedName.length(),
                    (int) (divorseIconSide + padding * 2),
                    textYStartPosition,
                    namePaint);
        }

        if (times != null) {
            CharSequence ellipsizedTimes = TextUtils.ellipsize(times, timesPaint, maximumTextWidth, TextUtils.TruncateAt.END);
            canvas.drawText(ellipsizedTimes,
                    0,
                    ellipsizedTimes.length(),
                    (int) (divorseIconSide + padding * 2),
                    (int) (nameFontSize) + textYStartPosition,
                    timesPaint);
        }
    }

    public void setName(String name) {
        this.name = name;
        invalidate();
        requestLayout();
    }

    public void setTimes(String[] divorces, String[] connections) {
        times = "";
        for (int i = 0; i < divorces.length; i++) {
            times += divorces[i] + " - " + connections[i] + "\t";
        }
        invalidate();
        requestLayout();
    }

    public void setDivorseState(int divorseState) {
        this.divorseState = divorseState;
        invalidate();
    }

    public void setNotificationState(boolean notificationState) {
        this.notificationState = notificationState;
        invalidate();
    }
}
