package jp.unknown.works.twinown;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import jp.unknown.works.twinown.models.Client;
import jp.unknown.works.twinown.models.Tab;
import jp.unknown.works.twinown.models.UserPreference;
import jp.unknown.works.twinown.twinown_twitter.Component;

public class ClientControlActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_control);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(getString(R.string.setting_action_client));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_client_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            View view = getLayoutInflater().inflate(R.layout.client_input_dialog, null);
            final AlertDialog dialog = new AlertDialog.Builder(this)
                    .setView(view)
                    .setTitle(R.string.dialog_title_add_client)
                    .show();
            final EditText clientNameEditText = (EditText) dialog.findViewById(R.id.clientNameEditText);
            final EditText clientKeyEditText = (EditText) dialog.findViewById(R.id.clientKeyEditText);
            final EditText clientSecretEditText = (EditText) dialog.findViewById(R.id.clientSecretEditText);
            dialog.findViewById(R.id.buttonOk).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clientNameEditText.length() > 0 && clientKeyEditText.length() > 0 && clientSecretEditText.length() > 0){
                        Client.createClient(
                                clientNameEditText.getText().toString(),
                                clientKeyEditText.getText().toString(),
                                clientSecretEditText.getText().toString()
                        );
                        dialog.dismiss();
                        EventBus.getDefault().post(new Component.ClientAddEvent());
                    }
                }
            });
            dialog.findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    static class ClientMenuItem {
        public long clientId;
        public String clientName;

        public ClientMenuItem(long clientId, String clientName) {
            this.clientId = clientId;
            this.clientName = clientName;
        }
    }

    public static class ClientControlFragment extends Fragment {
        private LayoutInflater layoutInflater;
        private ArrayList<ClientMenuItem> clientMenuItems = new ArrayList<>();
        private ClientAdapter clientAdapter;
        @Bind(android.R.id.list) ListView clientListView;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EventBus.getDefault().register(this);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            this.layoutInflater = inflater;
            View view = inflater.inflate(android.R.layout.list_content, container, false);
            ButterKnife.bind(this, view);
            clientAdapter = new ClientAdapter(getActivity(), 0, clientMenuItems);
            clientListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.confirm_dialog))
                            .setMessage(String.format(getString(R.string.delete_confirm), clientMenuItems.get(position).clientName))
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Client.get(clientMenuItems.get(position).clientId).delete();
                                    for (UserPreference userPreference : UserPreference.getByClientId(clientMenuItems.get(position).clientId)) {
                                        userPreference.delete();
                                        for (Tab tab : Tab.getUserTab(userPreference.userId)) {
                                        tab.delete();
                                        }
                                    }
                                    clientMenuItems.remove(position);
                                    clientAdapter.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                    return false;
                }
            });
            clientListView.setAdapter(clientAdapter);
            return view;
        }

        @Override
        public void onResume() {
            super.onResume();
            clientMenuItems.clear();
            for (Client client : Client.getAll()) {
                clientMenuItems.add(new ClientMenuItem(client.id, client.name));
            }
        }

        class ClientAdapter extends ArrayAdapter<ClientMenuItem> {
            public ClientAdapter(Context context, int resource, List objects) {
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
                ClientMenuItem item = getItem(position);
                holder.accountMenuItemText.setText(item.clientName);
                return convertView;
            }

            class ViewHolder {
                @Bind(android.R.id.text1) TextView accountMenuItemText;

                public ViewHolder(View view) {
                    ButterKnife.bind(this, view);
                }
            }
        }

        public void onEventMainThread(Component.ClientAddEvent clientAddEvent) {
            clientMenuItems.clear();
            for (Client client : Client.getAll()) {
                clientMenuItems.add(new ClientMenuItem(client.id, client.name));
            }
            clientAdapter.notifyDataSetChanged();
        }
    }
}
