package jp.unknown.works.twinown;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import jp.unknown.works.twinown.models.Tab;
import jp.unknown.works.twinown.models.UserPreference;
import jp.unknown.works.twinown.twinown_twitter.Component;
import jp.unknown.works.twinown.twinown_twitter.TwinownHelper;

public class TabControlActivity extends AppCompatActivity {
    private UserPreference userPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_control);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(getString(R.string.setting_action_tab));
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tab_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            showSelectTypeDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSelectTypeDialog() {
        final String[] items = {"Stream", "Mention", "List"};
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_title_choose_tab_type))
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // Stream
                                break;
                            case 1: // Mention
                                break;
                            case 2: // List
                                showSelectListAccountDialog();
                                break;
                        }
                    }

                })
                .show();
    }

    private void showSelectListAccountDialog() {
        final List<UserPreference> userPreferenceList = UserPreference.getAll();
        String[] userScreenNameList = new String[userPreferenceList.size()];
        for(int i = 0; i < userPreferenceList.size(); i++) {
            userScreenNameList[i] = userPreferenceList.get(i).screenName;
        }
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_title_choose_account))
                .setItems(userScreenNameList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userPreference = userPreferenceList.get(which);
                        TwinownHelper.getUserLists(userPreference);
                    }
                })
                .show();
    }

    static class TabMenuItem {
        public String tabName;

        public TabMenuItem(String tabName) {
            this.tabName = tabName;
        }
    }

    public static class TabControlFragment extends Fragment {
        private LayoutInflater layoutInflater;
        private ArrayList<TabMenuItem> tabMenuItems = new ArrayList<>();
        private TabAdapter tabAdapter;
        @Bind(android.R.id.list) ListView tabListView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            this.layoutInflater = inflater;
            View view = inflater.inflate(android.R.layout.list_content, container, false);
            ButterKnife.bind(this, view);
            tabAdapter = new TabAdapter(getActivity(), 0, tabMenuItems);
            tabListView.setAdapter(tabAdapter);
            return view;
        }

        @Override
        public void onResume() {
            super.onResume();
            refreshTabAdapter();
        }

        public void refreshTabAdapter() {
            tabMenuItems.clear();
            for (Tab tab : Tab.getAll()) {
                tabMenuItems.add(new TabMenuItem(tab.name));
            }
            tabAdapter.notifyDataSetChanged();
        }

        class TabAdapter extends ArrayAdapter<TabMenuItem> {
            public TabAdapter(Context context, int resource, List objects) {
                //noinspection unchecked
                super(context, resource, objects);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder;
                if (convertView == null) {
                    convertView = layoutInflater.inflate(android.R.layout.simple_list_item_1, null);
                    holder = new ViewHolder(convertView);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                TabMenuItem item = getItem(position);
                holder.tabMenuItemText.setText(item.tabName);
                return convertView;
            }

            class ViewHolder {
                @Bind(android.R.id.text1) TextView tabMenuItemText;

                public ViewHolder(View view) {
                    ButterKnife.bind(this, view);
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(final Component.UserListsEvent userListsEvent) {
        final String[] userListFullNameList = new String[userListsEvent.userLists.size()];
        for(int i = 0; i < userListsEvent.userLists.size(); i++) {
            userListFullNameList[i] = userListsEvent.userLists.get(i).getFullName();
        }
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_title_choose_list))
                .setItems(userListFullNameList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Tab.createListTab(userPreference, userListsEvent.userLists.get(which));
                        TabControlFragment tabControlFragment = (TabControlFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
                        tabControlFragment.refreshTabAdapter();
                    }
                })
                .show();
    }
}
