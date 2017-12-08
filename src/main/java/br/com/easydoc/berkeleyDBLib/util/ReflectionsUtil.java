package br.com.easydoc.berkeleyDBLib.util;

import java.io.IOException;
import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectionsUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionsUtil.class);
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Field getAnnotatedField(Class clazz, Class annotation) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=getAnnotatedField status=init");
		}
		if (!annotation.isAnnotation()) {
			throw new RuntimeException("parameter 'annotation' is not a Annotation Class");
		}
		for (Field field : clazz.getDeclaredFields()) {
			LOGGER.info("{} - {}", field.getName(), clazz.getName());
			if (field.isAnnotationPresent(annotation)) {
				field.setAccessible(true);
				if (LOGGER.isTraceEnabled()) {
					LOGGER.trace("function=getAnnotatedField status=done");
				}
				return field;
			}
		}
		LOGGER.error("function=getAnnotatedField msg=[Object doesn't have a attribute with @{} annotation]", annotation.getSimpleName());
		throw new RuntimeException("Object doesn't have a attribute with @" + 
				annotation.getSimpleName() + " annotation");
	}
	
	@SuppressWarnings("rawtypes")
	public static Class getAnnotatedFieldClass(Object object, Class annotation) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=getAnnotatedFieldClass status=init");
		}
		Field field = getAnnotatedField(object.getClass(), annotation);
		Class clazz = field.getType(); 
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=getAnnotatedFieldClass status=done");
		}
		return clazz;
	}
	
	@SuppressWarnings("rawtypes")
	public static <T> T getAnnotatedFieldValue(Object data, Class annotation, Class<T> clazz) 
			throws IOException, IllegalArgumentException, IllegalAccessException { 
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=getAnnotatedFieldValue status=init");
		}
		Field field = getAnnotatedField(data.getClass(), annotation);
		Object dataValue = field.get(data);
		T value = JsonUtil.fromJson(JsonUtil.toJson(dataValue), clazz);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=getAnnotatedFieldValue status=done");
		}
		return value;
	}
	
	public static void setFieldValue(Field field, Object data, Object value) 
			throws IllegalArgumentException, IllegalAccessException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=setFieldValue status=init");
		}
		if (value != null && field.getType().equals(value.getClass())) {
			field.setAccessible(true);
			field.set(data, value);
		}
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=setFieldValue status=done");
		}
	}
}
