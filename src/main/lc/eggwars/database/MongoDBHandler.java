package lc.eggwars.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import lc.eggwars.EggwarsPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bukkit.configuration.file.FileConfiguration;

public final class MongoDBHandler {

    private MongoClient client;
    private MongoDatabase database;

    public void init(EggwarsPlugin plugin) throws Exception {
        disableLogging();
        final FileConfiguration config = plugin.getConfig();

        this.client = connect(config.getString("mongodb.connection-string"));
        updateDatabaseManager("eggwars", config.getString("mongodb.database"));
    }

    private MongoClient connect(final String uri) {      
        final ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        final MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .serverApi(serverApi)
                .build();
        return MongoClients.create(settings);
    }

    private void disableLogging() {
        Logger.getLogger("org.mongodb.driver").setLevel(Level.OFF);
        Logger.getLogger("org.mongodb.driver.connection").setLevel(Level.OFF);
        Logger.getLogger("org.mongodb.driver.management").setLevel(Level.OFF);
        Logger.getLogger("org.mongodb.driver.cluster").setLevel(Level.OFF);
        Logger.getLogger("org.mongodb.driver.protocol.insert").setLevel(Level.OFF);
        Logger.getLogger("org.mongodb.driver.protocol.query").setLevel(Level.OFF);
        Logger.getLogger("org.mongodb.driver.protocol.update").setLevel(Level.OFF);
    }

    private MongoCollection<Document> updateDatabaseManager(final String collectionName, final String databaseName) {
        this.database = client.getDatabase(databaseName);

        final MongoCollection<Document> documents = this.database.getCollection(collectionName);
        final FindIterable<Document> searchDocuments = documents.find();

        if (searchDocuments == null || searchDocuments.first() == null) {
            database.createCollection(collectionName);
            documents.insertOne(new Document());
        }
        DatabaseManager.update(new DatabaseManager(documents));
        return documents;
    }

    public void shutdown() {
        database = null;
        if (this.client != null) {
            this.client.close();
        }
    }
}