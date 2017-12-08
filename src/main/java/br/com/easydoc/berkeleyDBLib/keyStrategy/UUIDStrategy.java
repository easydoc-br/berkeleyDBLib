package br.com.easydoc.berkeleyDBLib.keyStrategy;

import java.util.UUID;

import com.fasterxml.uuid.Generators;

import br.com.easydoc.berkeleyDBLib.keyComparator.KeyComparator;
import br.com.easydoc.berkeleyDBLib.keyComparator.UUIDKeyComparator;

public class UUIDStrategy extends KeyStrategy<UUID>{

	@Override
	public UUID generate(UUID lastValue) {
		return Generators.timeBasedGenerator().generate();
	}

	@Override
	public KeyComparator<UUID> getComparator() {
		return new UUIDKeyComparator();
	}
}
