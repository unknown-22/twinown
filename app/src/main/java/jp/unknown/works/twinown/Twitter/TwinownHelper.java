package jp.unknown.works.twinown.Twitter;

import android.os.AsyncTask;

import jp.unknown.works.twinown.models.UserPreference;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwinownHelper {
    public static TwitterStream createUserStream(UserPreference userPreference) {
        ConfigurationBuilder conf  = new ConfigurationBuilder()
                .setOAuthAccessToken(userPreference.tokenKey)
                .setOAuthAccessTokenSecret(userPreference.tokenSecret)
                .setOAuthConsumerKey(userPreference.consumerKey)
                .setOAuthConsumerSecret(userPreference.consumerSecret);
        TwitterStream twitterStream = new TwitterStreamFactory(conf.build()).getInstance();
        twitterStream.addListener(new TwinownUserStreamListener());
        return twitterStream;
    }

    public static void startUserStream(final TwitterStream twitterStream) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                twitterStream.user();
                return null;
            }
        };
        task.execute();
    }

    public static void stopUserStream(final TwitterStream twitterStream) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                twitterStream.cleanUp();
                return null;
            }
        };
        task.execute();
    }
}
