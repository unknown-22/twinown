package jp.unknown.works.twinown.twinown_views;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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
    private final ArrayList<Status> timelineList;
    private final RoundedTransformation transform;

    public TimelineAdapter(FragmentManager fragmentManager, Context context, UserPreference userPreference) {
        this.fragmentManager = fragmentManager;
        this.userPreference = userPreference;
        inflater = LayoutInflater.from(context);
        transform = new RoundedTransformation((int) (context.getResources().getDimension(R.dimen.icon_size) / 8));
        timelineList = new ArrayList<>();
    }

    public void addStatus(Status status) {
        timelineList.add(0, status);
        notifyItemInserted(0);
    }

    public void addStatusList(ResponseList<Status> statuses) {
        timelineList.addAll(statuses);
        notifyItemRangeInserted(timelineList.size() - statuses.size(), statuses.size());
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
}
