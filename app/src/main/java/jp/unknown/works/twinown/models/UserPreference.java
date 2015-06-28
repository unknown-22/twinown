package jp.unknown.works.twinown.models;


import java.util.List;

import ollie.Model;
import ollie.annotation.Column;
import ollie.annotation.Table;
import ollie.query.Select;

@Table("users")
public class UserPreference extends Model {
    @Column("user_id") public Long userId;
    @Column("screen_name") public String screenName;
    @Column("token_key") public String tokenKey;
    @Column("token_secret") public String tokenSecret;
    @Column("consumer_key") public String consumerKey;
    @Column("consumer_secret") public String consumerSecret;

    public List<UserPreference> getAll() {
        return Select.from(UserPreference.class).fetch();
    }

    public static int getCount() {
        return Select.columns("COUNT(*)").from(UserPreference.class).fetchValue(Integer.class);
    }
}
