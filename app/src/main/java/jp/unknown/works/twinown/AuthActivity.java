package jp.unknown.works.twinown;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
    }

    public static class AuthActivityFragment extends Fragment {
        private Client client;
        private RequestToken mRequestToken;
        final AsyncTwitterFactory factory = new AsyncTwitterFactory();
        final AsyncTwitter twitter = factory.getInstance();
        @Bind(R.id.pin_code_edit_text) EditText pinCodeEditText;

        @SuppressWarnings("unused")
        @OnClick(R.id.open_authorization_button)
        public void openAuthorization(Button button) {
            if (mRequestToken == null) {
                Globals.showSnackBar(button, getResources().getText(R.string.error));
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mRequestToken.getAuthorizationURL()));
                startActivity(intent);
            }
        }

        @SuppressWarnings("unused")
        @OnClick(R.id.pin_code_ok_button)
        public void pinCodeOk(Button button) {
            if (pinCodeEditText.getText().toString().length() == 0) {
                Globals.showSnackBar(button, getResources().getText(R.string.please_enter_pin_code));
            } else {
                String pinCode = pinCodeEditText.getText().toString();
                twitter.getOAuthAccessTokenAsync(mRequestToken, pinCode);
            }
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

            return view;
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
                        Globals.showToast(getActivity(), getResources().getText(R.string.added_user_preference));
                        getActivity().finish();
                    }
                });
            }
        };
    }
}
