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
package l1j.server.server.serverpackets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
//语言 




import com.ZHConterver;

public abstract class ServerBasePacket {
	private static Logger _log = Logger.getLogger(ServerBasePacket.class
			.getName());
	protected Random _random = new Random();
	ByteArrayOutputStream _bao = new ByteArrayOutputStream();
	ByteArrayOutputStream _bao3 = new ByteArrayOutputStream();
	
	protected ServerBasePacket() {
	}

	protected void writeBoolean(final boolean b) {
        this._bao.write(b ? 0x01 : 0x00);
        this._bao3.write(b ? 0x01 : 0x00);
    }
	
	protected void writeD(long value) {
		_bao.write((int)(value & 0xff));
		_bao.write((int)(value >> 8 & 0xff));
		_bao.write((int)(value >> 16 & 0xff));
		_bao.write((int)(value >> 24 & 0xff));
		
		_bao3.write((int)(value & 0xff));
		_bao3.write((int)(value >> 8 & 0xff));
		_bao3.write((int)(value >> 16 & 0xff));
		_bao3.write((int)(value >> 24 & 0xff));
	}

	protected void writeH(int value) {
		_bao.write(value & 0xff);
		_bao.write(value >> 8 & 0xff);
		
		_bao3.write(value & 0xff);
		_bao3.write(value >> 8 & 0xff);
	}

	protected void writeC(int value) {
		_bao.write(value & 0xff);
		
		_bao3.write(value & 0xff);
	}

	protected void writeP(int value) {
		_bao.write(value);
		
		_bao3.write(value);
	}

	protected void writeL(long value) {
		_bao.write((int) (value & 0xff));
		
		_bao3.write((int) (value & 0xff));
	}

	protected void writeF(double org) {
		long value = Double.doubleToRawLongBits(org);
		_bao.write((int) (value & 0xff));
		_bao.write((int) (value >> 8 & 0xff));
		_bao.write((int) (value >> 16 & 0xff));
		_bao.write((int) (value >> 24 & 0xff));
		_bao.write((int) (value >> 32 & 0xff));
		_bao.write((int) (value >> 40 & 0xff));
		_bao.write((int) (value >> 48 & 0xff));
		_bao.write((int) (value >> 56 & 0xff));
		
		_bao3.write((int) (value & 0xff));
		_bao3.write((int) (value >> 8 & 0xff));
		_bao3.write((int) (value >> 16 & 0xff));
		_bao3.write((int) (value >> 24 & 0xff));
		_bao3.write((int) (value >> 32 & 0xff));
		_bao3.write((int) (value >> 40 & 0xff));
		_bao3.write((int) (value >> 48 & 0xff));
		_bao3.write((int) (value >> 56 & 0xff));
	}

	protected void writeS(String text) {
		try {
			if (text != null) {
				_bao.write(text.getBytes("GBK"));//改成Config.LANGUAGE 
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}

		_bao.write(0);
		try {
			if (text != null) {
				final String newtext = ZHConterver.convert(text, ZHConterver.TRADITIONAL);
				_bao3.write(newtext.getBytes("BIG5"));//改成Config.LANGUAGE 
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}

		_bao3.write(0);
	}
	
	protected void writeShopMessage(String text) {
		try {
			if (text != null) {
				final byte[] a= text.getBytes("GBK");
				_bao.write(a);
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		try {
			if (text != null) {
				final String newtext = ZHConterver.convert(text, ZHConterver.TRADITIONAL);
				final byte[] b = newtext.getBytes("BIG5");
				_bao3.write(b);
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
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
	
	protected void writeByte(byte[] text) {
		try {
			if (text != null) {
				_bao.write(text);
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		try {
			if (text != null) {
				_bao3.write(text);
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}
	
	protected void writeByte(int value) {
		_bao.write((byte) (value >> 0 & 0xff));

		_bao3.write((byte) (value >> 0 & 0xff));
	}

	public int getLength() {
		return _bao.size() + 2;
	}
	
	public int getLengthBIG5() {
		return _bao3.size() + 2;
	}
	
	protected void randomByte() {
		// 1 x 1
		writeByte((byte) _random.nextInt(256));
	}

	protected void randomShort() {
		// 1 x 2
		randomByte();
		randomByte();
	}

	protected void randomInt() {
		// 2 x 2
		randomShort();
		randomShort();
	}

	public byte[] getBytes() {
		int padding = _bao.size() % 4;

		if (padding != 0) {
			for (int i = padding; i < 4; i++) {
				_bao.write(0x00);
			}
		}
		return _bao.toByteArray();
	}
	
	public byte[] getBIG5Bytes() {
		int padding = _bao3.size() % 4;

		if (padding != 0) {
			for (int i = padding; i < 4; i++) {
				_bao3.write(0x00);
			}
		}
		return _bao3.toByteArray();
	}
	
	public byte[] getBytes(boolean flg) {
		return _bao.toByteArray();
	}
	
	public void close() {
		try {
			_bao.close();
		} catch (Exception e) {
		}
		try {
			_bao3.close();
		} catch (Exception e) {
		}
	}

	public abstract byte[] getContent() throws IOException;

	/**
	 * 种类表文字列返。("[S] S_WhoAmount" 等)
	 */
	public String getType() {
		return "[S] " + this.getClass().getSimpleName();
	}
}
