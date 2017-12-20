package br.com.easydoc.berkeleyDBLib.dao;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

import br.com.easydoc.berkeleyDBLib.annotation.Id;
import br.com.easydoc.berkeleyDBLib.connection.DatabaseConnection;
import br.com.easydoc.berkeleyDBLib.keyStrategy.KeyStrategy;
import br.com.easydoc.berkeleyDBLib.util.JsonUtil;
import br.com.easydoc.berkeleyDBLib.util.ReflectionsUtil;

public class BerkeleyDAO<T, K> {

	private static final Logger LOGGER = LoggerFactory.getLogger(BerkeleyDAO.class);	
	private final String tableName;
	
	private final Database database;
	
	private Class<T> clazz;
	
	public BerkeleyDAO(String dbPath, Class<T> clazz) throws DatabaseException {
		this.tableName = clazz.getSimpleName();
		this.database = DatabaseConnection.getInstance(dbPath).createEntity(this.tableName);
		this.clazz = clazz;
	}

	public String getName() {
		return tableName;
	}

	public long size() throws DatabaseException {
		return database.count();
	}
	
	public void close() throws DatabaseException {
		database.close();
	}
	
	public T save(T entity) 
			throws DatabaseException, IllegalArgumentException, IllegalAccessException, 
			IOException, InstantiationException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=save status=init");
		}
		if (entity == null) {
			throw new RuntimeException("Entity instance of " + clazz.getSimpleName() + " is null");
		}
		DatabaseEntry key = new DatabaseEntry();
		DatabaseEntry data = new DatabaseEntry();
		Cursor cursor = null;
		try {
			cursor = database.openCursor(null, null);
			cursor.getLast(key, data, LockMode.RMW);
			T last = null;
			if (data != null && data.getData() != null && data.getData().length > 0) {
				final String json = new String(data.getData(), StandardCharsets.UTF_8);
				last = JsonUtil.fromJson(json, clazz);
			}
			K id = getId(entity, last);
			updateEntityKey(id, entity);
			String json = JsonUtil.toJson(entity);
			final DatabaseEntry newKey;
			newKey = getNewKey(entity);
			final DatabaseEntry newData = new DatabaseEntry(json.getBytes());
			database.put(null, newKey, newData);
			database.sync();
			cursor.close();
			cursor = null;
		} catch (Exception e) {
			LOGGER.error("Error save entity", e);
			if (cursor != null) {
				cursor.close();
			}
			cursor = null;
			throw e;
		}
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=save status=done");
		}
		return entity;
	}
	
	public List<T> findAll() throws DatabaseException, IOException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=findAll status=init");
		}
		List<T> list = new ArrayList<T>();
		DatabaseEntry key = new DatabaseEntry();
		DatabaseEntry data = new DatabaseEntry();
		Cursor cursor = null;
		try {
			cursor = database.openCursor(null, null);
			while(cursor.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				final String json = new String(data.getData(), StandardCharsets.UTF_8);
				T item = JsonUtil.fromJson(json, clazz);
				list.add(item);
			}
			cursor.close();
			cursor = null;
		} catch (Exception e) {
			cursor.close();
			cursor = null;
			throw e;
		}
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=findAll status=done");
		}
		return list;
	}
	
	public T findById(K id) throws DatabaseException, IOException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=findById status=init");
		}
		final DatabaseEntry key = new DatabaseEntry(id.toString().getBytes());
		final DatabaseEntry data = new DatabaseEntry();
		Cursor cursor = null;
		try {
			cursor = database.openCursor(null, null);
			cursor.getSearchKey(key, data, LockMode.RMW);
			if (data.getData() == null) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("function=findById msg=[No data found for id {}]", id.toString());
				}
				cursor.close();
				cursor = null;
				return null;
			}
			final String json = new String(data.getData(), StandardCharsets.UTF_8);
			T item = JsonUtil.fromJson(json, clazz);
			cursor.close();
			cursor = null;
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("function=findById status=done");
			}
			return item;
		} catch (Exception e) {
			cursor.close();
			cursor = null;
			throw e;
		}
	}
	
	public T delete(K id) 
			throws IllegalArgumentException, IllegalAccessException, InstantiationException, IOException, DatabaseException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=delete status=init");
		}
		final DatabaseEntry key = new DatabaseEntry(id.toString().getBytes());
		final DatabaseEntry data = new DatabaseEntry();
		Cursor cursor = null;
		try {
			cursor = database.openCursor(null, null);
			cursor.getSearchKey(key, data, LockMode.RMW);
			if (data.getData() == null) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("function=delete msg=[No data found for id {}]", id.toString());
				}
				cursor.close();
				cursor = null;
				return null;
			}
			final String json = new String(data.getData(), StandardCharsets.UTF_8);
			T item = JsonUtil.fromJson(json, clazz);
			cursor.delete();
			database.sync();
			cursor.close();
			cursor = null;
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("function=delete status=done");
			}
			return item;
		} catch (Exception e) {
			cursor.close();
			cursor = null;
			throw e;
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private K getId(T entity, T last) throws IllegalArgumentException, IllegalAccessException, IOException, InstantiationException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=getId status=init");
		}
		Class<K> idClazz = ReflectionsUtil.getAnnotatedFieldClass(entity, Id.class);
		K id = ReflectionsUtil.getAnnotatedFieldValue(entity, Id.class, idClazz);
		if (id == null) {
			Field field = ReflectionsUtil.getAnnotatedField(entity.getClass(), Id.class);
			Id idAnnotation = (Id) field.getAnnotation(Id.class);
			Class<? extends KeyStrategy> clazz = idAnnotation.strategy();
			if (clazz == null) {
				LOGGER.error("function=getNewKey msg=[Cannot generate a new Key without strategy for class {}]", 
						entity.getClass().getSimpleName());
				throw new RuntimeException("Cannot generate a new Key without strategy");
			}
			if (last != null) {
				id = (K) clazz.newInstance().generate(field.get(last));
			} else {
				id = (K) clazz.newInstance().generate(null);
			}
		}
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=getId status=done");
		}
		return id;
	}
	
	@SuppressWarnings({ "rawtypes"})
	private DatabaseEntry getNewKey(Object entity) 
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, IOException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=getNewKey status=init");
		}
		Field field = ReflectionsUtil.getAnnotatedField(entity.getClass(), Id.class);
		Id idAnnotation = (Id) field.getAnnotation(Id.class);
		Class<? extends KeyStrategy> clazz = idAnnotation.strategy();
		if (clazz == null) {
			LOGGER.error("function=getNewKey msg=[Cannot generate a new Key without strategy for class {}]", 
					entity.getClass().getSimpleName());
			throw new RuntimeException("Cannot generate a new Key without strategy");
		}
		byte[] bytes = field.get(entity).toString().getBytes(StandardCharsets.UTF_8);
		DatabaseEntry key = new DatabaseEntry(bytes);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=getNewKey status=done");
		}
		return key;
	}
	
	private void updateEntityKey(K id, T entity) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=updateEntityKey status=init");
		}
		try {
			Field field = ReflectionsUtil.getAnnotatedField(entity.getClass(), Id.class);
			ReflectionsUtil.setFieldValue(field, entity, id);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			LOGGER.warn("function=updateEntityKey msg=[Cannot refresh entity]", e);
		}
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=updateEntityKey status=done");
		}
	}
}
