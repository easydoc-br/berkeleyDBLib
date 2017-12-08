package br.com.easydoc.berkeleyDBLib.keyComparator;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import br.com.easydoc.berkeleyDBLib.util.Version;

public class UUIDKeyComparator extends KeyComparator<UUID>{

	private static final long serialVersionUID = Version.NUMBER;

	@Override
	public int compare(byte[] o1, byte[] o2) {
		String str1 = new String(o1, StandardCharsets.UTF_8);
		String str2 = new String(o2, StandardCharsets.UTF_8);
		long key1 = UUID.fromString(str1).timestamp();
		long key2 = UUID.fromString(str2).timestamp();
		return key1 < key2 ? -1 : key1 > key2 ? 1 : str1.compareTo(str2);
	}
}
