package jp.unknown.works.twinown.Twitter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import jp.unknown.works.twinown.R;
import twitter4j.Status;

public class TimelineAdapter extends RecyclerView.Adapter{
    private final LayoutInflater inflater;
    private ArrayList<Status> timelineList;

    public TimelineAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        timelineList = new ArrayList<>();
    }

    public void addStatus(Status status) {
        timelineList.add(0, status);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new StatusViewHolder(inflater.inflate(R.layout.status, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        ((StatusViewHolder)viewHolder).mTextView.setText(timelineList.get(i).getText());
    }

    @Override
    public int getItemCount() {
        return timelineList.size();
    }

    private static class StatusViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public StatusViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView)itemView.findViewById(R.id.textView);
        }
    }
}
