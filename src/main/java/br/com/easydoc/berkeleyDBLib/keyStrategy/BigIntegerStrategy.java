package br.com.easydoc.berkeleyDBLib.keyStrategy;

import java.math.BigInteger;

import br.com.easydoc.berkeleyDBLib.keyComparator.BigIntegerKeyComparator;
import br.com.easydoc.berkeleyDBLib.keyComparator.KeyComparator;

public class BigIntegerStrategy extends KeyStrategy<BigInteger> {

	@Override
	public BigInteger generate(BigInteger lastValue) {
		if (lastValue == null) {
			return BigInteger.ONE;
		}
		return lastValue.add(BigInteger.ONE);
	}

	@Override
	public KeyComparator<BigInteger> getComparator() {
		return new BigIntegerKeyComparator();
	}

}
