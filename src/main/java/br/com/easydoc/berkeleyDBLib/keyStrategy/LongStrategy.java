package br.com.easydoc.berkeleyDBLib.keyStrategy;

import br.com.easydoc.berkeleyDBLib.keyComparator.KeyComparator;
import br.com.easydoc.berkeleyDBLib.keyComparator.LongKeyComparator;

public class LongStrategy extends KeyStrategy<Long>{

	@Override
	public Long generate(Long lastValue) {
		if (lastValue == null) {
			return 1l;
		}
		return lastValue + 1;
	}

	@Override
	public KeyComparator<Long> getComparator() {
		return new LongKeyComparator();
	}

}
