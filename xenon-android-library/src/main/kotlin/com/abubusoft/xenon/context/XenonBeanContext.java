package com.abubusoft.xenon.context;

import java.lang.reflect.Field;

import com.abubusoft.kripton.android.Logger;

import android.content.Context;

/**
 * <p>
 * Utilità per la creazione di componenti argon.
 * 
 * @author Francesco Benincasa
 * 
 */
public abstract class XenonBeanContext {

	/**
	 * <p>
	 * Crea un'instanza di un determinato oggetto. E' richiesto che tale oggetto abbia il costruttore di default. Tutti i field con annotazione XenonBeanInject vengono risolti con gli oggetti già instanziati.
	 * </p>
	 * 
	 * @param clazz
	 * 		classe da istanziare
	 * @return
	 * 		crea una nuova istanza
	 */
	public static <E> E createInstance(Class<E> clazz) {
		E object = null;
		try {
			// creiamo istanza oggetto
			Logger.debug("BeanInject - createInstance of allocation %s", clazz.getSimpleName());
			object = clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
			throw (new RuntimeException(e));
		}

		fillBean(object);

		return object;
	}

	/**
	 * <p>
	 * Effettua nuovamente il binding delle proprietà se eventualmente ci sono.
	 * </p>
	 * 
	 * @param bean
	 */
	public static void fillBean(Object bean) {
		// verifichiamo se è un argonComponent
		// if (clazz.getAnnotation(XenonBean.class) != null) {
		Field[] fields = bean.getClass().getFields();
		XenonBeanInject inject;
		int n = fields.length;
		for (int i = 0; i < n; i++) {
			inject = fields[i].getAnnotation(XenonBeanInject.class);

			if (inject != null) {
				try {
					fields[i].set(bean, inject.value().value);
					Logger.debug("BeanInject - Injected field %s with bean %s", fields[i].getName(), inject.value().toString());
				} catch (IllegalAccessException | IllegalArgumentException e) {
					e.printStackTrace();
					Logger.error(e.getMessage());
				}

			}
		}
		// }
	}

	/**
	 * <p>
	 * Imposta un bean predefinito.
	 * </p>
	 * @param type
	 * 		tipo da impostare
	 * @param value
	 * 		istanza
	 * 		
	 */
	public static void setBean(XenonBeanType type, Object value) {
		if (value != null)
			Logger.debug("BeanInject - setBean %s of allocation %s", type.toString(), value.getClass().getName());
		else {
			Logger.debug("BeanInject - setBean %s as null", type.toString());
		}
		type.value = value;
	}

	/**
	 * <p>
	 * Recupera un bean predefinito.
	 * </p>
	 * 
	 * @param type
	 * @return bean predefinito
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(XenonBeanType type) {
		return (T) type.value;
	}

	/**
	 * <p>
	 * Permette di recuperare da qualunque punto il context
	 * </p>
	 * 
	 * @return
	 * 		contesto android
	 */
	public static Context getContext() {
		return getBean(XenonBeanType.CONTEXT);
	}
}
