package jp.unknown.works.twinown.models;


import java.util.List;

import ollie.Model;
import ollie.annotation.Column;
import ollie.annotation.Table;
import ollie.query.Select;

@Table("mute")
public class MuteKeyWord extends Model {
    public static final int MUTE_TYPE_STRING = 0;
    public static final int MUTE_TYPE_USER = 1;
    public static final int MUTE_TYPE_CLIENT = 2;

    @Column("word") public String word;
    @Column("mute_type") public Integer muteType;

    public static List<MuteKeyWord> getAll() {
        return Select.from(MuteKeyWord.class).fetch();
    }

    public static void addMuteKeyWord(String word) {
        MuteKeyWord muteKeyWord = new MuteKeyWord();
        muteKeyWord.word = word;
        muteKeyWord.muteType = MUTE_TYPE_STRING;
        muteKeyWord.save();
    }

    public static void addMuteUser(String screenName) {
        MuteKeyWord muteKeyWord = new MuteKeyWord();
        muteKeyWord.word = screenName;
        muteKeyWord.muteType = MUTE_TYPE_USER;
        muteKeyWord.save();
    }

    public static void addMuteClient(String client_name) {
        MuteKeyWord muteKeyWord = new MuteKeyWord();
        muteKeyWord.word = client_name;
        muteKeyWord.muteType = MUTE_TYPE_CLIENT;
        muteKeyWord.save();
    }
}
