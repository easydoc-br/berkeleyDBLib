package br.com.easydoc.berkeleyDBLib.keyComparator;

import java.nio.ByteBuffer;

import br.com.easydoc.berkeleyDBLib.util.Version;

public class IntegerKeyComparator extends KeyComparator<Integer>{

	private static final long serialVersionUID = Version.NUMBER;

	@Override
	public int compare(byte[] o1, byte[] o2) {
		Integer key1 = ByteBuffer.wrap(o1).getInt();
		Integer key2 = ByteBuffer.wrap(o2).getInt();
		return key1 < key2 ? -1 : key1 > key2 ? 1 : 0;
	}
}
