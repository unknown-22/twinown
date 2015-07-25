package jp.unknown.works.twinown.twinown_views;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import jp.unknown.works.twinown.Utils;
import jp.unknown.works.twinown.R;
import jp.unknown.works.twinown.models.UserPreference;
import jp.unknown.works.twinown.twinown_twitter.Component;
import jp.unknown.works.twinown.twinown_twitter.TwinownHelper;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;

public class MenuDialogFragment extends DialogFragment {
    private static final int MENU_ACTION_TYPE_REPLY = 0;
    private static final int MENU_ACTION_TYPE_RT = 1;
    private static final int MENU_ACTION_TYPE_FAVORITE = 2;
    private static final int MENU_ACTION_TYPE_USER_SCREEN_NAME = 3;
    private static final int MENU_ACTION_TYPE_LIST = 4;
    private static final int MENU_ACTION_TYPE_LINK_URL = 5;
    private static final int MENU_ACTION_TYPE_LINK_MEDIA = 6;
    private static final int MENU_ACTION_TYPE_OPEN_BROWSER = 7;
    private static final int MENU_ACTION_TYPE_SHARE = 8;

    private LayoutInflater layoutInflater;
    private UserPreference userPreference;
    private Status status;
    @Bind(R.id.statusMenuListVew) ListView statusMenuListVew;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        userPreference = (UserPreference) getArguments().getSerializable(Utils.ARGUMENTS_KEYWORD_USER_PREFERENCE);
        status = (Status) getArguments().getSerializable(Utils.ARGUMENTS_KEYWORD_STATUS);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        layoutInflater = getActivity().getLayoutInflater();
        View dialogView = layoutInflater.inflate(R.layout.status_menu, null);
        builder.setView(dialogView);
        ButterKnife.bind(this, dialogView);
        View headerView = layoutInflater.inflate(R.layout.status, null);
        final ImageView statusIconView = setHeaderView(headerView);
        final ArrayList<StatusMenuItem> statusMenuItemList = new ArrayList<>();
        statusMenuItemList.add(new StatusMenuItem(getString(R.string.menu_action_reply), MENU_ACTION_TYPE_REPLY));
        statusMenuItemList.add(new StatusMenuItem(getString(R.string.menu_action_rt), MENU_ACTION_TYPE_RT));
        statusMenuItemList.add(new StatusMenuItem(getString(R.string.menu_action_favorite), MENU_ACTION_TYPE_FAVORITE));
        statusMenuItemList.add(new StatusMenuItem(String.format("@%s", status.getUser().getScreenName()), MENU_ACTION_TYPE_USER_SCREEN_NAME, status.getUser().getScreenName()));
        for (UserMentionEntity userMentionEntity : status.getUserMentionEntities()) {
            statusMenuItemList.add(new StatusMenuItem(String.format("@%s", userMentionEntity.getScreenName()), MENU_ACTION_TYPE_USER_SCREEN_NAME, userMentionEntity.getScreenName()));
        }
        statusMenuItemList.add(new StatusMenuItem(getString(R.string.menu_action_list), MENU_ACTION_TYPE_LIST));
        for (URLEntity urlEntity : status.getURLEntities()) {
            statusMenuItemList.add(new StatusMenuItem(urlEntity.getExpandedURL(), MENU_ACTION_TYPE_LINK_URL, urlEntity.getExpandedURL()));
        }
        for (MediaEntity mediaEntity : status.getMediaEntities()) {
            statusMenuItemList.add(new StatusMenuItem(mediaEntity.getExpandedURL(), MENU_ACTION_TYPE_LINK_MEDIA, mediaEntity.getExpandedURL()));
        }
        statusMenuItemList.add(new StatusMenuItem(getString(R.string.menu_action_open_browser), MENU_ACTION_TYPE_OPEN_BROWSER));
        statusMenuItemList.add(new StatusMenuItem(getString(R.string.menu_action_share), MENU_ACTION_TYPE_SHARE));
        final StatusMenuAdapter statusMenuAdapter = new StatusMenuAdapter(getActivity(), 0, statusMenuItemList);
        statusMenuListVew.setAdapter(statusMenuAdapter);
        statusMenuListVew.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final StatusMenuItem statusMenuItem = statusMenuItemList.get(position - 1);
                switch (statusMenuItem.actionType) {
                    case MENU_ACTION_TYPE_REPLY:
                        EventBus.getDefault().post(new Component.MenuActionReply(status));
                        break;
                    case MENU_ACTION_TYPE_RT:
                        Utils.showToastLong(getActivity(), "RTやめろ");  // TODO 修正
                        break;
                    case MENU_ACTION_TYPE_FAVORITE:
                        TwinownHelper.createFavorite(userPreference, status);
                        break;
                    case MENU_ACTION_TYPE_USER_SCREEN_NAME:
                        new AsyncTask<Void, Void, User>() {
                            @Override
                            protected User doInBackground(Void... params) {
                                if (Objects.equals(statusMenuItem.text, userPreference.screenName)) {
                                    return status.getUser();
                                } else {
                                    return TwinownHelper.getUserSync(userPreference, statusMenuItem.text);
                                }
                            }

                            @Override
                            protected void onPostExecute(User user) {
                                if (user == null) {
                                    Utils.showToastLong(getActivity(), String.format(getString(R.string.error_user_show), statusMenuItem.statusMenuItemText));
                                    return;
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    getActivity().startActivity(
                                            new Intent(getActivity(), UserActivity.class)
                                                    .putExtra(Utils.ARGUMENTS_KEYWORD_USER_PREFERENCE, userPreference)
                                                    .putExtra(Utils.ARGUMENTS_KEYWORD_USER, user),
                                            ActivityOptionsCompat.makeSceneTransitionAnimation(
                                                    getActivity(),
                                                    statusIconView,
                                                    Utils.SHARED_ELEMENT_NAME_STATUS_ICON).toBundle()
                                    );
                                } else {
                                    startActivity(new Intent(getActivity(), UserActivity.class)
                                            .putExtra(Utils.ARGUMENTS_KEYWORD_USER_PREFERENCE, userPreference)
                                            .putExtra(Utils.ARGUMENTS_KEYWORD_USER, user));
                                }
                                new AsyncTask<Void, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        dismiss();
                                        return null;
                                    }
                                }.execute();
                            }
                        }.execute();
                        return;
                    case MENU_ACTION_TYPE_LIST:
                        Utils.showToastLong(getActivity(), "リストは甘え");  // TODO 修正
                        break;
                    case MENU_ACTION_TYPE_LINK_URL:
                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(statusMenuItem.text)
                        ));
                        break;
                    case MENU_ACTION_TYPE_LINK_MEDIA:
                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(statusMenuItem.text)
                        ));
                        break;
                    case MENU_ACTION_TYPE_OPEN_BROWSER:
                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(String.format("https://twitter.com/%s/status/%d", status.getUser().getScreenName(), status.getId()))
                        ));
                        break;
                    case MENU_ACTION_TYPE_SHARE:
                        Utils.showToastLong(getActivity(), "まだ");  // TODO 修正
                        break;
                }
                dismiss();
            }
        });
        int dialogWidth = (int) (getResources().getDisplayMetrics().widthPixels * 0.85);  // TODO 画面サイズで分けるかも
        AlertDialog alertDialog = builder.create();
        WindowManager.LayoutParams lp = alertDialog.getWindow().getAttributes();
        lp.width = dialogWidth;
        alertDialog.getWindow().setAttributes(lp);
        return alertDialog;
    }

    private ImageView setHeaderView(View headerView) {
        headerView.setClickable(false);
        ImageView statusIconView = (ImageView) headerView.findViewById(R.id.statusIconView);
        TextView statusNameView = (TextView) headerView.findViewById(R.id.statusNameView);
        TextView statusTextView = (TextView) headerView.findViewById(R.id.statusTextView);
        RoundedTransformation transform = new RoundedTransformation((int) (getActivity().getResources().getDimension(R.dimen.icon_size) / 8));
        Picasso.with(getActivity()).load(status.getUser().getBiggerProfileImageURL())
                .resizeDimen(R.dimen.icon_size, R.dimen.icon_size).transform(transform).into(statusIconView);
        statusNameView.setText(status.getUser().getScreenName());
        statusTextView.setText(status.getText());
        statusMenuListVew.addHeaderView(headerView, null, false);
        return statusIconView;
    }

    class StatusMenuItem {
        public String statusMenuItemText;
        public int actionType;
        public String text;

        public StatusMenuItem(String statusMenuItemText, int actionType) {
            this.statusMenuItemText = statusMenuItemText;
            this.actionType = actionType;
        }

        public StatusMenuItem(String statusMenuItemText, int actionType, String text) {
            this.statusMenuItemText = statusMenuItemText;
            this.actionType = actionType;
            this.text = text;
        }
    }

    class StatusMenuAdapter extends ArrayAdapter {
        public StatusMenuAdapter(Context context, int resource, List<StatusMenuItem> objects) {
            //noinspection unchecked
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.status_menu_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            StatusMenuItem item = (StatusMenuItem) getItem(position);
            holder.statusMenuItemText.setText(item.statusMenuItemText);
            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.statusMenuItemText) TextView statusMenuItemText;

            public ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
}
