package jp.unknown.works.twinown.models;


import java.io.Serializable;
import java.util.List;

import ollie.Model;
import ollie.annotation.Column;
import ollie.annotation.Table;
import ollie.query.Select;

@Table("tab")
public class Tab extends Model implements Serializable {
    public static final int TAB_TYPE_STREAM = 0;
    public static final int TAB_TYPE_MENTION = 1;

    @Column("name") public String name;
    @Column("user_id") public Long userId;
    @Column("type") public Integer type;
    @Column("extra") public String extra;  // typeによって使う付加情報。直接参照/代入はしない。

    public static List<Tab> getAll() {
        return Select.from(Tab.class).fetch();
    }

    public static int getCount() {
        return Select.columns("COUNT(*)").from(Tab.class).fetchValue(Integer.class);
    }

    public static Tab createStreamTab(UserPreference userPreference) {
        Tab streamTab = new Tab();
        streamTab.name = String.format("@%s stream", userPreference.screenName);
        streamTab.userId = userPreference.userId;
        streamTab.type = Tab.TAB_TYPE_STREAM;
        streamTab.extra = "";
        streamTab.save();
        return streamTab;
    }

    public static Tab createMentionTab(UserPreference userPreference) {
        Tab mentionTab = new Tab();
        mentionTab.name = String.format("@%s mention", userPreference.screenName);
        mentionTab.userId = userPreference.userId;
        mentionTab.type = Tab.TAB_TYPE_MENTION;
        mentionTab.extra = "";
        mentionTab.save();
        return mentionTab;
    }
}
