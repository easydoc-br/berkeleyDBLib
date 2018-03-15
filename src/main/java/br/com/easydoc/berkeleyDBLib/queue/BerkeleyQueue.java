package br.com.easydoc.berkeleyDBLib.queue;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

import br.com.easydoc.berkeleyDBLib.connection.DatabaseConnection;
import br.com.easydoc.berkeleyDBLib.util.JsonUtil;

public class BerkeleyQueue<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(BerkeleyQueue.class);
	
	private final String queueName;
	
	private final Database queue;
	
	private Class<T> clazz;
	
	public BerkeleyQueue(String dbPath, String queueName, Class<T> clazz) throws DatabaseException {
		this.queueName = queueName;
		this.queue = DatabaseConnection.getInstance(dbPath).createEntity(queueName);
		this.clazz = clazz;
	}

	public String getName() {
		return queueName;
	}

	public long size() throws DatabaseException {
		return queue.count();
	}
	
	public void close() throws DatabaseException {
		queue.close();
	}
	
	public void push(T item) throws DatabaseException, IOException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=push status=init");
		}
		DatabaseEntry key = new DatabaseEntry();
		DatabaseEntry data = new DatabaseEntry();
		Cursor cursor = queue.openCursor(null, null);
		cursor.getLast(key, data, LockMode.RMW);
		BigInteger prevKeyValue;
		if (key.getData() == null) {
			prevKeyValue = BigInteger.valueOf(-1);
		} else {
			prevKeyValue = new BigInteger(key.getData());
		}
		BigInteger newKeyValue = prevKeyValue.add(BigInteger.ONE);
		final DatabaseEntry newKey = new DatabaseEntry(newKeyValue.toByteArray());
		final DatabaseEntry newData = new DatabaseEntry();
		String json = JsonUtil.toJson(item);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("function=push msg=[Data saved: '{}']", json);
		}
		StringBinding.stringToEntry(json , newData);
		queue.put(null, newKey, newData);
		queue.sync();
		cursor.close();
		cursor = null;
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=push status=init");
		}
	}
	
	public T pull() throws DatabaseException, IOException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=findAll status=init");
		}
		final DatabaseEntry key = new DatabaseEntry();
		final DatabaseEntry data = new DatabaseEntry();
		Cursor cursor = queue.openCursor(null, null);
		OperationStatus status = cursor.getFirst(key, data, LockMode.RMW);
		if (status.equals(OperationStatus.NOTFOUND) || data.getData() == null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("function=pull msg=[No data found]");
			}
			return null;
		}
		final String json = StringBinding.entryToString(data);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("function=pull msg=[Data retrieved: '{}']", json);
		}
		T item = JsonUtil.fromJson(json, clazz);
		cursor.delete();
		queue.sync();
		cursor.close();
		cursor = null;
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=findAll status=init");
		}
		return item;
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
			cursor = queue.openCursor(null, null);
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
}
