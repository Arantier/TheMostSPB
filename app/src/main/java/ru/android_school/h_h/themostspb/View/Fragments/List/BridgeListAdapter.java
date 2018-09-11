package ru.android_school.h_h.themostspb.View.Fragments.List;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.android_school.h_h.themostspb.Model.Bridge;
import ru.android_school.h_h.themostspb.Model.BridgeManager;
import ru.android_school.h_h.themostspb.View.BridgeView;
import ru.android_school.h_h.themostspb.View.SelectorActivity.OnBridgeClickListener;

public class BridgeListAdapter extends RecyclerView.Adapter<BridgeListAdapter.ViewHolder> {

    ArrayList<Bridge> listOfBridges;
    OnBridgeClickListener listener;

    public class ViewHolder extends RecyclerView.ViewHolder {

        BridgeView view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = (BridgeView) itemView;
        }

        public void bind(final Bridge bridge, final OnBridgeClickListener listener){
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            view.setName(bridge.name);
            String[] times = new String[bridge.timeDivorse.length*2];
            view.setTimes(bridge.timeDivorse,bridge.timeConnect);
            view.setDivorseState(BridgeManager.getDivorseState(bridge));
            view.setNotificationState(BridgeManager.getNotificationState(bridge));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onBridgeClick(bridge.id);
                }
            });
        }

    }

    @NonNull
    @Override
    public BridgeListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(new BridgeView(viewGroup.getContext()));
    }

    @Override
    public void onBindViewHolder(@NonNull BridgeListAdapter.ViewHolder viewHolder, int i) {
        viewHolder.bind(listOfBridges.get(i),listener);
    }

    @Override
    public int getItemCount() {
        return (listOfBridges!=null) ? listOfBridges.size() : 0;
    }

    public BridgeListAdapter(ArrayList<Bridge> listOfBridges, OnBridgeClickListener listener) {
        this.listOfBridges = listOfBridges;
        this.listener = listener;
    }
}
