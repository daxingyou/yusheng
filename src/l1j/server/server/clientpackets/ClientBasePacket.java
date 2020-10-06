/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
// 语言 
//import l1j.server.server.ClientThread;

public abstract class ClientBasePacket {

	private static final Log _log = LogFactory.getLog(ClientBasePacket.class);

	public static final String[] LANGUAGE_CODE = {"UTF8", "EUCKR", "UTF8", "BIG5", "SJIS", "GBK"};
	private int _language;
	
	private byte _decrypt[];

	private int _off;

	public ClientBasePacket(byte abyte0[]) {
//		_log.finest("type=" + getType() + ", len=" + abyte0.length);
		_language = abyte0[0] & 0xff;
		_decrypt = abyte0;
		_off = 1;
	}

/*	public ClientBasePacket(ByteBuffer bytebuffer, ClientThread clientthread) {
	}
*/
	public int readD() {
		int i = _decrypt[_off++] & 0xff;
		i |= _decrypt[_off++] << 8 & 0xff00;
		i |= _decrypt[_off++] << 16 & 0xff0000;
		i |= _decrypt[_off++] << 24 & 0xff000000;
		return i;
	}

	public int readC() {
		int i = _decrypt[_off++] & 0xff;
		return i;
	}

	public int readH() {
		int i = _decrypt[_off++] & 0xff;
		i |= _decrypt[_off++] << 8 & 0xff00;
		return i;
	}

	public int readCH() {
		int i = _decrypt[_off++] & 0xff;
		i |= _decrypt[_off++] << 8 & 0xff00;
		i |= _decrypt[_off++] << 16 & 0xff0000;
		return i;
	}

	public double readF() {
		long l = _decrypt[_off++] & 0xff;
		l |= _decrypt[_off++] << 8 & 0xff00;
		l |= _decrypt[_off++] << 16 & 0xff0000;
		l |= _decrypt[_off++] << 24 & 0xff000000;
		l |= (long) _decrypt[_off++] << 32 & 0xff00000000L;
		l |= (long) _decrypt[_off++] << 40 & 0xff0000000000L;
		l |= (long) _decrypt[_off++] << 48 & 0xff000000000000L;
		l |= (long) _decrypt[_off++] << 56 & 0xff00000000000000L;
		return Double.longBitsToDouble(l);
	}

	public String readS() {
		String s = "";
		try {
			s = new String(_decrypt, _off, _decrypt.length - _off, LANGUAGE_CODE[_language]);//改成Config.LANGUAGE
			if (s.indexOf('\0')<0) {
				return "";
			}
			s = s.substring(0, s.indexOf('\0'));
			_off += s.getBytes(LANGUAGE_CODE[_language]).length + 1;//改成Config.LANGUAGE 
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return s;
	}

	public byte[] readByte() {
		byte[] result = new byte[_decrypt.length - _off];
		try {
			System.arraycopy(_decrypt, _off, result, 0, _decrypt.length - _off);
			_off = _decrypt.length;
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return result;
	}
	
	public String[] readShopString() {
		final String s[] = new String[2];
		s[0] = null;
		s[1] = null;
		byte[] result = new byte[_decrypt.length - _off];
		try {
			System.arraycopy(_decrypt, _off, result, 0, _decrypt.length - _off);
			_off = _decrypt.length;
			if (result.length <= 2){
				return s;
			}
			int ffindex = 0;
			for(int i = 0;i<result.length;i++){
				if (result[i] == -1){//查找下一句的位置
					ffindex = i;
					break;
				}
			}
			if (ffindex > 0){
				s[0] = new String(result, 0, ffindex, LANGUAGE_CODE[_language]);
				s[1] = new String(result, ffindex+1,result.length-ffindex-1,LANGUAGE_CODE[_language]);
			}else{
				s[1] = new String(result, 0, result.length,LANGUAGE_CODE[_language]);
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return s;
	}
	public String printData(final byte[] data, final int len) {

		final StringBuffer result = new StringBuffer();

		int counter = 0;

		for (int i = 0; i < len; i++) {

			if (counter % 16 == 0) {
				result.append(this.fillHex(i, 4) + ": ");
			}

			result.append(this.fillHex(data[i] & 0xff, 2) + " ");
			counter++;

			if (counter == 16) {
				result.append("   ");

				int charpoint = i - 15;
				for (int a = 0; a < 16; a++) {
					final int t1 = data[charpoint++];

					if ((t1 > 0x1f) && (t1 < 0x80)) {
						result.append((char) t1);
					} else {
						result.append('.');
					}
				}

				result.append("\n");
				counter = 0;
			}
		}

		final int rest = data.length % 16;

		if (rest > 0) {

			for (int i = 0; i < 17 - rest; i++) {
				result.append("   ");
			}

			int charpoint = data.length - rest;

			for (int a = 0; a < rest; a++) {

				final int t1 = data[charpoint++];

				if ((t1 > 0x1f) && (t1 < 0x80)) {
					result.append((char) t1);
				} else {
					result.append('.');
				}
			}

			result.append("\n");
		}

		return result.toString();
	}

	/**
	 * <font color=#0000ff>将数字转成 16 进位</font>
	 * @param data
	 * @param digits
	 * @return
	 */
	private String fillHex(final int data, final int digits) {

		String number = Integer.toHexString(data);

		for (int i = number.length(); i < digits; i++) {
			number = "0" + number;
		}

		return number;
	}
	protected byte[] readBookByte() {
		final byte[] result = new byte[127];
		try {
			System.arraycopy(this._decrypt, this._off, result, 0, 127);
			this._off = this._decrypt.length;

		} catch (final Exception e) {
		}
		return result;
	}
	
	/**
	 * 结束byte[]取回
	 */
	public void over() {
		try {
			this._decrypt = null;
			this._off = 0;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 种类表文字列返。("[C] C_DropItem" 等)
	 */
	public String getType() {
		return "[C] " + this.getClass().getSimpleName();
	}
}
