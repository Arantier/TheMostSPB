package ru.android_school.h_h.themostspb.View.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;
import ru.android_school.h_h.themostspb.Model.Bridge;
import ru.android_school.h_h.themostspb.View.OnBridgeClickListener;

public class ListFragment extends Fragment {

    OnBridgeClickListener listener;
    ArrayList<Bridge> listOfBridges;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public static ListFragment newInstance(ArrayList<Bridge> bridges, OnBridgeClickListener listener) {
        ListFragment instance = new ListFragment();
        instance.listener = listener;
        instance.listOfBridges = bridges;
        return instance;
    }

    private ListFragment(){

    };
}
