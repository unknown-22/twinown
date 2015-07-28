package jp.unknown.works.twinown.twinown_twitter;

import android.os.AsyncTask;

import java.util.HashMap;

import de.greenrobot.event.EventBus;
import jp.unknown.works.twinown.models.Client;
import jp.unknown.works.twinown.models.UserPreference;
import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

public class TwinownHelper {
    private static final TwitterFactory twitterFactory = new TwitterFactory();
    private static final HashMap<Long, Twitter> userIdTwitterHashMap = new HashMap<>();
    private static final AsyncTwitterFactory asyncTwitterFactory = new AsyncTwitterFactory();
    private static final HashMap<Long, AsyncTwitter> userIdAsyncTwitterHashMap = new HashMap<>();

    private static AsyncTwitter createAsyncTwitter(UserPreference userPreference) {
        Client client = Client.get(userPreference.clientId);
        AsyncTwitter twitter = asyncTwitterFactory.getInstance();
        twitter.setOAuthConsumer(client.consumerKey, client.consumerSecret);
        twitter.setOAuthAccessToken(new AccessToken(userPreference.tokenKey, userPreference.tokenSecret));
        twitter.addListener(new TwitterListener(userPreference));
        userIdAsyncTwitterHashMap.put(userPreference.userId, twitter);
        return twitter;
    }

    private static Twitter getOrCreateTwitter(UserPreference userPreference) {
        Twitter twitter;
        if (userIdTwitterHashMap.containsKey(userPreference.userId)){
            twitter = userIdTwitterHashMap.get(userPreference.userId);
        } else {
            Client client = Client.get(userPreference.clientId);
            twitter = twitterFactory.getInstance();
            twitter.setOAuthConsumer(client.consumerKey, client.consumerSecret);
            twitter.setOAuthAccessToken(new AccessToken(userPreference.tokenKey, userPreference.tokenSecret));
            userIdTwitterHashMap.put(userPreference.userId, twitter);
        }
        return twitter;
    }

    public static void updateStatus(UserPreference userPreference, String statusText, Status toReplyStatus) {
        AsyncTwitter twitter;
        if (userIdAsyncTwitterHashMap.containsKey(userPreference.userId)){
            twitter = userIdAsyncTwitterHashMap.get(userPreference.userId);
        } else {
            twitter = createAsyncTwitter(userPreference);
        }
        StatusUpdate statusUpdate = new StatusUpdate(statusText);
        if (toReplyStatus != null) {
            statusUpdate.setInReplyToStatusId(toReplyStatus.getId());
        }
        twitter.updateStatus(statusUpdate);
    }

    public static void getUser(final UserPreference userPreference) {
        new AsyncTask<Void, Void, User>() {
            Twitter twitter;

            @Override
            protected void onPreExecute() {
                twitter = getOrCreateTwitter(userPreference);
            }

            @Override
            protected User doInBackground(Void... params) {
                try {
                    return twitter.showUser(userPreference.userId);
                } catch (TwitterException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(User user) {
                if (user != null) {
                    EventBus.getDefault().post(new Component.UserEvent(user));
                }
            }
        }.execute();
    }

    public static User getUserSync(UserPreference userPreference, String screenName) {
        Twitter twitter = getOrCreateTwitter(userPreference);
        try {
            return twitter.showUser(screenName);
        } catch (TwitterException e) {
            return null;
        }
    }

    public static void createFavorite(UserPreference userPreference, Status status) {
        AsyncTwitter twitter;
        if (userIdAsyncTwitterHashMap.containsKey(userPreference.userId)){
            twitter = userIdAsyncTwitterHashMap.get(userPreference.userId);
        } else {
            twitter = createAsyncTwitter(userPreference);
        }
        twitter.createFavorite(status.getId());
    }

    public static void getHomeTimeline(UserPreference userPreference, Paging paging) {
        AsyncTwitter twitter;
        if (userIdAsyncTwitterHashMap.containsKey(userPreference.userId)){
            twitter = userIdAsyncTwitterHashMap.get(userPreference.userId);
        } else {
            twitter = createAsyncTwitter(userPreference);
        }
        twitter.getHomeTimeline(paging);
    }

    public static void getMentionTimeline(UserPreference userPreference, Paging paging) {
        AsyncTwitter twitter;
        if (userIdAsyncTwitterHashMap.containsKey(userPreference.userId)){
            twitter = userIdAsyncTwitterHashMap.get(userPreference.userId);
        } else {
            twitter = createAsyncTwitter(userPreference);
        }
        twitter.getMentions(paging);
    }

    public static void getUserTimeLine(final UserPreference userPreference, final Long userId, final Paging paging) {
        final Twitter twitter = getOrCreateTwitter(userPreference);
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    ResponseList<twitter4j.Status> statuses = twitter.getUserTimeline(userId, paging);
                    EventBus.getDefault().post(new Component.UserTimeLineEvent(statuses, userPreference, userId));
                } catch (TwitterException e) {
                    EventBus.getDefault().post(new Component.UserTimeLineEvent(null, userPreference, userId));
                }
                return null;
            }
        };
        task.execute();
    }

    public static void getUserLists(UserPreference userPreference) {
        AsyncTwitter twitter;
        if (userIdAsyncTwitterHashMap.containsKey(userPreference.userId)){
            twitter = userIdAsyncTwitterHashMap.get(userPreference.userId);
        } else {
            twitter = createAsyncTwitter(userPreference);
        }
        twitter.getUserLists(userPreference.userId);
    }

    public static void getUserListStatuses(final UserPreference userPreference, final Long listId, final Paging paging) {
        final Twitter twitter = getOrCreateTwitter(userPreference);
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    ResponseList<twitter4j.Status> statuses = twitter.getUserListStatuses(listId, paging);
                    EventBus.getDefault().post(new Component.UserListStatusesEvent(statuses, userPreference, listId));
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.execute();
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
