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

    public static List<Tab> getALL() {
        return Select.from(Tab.class).fetch();
    }

    public static int getCount() {
        return Select.columns("COUNT(*)").from(Tab.class).fetchValue(Integer.class);
    }
}
