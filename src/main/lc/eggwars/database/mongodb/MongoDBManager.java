package lc.eggwars.database.mongodb;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.hash.TIntHashSet;

public final class MongoDBManager {

    private static MongoDBManager queryManager;

    private final MongoCollection<Document> profiles;

    public MongoDBManager(MongoCollection<Document> profiles) {
        this.profiles = profiles;
    }

    public PlayerData getData(final UUID uuid) {
        final Document query = new Document();
        query.put("_id", uuid);
        final FindIterable<Document> results = profiles.find(query);
        final Document document = results.first();

        if (document == null) {
            return PlayerData.createEmptyData();
        }

        final PlayerData data = new PlayerData();

        data.coins = document.getInteger("coins", 0);
        data.kills = document.getInteger("kills", 0);
        data.finalKills = document.getInteger("finalKills", 0);
        data.deaths = document.getInteger("deaths", 0);
        data.finalDeaths = document.getInteger("finalDeaths", 0);
        data.wins = document.getInteger("wins", 0);
        data.skinSelected = document.getInteger("skin", data.skinSelected);
        data.destroyedEggs = document.getInteger("eggs", 0);
        data.level = document.getInteger("level", 0);
        data.kitSelected = document.getInteger("kit", 0);
        data.kits = createHashSet(document.getList(data, Integer.class), null);
        data.skins = createHashSet(document.getList(data, Integer.class), data.skinSelected);

        return data;
    }

    public void saveData(final UUID uuid, final PlayerData data) {
        final Document query = new Document();
        query.put("_id", uuid);

        final List<Integer> kits = toIntegerArray(data.kits);
        final List<Integer> skins = toIntegerArray(data.skins);

        final UpdateOptions options = new UpdateOptions().upsert(true);
        final Bson update = createUpdateQuery(data, kits, skins);

        profiles.updateOne(query, update, options);
    }

    private Bson createUpdateQuery(final PlayerData data, final List<Integer> kits, final List<Integer> skins) {
        return Updates.combine(
            Updates.set("coins", data.coins),
            Updates.set("kills", data.kills),
            Updates.set("finalKills", data.finalKills),
            Updates.set("deaths", data.deaths),
            Updates.set("finalDeaths", data.finalDeaths),
            Updates.set("wins", data.wins),
            Updates.set("skin", data.skinSelected),
            Updates.set("kit", data.kitSelected),
            Updates.set("eggs", data.destroyedEggs),
            Updates.set("level", data.level),
            Updates.set("kits", kits),
            Updates.set("skins", skins)
        );
    }

    private TIntHashSet createHashSet(final List<Integer> data, final Integer defaultValue) {
        if (data == null) {
            final TIntHashSet set = new TIntHashSet();
            if (defaultValue != null) {
                set.add(defaultValue);
            }
            return set;
        }
        final TIntHashSet values = new TIntHashSet(data.size());
        for (final Integer value : data) {
            if (value != null && value != -1) {
                values.add(value);
            }
        }
        return values;
    }

    private List<Integer> toIntegerArray(final TIntHashSet set) {
        final int size = set.size();
        final List<Integer> list = new ArrayList<>(size);
        final TIntIterator iterator = set.iterator();
        for (int i = 0; i < size; i++) {
            list.add(iterator.next());
        }
        return list;
    }

    static void update(MongoDBManager manager) {
        queryManager = manager;
    }
    
    public static MongoDBManager getManager() {
        return queryManager;
    }
}