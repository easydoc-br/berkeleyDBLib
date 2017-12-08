package br.com.easydoc.berkeleyDBLib.keyComparator;

import java.nio.ByteBuffer;

import br.com.easydoc.berkeleyDBLib.util.Version;

public class LongKeyComparator extends KeyComparator<Long>{

	private static final long serialVersionUID = Version.NUMBER;

	@Override
	public int compare(byte[] o1, byte[] o2) {
		Long key1 = ByteBuffer.wrap(o1).getLong();
		Long key2 = ByteBuffer.wrap(o2).getLong();
		return key1 < key2 ? -1 : key1 > key2 ? 1 : 0;
	}
}
