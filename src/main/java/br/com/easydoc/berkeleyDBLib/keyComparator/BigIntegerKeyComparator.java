package br.com.easydoc.berkeleyDBLib.keyComparator;

import java.math.BigInteger;

import br.com.easydoc.berkeleyDBLib.util.Version;

public class BigIntegerKeyComparator extends KeyComparator<BigInteger>{

	private static final long serialVersionUID = Version.NUMBER;

	@Override
	public int compare(byte[] o1, byte[] o2) {
		return new BigInteger(o1).compareTo(new BigInteger(o2));
	}

}
