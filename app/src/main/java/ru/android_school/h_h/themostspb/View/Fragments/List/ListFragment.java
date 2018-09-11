package ru.android_school.h_h.themostspb.View.Fragments.List;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.android_school.h_h.themostspb.Model.Bridge;
import ru.android_school.h_h.themostspb.R;
import ru.android_school.h_h.themostspb.View.SelectorActivity.OnBridgeActionListener;

public class ListFragment extends Fragment {

    OnBridgeActionListener listener;
    ArrayList<Bridge> listOfBridges;

    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list,container,false);
        BridgeListAdapter adapter = new BridgeListAdapter(listOfBridges,listener);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        return view;
    }

    public static ListFragment newInstance(ArrayList<Bridge> bridges, OnBridgeActionListener listener) {
        ListFragment instance = new ListFragment();
        instance.listener = listener;
        instance.listOfBridges = bridges;
        return instance;
    }

}
