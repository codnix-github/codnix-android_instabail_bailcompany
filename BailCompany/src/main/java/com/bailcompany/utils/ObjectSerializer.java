package com.bailcompany.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.bailcompany.model.User;

/**
 * Object Serializer is a utility class to provide an option to save an object
 * in preferences or databases in String form. Like an object type serializable
 * arraylist or serializable object or bean. The Serialized string value can be
 * deserialized into adequate object form retaining all saved data. ##THE OBJECT
 * TO CONVERT IN STRING SHOULD BE SERIALIZABLE##
 * 
 * @author Accenture Services Pvt. Ltd.
 * 
 */
public class ObjectSerializer {

	/**
	 * Converts input serializable object into String
	 * 
	 * @param obj
	 *            input serializable object
	 * @return Serialized object in String form
	 * @throws IOException
	 *             Throws if an error occurs while writing the object stream
	 *             header
	 */
	public static String serialize(Serializable obj) throws IOException {

		if (obj == null)
			return "";

		ByteArrayOutputStream serialObj = new ByteArrayOutputStream();
		ObjectOutputStream objStream = new ObjectOutputStream(serialObj);
		objStream.writeObject(obj);
		objStream.close();
		return encodeBytes(serialObj.toByteArray());
	}

	/**
	 * Converts the Serialized String into object
	 * 
	 * @param str
	 *            Serialized String
	 * @return De-serialized object
	 * @throws IOException
	 *             Throws exception if error occurs in reading input stream
	 * @throws ClassNotFoundException
	 *             Throws exception if the class of one of the objects in the
	 *             object graph cannot be found
	 */
	public static Object deserialize(String str) throws IOException,
			ClassNotFoundException {

		if (str == null || str.trim().length() == 0)
			return null;

		ByteArrayInputStream serialObj = new ByteArrayInputStream(
				decodeBytes(str));
		ObjectInputStream objStream = new ObjectInputStream(serialObj);
		Object deserializedEmployee = objStream.readObject();
		return deserializedEmployee;
	}

	/**
	 * Encode byte array to String
	 * 
	 * @param bytes
	 *            input byte array
	 * @return encoded string
	 */
	private static String encodeBytes(byte[] bytes) {

		StringBuffer strBuf = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			strBuf.append((char) (((bytes[i] >> 4) & 0xF) + ((int) 'a')));
			strBuf.append((char) (((bytes[i]) & 0xF) + ((int) 'a')));
		}

		return strBuf.toString();
	}

	/**
	 * Decode String into byte array
	 * 
	 * @param str
	 *            input string
	 * @return decoded byte array
	 */
	private static byte[] decodeBytes(String str) {
		byte[] bytes = new byte[str.length() / 2];
		for (int i = 0; i < str.length(); i += 2) {
			char c = str.charAt(i);
			bytes[i / 2] = (byte) ((c - 'a') << 4);
			c = str.charAt(i + 1);
			bytes[i / 2] += (c - 'a');
		}

		return bytes;
	}
}
