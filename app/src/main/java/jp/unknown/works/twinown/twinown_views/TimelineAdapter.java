package jp.unknown.works.twinown.twinown_views;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.unknown.works.twinown.Utils;
import jp.unknown.works.twinown.R;
import jp.unknown.works.twinown.models.UserPreference;
import jp.unknown.works.twinown.twinown_twitter.Component;
import jp.unknown.works.twinown.twinown_twitter.TwinownHelper;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.User;

class TimelineAdapter extends RecyclerView.Adapter{
    private FragmentManager fragmentManager;
    private final UserPreference userPreference;
    private RecyclerView recyclerView;
    private LayoutInflater inflater;
    @SuppressWarnings("unchecked")
    private final SortedList<Status> timelineList = new SortedList(Status.class, new TimelineCallback(this));
    private final RoundedTransformation transform;

    private boolean is_show_created_at;
    private boolean is_show_client_name;


    static final SimpleDateFormat todayDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    static final SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
    static final Pattern clientNamePattern = Pattern.compile("^<.*>(.*?)</.*?>$");

    static final float SMALL_TEXT_SCALE = 0.9f;


    public TimelineAdapter(FragmentManager fragmentManager, Context context, UserPreference userPreference) {
        this.fragmentManager = fragmentManager;
        this.userPreference = userPreference;
        inflater = LayoutInflater.from(context);
        transform = new RoundedTransformation((int) (context.getResources().getDimension(R.dimen.icon_size) / 8));

        is_show_created_at = Utils.getPreferenceBoolean(context, context.getString(R.string.preference_key_show_created_at), true);
        is_show_client_name = Utils.getPreferenceBoolean(context, context.getString(R.string.preference_key_show_client_name), true);
    }

    public void refreshActivity(FragmentManager fragmentManager, Context context) {
        this.fragmentManager = fragmentManager;
        inflater = LayoutInflater.from(context);
    }

    public void addStatus(Status status) {
        timelineList.add(status);
    }

    public void addStatusList(ResponseList<Status> statuses) {
        timelineList.addAll(statuses);
    }

    public void deleteStatus(long statusId) {
        Component.DummyStatus dummyStatus = new Component.DummyStatus(statusId);
        timelineList.remove(dummyStatus);
    }

    public void addFavorite(Status status) {
        if (timelineList.indexOf(status) != -1) {
            try {
                Field field = status.getClass().getDeclaredField("isFavorited");
                field.setAccessible(true);
                field.set(status, true);
                timelineList.add(status);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteFavorite(Status status) {
        if (timelineList.indexOf(status) != -1) {
            try {
                Field field = status.getClass().getDeclaredField("isFavorited");
                field.setAccessible(true);
                field.set(status, false);
                timelineList.add(status);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
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
        @Bind(R.id.statusScreenNameView) TextView statusScreenNameView;
        @Bind(R.id.statusTextView) TextView statusTextView;
        @Bind(R.id.statusCreatedAt) TextView statusCreatedAt;
        @Bind(R.id.statusClientName) TextView statusClientName;
        @Bind(R.id.statusRetweetedScreenName) TextView statusRetweetedScreenName;
        @Bind(R.id.colorBarView) View colorBarView;
        // private final float textSize;
        private final float textSizeSmall;

        @SuppressWarnings("unused")
        @OnClick(R.id.itemView)
        public void showStatusMenu(View itemView) {
            Status status = timelineList.get(recyclerView.getChildAdapterPosition(itemView));
            MenuDialogFragment menuDialogFragment = new MenuDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Utils.ARGUMENTS_KEYWORD_USER_PREFERENCE, userPreference);
            bundle.putSerializable(Utils.ARGUMENTS_KEYWORD_STATUS, status);
            menuDialogFragment.setArguments(bundle);
            menuDialogFragment.show(fragmentManager, "menu_dialog_fragment");
        }

        public StatusViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
            // textSize = statusTextView.getTextSize();
            itemView.setFocusable(true);
            textSizeSmall = statusTextView.getTextSize() * SMALL_TEXT_SCALE;
            itemView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (KeyEvent.ACTION_DOWN == event.getAction()){
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_F:
                                TwinownHelper.createFavorite(userPreference, timelineList.get(getAdapterPosition()));
                                return true;
                        }
                    }
                    return false;
                }
            });
        }

        public void setStatus(Status status) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            if (!status.isRetweet()) {
                User user = status.getUser();
                Picasso.with(context).load(user.getBiggerProfileImageURL())
                        .resizeDimen(R.dimen.icon_size, R.dimen.icon_size).transform(transform).into(statusIconView);
                statusNameView.setText(user.getName());
                statusScreenNameView.setText(String.format("@%s", user.getScreenName()));
                statusTextView.setText(status.getText());
                if (status.getCreatedAt().after(calendar.getTime())) {
                    statusCreatedAt.setText(todayDateFormat.format(status.getCreatedAt()));
                } else {
                    statusCreatedAt.setText(fullDateFormat.format(status.getCreatedAt()));
                }
                Matcher matcher = clientNamePattern.matcher(status.getSource());
                while (matcher.find()) {
                    statusClientName.setText(String.format("from %s", matcher.group(1)));
                }
                statusRetweetedScreenName.setVisibility(View.GONE);
            } else {
                Status retweetedStatus = status.getRetweetedStatus();
                User user = retweetedStatus.getUser();
                Picasso.with(context).load(user.getBiggerProfileImageURL())
                        .resizeDimen(R.dimen.icon_size, R.dimen.icon_size).transform(transform).into(statusIconView);
                statusNameView.setText(user.getName());
                statusScreenNameView.setText(String.format("@%s", user.getScreenName()));
                statusTextView.setText(retweetedStatus.getText());
                if (retweetedStatus.getCreatedAt().after(calendar.getTime())) {
                    statusCreatedAt.setText(todayDateFormat.format(retweetedStatus.getCreatedAt()));
                } else {
                    statusCreatedAt.setText(fullDateFormat.format(retweetedStatus.getCreatedAt()));
                }
                Matcher matcher = clientNamePattern.matcher(retweetedStatus.getSource());
                while (matcher.find()) {
                    statusClientName.setText(String.format("from %s", matcher.group(1)));
                }
                statusRetweetedScreenName.setVisibility(View.VISIBLE);
                statusRetweetedScreenName.setText(String.format("@%s", status.getUser().getScreenName()));
            }
            statusScreenNameView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeSmall);
            statusCreatedAt.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeSmall);
            statusClientName.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeSmall);
            statusRetweetedScreenName.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeSmall);
            if (!is_show_created_at) {
                statusCreatedAt.setVisibility(View.GONE);
            } else {
                statusCreatedAt.setVisibility(View.VISIBLE);
            }
            if (!is_show_client_name) {
                statusClientName.setVisibility(View.GONE);
            } else {
                statusClientName.setVisibility(View.VISIBLE);
            }
            if (status.isFavorited()){
                colorBarView.setVisibility(View.VISIBLE);
            } else {
                colorBarView.setVisibility(View.GONE);
            }
        }
    }

    private class TimelineCallback extends SortedList.Callback<Status> {
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
            adapter.notifyItemRangeRemoved(position, count);
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
            return (oldItem.getId() == newItem.getId()
                    && oldItem.isFavorited() == newItem.isFavorited()
                    && oldItem.isRetweeted() == newItem.isRetweeted());
        }

        @Override
        public boolean areItemsTheSame(Status item1, Status item2) {
            return item1.getId() == item2.getId();
        }
    }
}
