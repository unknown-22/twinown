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
import jp.unknown.works.twinown.models.UserPreference;
import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterListener;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class AuthActivity extends AppCompatActivity {
    private static final Handler handler = new Handler();
    private String consumerKey;
    private String consumerSecret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Globals.ACTION_KEYWORD_AUTHORIZATION.equals(getIntent().getAction())) {
            consumerKey = getIntent().getStringExtra(Globals.EXTRA_KEYWORD_CONSUMER_KEY);
            consumerSecret = getIntent().getStringExtra(Globals.EXTRA_KEYWORD_CONSUMER_SECRET);
        }
        setContentView(R.layout.activity_auth);
    }

    public static class AuthActivityFragment extends Fragment {
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
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_auth, container, false);
            ButterKnife.bind(this, view);
            twitter.addListener(listener);
            AuthActivity authActivity = (AuthActivity) getActivity();
            twitter.setOAuthConsumer(authActivity.consumerKey, authActivity.consumerSecret);
            twitter.getOAuthRequestTokenAsync();
            return view;
        }

        private final TwitterListener listener = new TwitterAdapter() {
            @Override
            public void gotOAuthRequestToken(RequestToken token) {
                mRequestToken = token;
            }

            @Override
            public void gotOAuthAccessToken(AccessToken token) {
                AuthActivity authActivity = (AuthActivity) getActivity();
                UserPreference userPreference = new UserPreference();
                userPreference.userId = token.getUserId();
                userPreference.screenName = token.getScreenName();
                userPreference.tokenKey = token.getToken();
                userPreference.tokenSecret = token.getTokenSecret();
                userPreference.consumerKey = authActivity.consumerKey;
                userPreference.consumerSecret = authActivity.consumerSecret;
                userPreference.save();
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
