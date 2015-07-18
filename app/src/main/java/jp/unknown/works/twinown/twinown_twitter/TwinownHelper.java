package jp.unknown.works.twinown.twinown_twitter;

import android.os.AsyncTask;

import java.util.HashMap;

import jp.unknown.works.twinown.models.Client;
import jp.unknown.works.twinown.models.UserPreference;
import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.StatusUpdate;
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
        twitter.addListener(new TwitterListener(userPreference));
        userIdTwitterHashMap.put(userPreference.userId, twitter);
        return twitter;
    }

    public static void updateStatus(UserPreference userPreference, String statusText, Status toReplyStatus) {
        AsyncTwitter twitter;
        if (userIdTwitterHashMap.containsKey(userPreference.userId)){
            twitter = userIdTwitterHashMap.get(userPreference.userId);
        } else {
            twitter = createTwitter(userPreference);
        }
        StatusUpdate statusUpdate = new StatusUpdate(statusText);
        if (toReplyStatus != null) {
            statusUpdate.setInReplyToStatusId(toReplyStatus.getId());
        }
        twitter.updateStatus(statusUpdate);
    }

    public static void getUser(UserPreference userPreference) {
        AsyncTwitter twitter;
        if (userIdTwitterHashMap.containsKey(userPreference.userId)){
            twitter = userIdTwitterHashMap.get(userPreference.userId);
        } else {
            twitter = createTwitter(userPreference);
        }
        twitter.showUser(userPreference.userId);
    }

    public static void createFavorite(UserPreference userPreference, Status status) {
        AsyncTwitter twitter;
        if (userIdTwitterHashMap.containsKey(userPreference.userId)){
            twitter = userIdTwitterHashMap.get(userPreference.userId);
        } else {
            twitter = createTwitter(userPreference);
        }
        twitter.createFavorite(status.getId());
    }

    public static void getHomeTimeline(UserPreference userPreference) {
        AsyncTwitter twitter;
        if (userIdTwitterHashMap.containsKey(userPreference.userId)){
            twitter = userIdTwitterHashMap.get(userPreference.userId);
        } else {
            twitter = createTwitter(userPreference);
        }
        twitter.getHomeTimeline();
    }

    public static void getMentionTimeline(UserPreference userPreference) {
        AsyncTwitter twitter;
        if (userIdTwitterHashMap.containsKey(userPreference.userId)){
            twitter = userIdTwitterHashMap.get(userPreference.userId);
        } else {
            twitter = createTwitter(userPreference);
        }
        twitter.getMentions();
    }

    public static void getUserLists(UserPreference userPreference) {
        AsyncTwitter twitter;
        if (userIdTwitterHashMap.containsKey(userPreference.userId)){
            twitter = userIdTwitterHashMap.get(userPreference.userId);
        } else {
            twitter = createTwitter(userPreference);
        }
        twitter.getUserLists(userPreference.userId);
    }

    public static void getTabTimeline(UserPreference userPreference, Long listId) {
        AsyncTwitter twitter;
        if (userIdTwitterHashMap.containsKey(userPreference.userId)){
            twitter = userIdTwitterHashMap.get(userPreference.userId);
        } else {
            twitter = createTwitter(userPreference);
        }
        twitter.getUserListStatuses(listId, new Paging());
    }

    private static TwitterStream createUserStream(UserPreference userPreference) {
        Client client = Client.get(userPreference.clientId);
        ConfigurationBuilder conf  = new ConfigurationBuilder()
                .setOAuthAccessToken(userPreference.tokenKey)
                .setOAuthAccessTokenSecret(userPreference.tokenSecret)
                .setOAuthConsumerKey(client.consumerKey)
                .setOAuthConsumerSecret(client.consumerSecret);
        TwitterStream twitterStream = new TwitterStreamFactory(conf.build()).getInstance();
        twitterStream.addListener(new TwinownUserStreamListener(userPreference));
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
