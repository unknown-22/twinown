package jp.unknown.works.twinown.models;


import java.io.Serializable;

import ollie.Model;
import ollie.annotation.Column;
import ollie.annotation.Table;
import ollie.query.Select;

@Table("client")
public class Client extends Model implements Serializable{
    @Column("name") public String name;
    @Column("consumer_key") public String consumerKey;
    @Column("consumer_secret") public String consumerSecret;

    public static Client get() {
        return Select.from(Client.class).fetchSingle();
    }

    public static Client get(Long clientId) {
        return Select.from(Client.class).where(String.format("%s=%d", Model._ID, clientId)).fetchSingle();
    }

    public static int getCount() {
        return Select.columns("COUNT(*)").from(Client.class).fetchValue(Integer.class);
    }

    public static Client createClient(String name, String consumerKey, String consumerSecret) {
        Client client = new Client();
        client.name = name;
        client.consumerKey = consumerKey;
        client.consumerSecret = consumerSecret;
        client.save();
        return client;
    }
}
