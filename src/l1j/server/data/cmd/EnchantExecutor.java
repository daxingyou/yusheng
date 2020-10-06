package l1j.server.data.cmd;

import l1j.server.Config;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.datatables.EnchantDmgReductionTable;
import l1j.server.server.datatables.LogEnchantTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_BlueMessage;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.ServerBasePacket;
import l1j.server.server.templates.L1EnchantDmgreduction;
import l1j.server.server.world.L1World;

public class EnchantExecutor {

	/**
	 * 强化失败
	 * 
	 * @param pc
	 *            执行者
	 * @param item
	 *            对象物件
	 */
	public void failureEnchant(L1PcInstance pc, L1ItemInstance item) {
		String s = "";
		String sa = "";
		int itemType = item.getItem().getType2();
		String nameId = item.getName();
		String pm = "";
		if (itemType == 1) {
			if (!item.isIdentified() || item.getEnchantLevel() == 0) {
				s = nameId;
				sa = "$245";
			} else {
				if (item.getEnchantLevel() > 0)
					pm = "+";
				s = (new StringBuilder())
						.append((new StringBuilder(String.valueOf(pm))).append(
								item.getEnchantLevel()).toString()).append(" ")
						.append(nameId).toString();
				sa = "$245";
			}
		} else if (itemType == 2)
			if (!item.isIdentified() || item.getEnchantLevel() == 0) {
				s = nameId;
				sa = " $252";
			} else {
				if (item.getEnchantLevel() > 0)
					pm = "+";
				s = (new StringBuilder())
						.append((new StringBuilder(String.valueOf(pm))).append(
								item.getEnchantLevel()).toString()).append(" ")
						.append(nameId).toString();
				sa = " $252";
			}
		if (item.getEnchantLevel() - item.getItem().get_safeenchant() >= 1) {
			ShowMessage(new S_ServerMessage(166, (new StringBuilder("\\fR【"))
					.append(pc.getName()).append("】将【").append("+")
					.append(item.getEnchantLevel()).append(item.getName())
					.append("】點爆").toString()));
		}
		String attrStr = "";
		if (item.getItem().getType2() == 1) { // 武器
			int attrEnchantLevel = item.getAttrEnchantLevel();
			if (attrEnchantLevel > 0) {
				switch (item.getAttrEnchantKind()) {
				case 1: // 地
					if (attrEnchantLevel == 1) {
						attrStr = "地之 ";
					} else if (attrEnchantLevel == 2) {
						attrStr = "崩裂 ";
					} else if (attrEnchantLevel == 3) {
						attrStr = "地灵 ";
					}
					break;
				case 2: // 火
					if (attrEnchantLevel == 1) {
						attrStr = "火之 ";
					} else if (attrEnchantLevel == 2) {
						attrStr = "烈焰 ";
					} else if (attrEnchantLevel == 3) {
						attrStr = "火灵 ";
					}
					break;
				case 4: // 水
					if (attrEnchantLevel == 1) {
						attrStr = "水之 ";
					} else if (attrEnchantLevel == 2) {
						attrStr = "海啸 ";
					} else if (attrEnchantLevel == 3) {
						attrStr = "水灵 ";
					}
					break;
				case 8: // 风
					if (attrEnchantLevel == 1) {
						attrStr = "风之 ";
					} else if (attrEnchantLevel == 2) {
						attrStr = "暴风 ";
					} else if (attrEnchantLevel == 3) {
						attrStr = "风灵 ";
					}
					break;
				default:
					break;
				}
			}
		}
		WriteLogTxt.Recording(
				"强化失败记录",
				(new StringBuilder("玩家:<")).append(pc.getName())
						.append(">PCOBJID:#").append(pc.getId())
						.append("#点爆了+").append(item.getEnchantLevel())
						.append(attrStr).append(item.getName())
						.append("itemid:(").append(item.getItemId())
						.append("),objidid:#").append(item.getId()).append("#")
						.toString());
		pc.sendPackets(new S_ServerMessage(164, s, sa));
		pc.getInventory().removeItem(item, item.getCount());
	}

