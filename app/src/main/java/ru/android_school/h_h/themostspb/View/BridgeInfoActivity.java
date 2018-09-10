package ru.android_school.h_h.themostspb.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import ru.android_school.h_h.themostspb.R;

public class BridgeInfoActivity extends AppCompatActivity {

    public static final String ID_EXTRA = "id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bridge_info);
    }
}
