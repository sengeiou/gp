package com.ubtechinc.nets.utils;



import java.security.MessageDigest;

public class MD5Util {

	private static String byteArrayToHexString(byte b[]) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++)
			resultSb.append(byteToHexString(b[i]));

		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n += 256;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	public static String MD5Encode(String origin, String charsetname) {
		String resultString = null;
		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			if (charsetname == null || "".equals(charsetname))
				resultString = byteArrayToHexString(md.digest(resultString
						.getBytes()));
			else
				resultString = byteArrayToHexString(md.digest(resultString
						.getBytes(charsetname)));
		} catch (Exception exception) {
		}
		return resultString;
	}
	public static String MD5Encode(String origin) {
		
		return MD5Encode(origin,"utf-8").toLowerCase();
	}

	public static void main(String[] args) {
		String or = "1541497286" + "100080012" + "60082a6810" + "5dea8150e185941b56eb130c05c249dbbe3bc360082a6810";
		System.out.println("xxor:" + or);
//		String or1 = "1540881246" + "12212126b47a4fd6112e08d38d0de112" + "5NNlX4oRWB" + "UBT2018T10200012N";
		System.out.print("xx:"+  MD5Encode(or));
	}

	private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

}
