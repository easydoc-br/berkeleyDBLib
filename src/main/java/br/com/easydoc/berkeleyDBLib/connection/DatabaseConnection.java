package br.com.easydoc.berkeleyDBLib.connection;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

import br.com.easydoc.berkeleyDBLib.annotation.Id;
import br.com.easydoc.berkeleyDBLib.keyComparator.BigIntegerKeyComparator;
import br.com.easydoc.berkeleyDBLib.keyStrategy.KeyStrategy;
import br.com.easydoc.berkeleyDBLib.util.ReflectionsUtil;

public class DatabaseConnection {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConnection.class);
	private static DatabaseConnection instance;
	private static String dbPath;
	private Map<String, Database> databases;
	
	private Environment dbEnv;
	
	private DatabaseConnection(String dbPath) throws DatabaseException {
		File path = new File(dbPath);
		if (!path.exists()) {
			path.mkdirs();
		}
		final EnvironmentConfig dbEnvConfig = new EnvironmentConfig();
		dbEnvConfig.setTransactional(false);
		dbEnvConfig.setAllowCreate(true);
		dbEnvConfig.setLockTimeout(10000000);
		this.dbEnv = new Environment(path, dbEnvConfig);
		DatabaseConnection.dbPath = dbPath;
		this.databases = new HashMap<String, Database>();
	}
	
	public static DatabaseConnection getInstance(String dbPath) throws DatabaseException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=getInstance status=init");
		}
		if (DatabaseConnection.dbPath == null || !DatabaseConnection.dbPath.equals(dbPath)) {
			LOGGER.info("{} - {}", DatabaseConnection.dbPath, dbPath);
			if (instance == null) {
				instance = new DatabaseConnection(dbPath);
			} else {
				instance.closeDatabase();
				instance = new DatabaseConnection(dbPath);
			}
		}
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=getInstance status=done");
		}
		return instance;
	}
	
	public Database createEntity(String name) throws DatabaseException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=createEntity status=init");
		}
		if (databases.containsKey(name)) {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("function=createEntity status=done");
			}
			return databases.get(name);
		}
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setTransactional(false);
		dbConfig.setAllowCreate(true);
		dbConfig.setDeferredWrite(true);
		dbConfig.setBtreeComparator(new BigIntegerKeyComparator());
		Database database = dbEnv.openDatabase(null, name, dbConfig);
		databases.put(name, database);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=createEntity status=done");
		}
		return database;
	}

	@SuppressWarnings("rawtypes")
	public Database createEntity(Class clazz) 
			throws DatabaseException, InstantiationException, IllegalAccessException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=createEntity status=init");
		}
		if (databases.containsKey(clazz.getSimpleName())) {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("function=createEntity status=done");
			}
			return databases.get(clazz.getSimpleName());
		}
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setTransactional(false);
		dbConfig.setAllowCreate(true);
		dbConfig.setDeferredWrite(true);
		dbConfig.setBtreeComparator(getComparatorForClass(clazz));
		Database database = dbEnv.openDatabase(null, clazz.getSimpleName(), dbConfig);
		databases.put(clazz.getSimpleName(), database);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=createEntity status=done");
		}
		return database;
	}
	
	public void closeDatabase() throws DatabaseException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=closeDatabase status=init");
		}
		if (dbEnv != null) {
			for (String name : databases.keySet()) {
				databases.get(name).close();
			}
			dbEnv.close();
			dbEnv = null;
		}
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=closeDatabase status=done");
		}
	}

	@SuppressWarnings("rawtypes")
	public List getDatabaseNames() throws DatabaseException {
		return dbEnv.getDatabaseNames();
	}
	@SuppressWarnings("rawtypes")
	private Comparator getComparatorForClass(Class clazz) 
			throws InstantiationException, IllegalAccessException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=getComparatorForClass status=init");
		}
		Field field = ReflectionsUtil.getAnnotatedField(clazz, Id.class);
		Id id = (Id) field.getAnnotation(Id.class);
		Comparator comparator = ((KeyStrategy) id.strategy().newInstance()).getComparator(); 
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=getComparatorForClass status=done");
		}
		return comparator;
	}
}
