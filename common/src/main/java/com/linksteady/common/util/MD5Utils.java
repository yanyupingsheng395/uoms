package com.linksteady.common.util;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

/**
 * MD5加密的工具类
 */
@SuppressWarnings("ALL")
public class MD5Utils {

	protected MD5Utils(){

	}

	private static final String SALT = "linksteady";

	private static final String ALGORITH_NAME = "md5";

	private static final int HASH_ITERATIONS = 2;

	public static String encrypt(String pswd) {
		return new SimpleHash(ALGORITH_NAME, pswd, ByteSource.Util.bytes(SALT), HASH_ITERATIONS).toHex();
	}

	public static String encrypt(String username, String pswd) {
		return new SimpleHash(ALGORITH_NAME, pswd, ByteSource.Util.bytes(username.toLowerCase() + SALT),
				HASH_ITERATIONS).toHex();
	}

	public static void main(String[] args) {
		System.out.println(MD5Utils.encrypt("admin","linksteady911"));
	}

}
