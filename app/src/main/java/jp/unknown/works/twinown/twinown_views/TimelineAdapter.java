package jp.unknown.works.twinown.twinown_views;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.unknown.works.twinown.R;
import twitter4j.Status;

class TimelineAdapter extends RecyclerView.Adapter{
    private RecyclerView recyclerView;
    private final LayoutInflater inflater;
    private final ArrayList<Status> timelineList;
    private final RoundedTransformation transform;

    public TimelineAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        transform = new RoundedTransformation((int) (context.getResources().getDimension(R.dimen.icon_size) / 8));
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

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView= recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    public class StatusViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        @Bind(R.id.statusIconView) ImageView statusIconView;
        @Bind(R.id.statusNameView) TextView statusNameView;
        @Bind(R.id.statusTextView) TextView statusTextView;

        public StatusViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerView.getChildAdapterPosition(itemView);
                }
            });
        }

        public void setStatus(Status status) {
            Picasso.with(context).load(status.getUser().getBiggerProfileImageURL())
                .resizeDimen(R.dimen.icon_size, R.dimen.icon_size).transform(transform).into(statusIconView);
            statusNameView.setText(status.getUser().getScreenName());
            statusTextView.setText(status.getText());
        }
    }
}
