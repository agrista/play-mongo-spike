package factories.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.WriteConcern;
import play.Configuration;

public class MongoClientFactory {

    protected String uri;
    protected String writeConcern;

    public MongoClientFactory(Configuration config) {
        this.uri = config.getString("daMongo.uri");
        this.writeConcern = config.getString("daMongo.defaultWriteConcern");
    }

    protected MongoClientFactory(Configuration config, boolean isTest) {
        this.uri = (isTest ? config.getString("daMongo.test-uri", "mongodb://127.0.0.1:27017/test") : config.getString("daMongo.uri", "mongodb://127.0.0.1:27017/play"));
        this.writeConcern = config.getString("daMongo.defaultWriteConcern");
    }

    public MongoClientFactory(String uri, String writeConcern) {
        this.uri = uri;
        this.writeConcern = writeConcern;
    }

    /**
     * Creates and returns a new instance of a MongoClient.
     *
     * @return a new MongoClient
     * @throws Exception
     */
    public MongoClient createClient() throws Exception {
        MongoClientURI connectionString = getClientURI();
        //Authentication passed through in the connection string
        MongoClient mongoClient = new MongoClient(connectionString);
        //MongoDatabase db = mongoClient.getDatabase("mydb");

        // Set write concern if configured
        if(writeConcern != null) {
            mongoClient.setWriteConcern(WriteConcern.valueOf(writeConcern));
        }

        return mongoClient;
    }

    /**
     * Returns the database name associated with the current configuration.
     *
     * @return The database name
     */
    public String getDBName() {
        return getClientURI().getDatabase();
    }

    protected MongoClientURI getClientURI() {
        return new MongoClientURI(uri);
    }
}
