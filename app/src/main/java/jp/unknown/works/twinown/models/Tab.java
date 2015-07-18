package jp.unknown.works.twinown.models;


import java.io.Serializable;
import java.util.List;

import ollie.Model;
import ollie.annotation.Column;
import ollie.annotation.Table;
import ollie.query.Select;
import twitter4j.UserList;

@Table("tab")
public class Tab extends Model implements Serializable {
    public static final int TAB_TYPE_STREAM = 0;
    public static final int TAB_TYPE_MENTION = 1;
    public static final int TAB_TYPE_LIST = 2;

    @Column("name") public String name;
    @Column("user_id") public Long userId;
    @Column("type") public Integer type;
    @Column("extra") public String extra;  // typeによって使う付加情報。直接参照/代入はしない。
    @Column("sort_key") public Long sort_key;

    public static List<Tab> getAll() {
        return Select.from(Tab.class).orderBy("sort_key").fetch();
    }

    public static int getCount() {
        return Select.columns("COUNT(*)").from(Tab.class).fetchValue(Integer.class);
    }

    public static Long getMaxSortKey() {
        return Select.columns("MAX(sort_key)").from(Tab.class).fetchValue(Long.class);
    }

    public static Tab createStreamTab(UserPreference userPreference) {
        Tab streamTab = new Tab();
        streamTab.name = String.format("@%s stream", userPreference.screenName);
        streamTab.userId = userPreference.userId;
        streamTab.type = Tab.TAB_TYPE_STREAM;
        streamTab.extra = "";
        streamTab.sort_key = getMaxSortKey() + 1;
        streamTab.save();
        return streamTab;
    }

    public static Tab createMentionTab(UserPreference userPreference) {
        Tab mentionTab = new Tab();
        mentionTab.name = String.format("@%s mention", userPreference.screenName);
        mentionTab.userId = userPreference.userId;
        mentionTab.type = Tab.TAB_TYPE_MENTION;
        mentionTab.extra = "";
        mentionTab.sort_key = getMaxSortKey() + 1;
        mentionTab.save();
        return mentionTab;
    }

    public static Tab createListTab(UserPreference userPreference, UserList userList) {
        Tab listTab = new Tab();
        listTab.name = userList.getFullName();
        listTab.userId = userPreference.userId;
        listTab.type = Tab.TAB_TYPE_LIST;
        listTab.extra = String.valueOf(userList.getId());
        listTab.sort_key = getMaxSortKey() + 1;
        listTab.save();
        return listTab;
    }

    public Long getListId() {
        return Long.valueOf(extra);
    }
}
