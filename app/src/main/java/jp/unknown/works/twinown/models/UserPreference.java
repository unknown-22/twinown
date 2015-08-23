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
    @Column("client_id") public Long clientId;

    public static UserPreference get(Long userId) {
        return Select.from(UserPreference.class).where(String.format("%s=%d", "user_id", userId)).fetchSingle();
    }

    public static List<UserPreference> getByClientId(long clientId) {
        return Select.from(UserPreference.class).where(String.format("%s=%d", "client_id", clientId)).fetch();
    }

    public static List<UserPreference> getAll() {
        return Select.from(UserPreference.class).fetch();
    }

    public static int getCount() {
        return Select.columns("COUNT(*)").from(UserPreference.class).fetchValue(Integer.class);
    }

    public static UserPreference createUserPreference(Long userId, String screenName, String tokenKey,
                                                      String tokenSecret, Long clientId) {
        UserPreference userPreference = new UserPreference();
        userPreference.userId = userId;
        userPreference.screenName = screenName;
        userPreference.tokenKey = tokenKey;
        userPreference.tokenSecret = tokenSecret;
        userPreference.clientId = clientId;
        userPreference.save();
        return userPreference;
    }
}
