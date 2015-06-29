package jp.unknown.works.twinown.Views;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import jp.unknown.works.twinown.R;
import twitter4j.Status;

class TimelineAdapter extends RecyclerView.Adapter{
    private final LayoutInflater inflater;
    private final ArrayList<Status> timelineList;

    public TimelineAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        timelineList = new ArrayList<>();
    }

    public void addStatus(Status status) {
        timelineList.add(0, status);
        notifyItemInserted(0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new StatusViewHolder(inflater.inflate(R.layout.status, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        ((StatusViewHolder)viewHolder).setStatus(timelineList.get(i));
    }

    @Override
    public int getItemCount() {
        return timelineList.size();
    }

    private static class StatusViewHolder extends RecyclerView.ViewHolder {
        private final TextView statusTextView;
        private final TextView statusNameView;
        public StatusViewHolder(View itemView) {
            super(itemView);
            statusNameView = (TextView)itemView.findViewById(R.id.statusNameView);
            statusTextView = (TextView)itemView.findViewById(R.id.statusTextView);
        }

        public void setStatus(Status status) {
            statusNameView.setText(status.getUser().getScreenName());
            statusTextView.setText(status.getText());
        }
    }
}