	private void ShowMessage(final ServerBasePacket packet) {
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			if (!pc.isShowEnchantMessage()) {
				continue;
			}
			pc.sendPackets(packet);
		}
	}

	/**
	 * 强化成功
	 * 
	 * @param pc
	 *            执行者
	 * @param item
	 *            对象物件
	 * @param i
	 *            强化质
	 */
	public void successEnchant(L1PcInstance pc, L1ItemInstance item, int i) {
		String s = "";
		String sa = "";
		String sb = "";
		String s1 = item.getName();
		String pm = "";
		if (item.getEnchantLevel() > 0)
			pm = "+";
		if (item.getItem().getType2() == 1) {
			if (!item.isIdentified() || item.getEnchantLevel() == 0)
				switch (i) {
				case -1:
					s = s1;
					sa = "$246";
					sb = "$247";
					break;

				case 1: // '\001'
					s = s1;
					sa = "$245";
					sb = "$247";
					break;

				case 2: // '\002'
					s = s1;
					sa = "$245";
					sb = "$248";
					break;

				case 3: // '\003'
					s = s1;
					sa = "$245";
					sb = "$248";
					break;
				}
			else
				switch (i) {
				case -1:
					s = (new StringBuilder())
							.append((new StringBuilder(String.valueOf(pm)))
									.append(item.getEnchantLevel()).toString())
							.append(" ").append(s1).toString();
					sa = "$246";
					sb = "$247";
					break;

				case 1: // '\001'
					s = (new StringBuilder())
							.append((new StringBuilder(String.valueOf(pm)))
									.append(item.getEnchantLevel()).toString())
							.append(" ").append(s1).toString();
					sa = "$245";
					sb = "$247";
					break;

				case 2: // '\002'
					s = (new StringBuilder())
							.append((new StringBuilder(String.valueOf(pm)))
									.append(item.getEnchantLevel()).toString())
							.append(" ").append(s1).toString();
					sa = "$245";
					sb = "$248";
					break;

				case 3: // '\003'
					s = (new StringBuilder())
							.append((new StringBuilder(String.valueOf(pm)))
									.append(item.getEnchantLevel()).toString())
							.append(" ").append(s1).toString();
					sa = "$245";
					sb = "$248";
					break;
				}
		} else if (item.getItem().getType2() == 2)
//			if (item.getItemId() == 25063) {
//				pc.sendPackets(new S_SystemMessage("该物品不可使用此卷轴强化！"));
//				return;
//			}
		if (!item.isIdentified() || item.getEnchantLevel() == 0)
			switch (i) {
			case -1:
				s = s1;
				sa = "$246";
				sb = "$247";
				break;

			case 1: // '\001'
				s = s1;
				sa = "$252";
				sb = "$247 ";
				break;

			case 2: // '\002'
				s = s1;
				sa = "$252";
				sb = "$248 ";
				break;

			case 3: // '\003'
				s = s1;
				sa = "$252";
				sb = "$248 ";
				break;
			}
		else
			switch (i) {
			case -1:
				s = (new StringBuilder())
						.append((new StringBuilder(String.valueOf(pm))).append(
								item.getEnchantLevel()).toString()).append(" ")
						.append(s1).toString();
				sa = "$246";
				sb = "$247";
				break;

			case 1: // '\001'
				s = (new StringBuilder())
						.append((new StringBuilder(String.valueOf(pm))).append(
								item.getEnchantLevel()).toString()).append(" ")
						.append(s1).toString();
				sa = "$252";
				sb = "$247 ";
				break;

			case 2: // '\002'
				s = (new StringBuilder())
						.append((new StringBuilder(String.valueOf(pm))).append(
								item.getEnchantLevel()).toString()).append(" ")
						.append(s1).toString();
				sa = "$252";
				sb = "$248 ";
				break;

			case 3: // '\003'
				s = (new StringBuilder())
						.append((new StringBuilder(String.valueOf(pm))).append(
								item.getEnchantLevel()).toString()).append(" ")
						.append(s1).toString();
				sa = "$252";
				sb = "$248 ";
				break;
			}
		pc.sendPackets(new S_ServerMessage(161, s, sa, sb));
		int oldEnchantLvl = item.getEnchantLevel();
		int newEnchantLvl = item.getEnchantLevel() + i;
		if (oldEnchantLvl != newEnchantLvl) {
			final int safeenchant = newEnchantLvl
					- item.getItem().get_safeenchant();
			if (safeenchant >= 1) {
				L1World.getInstance().broadcastPacketToAll(
						new S_BlueMessage(166, "\\f3恭喜玩家\\f2【" + pc.getName()
								+ "】\\f3的\\f2【" + "+" + oldEnchantLvl
								+ item.getName() + "】\\f3強化成功到\\f2 "
								+ newEnchantLvl + ""));
				ShowMessage(new S_ServerMessage(166, "恭喜玩家【" + pc.getName()
						+ "】的【" + "+" + oldEnchantLvl + item.getName()
						+ "】強化成功到 " + newEnchantLvl + ""));
			}
		}
		int safe_enchant = item.getItem().get_safeenchant();
		item.setEnchantLevel(newEnchantLvl);

		if (newEnchantLvl > safe_enchant)
			pc.getInventory().saveItem(item, 4);
		if (item.getItem().getType2() == 1
				&& Config.LOGGING_WEAPON_ENCHANT != 0
				&& (safe_enchant == 0 || newEnchantLvl >= Config.LOGGING_WEAPON_ENCHANT))
			LogEnchantTable.storeLogEnchant(pc.getId(), pc.getName(),
					item.getId(), oldEnchantLvl, newEnchantLvl);
		if (item.getItem().getType2() == 2
				&& Config.LOGGING_ARMOR_ENCHANT != 0
				&& (safe_enchant == 0 || newEnchantLvl >= Config.LOGGING_ARMOR_ENCHANT))
			LogEnchantTable.storeLogEnchant(pc.getId(), pc.getName(),
					item.getId(), oldEnchantLvl, newEnchantLvl);
		WriteLogTxt.Recording(
				"强化成功记录",
				(new StringBuilder("玩家:<")).append(pc.getName())
						.append(">PCOBJID:#").append(pc.getId()).append("#把 +")
						.append(oldEnchantLvl).append(" 物品 ")
						.append(item.getName()).append("点到 +")
						.append(newEnchantLvl).append(" itemid:(")
						.append(item.getItemId()).append("),objidid:#")
						.append(item.getId()).append("#").toString());

		if (item.isEquipped()) {
			if (item.getItem().getType2() == 2) {
				final int i2 = item.getItem().getItemId();
				switch (i2) {
				case 20011:
				case 20110:
				case 120011:
					pc.addMr(i);
					pc.sendPackets(new S_SPMR(pc));
					break;
				case 20056:
				case 0x1d4f8:
				case 0x35b98:
					pc.addMr(i * 2);
					pc.sendPackets(new S_SPMR(pc));
					break;
				// 以下是蓝瓜臂甲 强化变化属性 装备着点强化后人物属性编号
				case 25063:
				case 25064:
					final int old_enchant = oldEnchantLvl - safe_enchant;
					final int new_enchant = newEnchantLvl - safe_enchant;
					// 这是减去原来强化的属性
					if (old_enchant == 1) {// +5
						pc.addDamageReduction(-2, -10);
						pc.addMaxHp(-10);
						pc.addMaxMp(-10);
						pc.addMr(-2);
					} else if (old_enchant == 2) {// +6
						pc.addDamageReduction(-3, -12);
						pc.addMaxHp(-20);
						pc.addMaxMp(-20);
						pc.addMr(-4);
					} else if (old_enchant == 3) {// +7
						pc.addDamageReduction(-4, -13);
						pc.addMaxHp(-30);
						pc.addMaxMp(-30);
						pc.addMr(-6);
					} else if (old_enchant == 4) {// +8
						pc.addDamageReduction(-5, -14);
						pc.addMaxHp(-40);
						pc.addMaxMp(-40);
						pc.addMr(-8);
					} else if (old_enchant == 5) {// +9
						pc.addDamageReduction(-8, -15);
						pc.addMaxHp(-50);
						pc.addMaxMp(-50);
						pc.addMr(-10);
					} else if (old_enchant == 6) {// +9
						pc.addDamageReduction(-8, -15);
						pc.addMaxHp(-60);
						pc.addMaxMp(-60);
						pc.addMr(-12);
					} else if (old_enchant == 7) {// +9
						pc.addDamageReduction(-8, -15);
						pc.addMaxHp(-70);
						pc.addMaxMp(-70);
						pc.addMr(-14);
					} else if (old_enchant == 8) {// +9
						pc.addDamageReduction(-8, -15);
						pc.addMaxHp(-80);
						pc.addMaxMp(-80);
						pc.addMr(-16);
					} else if (old_enchant == 9) {// +9
						pc.addDamageReduction(-8, -15);
						pc.addMaxHp(-80);
						pc.addMaxMp(-80);
						pc.addMr(-20);
					}
					// 这是加新强化后的属性
					if (new_enchant == 1) {// +5
						pc.addDamageReduction(2, 10);
						pc.addMaxHp(10);
						pc.addMaxMp(10);
						pc.addMr(2);
					} else if (new_enchant == 2) {// +6
						pc.addDamageReduction(3, 12);
						pc.addMaxHp(20);
						pc.addMaxMp(20);
						pc.addMr(4);
					} else if (new_enchant == 3) {// +7
						pc.addDamageReduction(4, 13);
						pc.addMaxHp(30);
						pc.addMaxMp(30);
						pc.addMr(6);
					} else if (new_enchant == 4) {// +8
						pc.addDamageReduction(5, 14);
						pc.addMaxHp(40);
						pc.addMaxMp(40);
						pc.addMr(8);
					} else if (new_enchant >= 5) {// +9
						pc.addDamageReduction(8, 15);
						pc.addMaxHp(50);
						pc.addMaxMp(50);
						pc.addMr(10);
					} else if (old_enchant == 6) {// +9
						pc.addDamageReduction(-8, -15);
						pc.addMaxHp(60);
						pc.addMaxMp(60);
						pc.addMr(12);
					} else if (old_enchant == 7) {// +9
						pc.addDamageReduction(-8, -15);
						pc.addMaxHp(70);
						pc.addMaxMp(70);
						pc.addMr(14);
					} else if (old_enchant == 8) {// +9
						pc.addDamageReduction(-8, -15);
						pc.addMaxHp(80);
						pc.addMaxMp(80);
						pc.addMr(16);
					} else if (old_enchant == 9) {// +9
						pc.addDamageReduction(-8, -15);
						pc.addMaxHp(80);
						pc.addMaxMp(80);
						pc.addMr(20);
					}
					// 以上是蓝瓜臂甲 强化变化属性
					break;
				default:
					break;
				}
			}
			final L1EnchantDmgreduction oldEnchant = EnchantDmgReductionTable
					.get().getEnchantDmgReduction(item.getItem().getItemId(),
							oldEnchantLvl);
			boolean updateChar = false;
			if (oldEnchant != null) {
				pc.addDamageReductionByArmor(-oldEnchant.get_dmgReduction());
				pc.addAc(-oldEnchant.get_ac());
				pc.addMaxHp(-oldEnchant.get_hp());
				pc.addMaxMp(-oldEnchant.get_mp());
				pc.addHpr(-oldEnchant.get_hpr());
				pc.addMpr(-oldEnchant.get_mpr());
				pc.addStr(-oldEnchant.get_str());
				pc.addDex(-oldEnchant.get_dex());
				pc.addInt(-oldEnchant.get_Intel());
				pc.addWis(-oldEnchant.get_wis());
				pc.addCha(-oldEnchant.get_cha());
				pc.addCon(-oldEnchant.get_con());
				pc.addSp(-oldEnchant.get_sp());
				pc.addMr(-oldEnchant.get_mr());

				updateChar = true;
			}
			final L1EnchantDmgreduction newEnchant = EnchantDmgReductionTable
					.get().getEnchantDmgReduction(item.getItem().getItemId(),
							newEnchantLvl);
			if (oldEnchant != null) {
				pc.addDamageReductionByArmor(newEnchant.get_dmgReduction());
				pc.addAc(newEnchant.get_ac());
				pc.addMaxHp(newEnchant.get_hp());
				pc.addMaxMp(newEnchant.get_mp());
				pc.addHpr(newEnchant.get_hpr());
				pc.addMpr(newEnchant.get_mpr());
				pc.addStr(newEnchant.get_str());
				pc.addDex(newEnchant.get_dex());
				pc.addInt(newEnchant.get_Intel());
				pc.addWis(newEnchant.get_wis());
				pc.addCha(newEnchant.get_cha());
				pc.addCon(newEnchant.get_con());
				pc.addSp(newEnchant.get_sp());
				pc.addMr(newEnchant.get_mr());

				updateChar = true;
			}
			if (updateChar) {
				pc.sendPackets(new S_SPMR(pc));
			}
			pc.getEquipSlot().OnChanceAc();
			pc.sendPackets(new S_OwnCharStatus(pc));
		}
		pc.getInventory().updateItem(item, 4);
	}
}
