package jp.unknown.works.twinown.twinown_views;


import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.unknown.works.twinown.R;
import jp.unknown.works.twinown.Utils;
import jp.unknown.works.twinown.models.TwitterActivity;

public class TwitterActivityAdapter extends RecyclerView.Adapter{
    private Fragment fragment;
    @SuppressWarnings("unchecked")
    private final SortedList<TwitterActivity> sortedList = new SortedList(TwitterActivity.class, new TwitterActivityCallback(this));

    public TwitterActivityAdapter(Fragment fragment) {
        this.fragment = fragment;
    }

    public void addTwitterActivity(TwitterActivity twitterActivity) {
        sortedList.add(twitterActivity);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(fragment.getActivity());
        return new TwitterActivityViewHolder(inflater.inflate(R.layout.activity, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ((TwitterActivityViewHolder)viewHolder).setTwitterActivity(sortedList.get(position));
    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }

    public class TwitterActivityViewHolder extends RecyclerView.ViewHolder {
        @Bind(android.R.id.text1) TextView activityTextView;

        public TwitterActivityViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setTwitterActivity(TwitterActivity twitterActivity) {
            if (twitterActivity.type == TwitterActivity.TYPE_FAVORITED) {
                activityTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_white, 0, 0, 0);
            }
            activityTextView.setText(twitterActivity.text);
        }

        @OnClick(android.R.id.text1)
        public void showStatusMenu(View itemView) {
            Utils.debugLog("hoge hoge");
        }
    }

    private class TwitterActivityCallback extends SortedList.Callback<TwitterActivity> {
        private RecyclerView.Adapter adapter;
        TwitterActivityCallback(@NonNull RecyclerView.Adapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public int compare(TwitterActivity o1, TwitterActivity o2) {
            return o2.id.compareTo(o1.id);
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
        public boolean areContentsTheSame(TwitterActivity oldItem, TwitterActivity newItem) {
            return Objects.equals(oldItem.id, newItem.id);
        }

        @Override
        public boolean areItemsTheSame(TwitterActivity item1, TwitterActivity item2) {
            return Objects.equals(item1.id, item2.id);
        }
    }
}
