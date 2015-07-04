package jp.unknown.works.twinown.twinown_twitter;

import android.os.AsyncTask;

import java.util.HashMap;

import jp.unknown.works.twinown.models.Client;
import jp.unknown.works.twinown.models.UserPreference;
import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

public class TwinownHelper {
    private static final AsyncTwitterFactory factory = new AsyncTwitterFactory();
    private static final HashMap<Long, AsyncTwitter> userIdTwitterHashMap = new HashMap<>();

    private static AsyncTwitter createTwitter(UserPreference userPreference) {
        Client client = Client.get(userPreference.clientId);
        AsyncTwitter twitter = factory.getInstance();
        twitter.setOAuthConsumer(client.consumerKey, client.consumerSecret);
        twitter.setOAuthAccessToken(new AccessToken(userPreference.tokenKey, userPreference.tokenSecret));
        return twitter;
    }

    public static void statusUpdate(UserPreference userPreference, String statusText) {
        if (userIdTwitterHashMap.containsKey(userPreference.userId)){
            userIdTwitterHashMap.get(userPreference.userId).updateStatus(statusText);
        }
        AsyncTwitter twitter = createTwitter(userPreference);
        twitter.updateStatus(statusText);
        userIdTwitterHashMap.put(userPreference.userId, twitter);
    }

    private static TwitterStream createUserStream(UserPreference userPreference) {
        Client client = Client.get(userPreference.clientId);
        ConfigurationBuilder conf  = new ConfigurationBuilder()
                .setOAuthAccessToken(userPreference.tokenKey)
                .setOAuthAccessTokenSecret(userPreference.tokenSecret)
                .setOAuthConsumerKey(client.consumerKey)
                .setOAuthConsumerSecret(client.consumerSecret);
        TwitterStream twitterStream = new TwitterStreamFactory(conf.build()).getInstance();
        twitterStream.addListener(new TwinownUserStreamListener());
        return twitterStream;
    }

    private static void startUserStream(final TwitterStream twitterStream) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                twitterStream.user();
                return null;
            }
        };
        task.execute();
    }

    private static void stopUserStream(final TwitterStream twitterStream) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                twitterStream.cleanUp();
                twitterStream.shutdown();
                return null;
            }
        };
        task.execute();
    }

    public static class StreamSingleton {
        private static final StreamSingleton ourInstance = new StreamSingleton();
        private static final HashMap<Long, TwitterStream> userIdTwitterStreamHashMap = new HashMap<>();

        public static StreamSingleton getInstance() {
            return ourInstance;
        }

        public TwitterStream getOrCreateTwitterStream(UserPreference userPreference) {
            if (userIdTwitterStreamHashMap.containsKey(userPreference.userId)) {
                return userIdTwitterStreamHashMap.get(userPreference.userId);
            }
            TwitterStream twitterStream = createUserStream(userPreference);
            userIdTwitterStreamHashMap.put(userPreference.userId, twitterStream);
            return twitterStream;
        }

        public void startUserStream(UserPreference userPreference) {
            if (userIdTwitterStreamHashMap.containsKey(userPreference.userId)) {
                TwinownHelper.startUserStream(userIdTwitterStreamHashMap.get(userPreference.userId));
            }
        }

        public void stopAndDeleteUserStream(UserPreference userPreference) {
            if (userIdTwitterStreamHashMap.containsKey(userPreference.userId)) {
                TwinownHelper.stopUserStream(userIdTwitterStreamHashMap.get(userPreference.userId));
                userIdTwitterStreamHashMap.remove(userPreference.userId);
            }
        }

        public void stopAllUserStream() {
            for (TwitterStream twitterStream : userIdTwitterStreamHashMap.values()) {
                TwinownHelper.stopUserStream(twitterStream);
            }
            userIdTwitterStreamHashMap.clear();
        }
    }
}