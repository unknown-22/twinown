package jp.unknown.works.twinown.twinown_views;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.unknown.works.twinown.Globals;
import jp.unknown.works.twinown.R;
import jp.unknown.works.twinown.models.UserPreference;
import jp.unknown.works.twinown.twinown_twitter.TwinownHelper;
import twitter4j.Status;

public class MenuDialogFragment extends DialogFragment {
    private static final int MENU_ACTION_TYPE_REPLY = 0;
    private static final int MENU_ACTION_TYPE_RT = 1;
    private static final int MENU_ACTION_TYPE_FAVORITE = 2;
    private static final int MENU_ACTION_TYPE_LIST = 3;
    private static final int MENU_ACTION_TYPE_OPEN_BROWSER = 4;
    private static final int MENU_ACTION_TYPE_SHARE = 5;

    private LayoutInflater layoutInflater;
    private UserPreference userPreference;
    private Status status;
    @Bind(R.id.statusMenuListVew) ListView statusMenuListVew;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        userPreference = (UserPreference) getArguments().getSerializable(Globals.ARGUMENTS_KEYWORD_USER_PREFERENCE);
        status = (Status) getArguments().getSerializable(Globals.ARGUMENTS_KEYWORD_STATUS);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        layoutInflater = getActivity().getLayoutInflater();
        View dialogView = layoutInflater.inflate(R.layout.status_menu, null);
        builder.setView(dialogView);
        ButterKnife.bind(this, dialogView);
        final ArrayList<StatusMenuItem> statusMenuItemList = new ArrayList<>();
        statusMenuItemList.add(new StatusMenuItem(getString(R.string.menu_action_reply), MENU_ACTION_TYPE_REPLY));
        statusMenuItemList.add(new StatusMenuItem(getString(R.string.menu_action_rt), MENU_ACTION_TYPE_RT));
        statusMenuItemList.add(new StatusMenuItem(getString(R.string.menu_action_favorite), MENU_ACTION_TYPE_FAVORITE));
        statusMenuItemList.add(new StatusMenuItem(getString(R.string.menu_action_list), MENU_ACTION_TYPE_LIST));
        statusMenuItemList.add(new StatusMenuItem(getString(R.string.menu_action_open_browser), MENU_ACTION_TYPE_OPEN_BROWSER));
        statusMenuItemList.add(new StatusMenuItem(getString(R.string.menu_action_share), MENU_ACTION_TYPE_SHARE));
        StatusMenuAdapter statusMenuAdapter = new StatusMenuAdapter(getActivity(), 0, statusMenuItemList);
        statusMenuListVew.setAdapter(statusMenuAdapter);
        statusMenuListVew.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StatusMenuItem statusMenuItem = statusMenuItemList.get(position);
                switch (statusMenuItem.actionType) {
                    case MENU_ACTION_TYPE_REPLY:
                        break;
                    case MENU_ACTION_TYPE_RT:
                        Globals.showToast(getActivity(), "RTやめろ");  // TODO 修正
                        break;
                    case MENU_ACTION_TYPE_FAVORITE:
                        TwinownHelper.createFavorite(userPreference, status);
                        break;
                    case MENU_ACTION_TYPE_LIST:
                        Globals.showToast(getActivity(), "リストは甘え");  // TODO 修正
                        break;
                    case MENU_ACTION_TYPE_OPEN_BROWSER:
                        Uri uri = Uri.parse(String.format("https://twitter.com/%s/status/%d", status.getUser().getScreenName(), status.getId()));
                        Intent i = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(i);
                        break;
                    case MENU_ACTION_TYPE_SHARE:
                        Globals.showToast(getActivity(), "まだ");  // TODO 修正
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

    class StatusMenuItem {
        public String statusMenuItemText;
        public int actionType;

        public StatusMenuItem(String statusMenuItemText, int actionType) {
            this.statusMenuItemText = statusMenuItemText;
            this.actionType = actionType;
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
