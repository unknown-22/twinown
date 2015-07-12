package jp.unknown.works.twinown;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }

    public static class SettingActivityFragment extends Fragment {
        private LayoutInflater layoutInflater;
        @Bind(R.id.settingListView)
        ListView settingListView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            this.layoutInflater = inflater;
            View view = inflater.inflate(R.layout.fragment_setting, container, false);
            ButterKnife.bind(this, view);
            final ArrayList<SettingMenuItem> settingMenuItems = new ArrayList<>();
            settingMenuItems.add(new SettingMenuItem("hoge", 0));
            SettingAdapter settingAdapter = new SettingAdapter(getActivity(), 0, settingMenuItems);
            settingListView.setAdapter(settingAdapter);
            return view;
        }

        class SettingMenuItem {
            public String settingMenuItemText;
            public int actionType;

            public SettingMenuItem(String statusMenuItemText, int actionType) {
                this.settingMenuItemText = statusMenuItemText;
                this.actionType = actionType;
            }
        }

        class SettingAdapter extends ArrayAdapter {
            public SettingAdapter(Context context, int resource, List objects) {
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
                SettingMenuItem item = (SettingMenuItem) getItem(position);
                holder.statusMenuItemText.setText(item.settingMenuItemText);
                return convertView;
            }

            class ViewHolder {
                @Bind(R.id.statusMenuItemText)
                TextView statusMenuItemText;

                public ViewHolder(View view) {
                    ButterKnife.bind(this, view);
                }
            }
        }
    }
}
