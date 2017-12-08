package br.com.easydoc.berkeleyDBLib.keyStrategy;

import br.com.easydoc.berkeleyDBLib.keyComparator.IntegerKeyComparator;
import br.com.easydoc.berkeleyDBLib.keyComparator.KeyComparator;

public class IntegerStrategy extends KeyStrategy<Integer>{

	@Override
	public Integer generate(Integer lastValue) {
		if (lastValue == null) {
			return 1;
		}
		return lastValue + 1;
	}

	@Override
	public KeyComparator<Integer> getComparator() {
		return new IntegerKeyComparator();
	}

}
