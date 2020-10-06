/**
 *                            License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS  
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE"). 
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.  
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR  
 * COPYRIGHT LAW IS PROHIBITED.
 * 
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND  
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE  
 * MAY BE CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED 
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 * 
 */
package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static l1j.server.server.ActionCodes.*;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public class SprTable {

	private static final Log _log = LogFactory.getLog(SprTable.class);

	private static class Spr {
		private final Map<Integer, Integer> moveSpeed = new HashMap<Integer, Integer>();

		private final Map<Integer, Integer> attackSpeed = new HashMap<Integer, Integer>();

		private final Map<Integer, Integer> specialSpeed = new HashMap<Integer, Integer>();

		private int nodirSpellSpeed = 1200;

		private int dirSpellSpeed = 1200;
	}

	private static final Map<Integer, Spr> _dataMap = new HashMap<Integer, Spr>();

	private static final SprTable _instance = new SprTable();

	private SprTable() {

	}

	public void load() {
		loadSprAction();
	}

	public static SprTable get() {
		return _instance;
	}

	/**
	 * spr_actionテーブルをロードする。
	 */
	private void loadSprAction() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Spr spr = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM spr_action");
			rs = pstm.executeQuery();
			while (rs.next()) {
				int key = rs.getInt("spr_id");
				if (!_dataMap.containsKey(key)) {
					spr = new Spr();
					_dataMap.put(key, spr);
				} else {
					spr = _dataMap.get(key);
				}

				int actid = rs.getInt("act_id");
				int frameCount = rs.getInt("framecount");
				int frameRate = rs.getInt("framerate");
				int speed = calcActionSpeed(frameCount, frameRate);

				switch (actid) {
				case ACTION_Walk:
				case ACTION_SwordWalk:
				case ACTION_AxeWalk:
				case ACTION_BowWalk:
				case ACTION_SpearWalk:
				case ACTION_StaffWalk:
				case ACTION_DaggerWalk:
				case ACTION_TwoHandSwordWalk:
				case ACTION_EdoryuWalk:
				case ACTION_ClawWalk:
				case ACTION_ThrowingKnifeWalk:
					spr.moveSpeed.put(actid, speed);
					break;
				case ACTION_SkillAttack:
					spr.dirSpellSpeed = speed;
					break;
				case ACTION_SkillBuff:
					spr.nodirSpellSpeed = speed;
					break;
				case ACTION_Attack:
				case ACTION_SwordAttack:
				case ACTION_AxeAttack:
				case ACTION_BowAttack:
				case ACTION_SpearAttack:
				case ACTION_AltAttack:
				case ACTION_SpellDirectionExtra:
				case ACTION_StaffAttack:
				case ACTION_DaggerAttack:
				case ACTION_TwoHandSwordAttack:
				case ACTION_EdoryuAttack:
				case ACTION_ClawAttack:
				case ACTION_ThrowingKnifeAttack:
					spr.attackSpeed.put(actid, speed);
					break;
				case ACTION_Think:
				case ACTION_Aggress:
					spr.specialSpeed.put(actid, speed);
					break;
				default:
					break;
				}
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("SPR资料库" + _dataMap.size() + "负载");
	}

	/**
	 * フレーム数とフレームレートからアクションの合计时间(ms)を计算して返す。
	 */
	private int calcActionSpeed(int frameCount, int frameRate) {
		return (int) (frameCount * 40 * (24D / frameRate));
	}

	/**
	 * 指定されたsprの攻击速度を返す。もしsprに指定されたweapon_typeのデータが 设定されていない场合は、1.attackのデータを返す。
	 * 
	 * @param sprid
	 *            - 调べるsprのID
	 * @param actid
	 *            - 武器の种类を表す值。L1Item.getType1()の返り值 + 1 と一致する
	 * @return 指定されたsprの攻击速度(ms)
	 */
	public int getAttackSpeed(int sprid, int actid) {
		if (_dataMap.containsKey(sprid)) {
			if (_dataMap.get(sprid).attackSpeed.containsKey(actid)) {
				return _dataMap.get(sprid).attackSpeed.get(actid);
			} else if (actid == ACTION_Attack) {
				return 0;
			} else {
				return _dataMap.get(sprid).attackSpeed.get(ACTION_Attack);
			}
		}
		return 0;
	}

	public int getMoveSpeed(int sprid, int actid) {
		if (_dataMap.containsKey(sprid)) {
			if (_dataMap.get(sprid).moveSpeed.containsKey(actid)) {
				return _dataMap.get(sprid).moveSpeed.get(actid);
			} else if (actid == ACTION_Walk) {
				return 0;
			} else {
				return _dataMap.get(sprid).moveSpeed.get(ACTION_Walk);
			}
		}
		return 0;
	}

	public int getDirSpellSpeed(int sprid) {
		if (_dataMap.containsKey(sprid)) {
			return _dataMap.get(sprid).dirSpellSpeed;
		}
		return 0;
	}

	public int getNodirSpellSpeed(int sprid) {
		if (_dataMap.containsKey(sprid)) {
			return _dataMap.get(sprid).nodirSpellSpeed;
		}
		return 0;
	}

	public int getSpecialSpeed(int sprid, int actid) {
		if (_dataMap.containsKey(sprid)) {
			if (_dataMap.get(sprid).specialSpeed.containsKey(actid)) {
				return _dataMap.get(sprid).specialSpeed.get(actid);
			} else {
				return 1200;
			}
		}
		return 0;
	}

	/** Npc 各动作延迟时间 */
	public int getSprSpeed(int sprid, int actid) {
		switch (actid) {
		case ACTION_Walk:
		case ACTION_SwordWalk:
		case ACTION_AxeWalk:
		case ACTION_BowWalk:
		case ACTION_SpearWalk:
		case ACTION_StaffWalk:
		case ACTION_DaggerWalk:
		case ACTION_TwoHandSwordWalk:
		case ACTION_EdoryuWalk:
		case ACTION_ClawWalk:
		case ACTION_ThrowingKnifeWalk:
			// 移动
			return getMoveSpeed(sprid, actid);
		case ACTION_SkillAttack:
			// 有向施法
			return getDirSpellSpeed(sprid);
		case ACTION_SkillBuff:
			// 无向施法
			return getNodirSpellSpeed(sprid);
		case ACTION_Attack:
		case ACTION_SwordAttack:
		case ACTION_AxeAttack:
		case ACTION_BowAttack:
		case ACTION_SpearAttack:
		case ACTION_AltAttack:
		case ACTION_SpellDirectionExtra:
		case ACTION_StaffAttack:
		case ACTION_DaggerAttack:
		case ACTION_TwoHandSwordAttack:
		case ACTION_EdoryuAttack:
		case ACTION_ClawAttack:
		case ACTION_ThrowingKnifeAttack:
			// 攻击
			return getAttackSpeed(sprid, actid);
		case ACTION_Think:
		case ACTION_Aggress:
			// 魔法娃娃表情动作
			return getSpecialSpeed(sprid, actid);
		default:
			break;
		}
		return 0;
	}
	
	public final boolean containsTripleArrowSpr(final int sprid) {
		if (_dataMap.containsKey(sprid)) {
			return _dataMap.get(sprid).attackSpeed
					.containsKey(ACTION_BowAttack);
		}
		return false;
	}

/*	public Collection<String> getspr() {
		final ArrayList<String> list = new ArrayList<String>();
		for (final L1Command command : CommandsTable.get().getList()) {
			list.add(command.getName() + ": " + command.get_note());
		}
		return list;
	}*/
}
