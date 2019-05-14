
package com.example.stegocipher.steganogarficipher.algorithm;

import com.example.stegocipher.steganogarficipher.ProgressHandler;
import android.util.Log;


public class LSB {

	private static int[] binary = { 16, 8, 0 };
	private static byte[] andByte = { (byte) 0x80, 0x40, 0x20, 0x10, 0x08,0x04,0x02,0x01};
	private static int[] toShift = {7, 6, 5, 4, 3, 2, 1, 0 };
	public static String END_MESSAGE_COSTANT = "#!@";
	public static String START_MESSAGE_COSTANT = "@!#";

	/**
	 * Method ini adalah initi dari LSB pada 2 bit terakhir (Encoding).
	 * @param oneDPix The <b>rgb</b> array.
	 * @param imgCols Image width.
	 * @param imgRows Image height.
	 * @param str adalah pesan yg di encode.
	 * @param hand adalah handler interface, untuk progress bar.
	 * @return Encoded message image.
	 */
	public static byte[] encodeMessage(int[] oneDPix, int imgCols, int imgRows,
			String str,ProgressHandler hand) {

		str += END_MESSAGE_COSTANT;
		str = START_MESSAGE_COSTANT +str;
		byte[] msg = str.getBytes();
		int channels = 3;
		int shiftIndex = 8;
		byte[] result = new byte[imgRows * imgCols * channels];
		
		if (hand != null)
			hand.setTotal(imgRows * imgCols * channels);
		int msgIndex = 0;
		int resultIndex = 0;
		boolean msgEnded = false;
		for (int row = 0; row < imgRows; row++) {
			for (int col = 0; col < imgCols; col++) {
				int element = row * imgCols + col;
				byte tmp = 0;

				for (int channelIndex = 0; channelIndex < channels; channelIndex++) {
					if (!msgEnded) {
						tmp = (byte) ((((oneDPix[element] >> binary[channelIndex]) & 0xFF) & 0xFE) | ((msg[msgIndex] >> toShift[(shiftIndex++)
								% toShift.length]) & 0x1));// 6
						if (shiftIndex % toShift.length == 0) {
							msgIndex++;
						}
						if (msgIndex == msg.length) {
							msgEnded = true;
						}
					} else {
						tmp = (byte) ((((oneDPix[element] >> binary[channelIndex]) & 0xFF)));
					}
					result[resultIndex++] = tmp;
					if (hand != null)
						hand.increment(1);
				}

			}

		}
		return result;

	}

	/**
	 * method untuk decode LSB pada 2 bit terakhir.
	 * @param oneDPix adalah byte array image.
	 * @param imgCols Image width.
	 * @param imgRows Image height.
	 * @return pesan yang sudah di decode.
	 */
	public static String decodeMessage(byte[] oneDPix, int imgCols,
			int imgRows) {


		StringBuilder strbuilder=new StringBuilder();

		String builder = "";
		int shiftIndex = 8;
		byte tmp = 0x00;
		for (int i = 0; i < oneDPix.length; i++) {
			tmp = (byte) (tmp | ((oneDPix[i] << toShift[shiftIndex
					% toShift.length]) & andByte[shiftIndex++ % toShift.length]));
			if (shiftIndex % toShift.length == 0) {
				byte[] nonso = { tmp };
				String str = new String(nonso);

				builder=strbuilder.toString();
				if (builder.endsWith(END_MESSAGE_COSTANT)) {
					break;
				} else {
					strbuilder.append(str);
					builder=strbuilder.toString();
					if (builder.length() == START_MESSAGE_COSTANT.length()
							&& !START_MESSAGE_COSTANT.equals(builder)) {
						builder = null;
						break;
					}
				}

				tmp = 0x00;
			}

		}

		if (builder != null)
			builder = builder.substring(START_MESSAGE_COSTANT.length(), builder
					.length()
					- END_MESSAGE_COSTANT.length());
		return builder;

	}
	
	/**
	 * Convert the byte array to an int array.
	 * @param b The byte array.
	 * @return The int array.
	 */

	public static int[] byteArrayToIntArray(byte[] b) {
		Log.v("Size byte array", b.length+"");
		int size=b.length / 3;
		Log.v("Size Int array",size+"");
		System.runFinalization();
		System.gc();
		Log.v("FreeMemory", Runtime.getRuntime().freeMemory()+"");
		int[] result = new int[size];
		int off = 0;
		int index = 0;
		while (off < b.length) {
			result[index++] = byteArrayToInt(b, off);
			off = off + 3;
		}

		return result;
	}

	/**
	 * Convert byte array ke int.
	 * 
	 * @param b The byte array
	 * @return The integer
	 */
	public static int byteArrayToInt(byte[] b) {
		return byteArrayToInt(b, 0);
	}

	/**
	 * Convert byte array ke int, mulai dari offset yang di input.
	 * 
	 * @param b
	 *            adalah byte array
	 * @param offset
	 *             adalah array offset
	 * @return  integer
	 */
	public static int byteArrayToInt(byte[] b, int offset) {
		int value = 0x00000000;
		for (int i = 0; i < 3; i++) {
			int shift = (3 - 1 - i) * 8;
			value |= (b[i + offset] & 0x000000FF) << shift;
		}
		value = value & 0x00FFFFFF;
		return value;
	}

	/**
	 * Convert integer array yang merepresentasikan nilai [argb] values ke byte array
	 * yang merepresentasikan nilai [rgb] 
	 * 
	 * @param array Integer, array yang merepresentasikan nilai [argb].
	 * @return byte Array, yang merepresentasikan nilai [rgb].
	 */

	public static byte[] convertArray(int[] array) {
		byte[] newarray = new byte[array.length * 3];

		for (int i = 0; i < array.length; i++) {

			/*
			 * newarray[i * 3] = (byte) ((array[i]) & 0xFF); newarray[i * 3 + 1]
			 * = (byte)((array[i] >> 8)& 0xFF); newarray[i * 3 + 2] =
			 * (byte)((array[i] >> 16)& 0xFF);
			 */

			newarray[i * 3] = (byte) ((array[i] >> 16) & 0xFF);
			newarray[i * 3 + 1] = (byte) ((array[i] >> 8) & 0xFF);
			newarray[i * 3 + 2] = (byte) ((array[i]) & 0xFF);

		}
		return newarray;
	}

}
