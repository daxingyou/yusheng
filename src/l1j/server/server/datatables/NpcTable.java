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
package l1j.server.server.datatables;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import l1j.server.L1DatabaseFactory;
import l1j.server.data.executor.NpcExecutor;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.SQLUtil;
import l1j.william.NpcSpawn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class NpcTable {
	private static final Log _log = LogFactory.getLog(NpcTable.class);

	private final boolean _initialized;

	private static NpcTable _instance;

	private final HashMap<Integer, L1Npc> _npcs;

	private static final Map<String, Integer> _familyTypes =
			NpcTable.buildFamily();
	
	private static final Map<String, Constructor<?>> _constructorCache = 
			new HashMap<String, Constructor<?>>();

	public static NpcTable getInstance() {
		if (_instance == null) {
			_instance = new NpcTable();
		}
		return _instance;
	}

	public boolean isInitialized() {
		return _initialized;
	}

	private NpcTable() {
		_npcs = new HashMap<Integer, L1Npc>();
		//加入 william功能
		try{
			NpcSpawn.getInstance().forNpcTable(_npcs);
		}catch(Exception ex){ 
			ex.printStackTrace();
		}
		//加入 william功能 end
		loadNpcData();
		_initialized = true;
	}

	private void loadNpcData() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM npc");
			rs = pstm.executeQuery();
			while (rs.next()) {
				L1Npc npc = new L1Npc();
				int npcId = rs.getInt("npcid");
				npc.set_npcId(npcId);
				npc.set_name(rs.getString("name"));
				npc.set_nameid(rs.getString("nameid"));
				final String classname = rs.getString("classname");
				npc.set_classname(classname);
				npc.setNpcExecutor(addClass(npcId, classname));
				npc.setImpl(rs.getString("impl"));
				npc.set_gfxid(rs.getInt("gfxid"));
				npc.set_level(rs.getInt("lvl"));
				npc.set_hp(rs.getInt("hp"));
				npc.set_mp(rs.getInt("mp"));
				npc.set_ac(rs.getInt("ac"));
				npc.set_str(rs.getByte("str"));
				npc.set_con(rs.getByte("con"));
				npc.set_dex(rs.getByte("dex"));
				npc.set_wis(rs.getByte("wis"));
				npc.set_int(rs.getByte("intel"));
				npc.set_mr(rs.getInt("mr"));
				npc.set_exp(rs.getInt("exp"));
				npc.set_lawful(rs.getInt("lawful"));
				npc.set_size(rs.getString("size"));
				npc.set_weakwater(rs.getInt("weak_water"));
				npc.set_weakwind(rs.getInt("weak_wind"));
				npc.set_weakfire(rs.getInt("weak_fire"));
				npc.set_weakearth(rs.getInt("weak_earth"));
				npc.set_ranged(rs.getInt("ranged"));
				npc.setTamable(rs.getBoolean("tamable"));
				npc.set_passispeed(rs.getInt("passispeed"));
				npc.set_atkspeed(rs.getInt("atkspeed"));
				npc.setAtkMagicSpeed(rs.getInt("atk_magic_speed"));
				npc.setSubMagicSpeed(rs.getInt("sub_magic_speed"));
				npc.set_undead(rs.getInt("undead"));
				npc.set_poisonatk(rs.getInt("poison_atk"));
				npc.set_paralysisatk(rs.getInt("paralysis_atk"));
				npc.set_agro(rs.getBoolean("agro"));
				npc.set_agrososc(rs.getBoolean("agrososc"));
				npc.set_agrocoi(rs.getBoolean("agrocoi"));
				Integer family = _familyTypes.get(rs.getString("family"));
				if (family == null) {
					npc.set_family(0);
				} else {
					npc.set_family(family.intValue());
				}
				int agrofamily = rs.getInt("agrofamily");
				if (npc.get_family() == 0 && agrofamily == 1) {
					npc.set_agrofamily(0);
				} else {
					npc.set_agrofamily(agrofamily);
				}
				npc.set_agrogfxid1(rs.getInt("agrogfxid1"));
				npc.set_agrogfxid2(rs.getInt("agrogfxid2"));
				npc.set_picupitem(rs.getBoolean("picupitem"));
				npc.set_digestitem(rs.getInt("digestitem"));
				npc.set_bravespeed(rs.getBoolean("bravespeed"));
				npc.set_hprinterval(rs.getInt("hprinterval"));
				npc.set_hpr(rs.getInt("hpr"));
				npc.set_mprinterval(rs.getInt("mprinterval"));
				npc.set_mpr(rs.getInt("mpr"));
				npc.set_teleport(rs.getBoolean("teleport"));
				npc.set_randomlevel(rs.getInt("randomlevel"));
				npc.set_randomhp(rs.getInt("randomhp"));
				npc.set_randommp(rs.getInt("randommp"));
				npc.set_randomac(rs.getInt("randomac"));
				npc.set_randomexp(rs.getInt("randomexp"));
				npc.set_randomlawful(rs.getInt("randomlawful"));
				npc.set_damagereduction(rs.getInt("damage_reduction"));
				npc.set_hard(rs.getBoolean("hard"));
				npc.set_doppel(rs.getBoolean("doppel"));
				npc.set_IsTU(rs.getBoolean("IsTU"));
				npc.set_IsErase(rs.getBoolean("IsErase"));
				npc.setBowActId(rs.getInt("bowActId"));
				npc.setKarma(rs.getInt("karma"));
				npc.setTransformId(rs.getInt("transform_id"));
				npc.setLightSize(rs.getInt("light_size"));
				npc.setAmountFixed(rs.getBoolean("amount_fixed"));
				npc.set_atkexspeed(rs.getInt("atkexspeed")); // 特殊物理延迟时间 
				npc.setAttStatus(rs.getInt("attStatus")); // 武器状态设定 
				npc.setBowUseId(rs.getInt("bowUseId")); // 远距离物理攻击动作 
				npc.setHascastle(rs.getInt("hascastle")); // 城堡警卫设定 
				npc.setBroad(rs.getBoolean("broad")); // 怪死公告判断
				final String chatMessage = rs.getString("chat_message");
				if (chatMessage != null && !chatMessage.equals("") && !chatMessage.isEmpty()){
					npc.set_chat_message(chatMessage.split(","));
				}
				npc.set_chat_type(rs.getInt("chat_type"));
				npc.set_chat_delay_time(rs.getInt("chat_delay_time"));
				npc.set_gifd_id(rs.getInt("run_gifdId"));
				npc.set_gifd_delay_time(rs.getInt("gifd_delay_time"));
				npc.setCheckAI(rs.getBoolean("checkAi"));
				
				this.registerConstructorCache(npc.getImpl());
				_npcs.put(new Integer(npcId), npc);
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public L1Npc getTemplate(int id) {
		return _npcs.get(id);
	}
	
	/**
	 * 加载NPC执行类位置
	 * @param implName
	 */
	private void registerConstructorCache(final String implName) {
		if (implName.isEmpty() || _constructorCache.containsKey(implName)) {
			return;
		}
		_constructorCache.put(implName, this.getConstructor(implName));
	}
	
	/**
	 * 取得执行类位置
	 * @param implName
	 * @return
	 */
	private Constructor<?> getConstructor(final String implName) {
		try {
			final String implFullName = "l1j.server.server.model.Instance."
				+ implName + "Instance";
			final Constructor<?> con = Class.forName(implFullName).getConstructors()[0];
			return con;
			
		} catch (final ClassNotFoundException e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}
	
	/**
	 * 加入独立执行CLASS位置
	 * @param npcid
	 * @param className
	 * @return 
	 */
	private NpcExecutor addClass(final int npcid, final String className) {
		try {
			if (!className.equals("0")) {
				String newclass = className;
				String[] set = null;
				if (className.indexOf(" ") != -1) {
					set = className.split(" ");
					try {
						newclass = set[0];
					} catch (final Exception e) {
					}
				}
				final StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append("l1j.server.data.npc.");
				stringBuilder.append(newclass);

				final Class<?> cls = Class.forName(stringBuilder.toString());
				final NpcExecutor exe = (NpcExecutor) cls.getMethod("get").invoke(null);
				if (set != null) {
					exe.set_set(set);
				}
				return exe;
			}

		} catch (final ClassNotFoundException e) {
			String error = "发生[NPC档案]错误, 检查档案是否存在:" + className + " NpcId:" + npcid;
			_log.info(error);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}
	
	/**
	 * 依照NPCID取回新的L1NpcInstance资料
	 * @param id NPCID
	 * @return 
	 */
	public L1NpcInstance newNpcInstance(final int id) {
		try {
			final L1Npc npcTemp = this.getTemplate(id);
			if (npcTemp == null) {
				_log.info("依照NPCID取回新的L1NpcInstance资料发生异常(没有这编号的NPC): " + id);
				return null;
			}
			return this.newNpcInstance(npcTemp);
			
		} catch (final Exception e) {
			_log.info("NPCID:" + id + "/" + e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 依照NPC资料 取回新的L1NpcInstance资料
	 * @param template NPC资料
	 * @return
	 */
	public L1NpcInstance newNpcInstance(final L1Npc template) {
		try {
			if (template == null) {
				_log.info("依照NPCID取回新的L1NpcInstance资料发生异常(NPC资料为空)");
				return null;
			}
			final Constructor<?> con = _constructorCache.get(template.getImpl());
			return (L1NpcInstance) con.newInstance(new Object[] { template });
			
		} catch (final Exception e) {
			_log.info("NPCID:" + template.get_npcId() + "/" + e.getLocalizedMessage(), e);
		}
		return null;
	}

	public static Map<String, Integer> buildFamily() {
		Map<String, Integer> result = new HashMap<String, Integer>();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("select distinct(family) as family from npc WHERE NOT trim(family) =''");
			rs = pstm.executeQuery();
			int id = 1;
			while (rs.next()) {
				String family = rs.getString("family");
				result.put(family, id++);
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return result;
	}

	public int findNpcIdByName(String name) {
		int npcid = 0;
		Iterator<Integer> iter = _npcs.keySet().iterator();
		while (iter.hasNext()) {
			npcid = iter.next();
			L1Npc npc = _npcs.get(npcid);
			if (npc.get_name().equals(name)) {
				break;
			}
		}
		return npcid;
	}

	public int findNpcIdByNameWithoutSpace(String name) {
		int npcid = 0;
		Iterator<Integer> iter = _npcs.keySet().iterator();
		while (iter.hasNext()) {
			npcid = iter.next();
			L1Npc npc = _npcs.get(npcid);
			if (npc.get_name().replace(" ", "").equals(name)) {
				break;
			}
		}
		return npcid;
	}
}
