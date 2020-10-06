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
package l1j.server.server;

import java.util.Calendar;
import java.util.TimeZone;

import l1j.server.Config;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Clan;//未被攻下的城堡自动置放守城警卫 
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;//攻城战开始时传送城堡附近的玩家 
import l1j.server.server.model.L1WarSpawn;
import l1j.server.server.model.Instance.L1CrownInstance;
import l1j.server.server.model.Instance.L1FieldObjectInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.templates.L1Castle;
import l1j.server.server.world.L1World;
//攻城结束删除警卫、城主 

public class WarTimeController implements Runnable {


	private static WarTimeController _instance;
	private L1Castle[] _l1castle = new L1Castle[8];
	private Calendar[] _war_start_time = new Calendar[8];
	private Calendar[] _war_end_time = new Calendar[8];
	private boolean[] _is_now_war = new boolean[8];
	
	private boolean[] _is_over_war = new boolean[8];

	private WarTimeController() {
		for (int i = 0; i < _l1castle.length; i++) {
			_l1castle[i] = CastleTable.getInstance().getCastleTable(i + 1);
			_war_start_time[i] = _l1castle[i].getWarTime();
			_war_end_time[i] = (Calendar) _l1castle[i].getWarTime().clone();
			_war_end_time[i].add(Config.ALT_WAR_TIME_UNIT, Config.ALT_WAR_TIME);
			_is_over_war[i] = false;
		}
	}
	
	public void reload(){
		for (int i = 0; i < _l1castle.length; i++) {
			_l1castle[i] = CastleTable.getInstance().getCastleTable(i + 1);
			_war_start_time[i] = _l1castle[i].getWarTime();
			_war_end_time[i] = (Calendar) _l1castle[i].getWarTime().clone();
			_war_end_time[i].add(Config.ALT_WAR_TIME_UNIT, Config.ALT_WAR_TIME);
			_is_over_war[i] = false;
		}
	}

	public static WarTimeController getInstance() {
		if (_instance == null) {
			_instance = new WarTimeController();
		}
		return _instance;
	}

	@Override
	public void run() {
		try {
			while (true) {
				checkWarOver(); //检查是否提前结束
				checkWarTime(); // 战争时间
				Thread.sleep(1000);
			}
		} catch (Exception e1) {
		}
	}

	public Calendar getRealTime() {
		TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		Calendar cal = Calendar.getInstance(tz);
		return cal;
	}

	public boolean isNowWar(int castle_id) {
		return _is_now_war[castle_id - 1];
	}

	public void checkCastleWar(L1PcInstance player) {
		for (int i = 0; i < 8; i++) {
			if (_is_now_war[i]) {
				player.sendPackets(
						new S_PacketBox(S_PacketBox.MSG_WAR_GOING, i + 1)); // %s攻城进行中。
			}
		}
	}
	
