package lc.eggwars.database;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import lc.eggwars.EggwarsPlugin;

import org.bson.Document;
import org.bukkit.configuration.file.FileConfiguration;
import org.tinylog.Logger;

import java.util.logging.Level;

public final class MongoDBHandler {

    private MongoClient client;
    private MongoDatabase database;

    public void init(EggwarsPlugin plugin) {
        this.disableLogging();
        final FileConfiguration config = plugin.getConfig();

        if (config.getBoolean("storage.mongo.uri-mode")) {
            this.client = MongoClients.create(config.getString("storage.mongo.uri.connection_string"));
            this.database = client.getDatabase(config.getString("storage.mongo.uri.database"));

            Logger.info("&7Initialized MongoDB successfully!");
            updateDatabaseManager("profiles");
            return;
        }
        boolean auth = config.getBoolean("storage.mongo.normal.authentication.enabled");
        String host = config.getString("storage.mongo.normal.host");
        int port = config.getInt("storage.mongo.normal.port");

        String uri = "mongodb://" + host + ":" + port;

        if (auth) {
            String username = config.getString("storage.mongo.normal.authentication.username");
            String password = config.getString("storage.mongo.normal.authentication.password");
            uri = "mongodb://" + username + ":" + password + "@" + host + ":" + port;
        }

        this.client = MongoClients.create(uri);
        this.database = client.getDatabase(config.getString("storage.mongo.uri.database"));

        Logger.info("&7Initialized MongoDB successfully!");
        updateDatabaseManager("profiles");
    }

    private MongoCollection<Document> updateDatabaseManager(final String collectionName) {
        final MongoCollection<Document> documents = this.database.getCollection(collectionName);
        DatabaseManager.update(new DatabaseManager(documents));
        return documents;
    }

    public void shutdown() {
        if (this.client != null) {
            this.client.close();
        }
    }

    public void disableLogging() {
        java.util.logging.Logger.getLogger("com.mongodb").setLevel(Level.SEVERE);
    }
}