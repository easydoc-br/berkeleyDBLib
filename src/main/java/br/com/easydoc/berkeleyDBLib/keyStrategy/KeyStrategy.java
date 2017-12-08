package br.com.easydoc.berkeleyDBLib.keyStrategy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.sleepycat.je.DatabaseEntry;

import br.com.easydoc.berkeleyDBLib.keyComparator.KeyComparator;

public abstract class KeyStrategy<K> {

	public DatabaseEntry generateKey(K lastValue) throws IOException {
		K newValue = generate(lastValue);
		lastValue = newValue;
		DatabaseEntry key = new DatabaseEntry(toByteArray(newValue));
		return key;
	}
	
	protected byte[] toByteArray(K newValue) {
		return newValue.toString().getBytes(StandardCharsets.UTF_8);
	}

	public abstract K generate(K lastValue);
	
	public abstract KeyComparator<K> getComparator();
}
