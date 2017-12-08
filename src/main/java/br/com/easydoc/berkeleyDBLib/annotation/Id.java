package br.com.easydoc.berkeleyDBLib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.easydoc.berkeleyDBLib.keyStrategy.KeyStrategy;
import br.com.easydoc.berkeleyDBLib.keyStrategy.LongStrategy;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Id {

	@SuppressWarnings("rawtypes")
	Class<? extends KeyStrategy> strategy() default LongStrategy.class;
}
