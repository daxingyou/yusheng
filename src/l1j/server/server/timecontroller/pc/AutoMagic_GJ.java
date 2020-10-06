package l1j.server.server.timecontroller.pc;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1BuffUtil;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;

/**
 * PC 可见物更新处理 判断
 * 
 * @author dexc
 * 
 */
public class AutoMagic_GJ {

	private static final Log _log = LogFactory.getLog(AutoMagic_GJ.class);

	private static final int skillIds[] = new int[] {42 , 26 , 43, 48, 115}; // 体魄通畅加速大剑

	/**
	 * 判断
	 * 
	 * @param tgpc
	 * @return true:执行 false:不执行
	 */
	public static boolean check(final L1PcInstance _pc) {
		try {
			if (_pc == null) {
				return false;
			}
			if (_pc.getOnlineStatus() == 0) {
				return false;
			}
			if (_pc.isDead()) {
				return false;
			}

			if (_pc.isTeleport()) { // 传送中
				return false;
			}

			if (_pc.isParalyzed()) {
				return false;
			}

			 if (!_pc.isActived()) { // 非挂机状态
			 return false;
			 }
			//
//			 _pc.sendPackets(new S_SystemMessage(
//						"_pc.getTempCharGfx()" + _pc.getTempCharGfx()));
			// _pc.getTempCharGfx();
			 
			if (_pc.isActived()) {
				// _pc.sendPackets(new S_SystemMessage("13"));
				// 自动喝水
				if (_pc.getInventory().checkItem(40308, 15)) {// 具有可使用的物品
					if (_pc.getCurrentHp() < _pc.getMaxHp() / 1.08) {
						if (L1BuffUtil.stopPotion(_pc)) {
							UseHeallingPotion(_pc, 15, 189);
							_pc.getInventory().consumeItem(40308, 15);
						}
					}

				} else {
					_pc.sendPackets(new S_SystemMessage(
							"金币不足15自动喝水.本次挂机停止并且回城等待..."));
					_pc.setActived(false);
					final L1Location newLocation = new L1Location(33437, 32812,
							4).randomLocation(10, false);
					L1Teleport.teleport(_pc, newLocation.getX(),
							newLocation.getY(), (short) newLocation.getMapId(),
							5, true);
				}

				if (_pc.getInventory().checkItem(40308, 500)) {// 具有可使用的物品
					if (_pc.getBraveSpeed() == 0) {
						L1BuffUtil.braveStart(_pc);
						_pc.sendPackets(new S_SkillBrave(_pc.getId(), 1, 300));
						_pc.broadcastPacket(new S_SkillBrave(_pc.getId(), 1, 0));
						_pc.sendPacketsAll(new S_SkillSound(_pc.getId(), 751));
						_pc.setSkillEffect(L1SkillId.STATUS_BRAVE, 300 * 1000);
						_pc.setBraveSpeed(1);
						_pc.getInventory().consumeItem(40308, 500);
						_pc.sendPackets(new S_SystemMessage(
								"自动施法(二段加速)扣除500金币!"));
					}

				} else {
					_pc.sendPackets(new S_SystemMessage(
							"金币不足500二段加速.本次挂机停止并且回城等待..."));
					_pc.setActived(false);
					final L1Location newLocation = new L1Location(33437, 32812,
							4).randomLocation(10, false);
					L1Teleport.teleport(_pc, newLocation.getX(),
							newLocation.getY(), (short) newLocation.getMapId(),
							5, true);
				}

				if (_pc.getInventory().checkItem(40308, 4000)) {
					if (_pc.getSkillEffectTimeSec(115) <= 0) {
						for (final int element : skillIds) {
							int skillId = element;
							if (skillId == 148) {
								if (_pc.isElf()) {
									skillId = 149;
								}
							}
							new L1SkillUse().handleCommands(_pc, skillId,
									_pc.getId(), _pc.getX(), _pc.getY(), null,
									1800, L1SkillUse.TYPE_GMBUFF);
						}
						_pc.getInventory().consumeItem(40308, 4000);
						_pc.sendPackets(new S_SystemMessage(
								"自动施法(挂机状态)扣除4000金币!"));
					}
				} else {
					_pc.sendPackets(new S_SystemMessage(
							"金币不足4000加状态.本次挂机停止并且回城等待..."));
					_pc.setActived(false);
					final L1Location newLocation = new L1Location(33437, 32812,
							4).randomLocation(10, false);
					L1Teleport.teleport(_pc, newLocation.getX(),
							newLocation.getY(), (short) newLocation.getMapId(),
							5, true);
				}

				// 自动变身
				if (_pc.getInventory().checkItem(40308, 2000) && _pc.getWeapon() != null) {
					if (_pc.getWeapon().getItem().getType1() == 20) {
						if (_pc.getTempCharGfx() == 6140) {
							return false;
						}
						L1PolyMorph.doPoly(_pc, 6140, 1800);
						_pc.getInventory().consumeItem(40308, 2000);
						_pc.sendPackets(new S_SystemMessage(
								"自动施法(变身)扣除2000金币!"));
					}
					if (_pc.getWeapon().getItem().getType1() != 20) {
						if (_pc.getTempCharGfx() == 6137) {
							return false;
						}
						L1PolyMorph.doPoly(_pc, 6137, 1800);
						_pc.getInventory().consumeItem(40308, 2000);
						_pc.sendPackets(new S_SystemMessage(
								"自动施法(变身)扣除2000金币!"));
					}
				} else {
					_pc.sendPackets(new S_SystemMessage(
							"金币不足2000自动变身.本次挂机停止并且回城等待..."));
					_pc.setActived(false);
					final L1Location newLocation = new L1Location(33437, 32812,
							4).randomLocation(10, false);
					L1Teleport.teleport(_pc, newLocation.getX(),
							newLocation.getY(), (short) newLocation.getMapId(),
							5, true);
				}

			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			return false;
		}
		return true;
	}

	private static void UseHeallingPotion(final L1PcInstance pc, int healHp,
			final int gfxid) {
		// 解除魔法技能绝对屏障
		L1BuffUtil.cancelAbsoluteBarrier(pc);

		final Random random = new Random();

		pc.sendPacketsAll(new S_SkillSound(pc.getId(), gfxid));

		healHp *= (random.nextGaussian() / 5.0D) + 1.0D;

		if (pc.hasSkillEffect(L1SkillId.POLLUTE_WATER)) {
			healHp = (healHp >> 1);
		}
		if (healHp > 0) {
			// 你觉得舒服多了讯息
			pc.sendPackets(new S_ServerMessage(77));
		}
		pc.setCurrentHp(pc.getCurrentHp() + healHp);
	}
}

// package l1j.server.server.timecontroller.pc;
//
// import java.util.Collection;
// import java.util.Iterator;
// import java.util.Random;
// import java.util.TimerTask;
//
// import l1j.server.server.GeneralThreadPool;
// import l1j.server.server.datatables.PolyTable;
// import l1j.server.server.model.L1Location;
// import l1j.server.server.model.L1PolyMorph;
// import l1j.server.server.model.L1Teleport;
// import l1j.server.server.model.Instance.L1PcInstance;
// import l1j.server.server.model.skill.L1BuffUtil;
// import l1j.server.server.model.skill.L1SkillId;
// import l1j.server.server.model.skill.L1SkillUse;
// import l1j.server.server.serverpackets.S_ServerMessage;
// import l1j.server.server.serverpackets.S_SkillBrave;
// import l1j.server.server.serverpackets.S_SkillSound;
// import l1j.server.server.serverpackets.S_SystemMessage;
// import l1j.server.server.world.L1World;
//
// import org.apache.commons.logging.Log;
// import org.apache.commons.logging.LogFactory;
//
// /**
// * 自动挂机时状态与其他
// *
// * public class HprMprTimerCrown extends TimerTask {
// *
// */
// public class AutoMagic_GJ extends TimerTask {
//
// private static final Log _log = LogFactory.getLog(AutoMagic_GJ.class);
//
// private static final int skillIds[] = new int[] { 26, 42, 43, 48, 79, 151,
// 158, 148, 115, 117 };
//
// public void start() {
// final int timeMillis = 500;// 1秒
// GeneralThreadPool.getInstance().scheduleAtFixedRate(this, timeMillis,
// timeMillis);
// }
//
// public void run() {
// try {
// // final Collection<L1PcInstance> all = WorldElf.get().all();
// final Collection<L1PcInstance> all = L1World.getInstance()
// .getAllPlayers();
// // 不包含元素
// if (all.isEmpty()) {
// return;
// }
//
// for (final Iterator<L1PcInstance> iter = all.iterator(); iter
// .hasNext();) {
// final L1PcInstance _pc = iter.next();
//
// if (_pc == null) {
// return;
// }
// if (_pc.getOnlineStatus() == 0) {
// _pc.sendPackets(new S_SystemMessage("9"));
// return;
// }
// if (_pc.isDead()) {
// _pc.sendPackets(new S_SystemMessage("10"));
// return;
// }
//
// if (_pc.isTeleport()) { // 传送中
// return;
// }
//
// if (_pc.isParalyzed()) {
// _pc.sendPackets(new S_SystemMessage("11"));
// return;
// }
//
// // if (!_pc.isActived()) { // 非挂机状态
// // _pc.sendPackets(new S_SystemMessage("12"));
// // return;
// // }
// //
// _pc.sendPackets(new S_SystemMessage("1" + _pc.getName()));
//
// if (_pc.isActived()) {
// //_pc.sendPackets(new S_SystemMessage("13"));
// // 自动喝水
// if (_pc.getInventory().checkItem(40308, 15)) {// 具有可使用的物品
// if (_pc.getCurrentHp() < _pc.getMaxHp() / 1.08) {
// if (L1BuffUtil.stopPotion(_pc)) {
// this.UseHeallingPotion(_pc, 15, 189);
// _pc.getInventory().consumeItem(40308, 15);
// }
// }
//
// } else {
// _pc.sendPackets(new S_SystemMessage(
// "金币不足15自动喝水.本次挂机停止并且回城等待..."));
// _pc.setActived(false);
// final L1Location newLocation = new L1Location(33437,
// 32812, 4).randomLocation(10, false);
// L1Teleport.teleport(_pc, newLocation.getX(),
// newLocation.getY(),
// (short) newLocation.getMapId(), 5, true);
// }
//
// if (_pc.getInventory().checkItem(40308, 500)) {// 具有可使用的物品
// if (_pc.getBraveSpeed() == 0) {
// L1BuffUtil.braveStart(_pc);
// _pc.sendPackets(new S_SkillBrave(_pc.getId(), 1,
// 300));
// _pc.broadcastPacket(new S_SkillBrave(_pc.getId(),
// 1, 0));
// _pc.sendPacketsAll(new S_SkillSound(_pc.getId(),
// 751));
// _pc.setSkillEffect(L1SkillId.STATUS_BRAVE,
// 300 * 1000);
// _pc.setBraveSpeed(1);
// _pc.getInventory().consumeItem(40308, 500);
// }
//
// } else {
// _pc.sendPackets(new S_SystemMessage(
// "金币不足500二段加速.本次挂机停止并且回城等待..."));
// _pc.setActived(false);
// final L1Location newLocation = new L1Location(33437,
// 32812, 4).randomLocation(10, false);
// L1Teleport.teleport(_pc, newLocation.getX(),
// newLocation.getY(),
// (short) newLocation.getMapId(), 5, true);
// }
//
// if (_pc.getInventory().checkItem(40308, 4000)) {
// if (_pc.getSkillEffectTimeSec(115) <= 0) {
// for (final int element : skillIds) {
// int skillId = element;
// if (skillId == 148) {
// if (_pc.isElf()) {
// skillId = 149;
// }
// }
// new L1SkillUse().handleCommands(_pc, skillId,
// _pc.getId(), _pc.getX(), _pc.getY(),
// null, 1800, L1SkillUse.TYPE_GMBUFF);
// }
// _pc.getInventory().consumeItem(40308, 4000);
// }
// } else {
// _pc.sendPackets(new S_SystemMessage(
// "金币不足4000加状态.本次挂机停止并且回城等待..."));
// _pc.setActived(false);
// final L1Location newLocation = new L1Location(33437,
// 32812, 4).randomLocation(10, false);
// L1Teleport.teleport(_pc, newLocation.getX(),
// newLocation.getY(),
// (short) newLocation.getMapId(), 5, true);
// }
//
// // 自动变身
// if (_pc.getInventory().checkItem(40308, 2000)) {
// if (_pc.getWeapon().getItem().getType1() == 20) {
// if (_pc.getTempCharGfx() == 6140) {
// return;
// }
// L1PolyMorph.doPoly(_pc, 6140, 1800);
// _pc.getInventory().consumeItem(40308, 2000);
// }
// if (_pc.getWeapon().getItem().getType1() != 20) {
// if (_pc.getTempCharGfx() == 6137) {
// return;
// }
// L1PolyMorph.doPoly(_pc, 6137, 1800);
// _pc.getInventory().consumeItem(40308, 2000);
// }
// } else {
// _pc.sendPackets(new S_SystemMessage(
// "金币不足2000自动变身.本次挂机停止并且回城等待..."));
// _pc.setActived(false);
// final L1Location newLocation = new L1Location(33437,
// 32812, 4).randomLocation(10, false);
// L1Teleport.teleport(_pc, newLocation.getX(),
// newLocation.getY(),
// (short) newLocation.getMapId(), 5, true);
// }
//
// }
// }
// } catch (final Exception e) {
// _log.error("挂机自动设置异常重起", e);
// }
// }
//
// /**
// * PC 執行 判斷
// *
// * @param tgpc
// * @return true:執行 false:不執行
// */
// private static boolean check(final L1PcInstance tgpc) {
// try {
// // 人物為空
// if (tgpc == null) {
// return false;
// }
// if (tgpc.getWeapon() == null) {
// tgpc.sendPackets(new S_SystemMessage("未装备武器无法自动状态"));
// return false;
// }
// // 人物登出
// if (tgpc.getOnlineStatus() == 0) {
// tgpc.sendPackets(new S_SystemMessage("1"));
// return false;
// }
// // 中斷連線
// if (tgpc.getNetConnection() == null) {
// tgpc.sendPackets(new S_SystemMessage("2"));
// return false;
// }
// // 死亡狀態
// if (tgpc.isDead() == true) {
// tgpc.sendPackets(new S_SystemMessage("3"));
// return false;
// }
// // 個人商店狀態
// if (tgpc.isPrivateShop()) {
// tgpc.sendPackets(new S_SystemMessage("4"));
// return false;
// }
// // 傳送狀態
// if (tgpc.isTeleport()) {
// return false;
// }
// if ((tgpc.isParalyzed()) || (tgpc.isSleeped())) {
// tgpc.sendPackets(new S_SystemMessage("5"));
// return false;
// }
//
// // 隱身狀態而且不是隱身狀態陣列判斷中能用的魔法
// if ((tgpc.isInvisble() || tgpc.isInvisDelay())) {
// tgpc.sendPackets(new S_SystemMessage("6"));
// return false;
// }
//
// // 負重過重
// if (tgpc.getInventory().getWeight240() >= 197) {
// // \f1你攜帶太多物品，因此無法使用法術。
// tgpc.sendPackets(new S_ServerMessage(316));
// return false;
// }
//
// //tgpc.sendPackets(new S_SystemMessage("7"));
//
// // int polyId = tgpc.getTempCharGfx();
// // L1PolyMorph poly = PolyTable.getInstance().getTemplate(polyId);
// // // 特殊外型無法使用技能
// // if (poly != null && !poly.canUseSkill()) {
// // // \f1在此狀態下無法使用魔法。
// // tgpc.sendPackets(new S_ServerMessage(285));
// // return false;
// // }
//
// // 技能施放延遲狀態中使用不可
// // if (tgpc.isSkillDelay()) {
// // return false;
// // }
//
// } catch (final Exception e) {
// _log.error(e.getLocalizedMessage(), e);
// return false;
// }
// return true;
// }
//
// private void UseHeallingPotion(final L1PcInstance pc, int healHp,
// final int gfxid) {
// // 解除魔法技能绝对屏障
// L1BuffUtil.cancelAbsoluteBarrier(pc);
//
// final Random random = new Random();
//
// pc.sendPacketsAll(new S_SkillSound(pc.getId(), gfxid));
//
// healHp *= (random.nextGaussian() / 5.0D) + 1.0D;
//
// if (pc.hasSkillEffect(L1SkillId.POLLUTE_WATER)) {
// healHp = (healHp >> 1);
// }
// if (healHp > 0) {
// // 你觉得舒服多了讯息
// pc.sendPackets(new S_ServerMessage(77));
// }
// pc.setCurrentHp(pc.getCurrentHp() + healHp);
// }
// }
//
// // if (_pc.isActived()) {
// // // 自动状态
// // if (_pc.getInventory().checkItem(40308, 4000)) {
// // if (_pc.getSkillEffectTimeSec(115) <= 0) {
// // for (final int element : skillIds) {
// // int skillId = element;
// // if (skillId == 148) {
// // if (_pc.isElf()) {
// // skillId = 149;
// // }
// // }
// // new L1SkillUse().handleCommands(_pc, skillId,
// // _pc.getId(), _pc.getX(), _pc.getY(), null,
// // 1800, L1SkillUse.TYPE_GMBUFF);
// // }
// // _pc.getInventory().consumeItem(40308, 4000);
// // }
// // } else {
// // _pc.setskillAuto_gj(false);
// // _pc.sendPackets(new S_SystemMessage(
// // "金币不足4000加状态.本次挂机停止并且回城等待..."));
// // _pc.setActived(false);
// // final L1Location newLocation = new L1Location(33437, 32812,
// // 4).randomLocation(10, false);
// // L1Teleport.teleport(_pc, newLocation.getX(),
// // newLocation.getY(), (short) newLocation.getMapId(),
// // 5, true);
// // }
// //
// // // 自动变身
// // if (_pc.getInventory().checkItem(40308, 2000)) {
// // if (_pc.getWeapon().getItem().getType1() == 20) {
// // if (_pc.getTempCharGfx() == 6140) {
// // return;
// // }
// // L1PolyMorph.doPoly(_pc, 6140, 1800);
// // _pc.getInventory().consumeItem(40308, 2000);
// // }
// // if (_pc.getWeapon().getItem().getType1() != 20) {
// // if (_pc.getTempCharGfx() == 6137) {
// // return;
// // }
// // L1PolyMorph.doPoly(_pc, 6137, 1800);
// // _pc.getInventory().consumeItem(40308, 2000);
// // }
// // } else {
// // _pc.setskillAuto_gj(false);
// // _pc.sendPackets(new S_SystemMessage(
// // "金币不足2000自动变身.本次挂机停止并且回城等待..."));
// // _pc.setActived(false);
// // final L1Location newLocation = new L1Location(33437, 32812,
// // 4).randomLocation(10, false);
// // L1Teleport.teleport(_pc, newLocation.getX(),
// // newLocation.getY(), (short) newLocation.getMapId(),
// // 5, true);
// // }
// //
// // // // 自动2段加速
// // if (_pc.getInventory().checkItem(40308, 500)) {
// // if (_pc.hasSkillEffect(L1SkillId.STATUS_BRAVE)) {
// // return;
// // }
// // L1BuffUtil.braveStart(_pc);
// // _pc.sendPackets(new S_SkillBrave(_pc.getId(), 1, 300));
// // _pc.broadcastPacket(new S_SkillBrave(_pc.getId(), 1, 0));
// // _pc.sendPacketsAll(new S_SkillSound(_pc.getId(), 751));
// // _pc.setSkillEffect(L1SkillId.STATUS_BRAVE, 300 * 1000);
// // _pc.setBraveSpeed(1);
// // _pc.getInventory().consumeItem(40308, 500);
// //
// // } else {
// // _pc.setskillAuto_gj(false);
// // _pc.sendPackets(new S_SystemMessage(
// // "金币不足500自动2段加速.本次挂机停止并且回城等待..."));
// // _pc.setActived(false);
// // final L1Location newLocation = new L1Location(33437, 32812,
// // 4).randomLocation(10, false);
// // L1Teleport.teleport(_pc, newLocation.getX(),
// // newLocation.getY(), (short) newLocation.getMapId(),
// // 5, true);
// // }
// // }
// // }
// // }
//
// /**
// * 自动施法延时用
// *
// * @param pc
// * @param time
// */
// // public static void automagic(final L1PcInstance pc, final int[] skillIds)
// {
// // try {
// // GeneralThreadPool.getInstance().schedule(
// // new AutoMagicTimer(pc, skillIds), time);
// // } catch (final Exception e) {
// // _log.error(e.getLocalizedMessage(), e);
// // }
// // }
// // }
