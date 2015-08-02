package jp.unknown.works.twinown.models;


import twitter4j.User;

public class TwitterActivity {
    public static final int TYPE_FAVORITED = 0;

    public Long id;
    public String text;
    public User user;
    public int type;

    public TwitterActivity(Long id, int type, String text, User user) {
        this.id = id;
        this.type = type;
        this.text = text;
        this.user = user;
    }
}
