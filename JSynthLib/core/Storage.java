/**
 * Storage.java - Implemenation for storing and restoring of objects that
 * support simple persistance (currently implemented as saving to properties
 * files)
 * @version $Id$
 * @author Zellyn Hunter (zellyn@zellyn.com)
 * @see Storable
 */
package core;

import java.util.Properties;
import java.util.Set;
import java.util.Iterator;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Array;

public class Storage {
	// Used for calling getters
	private static final Object[] _noArgs = {};

	/**
	 * Add the given object to the properties file, as a root-level object
	 * @param storable the object to be stored
	 * @param props the properties object to store it in
	 */
	public static void store(Storable storable, Properties props) {
		store(storable, props, "");
	}

	/**
	 * Add the given object to the properties file, prefixing the names of
	 * its variables with the given prefix
	 * @param storable the object to be stored
	 * @param props the properties object to store it in
	 * @param prefix the prefix to prepend to all variable names
	 */
	public static void store(Storable storable, Properties props, String prefix) {
		Set propNames = storable.storedProperties();
		for (Iterator i = propNames.iterator(); i.hasNext();) {
			String propName = (String)i.next();
			try {
				PropertyDescriptor prop =
					new PropertyDescriptor(propName, storable.getClass());
				Method readMethod = prop.getReadMethod();
				Object value = readMethod.invoke(storable, _noArgs);
				storeValue(value, props, propName, prefix);
			}
			catch (Exception e) {
				ErrorMsg.reportError("Storage Error",
						     "Error storing property '" + propName +
						     "' for class '" +storable.getClass()+ "'",
						     e);
			}
		}
	}

	/**
	 * Restore the given object from the properties file, as a root-level
	 * object
	 * @param storable the object to be restored
	 * @param props the properties object from which to restore it
	 */
	public static void restore(Storable storable, Properties props) {
		restore(storable, props, "");
	}

	/**
	 * Restore the given object from the properties file, prefixing the names
	 * of its variables with the given prefix
	 * @param storable the object to be restored
	 * @param props the properties object from which to restore it
	 * @param prefix the prefix to prepend to all variable names
	 */
	public static void restore(Storable storable, Properties props, String prefix) {
		//TODO:zjh - restore it here
		Set propNames = storable.storedProperties();
		for (Iterator i = propNames.iterator(); i.hasNext();) {
			String propName = (String)i.next();
			try {
				PropertyDescriptor prop =
					new PropertyDescriptor(propName, storable.getClass());
				Method writeMethod = prop.getWriteMethod();
				Class propType = prop.getPropertyType();
				Object value = restoreValue(propType, props, propName, prefix);
				// Don't try to set a value to null
				if (value!=null) {
					Object[] args = {value};
					writeMethod.invoke(storable, args);
				}
			}
			catch (Exception e) {
				ErrorMsg.reportError("Storage Error",
						     "Cannot get property '" + propName +
						     "' for class '" +storable.getClass()+ "'",
						     e);
			}
		}

		// tell the object it's just been restored
		storable.afterRestore();
	}

	/**
	 * Store one value into the properties file.  Recurse if necessary
	 * @param value the object to store
	 * @param props the properties file to store it into
	 * @param propName the name of the property
	 * @param prefix the string to prepend to the property name
	 */
	private static void storeValue(Object value, Properties props,
				       String propName, String prefix) {
		Class propType = value.getClass();

		// Basic types
		if ((propType == Boolean.class)
			|| (propType == Byte.class)
			|| (propType == Character.class)
			|| (propType == Short.class)
			|| (propType == Integer.class)
			|| (propType == Long.class)
			|| (propType == Float.class)
			|| (propType == Double.class)
			|| (propType == String.class))
		{
			props.setProperty(prefix + propName, value.toString());
		} // end of basic types

		// Arrays
		else if (propType.isArray()) {
			int length = Array.getLength(value);
			props.setProperty(prefix+propName+".length",
					  String.valueOf(length));
			for (int i=0; i<length; i++) {
				//storeValue(value, props, propname, prefix)
				storeValue(Array.get(value,i), props, "["+i+"]", prefix+propName);
			}
		}

		// Must be a regular object - it better be Storable
		else {
			Storable storable = (Storable)value;
			props.setProperty(prefix+propName+".class", propType.getName());
			store(storable, props, prefix+propName+".");
		}
	}

	/**
	 * Restore one value out of the properties file.  Recurse if necessary
	 * @param propType the Class of the value
	 * @param props the properties file to store it into
	 * @param propName the name of the property
	 * @param prefix the string to prepend to the property name
	 * @return the value
	 */
	private static Object restoreValue(Class propType, Properties props,
					   String propName, String prefix) {
		String storedValue = props.getProperty(prefix+propName);

		// Basic types
		boolean isNull = (storedValue==null);
		if (propType==Boolean.TYPE || propType == Boolean.class)
			return (isNull) ? null : Boolean.valueOf(storedValue);
		if (propType==Byte.TYPE || propType == Byte.class)
			return (isNull) ? null : Byte.valueOf(storedValue);
		if (propType==Character.TYPE || propType == Character.class)
			return (isNull) ? null : new Character(storedValue.charAt(0));
		if (propType==Short.TYPE || propType == Short.class)
			return (isNull) ? null : Short.valueOf(storedValue);
		if (propType==Integer.TYPE || propType == Integer.class)
			return (isNull) ? null : Integer.valueOf(storedValue);
		if (propType==Long.TYPE || propType == Long.class)
			return (isNull) ? null : Long.valueOf(storedValue);
		if (propType==Float.TYPE || propType == Float.class)
			return (isNull) ? null : Float.valueOf(storedValue);
		if (propType==Double.TYPE || propType == Double.class)
			return (isNull) ? null : Double.valueOf(storedValue);
		if (propType==String.class)
			return (isNull) ? null : storedValue;
		// end of basic types


		// Arrays
		else if (propType.isArray()) {
			String lengthString = props.getProperty(prefix+propName+".length");
			if (lengthString==null) {
				return null;
			}
			int length = Integer.parseInt(lengthString);

			Object aryValue = Array.newInstance(propType.getComponentType(), length);

			for (int i=0; i<length; i++) {
				//restoreValue(propType, props, propName, prefix)
				Object value = restoreValue(propType.getComponentType(), props,
							    "["+i+"]", prefix+propName);
				Array.set(aryValue, i, value);
			}

			return aryValue;
		}

		// Must be a regular object - restore it
		else {
			String className = props.getProperty(prefix+propName+".class");
			if (className!=null) {
				try {
					Class storableClass = Class.forName(className);
					Storable storable = (Storable)storableClass.newInstance();
					restore(storable, props, prefix+propName+".");
					return storable;
				}
				catch (Exception e) {
					ErrorMsg.reportError("Config Restore Error",
							     "Error restoring class '" + className
							     +"'", e);
				}
			};
		}

		return null;
	} //restoreValue
};
