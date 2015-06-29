package jp.unknown.works.twinown.models;


import java.io.Serializable;
import java.util.List;

import ollie.Model;
import ollie.annotation.Column;
import ollie.annotation.Table;
import ollie.query.Select;

@Table("users")
public class UserPreference extends Model implements Serializable {
    @Column("user_id") public Long userId;
    @Column("screen_name") public String screenName;
    @Column("token_key") public String tokenKey;
    @Column("token_secret") public String tokenSecret;
    @Column("consumer_key") public String consumerKey;
    @Column("consumer_secret") public String consumerSecret;

    public static UserPreference get() {
        return Select.from(UserPreference.class).fetchSingle(); // TODO userIdから引くと思う
    }

    @SuppressWarnings("unused")  // TODO 設定画面とかで使うはず
    public static List<UserPreference> getAll() {
        return Select.from(UserPreference.class).fetch();
    }

    public static int getCount() {
        return Select.columns("COUNT(*)").from(UserPreference.class).fetchValue(Integer.class);
    }
}
