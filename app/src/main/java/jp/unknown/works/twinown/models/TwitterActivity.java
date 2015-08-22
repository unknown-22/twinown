package jp.unknown.works.twinown.models;


import twitter4j.Status;
import twitter4j.User;

public class TwitterActivity {
    public static final int TYPE_FAVORITED = 0;

    public Long id;
    public UserPreference userPreference;
    public String text;
    public User user;
    public int type;
    public Status status;

    public TwitterActivity(Long id, UserPreference userPreference, int type, String text, User user, Status status) {
        this.id = id;
        this.userPreference = userPreference;
        this.type = type;
        this.text = text;
        this.user = user;
        this.status = status;
    }
}
