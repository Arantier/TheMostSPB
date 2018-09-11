package ru.android_school.h_h.themostspb.View.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.android_school.h_h.themostspb.R;
import ru.android_school.h_h.themostspb.View.SelectorActivity.OnErrorRefreshListener;

public class ErrorFragment extends Fragment {

    private OnErrorRefreshListener listener;

    public ErrorFragment() {
        // Required empty public constructor
    }

    public static ErrorFragment newInstance(OnErrorRefreshListener listener) {
        ErrorFragment fragment = new ErrorFragment();
        fragment.listener = listener;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_error, container, false);
        view.findViewById(R.id.button_errorRefresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onErrorRefresh();
            }
        });
        return view;
    }

}
