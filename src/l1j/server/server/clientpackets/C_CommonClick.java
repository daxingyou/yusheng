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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.L1Clan;
import l1j.server.server.serverpackets.S_CharAmount;
import l1j.server.server.serverpackets.S_CharPacks;
import l1j.server.server.utils.SQLUtil;
import l1j.server.server.world.L1World;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class C_CommonClick extends ClientBasePacket{
	

	private static final String C_COMMON_CLICK = "[C] C_CommonClick";

	private static final Log _log = LogFactory.getLog(C_CommonClick.class);
	
	public C_CommonClick(byte[] abyte0,LineageClient client) {
		super(abyte0);
		int amountOfChars = client.getAccount().countCharacters();
		deleteCharacter(client);
		client.sendPacket(new S_CharAmount(amountOfChars));
		if (amountOfChars > 0) {
			sendCharPacks(client);
		}
	}

/*	public C_CommonClick(LineageClient client) {
		
	}*/
	
	private void deleteCharacter(final LineageClient client) {
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			conn = L1DatabaseFactory.getInstance().getConnection();
			pstm = conn.prepareStatement("SELECT * FROM `characters` WHERE `account_name`=? ORDER BY `objid`");
			pstm.setString(1, client.getAccountName());
			rs = pstm.executeQuery();

			while (rs.next()) {
				final String name = rs.getString("char_name");
				final String clanname = rs.getString("Clanname");

				final Timestamp deleteTime = rs.getTimestamp("DeleteTime");
				if (deleteTime != null) {
					final Calendar cal = Calendar.getInstance();
					final long checkDeleteTime = ((cal.getTimeInMillis() - deleteTime.getTime()) / 1000) / 3600;
					if (checkDeleteTime >= 0) {
						final L1Clan clan = L1World.getInstance().getClan(
								clanname);
						if (clan != null) {
							clan.delMemberName(name);
						}
						// 刪除人物
						CharacterTable.getInstance().deleteCharacter(
								client.getAccountName(), name);
						WriteLogTxt.Recording("角色删除记录", ("帐号" + client.getAccountName()
								+ "删除30级以上角色" + name + "IP:" + client.getIp()));
					}
				}
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(conn);
		}
	}

	private void sendCharPacks(LineageClient client) {
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			conn = L1DatabaseFactory.getInstance().getConnection();
			pstm = conn
					.prepareStatement("SELECT * FROM characters WHERE account_name=? ORDER BY objid");
			pstm.setString(1, client.getAccountName());
			rs = pstm.executeQuery();

			while (rs.next()) {
				String name = rs.getString("char_name");
				String clanname = rs.getString("Clanname");
				int type = rs.getInt("Type");
				byte sex = rs.getByte("Sex");
				int lawful = rs.getInt("Lawful");

				int currenthp = rs.getInt("CurHp");
				if (currenthp < 1) {
					currenthp = 1;
				} else if (currenthp > 32767) {
					currenthp = 32767;
				}

				int currentmp = rs.getInt("CurMp");
				if (currentmp < 1) {
					currentmp = 1;
				} else if (currentmp > 32767) {
					currentmp = 32767;
				}

				int lvl;
				if (Config.CHARACTER_CONFIG_IN_SERVER_SIDE) {
					lvl = rs.getInt("level");
					if (lvl < 1) {
						lvl = 1;
					} else if (lvl > 127) {
						lvl = 127;
					}
				} else {
					lvl = 1;
				}

				final int ac = rs.getShort("Ac");
				final int str = rs.getShort("Str");
				final int dex = rs.getShort("Dex");
				final int con = rs.getShort("Con");
				final int wis = rs.getShort("Wis");
				final int cha = rs.getShort("Cha");
				final int intel = rs.getShort("Intel");
				final int time = rs.getInt("CreateTime");

                final S_CharPacks cpk = new S_CharPacks(name, clanname, type,
                        sex, lawful, currenthp, currentmp, ac, lvl, str, dex,
                        con, wis, cha, intel, time);

				client.sendPacket(cpk);
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(conn);
		}
	}

	public String getType() {
		return C_COMMON_CLICK;
	}
}