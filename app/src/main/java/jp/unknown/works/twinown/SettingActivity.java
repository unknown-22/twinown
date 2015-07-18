package jp.unknown.works.twinown;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingActivity extends AppCompatActivity {
    private static final int SETTING_ACTION_TYPE_ACCOUNT = 0;
    private static final int SETTING_ACTION_TYPE_TAB = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.action_settings));
    }

    public static class SettingFragment extends Fragment {
        private LayoutInflater layoutInflater;
        @Bind(android.R.id.list) ListView settingListView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            this.layoutInflater = inflater;
            View view = inflater.inflate(android.R.layout.list_content, container, false);
            ButterKnife.bind(this, view);
            final ArrayList<SettingMenuItem> settingMenuItems = new ArrayList<>();
            settingMenuItems.add(new SettingMenuItem(getString(R.string.setting_action_account), SETTING_ACTION_TYPE_ACCOUNT));
            settingMenuItems.add(new SettingMenuItem(getString(R.string.setting_action_tab), SETTING_ACTION_TYPE_TAB));
            final SettingAdapter settingAdapter = new SettingAdapter(getActivity(), 0, settingMenuItems);
            settingListView.setAdapter(settingAdapter);
            settingListView.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SettingMenuItem settingMenuItem = settingMenuItems.get(position);
                    switch (settingMenuItem.actionType) {
                        case SETTING_ACTION_TYPE_ACCOUNT:
                            startActivity(new Intent(getActivity(), AccountControlActivity.class));
                            break;
                        case SETTING_ACTION_TYPE_TAB:
                            startActivity(new Intent(getActivity(), TabControlActivity.class));
                            break;
                    }
                }
            });
            return view;
        }

        class SettingMenuItem {
            public String settingMenuItemText;
            public int actionType;

            public SettingMenuItem(String settingMenuItemText, int actionType) {
                this.settingMenuItemText = settingMenuItemText;
                this.actionType = actionType;
            }
        }

        class SettingAdapter extends ArrayAdapter<SettingMenuItem> {
            public SettingAdapter(Context context, int resource, List objects) {
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
                SettingMenuItem item = getItem(position);
                holder.settingMenuItemText.setText(item.settingMenuItemText);
                return convertView;
            }

            class ViewHolder {
                @Bind(android.R.id.text1) TextView settingMenuItemText;

                public ViewHolder(View view) {
                    ButterKnife.bind(this, view);
                }
            }
        }
    }
}
