package jp.unknown.works.twinown;

import android.content.Context;
import android.support.v4.app.Fragment;
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
import jp.unknown.works.twinown.models.Tab;

public class TabControlActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_control);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(getString(R.string.setting_action_tab));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tab_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}