	public void setCastleWarOver(int castle_id,boolean flg){
		_is_over_war[castle_id-1] = flg;	
	}
	private void checkWarOver() {
		for (int i = 0; i < 8; i++) {
			if (_is_over_war[i]) {
				if (_is_now_war[i] == true) {
					_is_now_war[i] = false;
					L1World.getInstance().broadcastPacketToAll(
							new S_PacketBox(S_PacketBox.MSG_WAR_END, i + 1)); // %s攻城终了。
					_war_start_time[i].add(Config.ALT_WAR_INTERVAL_UNIT,
							Config.ALT_WAR_INTERVAL);
					_war_end_time[i].add(Config.ALT_WAR_INTERVAL_UNIT,
							Config.ALT_WAR_INTERVAL);
					_l1castle[i].setTaxRate(10); // 税率10%
//					_l1castle[i].setPublicMoney(0); // 公金
					CastleTable.getInstance().updateCastle(_l1castle[i]);

					int castle_id = i + 1;
					for (L1Object l1object : L1World.getInstance().getObject()) {
						// 战争内旗消
						if (l1object instanceof L1FieldObjectInstance) {
							L1FieldObjectInstance flag = (L1FieldObjectInstance) l1object;
							if (L1CastleLocation
									.checkInWarArea(castle_id, flag)) {
								flag.deleteMe();
							}
						}
						// 场合、消spawn
						if (l1object instanceof L1CrownInstance) {
							L1CrownInstance crown = (L1CrownInstance) l1object;
							if (L1CastleLocation.checkInWarArea(castle_id,
									crown)) {
								crown.deleteMe();
								L1WarSpawn warspawn = new L1WarSpawn();
								warspawn.SpawnTower(castle_id);
							}
						}
					}
					
					//攻城结束删除警卫、城主 
	/*					for (L1Object obj : L1World.getInstance().getAllNpcs()) {
						if (obj instanceof L1CastleGuardInstance) {
							L1CastleGuardInstance npc = (L1CastleGuardInstance) obj;
							if (L1CastleLocation.checkInWarArea(castle_id, npc)) {
								npc.allTargetClear();
								npc.deleteMe();
							}
						}
					}*/
					//攻城结束删除警卫、城主  end
				}				
			}
		}
		
	}
	/**
     * 战争中城堡数量
     * 
     * @return
     */
    public int checkCastleWar() {
        int x = 0;
        for (int i = 0; i < 8; i++) {
            if (this._is_now_war[i]) {
                x++;
            }
        }
        return x;
    }
	private void checkWarTime() {
		for (int i = 0; i < 8; i++) {
			if (_war_start_time[i].before(getRealTime()) // 战争开始
					&& _war_end_time[i].after(getRealTime())) {
				if (_is_now_war[i] == false) {
					_is_now_war[i] = true;
					// 旗spawn
					L1WarSpawn warspawn = new L1WarSpawn();
					warspawn.SpawnFlag(i + 1);
					L1World.getInstance().broadcastPacketToAll(
							new S_PacketBox(S_PacketBox.MSG_WAR_BEGIN, i + 1)); // %s攻城始。
					
					//攻城战开始时传送城堡附近的玩家 
					int[] loc = new int[3];
					for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
						L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
						int castle_id = i + 1;
						if (clan != null) {//有血盟
							if (clan.getCastleId() != castle_id) {//盟主未拥有该城堡
								if (L1CastleLocation.checkInWarArea(castle_id, pc) && !pc.isGm()) {
									// 旗内居
									loc = L1CastleLocation.getGetBackLoc(castle_id);
									int locx = loc[0];
									int locy = loc[1];
									short mapid = (short) loc[2];
									L1Teleport.teleport(pc, locx, locy, mapid, 5, true);
								}
							}
						} else {//无血盟
							if (L1CastleLocation.checkInWarArea(castle_id, pc) && !pc.isGm()) {
								// 旗内居
								loc = L1CastleLocation.getGetBackLoc(castle_id);
								int locx = loc[0];
								int locy = loc[1];
								short mapid = (short) loc[2];
								L1Teleport.teleport(pc, locx, locy, mapid, 5, true);
							}
						}
					}
					//攻城战开始时传送城堡附近的玩家  end
					
					//未被攻下的城堡自动置放守城警卫 
					/*int castle_id = i + 1;
					for (L1Clan defClan : L1World.getInstance().getAllClans()) {
						if (castle_id == defClan.getCastleId()) {
							return;
						} else {
							l1j.william.CastleGuard.spawn(i + 8001);//置放城主、守城警卫
						}
					}*/
					//未被攻下的城堡自动置放守城警卫  end
				}
			} else if (_war_end_time[i].before(getRealTime())) { // 战争终了
				if (_is_now_war[i] == true) {
					_is_now_war[i] = false;
					L1World.getInstance().broadcastPacketToAll(
							new S_PacketBox(S_PacketBox.MSG_WAR_END, i + 1)); // %s攻城终了。
					_war_start_time[i].add(Config.ALT_WAR_INTERVAL_UNIT,
							Config.ALT_WAR_INTERVAL);
					_war_end_time[i].add(Config.ALT_WAR_INTERVAL_UNIT,
							Config.ALT_WAR_INTERVAL);
					_l1castle[i].setTaxRate(10); // 税率10%
//					_l1castle[i].setPublicMoney(0); // 公金
					CastleTable.getInstance().updateCastle(_l1castle[i]);

					int castle_id = i + 1;
					for (L1Object l1object : L1World.getInstance().getObject()) {
						// 战争内旗消
						if (l1object instanceof L1FieldObjectInstance) {
							L1FieldObjectInstance flag = (L1FieldObjectInstance) l1object;
							if (L1CastleLocation
									.checkInWarArea(castle_id, flag)) {
								flag.deleteMe();
							}
						}
						// 场合、消spawn
						if (l1object instanceof L1CrownInstance) {
							L1CrownInstance crown = (L1CrownInstance) l1object;
							if (L1CastleLocation.checkInWarArea(castle_id,
									crown)) {
								crown.deleteMe();
								L1WarSpawn warspawn = new L1WarSpawn();
								warspawn.SpawnTower(castle_id);
							}
						}
					}
					
					//攻城结束删除警卫、城主 
/*					for (L1Object obj : L1World.getInstance().getAllNpcs()) {
						if (obj instanceof L1CastleGuardInstance) {
							L1CastleGuardInstance npc = (L1CastleGuardInstance) obj;
							if (L1CastleLocation.checkInWarArea(castle_id, npc)) {
								npc.allTargetClear();
								npc.deleteMe();
							}
						}
					}*/
					//攻城结束删除警卫、城主  end
				}
			}

		}
	}
}
