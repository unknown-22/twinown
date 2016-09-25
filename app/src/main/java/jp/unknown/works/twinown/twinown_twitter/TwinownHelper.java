package jp.unknown.works.twinown.twinown_twitter;

import android.os.AsyncTask;
import android.util.LongSparseArray;

import java.io.InputStream;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import jp.unknown.works.twinown.models.Client;
import jp.unknown.works.twinown.models.UserPreference;
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
import twitter4j.UserList;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

public class TwinownHelper {
    private static final TwitterFactory twitterFactory = new TwitterFactory();
    private static final LongSparseArray<Twitter> userIdTwitterHashMap = new LongSparseArray<>();

    private static Twitter getOrCreateTwitter(UserPreference userPreference) {
        Twitter twitter;
        if (userIdTwitterHashMap.get(userPreference.userId) != null){
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

    public static void clearAllTwitter() {
        userIdTwitterHashMap.clear();
    }

    public static void updateStatus(UserPreference userPreference, final String statusText, final Status toReplyStatus,
                                    final ArrayList<InputStream> imageInputStreams) {
        final Twitter twitter = getOrCreateTwitter(userPreference);
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    StatusUpdate statusUpdate = new StatusUpdate(statusText);
                    if (toReplyStatus != null) {
                        statusUpdate.setInReplyToStatusId(toReplyStatus.getId());
                    }
                    if (imageInputStreams != null && imageInputStreams.size() > 0) {
                        long[] mediaIdArray = new long[imageInputStreams.size()];
                        for (int i=0; i < imageInputStreams.size(); i++) {
                            mediaIdArray[i] = twitter.uploadMedia(
                                    "upload_image", imageInputStreams.get(i)
                            ).getMediaId();
                        }
                        statusUpdate.setMediaIds(mediaIdArray);
                    }
                    twitter.updateStatus(statusUpdate);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.execute();
    }

    public static void retweetStatus(UserPreference userPreference, final Status status) {
        final Twitter twitter = getOrCreateTwitter(userPreference);
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    twitter.retweetStatus(status.getId());
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.execute();
    }

    public static void deleteStatus(UserPreference userPreference, final Status status) {
        final Twitter twitter = getOrCreateTwitter(userPreference);
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    twitter.destroyStatus(status.getId());
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.execute();
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

    public static Status getStatusSync(UserPreference userPreference, long statusId) {
        Twitter twitter = getOrCreateTwitter(userPreference);
        try {
            return twitter.showStatus(statusId);
        } catch (TwitterException e) {
            return null;
        }
    }

    public static User getUserSync(UserPreference userPreference, String screenName) {
        Twitter twitter = getOrCreateTwitter(userPreference);
        try {
            return twitter.showUser(screenName);
        } catch (TwitterException e) {
            return null;
        }
    }

    public static void createFavorite(UserPreference userPreference, final Status status) {
        final Twitter twitter = getOrCreateTwitter(userPreference);
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    twitter.createFavorite(status.getId());
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.execute();
    }

    public static void deleteFavorite(UserPreference userPreference, final Status status) {
        final Twitter twitter = getOrCreateTwitter(userPreference);
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    twitter.destroyFavorite(status.getId());
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.execute();
    }

    public static void getInReplyToStatus(final UserPreference userPreference, final Status status) {
        final Twitter twitter = getOrCreateTwitter(userPreference);
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    EventBus.getDefault().post(new Component.InReplyToEvent(
                            twitter.showStatus(status.getInReplyToStatusId()),
                            userPreference, status));
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.execute();
    }

    public static void getHomeTimeline(final UserPreference userPreference, final Paging paging,
                                       final boolean isReconnect) {
        final Twitter twitter = getOrCreateTwitter(userPreference);
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    ResponseList<twitter4j.Status> statuses = twitter.getHomeTimeline(paging);
                    EventBus.getDefault().post(
                            new Component.HomeStatusListEvent(statuses, userPreference, isReconnect)
                    );
                } catch (TwitterException e) {
                    EventBus.getDefault().post(new Component.HomeStatusListEvent(null, userPreference, isReconnect));
                }
                return null;
            }
        };
        task.execute();
    }

