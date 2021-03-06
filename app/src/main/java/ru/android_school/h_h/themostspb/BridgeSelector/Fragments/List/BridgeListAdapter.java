package ru.android_school.h_h.themostspb.BridgeSelector.Fragments.List;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.android_school.h_h.themostspb.Model.Bridge;
import ru.android_school.h_h.themostspb.Model.BridgeManager;
import ru.android_school.h_h.themostspb.BridgeView;
import ru.android_school.h_h.themostspb.BridgeSelector.SelectorActivity.ActivityCallback;

public class BridgeListAdapter extends RecyclerView.Adapter<BridgeListAdapter.ViewHolder> {

    ArrayList<Bridge> listOfBridges;
    ActivityCallback listener;

    public class ViewHolder extends RecyclerView.ViewHolder {

        BridgeView view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = (BridgeView) itemView;
        }

        public void bind(final Bridge bridge, final ActivityCallback listener){
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            view.setName(bridge.name);
            view.setTimes(bridge.timeDivorse,bridge.timeConnect);
            view.setDivorseState(BridgeManager.getDivorseState(bridge));
            view.setNotificationState(listener.getNotificationState(bridge.id));
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

    public BridgeListAdapter(ArrayList<Bridge> listOfBridges, ActivityCallback listener) {
        this.listOfBridges = listOfBridges;
        this.listener = listener;
    }
}
