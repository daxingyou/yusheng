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

import java.sql.Timestamp;

import l1j.server.server.WriteLogTxt;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.lock.CharaterTradeReading;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_DeleteCharOK;
import l1j.server.server.templates.L1CharaterTrade;
import l1j.server.server.world.L1World;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket, C_DeleteChar

public class C_DeleteChar extends ClientBasePacket {

	private static final String C_DELETE_CHAR = "[C] RequestDeleteChar";

	private static final Log _log = LogFactory.getLog(C_DeleteChar.class);

	public C_DeleteChar(byte decrypt[], LineageClient _client)
			throws Exception {
		super(decrypt);
		String name = readS();
//		System.out.println("删除或者恢复"+name);
//		_log.fine("deleting character : " + name);
		if (_client.getActiveChar()!=null) {
			_client.kick();
			return;
		}
		try {
			L1PcInstance pc = CharacterTable.getInstance().restoreCharacter(name);
			if (pc == null) {
				_client.kick();
				return;
			}
			if (pc.getBind()) {
				//绑定的不能删除
				return;
			}
			for(final L1CharaterTrade charaterTrade : CharaterTradeReading.get().getAllCharaterTradeValues()){
				if (charaterTrade.get_by_objId() == pc.getId() && 
						(charaterTrade.get_state() == 0 || charaterTrade.get_state() == 1)){
					//如果有挂卖信息 不能删除
					return;
				}
			}
			final L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan != null) {
				clan.delMemberName(name);
			}	
			if (pc.getAccountName().equalsIgnoreCase(_client.getAccountName())) {
				if (pc.getLevel()<30) {
					CharacterTable.getInstance().deleteCharacter(
							_client.getAccountName(), name);
					_client.sendPacket(new S_DeleteCharOK(S_DeleteCharOK.DELETE_CHAR_NOW));
					WriteLogTxt.Recording("角色删除记录", ("帐号" + _client.getAccountName()
							+ "删除角色" + pc.getName() + "IP:" + _client.getIp()));
				}else {
					if (pc.getDeleteTime() == null) {
						final Timestamp deleteTime = new Timestamp(
								System.currentTimeMillis() + 604800000); // 7日后
						if (pc.getType() < 32) {
							if (pc.isCrown()) {
								pc.setType(32);

							} else if (pc.isKnight()) {
								pc.setType(33);

							} else if (pc.isElf()) {
								pc.setType(34);

							} else if (pc.isWizard()) {
								pc.setType(35);

							} else if (pc.isDarkelf()) {
								pc.setType(36);
								
							} else if (pc.isDragonKnight()) {
								pc.setType(37);

							} else if (pc.isIllusionist()) {
								pc.setType(38);
							}
							pc.setDeleteTime(deleteTime);
						}									
					}else {
						if (pc.isCrown()) {
							pc.setType(0);

						} else if (pc.isKnight()) {
							pc.setType(1);

						} else if (pc.isElf()) {
							pc.setType(2);

						} else if (pc.isWizard()) {
							pc.setType(3);

						} else if (pc.isDarkelf()) {
							pc.setType(4);
							
						} else if (pc.isDragonKnight()) {
							pc.setType(5);

						} else if (pc.isIllusionist()) {
							pc.setType(6);
							
						}
						pc.setDeleteTime(null);
					}
					pc.save(); // 资料存档
					_client.sendPacket(new S_DeleteCharOK(S_DeleteCharOK.DELETE_CHAR_AFTER_7DAYS));
				}		
			}
			
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			return;
		}
		
	}

	@Override
	public String getType() {
		return C_DELETE_CHAR;
	}

}
