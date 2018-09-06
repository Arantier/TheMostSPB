package ru.android_school.h_h.themostspb.View.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.android_school.h_h.themostspb.R;

public class LoadFragment extends Fragment {

    public LoadFragment() {
        // Required empty public constructor
    }

    public static LoadFragment newInstance() {
        LoadFragment fragment = new LoadFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_load, container, false);
    }
}