    public static void getMentionTimeline(final UserPreference userPreference, final Paging paging) {
        final Twitter twitter = getOrCreateTwitter(userPreference);
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    ResponseList<twitter4j.Status> statuses = twitter.getMentionsTimeline(paging);
                    EventBus.getDefault().post(new Component.MentionStatusListEvent(statuses, userPreference));
                } catch (TwitterException e) {
                    EventBus.getDefault().post(new Component.MentionStatusListEvent(null, userPreference));
                }
                return null;
            }
        };
        task.execute();
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

    public static void getUserLists(final UserPreference userPreference) {
        final Twitter twitter = getOrCreateTwitter(userPreference);
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    ResponseList<UserList> userLists = twitter.getUserLists(userPreference.userId);
                    EventBus.getDefault().post(new Component.UserListsEvent(userLists));
                } catch (TwitterException e) {
                    EventBus.getDefault().post(new Component.UserListsEvent(null));
                }
                return null;
            }
        };
        task.execute();
    }

    public static void getUserListStatuses(final UserPreference userPreference, final Long listId, final Paging paging,
                                           final boolean isHead) {
        final Twitter twitter = getOrCreateTwitter(userPreference);
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    ResponseList<twitter4j.Status> statuses = twitter.getUserListStatuses(listId, paging);
                    EventBus.getDefault().post(
                            new Component.UserListStatusesEvent(statuses, userPreference, listId, isHead)
                    );
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.execute();
    }

    private static TwitterStream createUserStream(UserPreference userPreference, int count) {
        Client client = Client.get(userPreference.clientId);
        ConfigurationBuilder conf  = new ConfigurationBuilder()
                .setOAuthAccessToken(userPreference.tokenKey)
                .setOAuthAccessTokenSecret(userPreference.tokenSecret)
                .setOAuthConsumerKey(client.consumerKey)
                .setOAuthConsumerSecret(client.consumerSecret);
        TwitterStream twitterStream = new TwitterStreamFactory(conf.build()).getInstance();
        TwinownUserStreamListener twinownUserStreamListener = new TwinownUserStreamListener(userPreference, count);
        twitterStream.addListener(twinownUserStreamListener);
        twitterStream.addConnectionLifeCycleListener(twinownUserStreamListener);
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
        private static final LongSparseArray<TwitterStream> userIdTwitterStreamHashMap = new LongSparseArray<>();

        public static StreamSingleton getInstance() {
            return ourInstance;
        }

        public TwitterStream getOrCreateTwitterStream(UserPreference userPreference, int count) {
            if (userIdTwitterStreamHashMap.get(userPreference.userId) != null) {
                return userIdTwitterStreamHashMap.get(userPreference.userId);
            }
            TwitterStream twitterStream = createUserStream(userPreference, count);
            userIdTwitterStreamHashMap.put(userPreference.userId, twitterStream);
            return twitterStream;
        }

        public void startUserStream(UserPreference userPreference) {
            if (userIdTwitterStreamHashMap.get(userPreference.userId) != null) {
                TwinownHelper.startUserStream(userIdTwitterStreamHashMap.get(userPreference.userId));
            }
        }

        public void stopAndDeleteUserStream(UserPreference userPreference) {
            if (userIdTwitterStreamHashMap.get(userPreference.userId) != null) {
                TwinownHelper.stopUserStream(userIdTwitterStreamHashMap.get(userPreference.userId));
                userIdTwitterStreamHashMap.remove(userPreference.userId);
            }
        }

        public void stopAllUserStream() {
            for(int i = 0; i < userIdTwitterStreamHashMap.size(); i++) {
                long key = userIdTwitterStreamHashMap.keyAt(i);
                TwinownHelper.stopUserStream(userIdTwitterStreamHashMap.get(key));
            }
            userIdTwitterStreamHashMap.clear();
        }
    }
}
