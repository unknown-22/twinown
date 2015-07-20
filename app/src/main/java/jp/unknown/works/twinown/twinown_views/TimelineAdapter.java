package jp.unknown.works.twinown.twinown_views;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.unknown.works.twinown.Globals;
import jp.unknown.works.twinown.R;
import jp.unknown.works.twinown.models.UserPreference;
import twitter4j.ResponseList;
import twitter4j.Status;

class TimelineAdapter extends RecyclerView.Adapter{
    private final FragmentManager fragmentManager;
    private final UserPreference userPreference;
    private RecyclerView recyclerView;
    private final LayoutInflater inflater;
    @SuppressWarnings("unchecked")
    private final SortedList<Status> timelineList = new SortedList(Status.class, new TimelineCallback(this));
    private final RoundedTransformation transform;

    public TimelineAdapter(FragmentManager fragmentManager, Context context, UserPreference userPreference) {
        this.fragmentManager = fragmentManager;
        this.userPreference = userPreference;
        inflater = LayoutInflater.from(context);
        transform = new RoundedTransformation((int) (context.getResources().getDimension(R.dimen.icon_size) / 8));
    }

    public void addStatus(Status status) {
        timelineList.add(status);
    }

    public void addStatusList(ResponseList<Status> statuses) {
        timelineList.addAll(statuses);
    }

    public Status getStatus(int position) {
        return timelineList.get(position);
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

        @SuppressWarnings("unused")
        @OnClick(R.id.itemView)
        public void showStatusMenu(View itemView) {
            Status status = timelineList.get(recyclerView.getChildAdapterPosition(itemView));
            MenuDialogFragment menuDialogFragment = new MenuDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Globals.ARGUMENTS_KEYWORD_USER_PREFERENCE, userPreference);
            bundle.putSerializable(Globals.ARGUMENTS_KEYWORD_STATUS, status);
            menuDialogFragment.setArguments(bundle);
            menuDialogFragment.show(fragmentManager, "hoge");
        }

        public StatusViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
        }

        public void setStatus(Status status) {
            Picasso.with(context).load(status.getUser().getBiggerProfileImageURL())
                .resizeDimen(R.dimen.icon_size, R.dimen.icon_size).transform(transform).into(statusIconView);
            statusNameView.setText(status.getUser().getScreenName());
            statusTextView.setText(status.getText());
        }
    }

    private static class TimelineCallback extends SortedList.Callback<Status> {
        private RecyclerView.Adapter adapter;

        TimelineCallback(@NonNull RecyclerView.Adapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public int compare(Status o1, Status o2) {
            return o2.compareTo(o1);
        }

        @Override
        public void onInserted(int position, int count) {
            adapter.notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            adapter.notifyItemRangeInserted(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            adapter.notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
            adapter.notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(Status oldItem, Status newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areItemsTheSame(Status item1, Status item2) {
            return item1.getId() == item2.getId();
        }
    }
}
