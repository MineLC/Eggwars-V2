package lc.eggwars.database;

import java.util.List;
import java.util.UUID;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import gnu.trove.set.hash.TIntHashSet;

public final class DatabaseManager {

    private static DatabaseManager queryManager;

    private final MongoCollection<Document> profiles;

    public DatabaseManager(MongoCollection<Document> profiles) {
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

    public void saveData(final UUID uuid, final PlayerData newData) {
        final Document query = new Document();
        query.put("_id", uuid);

        final Document dataDocument = toDocument(newData);
        final Document document = profiles.findOneAndUpdate(query, dataDocument);

        if (document == null) {
            profiles.insertOne(dataDocument);
        }
    }

    private Document toDocument(final PlayerData data) {
        final Document document = new Document();
        document.put("coins", data.coins);
        document.put("kills", data.kills);
        document.put("finalKills", data.finalKills);
        document.put("deaths", data.deaths);
        document.put("finalDeaths", data.finalDeaths);
        document.put("wins", data.wins);
        document.put("skin", data.skinSelected);
        document.put("kit", data.kitSelected);
        document.put("eggs", data.destroyedEggs);
        document.put("level", data.level);
        document.put("skins", data.skins);
        document.put("kits", data.kits);
        return document;
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

    static void update(DatabaseManager manager) {
        queryManager = manager;
    }
    
    public static DatabaseManager getManager() {
        return queryManager;
    }
}