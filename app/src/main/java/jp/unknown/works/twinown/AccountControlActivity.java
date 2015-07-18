package jp.unknown.works.twinown;

import android.content.Context;
import android.content.Intent;
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
import jp.unknown.works.twinown.models.UserPreference;

public class AccountControlActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_control);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(getString(R.string.setting_action_account));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_account_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            Intent intent = new Intent(Globals.ACTION_KEYWORD_AUTHORIZATION);
            intent.setClass(this, AuthActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    static class AccountMenuItem {
        public String screenName;

        public AccountMenuItem(String screenName) {
            this.screenName = screenName;
        }
    }

    public static class AccountControlFragment extends Fragment {
        private LayoutInflater layoutInflater;
        private ArrayList<AccountMenuItem> accountMenuItems = new ArrayList<>();
        private AccountAdapter accountAdapter;
        @Bind(android.R.id.list) ListView accountListView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            this.layoutInflater = inflater;
            View view = inflater.inflate(android.R.layout.list_content, container, false);
            ButterKnife.bind(this, view);
            accountAdapter = new AccountAdapter(getActivity(), 0, accountMenuItems);
            accountListView.setAdapter(accountAdapter);
            return view;
        }

        @Override
        public void onResume() {
            super.onResume();
            accountMenuItems.clear();
            for (UserPreference userPreference : UserPreference.getAll()) {
                accountMenuItems.add(new AccountMenuItem(String.format("@%s", userPreference.screenName)));
            }
            accountAdapter.notifyDataSetChanged();
        }

        class AccountAdapter extends ArrayAdapter<AccountMenuItem> {
            public AccountAdapter(Context context, int resource, List objects) {
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
                AccountMenuItem item = getItem(position);
                holder.accountMenuItemText.setText(item.screenName);
                return convertView;
            }

            class ViewHolder {
                @Bind(android.R.id.text1) TextView accountMenuItemText;

                public ViewHolder(View view) {
                    ButterKnife.bind(this, view);
                }
            }
        }
    }
}
