package jp.unknown.works.twinown;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.unknown.works.twinown.models.Client;
import jp.unknown.works.twinown.models.Tab;
import jp.unknown.works.twinown.models.UserPreference;
import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterListener;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class AuthActivity extends AppCompatActivity {
    private static final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(getString(R.string.title_activity_auth));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_auth_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, ClientControlActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class AuthActivityFragment extends Fragment {
        private Client client;
        private RequestToken mRequestToken;
        final AsyncTwitterFactory factory = new AsyncTwitterFactory();
        AsyncTwitter twitter = factory.getInstance();
        @Bind(R.id.pin_code_edit_text) EditText pinCodeEditText;
        @Bind(R.id.clientNameTextView) TextView clientNameTextView;
        @Bind(R.id.change_client) Button changeClientButton;

        @SuppressWarnings("unused")
        @OnClick(R.id.open_authorization_button)
        public void openAuthorization(Button button) {
            if (mRequestToken == null) {
                Utils.showSnackBar(button, getResources().getText(R.string.error));
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mRequestToken.getAuthorizationURL()));
                startActivity(intent);
            }
        }

        @SuppressWarnings("unused")
        @OnClick(R.id.pin_code_ok_button)
        public void pinCodeOk(Button button) {
            if (pinCodeEditText.getText().toString().length() == 0) {
                Utils.showSnackBar(button, getResources().getText(R.string.please_enter_pin_code));
            } else {
                String pinCode = pinCodeEditText.getText().toString();
                twitter.getOAuthAccessTokenAsync(mRequestToken, pinCode);
            }
        }

        @SuppressWarnings("unused")
        @OnClick(R.id.change_client)
        public void selectClient() {
            final List<Client> clients = Client.getAll();
            final String[] clientNames = new String[clients.size()];
            for (int i = 0; i < clients.size(); i++) {
                clientNames[i] = clients.get(i).name;
            }
            new AlertDialog.Builder(getActivity())
                    .setTitle("Selector")
                    .setItems(clientNames, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setClient(clients.get(which));
                        }
                    })
                    .show();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            if (Client.getCount() == 0) {
                client = Client.createClient(
                        getResources().getString(R.string.default_client_name),
                        getResources().getString(R.string.default_consumer_key),
                        getResources().getString(R.string.default_consumer_secret)
                );
            } else {
                client = Client.get();
            }
            twitter.addListener(listener);
            twitter.setOAuthConsumer(client.consumerKey, client.consumerSecret);
            twitter.getOAuthRequestTokenAsync();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_auth, container, false);
            ButterKnife.bind(this, view);
            clientNameTextView.setText(String.format(getString(R.string.show_client_name), client.name));
            if (Client.getCount() > 1) {
                changeClientButton.setVisibility(View.VISIBLE);
            }
            return view;
        }

        public void setClient(Client client) {
            this.client = client;
            clientNameTextView.setText(String.format(getString(R.string.show_client_name), client.name));
            twitter = factory.getInstance();
            twitter.addListener(listener);
            twitter.setOAuthConsumer(client.consumerKey, client.consumerSecret);
            twitter.getOAuthRequestTokenAsync();
        }

        private final TwitterListener listener = new TwitterAdapter() {
            @Override
            public void gotOAuthRequestToken(RequestToken token) {
                mRequestToken = token;
            }

            @Override
            public void gotOAuthAccessToken(AccessToken token) {
                UserPreference userPreference = UserPreference.createUserPreference(
                        token.getUserId(), token.getScreenName(), token.getToken(), token.getTokenSecret(), client.id
                );
                Tab.createStreamTab(userPreference);
                Tab.createMentionTab(userPreference);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showToastLong(getActivity(), getResources().getText(R.string.added_user_preference));
                        getActivity().finish();
                    }
                });
            }
        };
    }
}
