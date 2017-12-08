package br.com.easydoc.berkeleyDBLib.keyStrategy;

import java.util.Date;

import br.com.easydoc.berkeleyDBLib.keyComparator.KeyComparator;
import br.com.easydoc.berkeleyDBLib.keyComparator.TimestampKeyComparator;

public class TimestampStrategy extends KeyStrategy<Long>{

	@Override
	public Long generate(Long lastValue) {
		return new Date().getTime();
	}

	@Override
	public KeyComparator<Long> getComparator() {
		return new TimestampKeyComparator();
	}
}
