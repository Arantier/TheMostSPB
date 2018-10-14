package ru.android_school.h_h.themostspb.InfoPage.InfoActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import ru.android_school.h_h.themostspb.R;

public class TimePickerDialog extends DialogFragment {

    interface OnNotificationStateChangeListener {

        void createNotificationAndRefreshButton(int minutesToCall);

        boolean getNotificationState();

        void cancel();

    }

    private String bridgeName;
    private int selectedTimeInMinutes;

    private OnNotificationStateChangeListener onNotificationStateChangeListenerToActivity;

    TextView title;

    public static TimePickerDialog newInstance(String bridgeName, OnNotificationStateChangeListener onNotificationStateChangeListenerToActivity) {
        TimePickerDialog instance = new TimePickerDialog();
        instance.bridgeName = bridgeName;
        instance.onNotificationStateChangeListenerToActivity = onNotificationStateChangeListenerToActivity;
        return instance;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_time_picker, null);
        title = dialogView.findViewById(R.id.title);
        title.setText(bridgeName);
        NumberPicker numberPicker = dialogView.findViewById(R.id.numberPicker);
        String times[] = {getString(R.string.string_times_15mins),
                getString(R.string.string_times_30mins),
                getString(R.string.string_times_45mins),
                getString(R.string.string_times_1h),
                getString(R.string.string_times_2h)};
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(times.length - 1);
        numberPicker.setDisplayedValues(times);
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                selectedTimeInMinutes = ((newVal < 3) ? (newVal + 1) * 15 : (newVal - 2) * 60);
            }
        });
        selectedTimeInMinutes = 15;
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.dialogPositive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onNotificationStateChangeListenerToActivity.createNotificationAndRefreshButton(selectedTimeInMinutes);
            }
        }).setNegativeButton(R.string.dialogNegative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TimePickerDialog.this.getDialog().cancel();
            }
        });
        if (onNotificationStateChangeListenerToActivity.getNotificationState()) {
            builder.setNeutralButton(R.string.dialogNeutral, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    onNotificationStateChangeListenerToActivity.cancel();
                }
            });
        }
        return builder.create();
    }

}
