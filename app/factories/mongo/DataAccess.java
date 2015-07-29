package factories.mongo;

import java.lang.reflect.Constructor;

import com.mongodb.*;
import org.jongo.Jongo;
import org.jongo.Mapper;
import org.jongo.MongoCollection;
import play.Configuration;
import play.Logger;
import play.Play;
import com.mongodb.gridfs.GridFS;

public class DataAccess {

    private static volatile DataAccess INSTANCE = null;

    MongoClient mongo = null;
    Jongo jongo = null;
    GridFS gridfs = null;

    DataAccess(Configuration config, ClassLoader classLoader, boolean isTestMode) throws Exception {
        String factoryName = config.getString("daMongo.mongoClientFactory");
        String uri = (isTestMode ? config.getString("daMongo.test-uri") : config.getString("daMongo.uri"));
        String writeConcern = config.getString("daMongo.defaultWriteConcern");

        MongoClientFactory factory = getMongoClientFactory(factoryName, uri, writeConcern, isTestMode);
        mongo = factory.createClient();

        if (mongo == null) {
            throw new IllegalStateException("No MongoClient was created by instance of " + factory.getClass().getName());
        }

        DB db = mongo.getDB(factory.getDBName());

        jongo = new Jongo(db, createMapper(config, classLoader));

        if (config.getBoolean("daMongo.gridfs.enabled", false)) {
            gridfs = new GridFS(jongo.getDatabase());
        }
    }

    DataAccess(Configuration config, String uri, String writeConcern, ClassLoader classLoader, boolean isTestMode) throws Exception {
        String factoryName = config.getString("daMongo.mongoClientFactory");
        MongoClientFactory factory = getMongoClientFactory(factoryName, uri, writeConcern, isTestMode);
        mongo = factory.createClient();

        if (mongo == null) {
            throw new IllegalStateException("No MongoClient was created by instance of " + factory.getClass().getName());
        }

        DB db = mongo.getDB(factory.getDBName());

        jongo = new Jongo(db, createMapper(config, classLoader));

        if (config.getBoolean("daMongo.gridfs.enabled", false)) {
            gridfs = new GridFS(jongo.getDatabase());
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected MongoClientFactory getMongoClientFactory(String className, String uri, String writeConcern, boolean isTestMode) throws Exception {

        if (className != null) {
            try {
                Class factoryClass = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
                if (!MongoClientFactory.class.isAssignableFrom(factoryClass)) {
                    throw new IllegalStateException("mongoClientFactory '" + className +
                            "' is not of type " + MongoClientFactory.class.getName());
                }

                Constructor constructor = null;
                try {
                    constructor = factoryClass.getConstructor(String.class, String.class);
                } catch (Exception e) {
                    // can't use that one
                }
                if (constructor == null) {
                    return (MongoClientFactory) factoryClass.newInstance();
                }
                return (MongoClientFactory) constructor.newInstance(uri, writeConcern);
            } catch (ClassNotFoundException e) {
                throw e;
            }
        }
        return new MongoClientFactory(uri, writeConcern);
    }

    private Mapper createMapper(Configuration config, ClassLoader classLoader) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        final String factoryClassName = config.getString("daMongo.mapperfactory",
                JongoMapperFactory.DefaultFactory.class.getName());
        JongoMapperFactory factory = (JongoMapperFactory) Class.forName(
                factoryClassName,
                true,
                classLoader).newInstance();
        return factory.create();
    }

    public static DataAccess getInstance() {
        return getInstance(Play.application().configuration().getString("daMongo.uri"), Play.application().configuration().getString("daMongo.defaultWriteConcern"));
    }

    public static DataAccess getInstance(String uri, String writeConcern) {
        if (INSTANCE == null) {
            synchronized (DataAccess.class) {
                if (INSTANCE == null) {
                    try {
                        INSTANCE = new DataAccess(Play.application().configuration(), uri, writeConcern, Play.application().classloader(), Play.isTest());
                    } catch (Exception e) {
                        Logger.error(e.getClass().getSimpleName(), e);
                    }
                }
            }
        }
        return INSTANCE;
    }

    public static DataAccess forceNewInstance() {
        return forceNewInstance(Play.application().configuration().getString("daMongo.uri"));
    }

    public static DataAccess forceNewInstance(String uri) {
        INSTANCE = null;
        return getInstance(uri, Play.application().configuration().getString("daMongo.defaultWriteConcern"));
    }

    public static Mongo mongo() {
        return getInstance().mongo;
    }

    public static Jongo jongo() {
        return getInstance().jongo;
    }

    public static GridFS gridfs() {
        return getInstance().gridfs;
    }

    public static MongoCollection getCollection(String name) {
        return getInstance().jongo.getCollection(name);
    }

    public static DB getDatabase() {
        return getInstance().jongo.getDatabase();
    }
}


