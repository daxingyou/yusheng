/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
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

import static l1j.server.server.model.skill.L1SkillId.AWAKEN_ANTHARAS;
import static l1j.server.server.model.skill.L1SkillId.AWAKEN_FAFURION;
import static l1j.server.server.model.skill.L1SkillId.AWAKEN_VALAKAS;
import static l1j.server.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;
import static l1j.server.server.model.skill.L1SkillId.COUNTER_BARRIER;
import static l1j.server.server.model.skill.L1SkillId.CURSE_BLIND;
import static l1j.server.server.model.skill.L1SkillId.DARKNESS;
import static l1j.server.server.model.skill.L1SkillId.DECAY_POTION;
import static l1j.server.server.model.skill.L1SkillId.ENTANGLE;
import static l1j.server.server.model.skill.L1SkillId.GREATER_HASTE;
import static l1j.server.server.model.skill.L1SkillId.HASTE;
import static l1j.server.server.model.skill.L1SkillId.HOLY_WALK;
import static l1j.server.server.model.skill.L1SkillId.MASS_SLOW;
import static l1j.server.server.model.skill.L1SkillId.MASS_TELEPORT;
import static l1j.server.server.model.skill.L1SkillId.MOVING_ACCELERATION;
import static l1j.server.server.model.skill.L1SkillId.POLLUTE_WATER;
import static l1j.server.server.model.skill.L1SkillId.SHAPE_CHANGE;
import static l1j.server.server.model.skill.L1SkillId.SLOW;
import static l1j.server.server.model.skill.L1SkillId.STATUS_BLUE_POTION;
import static l1j.server.server.model.skill.L1SkillId.STATUS_BRAVE;
import static l1j.server.server.model.skill.L1SkillId.STATUS_DEX_POISON;
import static l1j.server.server.model.skill.L1SkillId.STATUS_ELFBRAVE;
import static l1j.server.server.model.skill.L1SkillId.STATUS_FLOATING_EYE;
import static l1j.server.server.model.skill.L1SkillId.STATUS_HASTE;
import static l1j.server.server.model.skill.L1SkillId.STATUS_STR_POISON;
import static l1j.server.server.model.skill.L1SkillId.STATUS_UNDERWATER_BREATH;
import static l1j.server.server.model.skill.L1SkillId.STATUS_WISDOM_POTION;
import static l1j.server.server.model.skill.L1SkillId.TELEPORT;
import static l1j.server.server.model.skill.L1SkillId.WIND_WALK;
import static l1j.william.New_Id.Armor_AJ_1_1;
import static l1j.william.New_Id.Armor_AJ_1_2;
import static l1j.william.New_Id.Item_AJ_16;
import static l1j.william.New_Id.Item_AJ_17;
import static l1j.william.New_Id.Item_AJ_19;
import static l1j.william.New_Id.Item_AJ_20;
import static l1j.william.New_Id.Item_AJ_21;
import static l1j.william.New_Id.Item_AJ_25;
import static l1j.william.New_Id.Item_AJ_26;
import static l1j.william.New_Id.Item_AJ_28;
import static l1j.william.New_Id.Item_AJ_29;
import static l1j.william.New_Id.Item_AJ_2_1;
import static l1j.william.New_Id.Item_AJ_2_2;
import static l1j.william.New_Id.Item_AJ_30;
import static l1j.william.New_Id.Item_AJ_31;
import static l1j.william.New_Id.Item_EPU_1;
import static l1j.william.New_Id.Item_EPU_10;
import static l1j.william.New_Id.Item_EPU_101;
import static l1j.william.New_Id.Item_EPU_102;
import static l1j.william.New_Id.Item_EPU_103;
import static l1j.william.New_Id.Item_EPU_109;
import static l1j.william.New_Id.Item_EPU_110;
import static l1j.william.New_Id.Item_EPU_111;
import static l1j.william.New_Id.Item_EPU_17;
import static l1j.william.New_Id.Item_EPU_18;
import static l1j.william.New_Id.Item_EPU_19;
import static l1j.william.New_Id.Item_EPU_2;
import static l1j.william.New_Id.Item_EPU_21;
import static l1j.william.New_Id.Item_EPU_23;
import static l1j.william.New_Id.Item_EPU_24;
import static l1j.william.New_Id.Item_EPU_25;
import static l1j.william.New_Id.Item_EPU_26;
import static l1j.william.New_Id.Item_EPU_27;
import static l1j.william.New_Id.Item_EPU_28;
import static l1j.william.New_Id.Item_EPU_29;
import static l1j.william.New_Id.Item_EPU_30;
import static l1j.william.New_Id.Item_EPU_31;
import static l1j.william.New_Id.Item_EPU_32;
import static l1j.william.New_Id.Item_EPU_33;
import static l1j.william.New_Id.Item_EPU_34;
import static l1j.william.New_Id.Item_EPU_35;
import static l1j.william.New_Id.Item_EPU_36;
import static l1j.william.New_Id.Item_EPU_37;
import static l1j.william.New_Id.Item_EPU_38;
import static l1j.william.New_Id.Item_EPU_39;
import static l1j.william.New_Id.Item_EPU_40;
import static l1j.william.New_Id.Item_EPU_41;
import static l1j.william.New_Id.Item_EPU_42;
import static l1j.william.New_Id.Item_EPU_43;
import static l1j.william.New_Id.Item_EPU_44;
import static l1j.william.New_Id.Item_EPU_45;
import static l1j.william.New_Id.Item_EPU_46;
import static l1j.william.New_Id.Item_EPU_47;
import static l1j.william.New_Id.Item_EPU_63;
import static l1j.william.New_Id.Item_EPU_66;
import static l1j.william.New_Id.Item_EPU_7;
import static l1j.william.New_Id.Item_EPU_70;
import static l1j.william.New_Id.Item_EPU_71;
import static l1j.william.New_Id.Item_EPU_72;
import static l1j.william.New_Id.Item_EPU_73;
import static l1j.william.New_Id.Item_EPU_84;
import static l1j.william.New_Id.Npc_AJ_2_15;
import static l1j.william.New_Id.Npc_AJ_2_16;
import static l1j.william.New_Id.Npc_AJ_2_17;
import static l1j.william.New_Id.Npc_AJ_2_18;
import static l1j.william.New_Id.Npc_AJ_2_19;
import static l1j.william.New_Id.Npc_AJ_2_20;
import static l1j.william.New_Id.Npc_AJ_2_21;
import static l1j.william.New_Id.Skill_AJ_0_10;
import static l1j.william.New_Id.Skill_AJ_0_5;
import static l1j.william.New_Id.Skill_AJ_0_7;
import static l1j.william.New_Id.Skill_AJ_0_8;
import static l1j.william.New_Id.Skill_AJ_0_9;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TimeZone;

import l1j.server.AcceleratorChecker;
import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.data.ItemClass;
import l1j.server.data.cmd.EnchantExecutor;
import l1j.server.server.ActionCodes;
import l1j.server.server.FishingTimeController;
import l1j.server.server.IdFactory;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.FailureEnchantTable;
import l1j.server.server.datatables.FurnitureSpawnTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.LetterTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.datatables.ServerBlessEnchantTable;
import l1j.server.server.datatables.ServerFailureEnchantTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.Getback;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Cooking;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.L1HouseLocation;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1ItemDelay;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.Instance.L1CastleGuardInstance; // 守城警卫 
import l1j.server.server.model.Instance.L1DoorInstance; // 门 
import l1j.server.server.model.Instance.L1EffectInstance;
import l1j.server.server.model.Instance.L1FurnitureInstance;
import l1j.server.server.model.Instance.L1GuardianInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1TowerInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.item.L1TreasureBox;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_CurseBlind;
import l1j.server.server.serverpackets.S_Dexup;//能力药水 
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_Fishing;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_IdentifyDesc;
import l1j.server.server.serverpackets.S_ItemName;
import l1j.server.server.serverpackets.S_Letter;
import l1j.server.server.serverpackets.S_Light;
import l1j.server.server.serverpackets.S_Liquor;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_OwnCharStatus2;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_ShowPolyList;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconBlessOfEva;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_Sound;
import l1j.server.server.serverpackets.S_Strup;//激励药水 
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.storage.CharactersItemStorage;
import l1j.server.server.templates.L1Armor;
import l1j.server.server.templates.L1EtcItem;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1Pet;
import l1j.server.server.templates.L1ServerBlessEnchant;
import l1j.server.server.templates.L1ServerFailureEnchant;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.utils.L1SpawnUtil;
import l1j.server.server.utils.SQLUtil;
import l1j.server.server.world.L1World;
import l1j.william.ArmorUpgrade;
import l1j.william.ItemMagic;
import l1j.william.L1WilliamArmorUpgrade;
import l1j.william.L1WilliamItemMagic;
import l1j.william.L1WilliamItemSummon;
import l1j.william.L1WilliamMagicCrystalItem;
import l1j.william.L1WilliamSystemMessage;
import l1j.william.L1WilliamTeleportScroll;
import l1j.william.MagicCrystalItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
// 魔法娃娃 

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket
//
public class C_ItemUSe extends ClientBasePacket {

	private static final String C_ITEM_USE = "[C] C_ItemUSe";
	private static final Log _log = LogFactory.getLog(C_ItemUSe.class);

	private static Random _random = new Random();

	public C_ItemUSe(byte abyte0[], LineageClient _client) throws Exception {
		super(abyte0);
		int itemObjid = readD();

		L1PcInstance pc = _client.getActiveChar();
		if (pc.isGhost()) {
			return;
		}
		if (pc.isDead() == true) {
			return;
		}
		if (pc.isStop()) {
			return;
		}
		if (pc.isPrivateShop()) {
			return;
		}
		L1ItemInstance useItem = pc.getInventory().getItem(itemObjid);
		if (useItem == null) {
			return;
		}
		// System.out.println("步骤1");

		if (useItem.getItem().getUseType() == -1) { // none:使用
			pc.sendPackets(new S_ServerMessage(74, useItem.getLogName())); // \f1%0使用。
			return;
		}
		int pcObjid = pc.getId();
		if (pc.isTeleport()) { // 处理中
			return;
		}

		int itemId;
		try {
			itemId = useItem.getItem().getItemId();
		} catch (Exception e) {
			return;
		}
		int l = 0;

		String s = "";
		// int bmapid = 0;

		int blanksc_skillid = 0;
		int spellsc_objid = 0;
		int spellsc_x = 0;
		int spellsc_y = 0;
		int resid = 0;
		int letterCode = 0;
		String letterReceiver = "";
		byte[] letterText = null;
		int cookLevel = 0;
		int cookNo = 0;
		int fishX = 0;
		int fishY = 0;

		int use_type = useItem.getItem().getUseType();
		// System.out.println("use: "+use_type);

		if (pc.getCurrentHp() > 0) {
			int delay_id = 0;
			if (useItem.getItem().getType2() == 0) { // 种别：他
				delay_id = ((L1EtcItem) useItem.getItem()).get_delayid();
			}
			if (delay_id != 0) { // 设定
				if (pc.hasItemDelay(delay_id) == true) {
					return;
				}
			}

			// 再使用
			boolean isDelayEffect = false;
			if (useItem.getItem().getType2() == 0) {
				int item_minlvl = ((L1EtcItem) useItem.getItem()).getMinLevel();
				int item_maxlvl = ((L1EtcItem) useItem.getItem()).getMaxLevel();
				if (item_minlvl != 0 && item_minlvl > pc.getLevel()
						&& !pc.isGm()) {
					pc.sendPackets(new S_ServerMessage(318, String
							.valueOf(item_minlvl))); // %0以上使用。
					return;
				} else if (item_maxlvl != 0 && item_maxlvl < pc.getLevel()
						&& !pc.isGm()) {
					pc.sendPackets(new S_SystemMessage("等级" + item_maxlvl
							+ "以下才能使用此道具")); // %d以上使用。
					return;
				}
				int delayEffect = ((L1EtcItem) useItem.getItem())
						.get_delayEffect();
				if (delayEffect > 0) {
					// isDelayEffect = true;
					Timestamp lastUsed = useItem.getLastUsed();
					if (lastUsed != null) {
						Calendar cal = Calendar.getInstance();
						final long next_time = delayEffect
								- (cal.getTimeInMillis() - lastUsed.getTime())
								/ 1000;
						if (next_time > 0) {
							// \f1何起。
							pc.sendPackets(new S_ServerMessage(3898, String
									.valueOf(next_time)));
							return;
						}
					}
				}
			}
			if (pc.isCheckFZ()) {
				WriteLogTxt.Recording(
						pc.getName() + "使用",
						"变身ID" + pc.getTempCharGfx() + "使用物品 "
								+ useItem.getLogViewName());
			}
			// System.out.println("步骤2");
			boolean isClass = false;// 是否具有CLASS
			final String className = useItem.getItem().getclassname();// 独立执行项位置
			if (!className.equals("0")) {
				isClass = true;
			}
			// System.out.println("classname: "+className);
			// System.out.println("步骤3");
			if (isClass) {
				// 取得物件触发事件判断
				switch (use_type) {
				case -11:// 对读取方法调用无法分类的物品
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case -10:// 加速药水
					/*
					 * if (!CheckUtil.getUseItem(pc)) { return; }
					 */
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case -9:// 技术书
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case -8:// 料理书
					if (isClass) {
						try {
							final int[] newData = new int[2];
							newData[0] = this.readC();
							newData[1] = this.readC();
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case -7:// 增HP道具
					/*
					 * if (!CheckUtil.getUseItem(pc)) { return; }
					 */
					// System.out.println("步骤1");
					if (isClass) {
						// System.out.println("步骤2");
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case -6:// 增MP道具
					/*
					 * if (!CheckUtil.getUseItem(pc)) { return; }
					 */
					// System.out.println("步骤3");
					if (isClass) {
						// System.out.println("步骤4");
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case -4:// 项圈
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case -3:// 飞刀
					pc.getInventory().setSting(useItem.getItemId());
					// 452 %0%s 被选择了。
					pc.sendPackets(new S_ServerMessage(452, useItem
							.getLogName()));
					break;

				case -2:// 箭
					pc.getInventory().setArrow(useItem.getItemId());
					// 452 %0%s 被选择了。
					pc.sendPackets(new S_ServerMessage(452, useItem
							.getLogName()));
					break;

				case -12:// 宠物用具
				case -5:// 食人妖精竞赛票 / 死亡竞赛票
				case -1:// 无法使用
					// 无法使用讯息
					pc.sendPackets(new S_ServerMessage(74, useItem.getLogName()));
					break;

				case 0:// 一般物品(直接施放)
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case 1:// 武器
						// 武器禁止使用
					/*
					 * if (pc.hasItemDelay(L1ItemDelay.WEAPON) == true) {
					 * return; }
					 */
					if (this.useItem(pc, useItem)) {
						this.UseWeapon(pc, useItem);
					}
					break;

				case 2:// 盔甲
				case 18:// T恤
				case 19:// 斗篷
				case 20:// 手套
				case 21:// 靴
				case 22:// 头盔
				case 23:// 戒指
				case 24:// 项链
				case 25:// 盾牌
				case 37:// 腰带
				case 40:// 耳环
				case 43:// 副助道具
				case 44:// 副助道具
				case 45:// 副助道具
				case 48:// 副助道具
				case 47:// 副助道具
					// 防具禁止使用
					/*
					 * if (pc.hasItemDelay(L1ItemDelay.ARMOR) == true) { return;
					 * }
					 */
					if (this.useItem(pc, useItem)) {
						this.UseArmor(pc, useItem);
					}
					break;

				case 3:// 创造怪物魔杖(无须选取目标) (无数量:没有任何事情发生)
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case 4:// 希望魔杖(无须选取目标)(有数量:你想要什么 / 无数量:没有任何事情发生)
					break;

				case 5:// 魔杖类型(须选取目标)
					if (isClass) {
						try {
							final int[] newData = new int[3];
							newData[0] = this.readD();// 选取目标的OBJID
							newData[1] = this.readH();// X座标
							newData[2] = this.readH();// Y座标
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 6:// 瞬间移动卷轴
				case 29:// 祝福瞬间移动卷轴
					if (isClass) {
						try {
							final int[] newData = new int[3];
							newData[0] = this.readH();
							newData[1] = this.readH();
							newData[2] = this.readH();
							ItemClass.get().item(newData, pc, useItem);
							pc.sendPackets(new S_Paralysis(
									S_Paralysis.TYPE_TELEPORT_UNLOCK, 0, false));

						} catch (final Exception e) {
							return;
						}
					}
					break;
				case 7:// 鉴定卷轴
					if (isClass) {
						try {
							final int[] newData = new int[1];
							newData[0] = this.readD();// 选取物件的OBJID
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 8:// 复活卷轴
					if (isClass) {
						try {
							final int[] newData = new int[1];
							newData[0] = this.readD();// 选取目标的OBJID
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 9:// 传送回家的卷轴 / 血盟传送卷轴
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case 10:// 照明道具
					// 取得道具编号
					/*
					 * if ((useItem.getRemainingTime() <= 0) &&
					 * (useItem.getItemId() != 40004)) { return; }
					 */
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case 14:// 请选择一个物品(道具栏位) 灯油/磨刀石/胶水
					if (isClass) {
						try {
							final int[] newData = new int[1];
							newData[0] = this.readD();// 选取物件的OBJID
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 15:// 哨子
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case 16:// 变形卷轴
					if (isClass) {
						@SuppressWarnings("unused")
						final String cmd = this.readS();
						// /pc.setText(cmd);// 选取的变身命令
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case 17:// 选取目标 (近距离)
					if (isClass) {
						try {
							final int[] newData = new int[3];
							newData[0] = this.readD();// 选取目标的OBJID
							newData[1] = this.readH();// X座标
							newData[2] = this.readH();// Y座标
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 27:// 对盔甲施法的卷轴
				case 26:// 对武器施法的卷轴
				case 46:// 饰品强化卷轴
					// System.out.println("步骤4");
					if (isClass) {
						// System.out.println("步骤5");
						try {
							final int[] newData = new int[1];
							// 选取目标的OBJID
							newData[0] = this.readD();
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 28:// 空的魔法卷轴
					if (isClass) {
						try {
							final int[] newData = new int[1];
							newData[0] = this.readC();
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;
				case 30:// 选取目标 (Ctrl 远距离)
					if (isClass) {
						try {
							final int obj = this.readD();// 选取目标的OBJID
							final int[] newData = new int[] { obj };
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 12:// 信纸
				case 31:// 圣诞卡片
				case 33:// 情人节卡片
				case 35:// 白色情人节卡片
					if (isClass) {
						try {
							final int[] newData = new int[1];
							newData[0] = this.readH();
							this.readS();
							this.readByte();
							// pc.setText(this.readS());
							// pc.setTextByte(this.readByte());
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 13:// 信纸(打开)
				case 32:// 圣诞卡片(打开)
				case 34:// 情人节卡片(打开)
				case 36:// 白色情人节卡片(打开)
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case 38:// 食物
					/*
					 * if (!CheckUtil.getUseItem(pc)) { return; }
					 */
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case 39:// 选取目标 (远距离)
					if (isClass) {
						try {
							final int[] newData = new int[3];
							newData[0] = this.readD();// 选取目标的OBJID
							newData[1] = this.readH();// X座标
							newData[2] = this.readH();// Y座标
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 42:// 钓鱼杆
					if (isClass) {
						try {
							final int[] newData = new int[3];
							newData[0] = this.readH();// X座标
							newData[1] = this.readH();// Y座标
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 55:// 魔法娃娃成长药剂
					if (isClass) {
						try {
							final int[] newData = new int[1];
							newData[0] = this.readD();// 选取物件的OBJID
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				default:// 测试
					_log.info("未处理的物品分类: " + use_type);
					break;
				}
				return;
			} else {
				switch (itemId)// 改写成switch 1
				{
				case 40088:
				case 40096:
				case 140088:
				case 40410: { // 修正40410
					s = readS();
				}
					break;
				case L1ItemId.SCROLL_OF_ENCHANT_ARMOR:
				case L1ItemId.SCROLL_OF_ENCHANT_WEAPON:
				case L1ItemId.SCROLL_OF_ENCHANT_QUEST_WEAPON:
				case 40077:
				case 40078:
				case 40126:
					// 补充
				case 40009:
				case 40870:
				case 40879:
					// 补充
				case 40098:
				case 40129:
				case 40130:
				case 140129:
				case 140130:
				case L1ItemId.B_SCROLL_OF_ENCHANT_ARMOR:
				case L1ItemId.B_SCROLL_OF_ENCHANT_WEAPON:
				case L1ItemId.C_SCROLL_OF_ENCHANT_ARMOR:
				case L1ItemId.C_SCROLL_OF_ENCHANT_WEAPON:
				case 40317:
				case 41036:
				case Item_AJ_25:
				case Item_AJ_26: // 装备鉴定卷轴
				case Item_EPU_1: { // 溶解剂
					l = readD();
				}
					break;
				case 40090:
				case 40091:
				case 40092:
				case 40093:
				case 40094: {
					blanksc_skillid = readC();
				}
					break;
				case 40089:
				case 140089: {
					resid = readD();
				}
					break;
				case 40310:
				case 40311:
				case 40730:
				case 40731:
				case 40732: {
					letterCode = readH();
					letterReceiver = readS();
					letterText = readByte();
				}
					break;
				case 41255:
				case 41256:
				case 41257:
				case 41258:
				case 41259: { // 料理本
					cookLevel = readC();
					cookNo = readC();
				}
					break;
				case 41293:
				case 41294: { // 钓竿
					fishX = readH();
					fishY = readH();
				}
					break;
				default: {
					if (use_type == 30) { // spell_buff
						spellsc_objid = readD();
					} else if (use_type == 5 || use_type == 17) { // spell_long
																	// ,
						// spell_short
						spellsc_objid = readD();
						spellsc_x = readH();
						spellsc_y = readH();
					} else {
						l = readC();
					}
				}
					break;
				}// 改写成switch 1 end
			}

			L1ItemInstance l1iteminstance1 = pc.getInventory().getItem(l);
			/*
			 * _log.info("request item use (obj) = " + itemObjid + " action = "
			 * + l + " value = " + s);
			 */
			if (itemId == 40077 || itemId == L1ItemId.SCROLL_OF_ENCHANT_WEAPON
					|| itemId == L1ItemId.SCROLL_OF_ENCHANT_QUEST_WEAPON
					|| itemId == 40130 || itemId == 140130
					|| itemId == L1ItemId.B_SCROLL_OF_ENCHANT_WEAPON
					|| itemId == L1ItemId.C_SCROLL_OF_ENCHANT_WEAPON) { // 武器强化
				if (l1iteminstance1 == null
						|| l1iteminstance1.getItem().getType2() != 1) {
					pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
					return;
				}

				int safe_enchant = l1iteminstance1.getItem().get_safeenchant();
				if (safe_enchant < 0) { // 强化不可
					pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
					return;
				}
				if (l1iteminstance1.isSeal()) {
					pc.sendPackets(new S_SystemMessage(l1iteminstance1
							.getLogViewName() + "处于封印状态！"));
					return;
				}
				int quest_weapon = l1iteminstance1.getItem().getItemId();
				if (quest_weapon >= 246 && quest_weapon <= 249) { // 强化不可
					if (itemId == L1ItemId.SCROLL_OF_ENCHANT_QUEST_WEAPON) {
						// 试练
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
						return;
					}
				}

				if (itemId == L1ItemId.SCROLL_OF_ENCHANT_QUEST_WEAPON) {
					// 试练
					if (quest_weapon >= 246 && quest_weapon <= 249) { // 强化不可
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
						return;
					}
				}

				int enchant_level = l1iteminstance1.getEnchantLevel();

				if (itemId == L1ItemId.C_SCROLL_OF_ENCHANT_WEAPON) { // c-dai
					pc.getInventory().removeItem(useItem, 1);
					if (enchant_level < -6) {
						// -7以上。
						FailureEnchant(pc, l1iteminstance1, _client);
					} else {
						SuccessEnchant(pc, l1iteminstance1, _client, -1);
					}
				} else if (enchant_level < safe_enchant) {
					pc.getInventory().removeItem(useItem, 1);
					SuccessEnchant(pc, l1iteminstance1, _client,
							RandomELevel(l1iteminstance1, itemId, pc));
				} else {
					pc.getInventory().removeItem(useItem, 1);
					if (enchant_level >= Config.WEAPON_MAXENCHANTLEVEL) {
						FailureEnchant(pc, l1iteminstance1, _client);
						return;
					}
					final int targItemId = l1iteminstance1.getItem()
							.getItemId();
					int failureEnchantCount = 0;
					final L1ServerFailureEnchant serverFailureEnchant = ServerFailureEnchantTable
							.get().getItem(targItemId, enchant_level);
					if (serverFailureEnchant != null) {
						failureEnchantCount = FailureEnchantTable.get()
								.getFailureCount(pc.getId(), targItemId,
										enchant_level);
						// 失败次数小于3 100%失败
						if (failureEnchantCount < serverFailureEnchant
								.get_minFailureCount()) {
							FailureEnchant(pc, l1iteminstance1, _client);
							// 更新该人物的指定道具强化失败次数
							failureEnchantCount++;
							FailureEnchantTable.get().updateFailureCount(
									pc.getId(), targItemId, enchant_level,
									failureEnchantCount);
							return;
						}
						// 失败大于等于10 100%成功
						if (failureEnchantCount >= serverFailureEnchant
								.get_maxFailureCount()) {
							SuccessEnchant(pc, l1iteminstance1, _client, 1);
							// 更新该人物的指定道具强化失败次数
							FailureEnchantTable.get().updateFailureCount(
									pc.getId(), targItemId, enchant_level, 0);
							return;
						}
					}
					int rnd = _random.nextInt(100) + 1;
					int enchant_chance_wepon;
					int enchant_level_tmp;
					/*
					 * if (enchant_level >= 9) { enchant_chance_wepon = (100 + 3
					 * * Config.ENCHANT_CHANCE_WEAPON) / 6; } else {
					 * enchant_chance_wepon = (100 + 3 *
					 * Config.ENCHANT_CHANCE_WEAPON) / 3; }
					 */
					int sum = enchant_level - safe_enchant;
					if (safe_enchant == 0 || sum >= 2) { // 骨、用补正
						enchant_level_tmp = enchant_level + 2;
					} else {
						enchant_level_tmp = enchant_level;
					}

					if (enchant_level >= safe_enchant) {
						enchant_chance_wepon = (100 + 3 * Config.ENCHANT_CHANCE_WEAPON)
								/ enchant_level_tmp;
					} else {
						enchant_chance_wepon = (100 + 3 * Config.ENCHANT_CHANCE_WEAPON) / 3;
					}

					if (rnd < enchant_chance_wepon) {
						int randomEnchantLevel = RandomELevel(l1iteminstance1,
								itemId, pc);
						SuccessEnchant(pc, l1iteminstance1, _client,
								randomEnchantLevel);
						if (serverFailureEnchant != null) {
							FailureEnchantTable.get().updateFailureCount(
									pc.getId(), targItemId, enchant_level, 0);
						}
					} else if (enchant_level >= 9
							&& rnd < (enchant_chance_wepon * 2)) {
						// \f1%0%2强烈%1光、幸无事。
						pc.sendPackets(new S_ServerMessage(160, l1iteminstance1
								.getLogName(), "$245", "$248"));
					} else {
						FailureEnchant(pc, l1iteminstance1, _client);
						if (serverFailureEnchant != null) {
							// 更新该人物的指定道具强化失败次数
							failureEnchantCount++;
							FailureEnchantTable.get().updateFailureCount(
									pc.getId(), targItemId, enchant_level,
									failureEnchantCount);
						}
					}
				}
			} else if (itemId == 40078
					|| itemId == L1ItemId.SCROLL_OF_ENCHANT_ARMOR
					|| itemId == 40129 || itemId == 140129
					|| itemId == L1ItemId.B_SCROLL_OF_ENCHANT_ARMOR
					|| itemId == L1ItemId.C_SCROLL_OF_ENCHANT_ARMOR) { // 防具强化
				if (l1iteminstance1 == null
						|| l1iteminstance1.getItem().getType2() != 2) {
					pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
					return;
				}

				if (l1iteminstance1.isSeal()) {
					pc.sendPackets(new S_SystemMessage(l1iteminstance1
							.getLogViewName() + "处于封印状态！"));
					return;
				}

				int safe_enchant = ((L1Armor) l1iteminstance1.getItem())
						.get_safeenchant();
				if (safe_enchant < 0) { // 强化不可
					pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
					return;
				}

				int enchant_level = l1iteminstance1.getEnchantLevel();
				if (itemId == L1ItemId.C_SCROLL_OF_ENCHANT_ARMOR) { // c-zel
					pc.getInventory().removeItem(useItem, 1);
					if (enchant_level < -6) {
						// -7以上。
						FailureEnchant(pc, l1iteminstance1, _client);
					} else {
						SuccessEnchant(pc, l1iteminstance1, _client, -1);
					}
				} else if (enchant_level < safe_enchant) {
					pc.getInventory().removeItem(useItem, 1);
					SuccessEnchant(pc, l1iteminstance1, _client,
							RandomELevel(l1iteminstance1, itemId, pc));
				} else {
					pc.getInventory().removeItem(useItem, 1);
					if (enchant_level >= 9) {
						FailureEnchant(pc, l1iteminstance1, _client);
						return;
					}
					final int targArormItemId = l1iteminstance1.getItem()
							.getItemId();
					int failureEnchantArormCount = 0;
					final L1ServerFailureEnchant serverArormFailureEnchant = ServerFailureEnchantTable
							.get().getItem(targArormItemId, enchant_level);
					if (serverArormFailureEnchant != null) {
						failureEnchantArormCount = FailureEnchantTable.get()
								.getFailureCount(pc.getId(), targArormItemId,
										enchant_level);
						// 失败次数小于3 100%失败
						if (failureEnchantArormCount < serverArormFailureEnchant
								.get_minFailureCount()) {
							FailureEnchant(pc, l1iteminstance1, _client);
							// 更新该人物的指定道具强化失败次数
							failureEnchantArormCount++;
							FailureEnchantTable.get().updateFailureCount(
									pc.getId(), targArormItemId, enchant_level,
									failureEnchantArormCount);
							return;
						}
						// 失败大于等于10 100%成功
						if (failureEnchantArormCount >= serverArormFailureEnchant
								.get_maxFailureCount()) {
							SuccessEnchant(pc, l1iteminstance1, _client, 1);
							// 更新该人物的指定道具强化失败次数
							FailureEnchantTable.get().updateFailureCount(
									pc.getId(), targArormItemId, enchant_level,
									0);
							return;
						}
					}
					int rnd = _random.nextInt(100) + 1;
					int enchant_chance_armor;
					int enchant_level_tmp;
					int sum = enchant_level - safe_enchant;
					if (safe_enchant == 0 || sum >= 2) { // 骨、用补正
						enchant_level_tmp = enchant_level + 2;
					} else {
						enchant_level_tmp = enchant_level;
					}
					if (enchant_level >= safe_enchant) {
						enchant_chance_armor = (100 + enchant_level_tmp
								* Config.ENCHANT_CHANCE_ARMOR)
								/ (enchant_level_tmp * 2);
					} else {
						enchant_chance_armor = (100 + enchant_level_tmp
								* Config.ENCHANT_CHANCE_ARMOR)
								/ enchant_level_tmp;
					}

					if (rnd < enchant_chance_armor) {
						int randomEnchantLevel = RandomELevel(l1iteminstance1,
								itemId, pc);
						SuccessEnchant(pc, l1iteminstance1, _client,
								randomEnchantLevel);
						if (serverArormFailureEnchant != null) {
							// 更新该人物的指定道具强化失败次数
							FailureEnchantTable.get().updateFailureCount(
									pc.getId(), targArormItemId, enchant_level,
									0);
						}
					} else if (enchant_level >= 9
							&& rnd < (enchant_chance_armor * 2)) {
						String item_name_id = l1iteminstance1.getName();
						String pm = "";
						String msg = "";
						if (enchant_level > 0) {
							pm = "+";
						}
						msg = (new StringBuilder()).append(pm + enchant_level)
								.append(" ").append(item_name_id).toString();
						// \f1%0%2强烈%1光、幸无事。
						pc.sendPackets(new S_ServerMessage(160, msg, "$252",
								"$248"));
					} else {
						FailureEnchant(pc, l1iteminstance1, _client);
						if (serverArormFailureEnchant != null) {
							// 更新该人物的指定道具强化失败次数
							failureEnchantArormCount++;
							FailureEnchantTable.get().updateFailureCount(
									pc.getId(), targArormItemId, enchant_level,
									failureEnchantArormCount);
						}
					}
				}
			} else if (useItem.getItem().getType2() == 0) { // 种别：他
				int item_minlvl = ((L1EtcItem) useItem.getItem()).getMinLevel();
				int item_maxlvl = ((L1EtcItem) useItem.getItem()).getMaxLevel();
				if (item_minlvl != 0 && item_minlvl > pc.getLevel()
						&& !pc.isGm()) {
					pc.sendPackets(new S_ServerMessage(318, String
							.valueOf(item_minlvl))); // %0以上使用。
					return;
				} else if (item_maxlvl != 0 && item_maxlvl < pc.getLevel()
						&& !pc.isGm()) {
					pc.sendPackets(new S_ServerMessage(673, String
							.valueOf(item_maxlvl))); // %d以上使用。
					return;
				}

				if ((itemId == 40576 && !pc.isElf()) // 魂结晶破片（白）
						|| (itemId == 40577 && !pc.isWizard()) // 魂结晶破片（黑）
						|| (itemId == 40578 && !pc.isKnight())) { // 魂结晶破片（赤）
					pc.sendPackets(new S_ServerMessage(264)); // \f1使用。
					return;
				}

				if (useItem.getItem().getType() == 0) { // 
					pc.getInventory().setArrow(useItem.getItem().getItemId());
					pc.sendPackets(new S_ServerMessage(452, useItem
							.getLogName())); // %0选择。
				} else if (useItem.getItem().getType() == 15) { // 
					pc.getInventory().setSting(useItem.getItem().getItemId());
					pc.sendPackets(new S_ServerMessage(452, // %0选择。
							useItem.getLogName()));
				} else if (useItem.getItem().getType() == 16) { // treasure_box
					L1TreasureBox box = L1TreasureBox.get(itemId);

					if (box != null) {
						if (box.open(pc, useItem)) {
							L1EtcItem temp = (L1EtcItem) useItem.getItem();
							if (temp.get_delayEffect() > 0) {
								isDelayEffect = true;
							} else {
								pc.getInventory()
										.removeItem(useItem.getId(), 1);
							}
						}
					}
				} else
					// 改写成switch 2
					switch (itemId) {
					case 40001:
					case 40002:
					case 40005: {// 灯
						if (useItem.getEnchantLevel() != 0) { // 关灯
							pc.setPcLight(0);
							灯(pc, useItem); // 切换为关闭
						} else if (useItem.getChargeCount() > 0) { // 开灯
							pc.setPcLight(useItem.getItem().getLightRange());
							灯(pc, useItem); // 切换为打开
						}
					}
						break;
					case 40003: {// 灯油
						for (Object itemObject : pc.getInventory().getItems()) {
							L1ItemInstance item1 = (L1ItemInstance) itemObject;
							if (item1.getItem().getItemId() == 40002
									&& item1.getChargeCount() < 12000) {
								item1.setChargeCount(12000); // 补满
								pc.sendPackets(new S_ItemName(item1));
								pc.getInventory().updateItem(item1,
										L1PcInventory.COL_CHARGE_COUNT);
								pc.sendPackets(new S_ServerMessage(230)); // 你在灯笼里加满了新的灯油。
								pc.getInventory().removeItem(useItem, 1);
								return;
							}
						}
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 43000: { // 复活（Lv99使用可能/Lv1戾效果）
						pc.getInventory().takeoffEquip(945);// 用来脱掉全身装备
						pc.setExp(1);
						pc.resExp();
						// pc.resetLevel();
						// 血量调整
						switch (Config.REVIVAL_POTION) {
						case 1:
							pc.addBaseMaxHp((short) (-1 * (int) ((double) pc
									.getBaseMaxHp() * 0.9D)));
							pc.addBaseMaxMp((short) (-1 * (int) ((double) pc
									.getBaseMaxMp() * 0.9D)));
							break;
						case 2:
							pc.addBaseMaxHp((short) (-1 * (int) ((double) pc
									.getBaseMaxHp() * 0.8D)));
							pc.addBaseMaxMp((short) (-1 * (int) ((double) pc
									.getBaseMaxMp() * 0.8D)));
							break;
						case 3:
							pc.addBaseMaxHp((short) (-1 * (int) ((double) pc
									.getBaseMaxHp() * 0.7D)));
							pc.addBaseMaxMp((short) (-1 * (int) ((double) pc
									.getBaseMaxMp() * 0.7D)));
							break;
						case 4:
							pc.addBaseMaxHp((short) (-1 * (int) ((double) pc
									.getBaseMaxHp() * 0.6D)));
							pc.addBaseMaxMp((short) (-1 * (int) ((double) pc
									.getBaseMaxMp() * 0.6D)));
							break;
						case 5:
							pc.addBaseMaxHp((short) (-1 * (int) ((double) pc
									.getBaseMaxHp() * 0.5D)));
							pc.addBaseMaxMp((short) (-1 * (int) ((double) pc
									.getBaseMaxMp() * 0.5D)));
							break;
						case 6:
							pc.addBaseMaxHp((short) (-1 * (int) ((double) pc
									.getBaseMaxHp() * 0.4D)));
							pc.addBaseMaxMp((short) (-1 * (int) ((double) pc
									.getBaseMaxMp() * 0.4D)));
							break;
						case 7:
							pc.addBaseMaxHp((short) (-1 * (int) ((double) pc
									.getBaseMaxHp() * 0.3D)));
							pc.addBaseMaxMp((short) (-1 * (int) ((double) pc
									.getBaseMaxMp() * 0.3D)));
							break;
						case 8:
							pc.addBaseMaxHp((short) (-1 * (int) ((double) pc
									.getBaseMaxHp() * 0.2D)));
							pc.addBaseMaxMp((short) (-1 * (int) ((double) pc
									.getBaseMaxMp() * 0.2D)));
							break;
						case 9:
							pc.addBaseMaxHp((short) (-1 * (int) ((double) pc
									.getBaseMaxHp() * 0.1D)));
							pc.addBaseMaxMp((short) (-1 * (int) ((double) pc
									.getBaseMaxMp() * 0.1D)));
							break;
						}
						// 血量调整
						// 防御、魔防、命中、攻击重新计算
						pc.resetBaseAc();
						pc.resetBaseMr();
						pc.resetBaseHitup();
						pc.resetBaseDmgup();
						// 防御、魔防、命中、攻击重新计算 end
						pc.setBonusStats(0);
						pc.sendPackets(new S_SkillSound(pcObjid, 3944));
						pc.broadcastPacket(new S_SkillSound(pcObjid, 3944));
						pc.sendPackets(new S_OwnCharStatus(pc));
						pc.getInventory().removeItem(useItem, 1);
						pc.sendPackets(new S_ServerMessage(822)); // 独自、适当。
						pc.save(); // DB情报书迂
					}
						break;
					case 40033: { // :腕力
						if (pc.getBaseStr() < Config.BONUS_STATS3
								&& pc.getElixirStats() < Config.BONUS_STATS2) { // 调整能力值上限
							pc.addBaseStr((byte) 1); // 素STR值+1
							pc.setElixirStats(pc.getElixirStats() + 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.sendPackets(new S_OwnCharStatus2(pc));
							pc.save();
							; // DB情报书迂
						} else {
							// 删除pc.sendPackets(new S_ServerMessage(481)); //
							// \f1一能力值最大值25。他能力值选择。
							// 变更
							if (pc.getElixirStats() >= Config.BONUS_STATS2)
								pc.sendPackets(new S_ServerMessage(166,
										L1WilliamSystemMessage
												.ShowMessage(1093)));
							else if (pc.getBaseStr() >= Config.BONUS_STATS1)
								pc.sendPackets(new S_SystemMessage(
										L1WilliamSystemMessage
												.ShowMessage(1001))); // 从DB取得讯息
							// 变更 end
						}
					}
						break;
					case 40034: { // :体力
						if (pc.getBaseCon() < Config.BONUS_STATS3
								&& pc.getElixirStats() < Config.BONUS_STATS2) { // 调整能力值上限
							pc.addBaseCon((byte) 1); // 素CON值+1
							pc.setElixirStats(pc.getElixirStats() + 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.sendPackets(new S_OwnCharStatus2(pc));
							pc.save();
							; // DB情报书迂
						} else {
							// 删除pc.sendPackets(new S_ServerMessage(481)); //
							// \f1一能力值最大值25。他能力值选择。
							// 变更
							if (pc.getElixirStats() >= Config.BONUS_STATS2)
								pc.sendPackets(new S_ServerMessage(166,
										L1WilliamSystemMessage
												.ShowMessage(1093)));
							else if (pc.getBaseCon() >= Config.BONUS_STATS1)
								pc.sendPackets(new S_SystemMessage(
										L1WilliamSystemMessage
												.ShowMessage(1001))); // 从DB取得讯息
							// 变更 end
						}
					}
						break;
					case 40035: { // :机敏
						if (pc.getBaseDex() < Config.BONUS_STATS3
								&& pc.getElixirStats() < Config.BONUS_STATS2) { // 调整能力值上限
							pc.addBaseDex((byte) 1); // 素DEX值+1
							pc.resetBaseAc();
							pc.setElixirStats(pc.getElixirStats() + 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.sendPackets(new S_OwnCharStatus2(pc));
							pc.save();
							; // DB情报书迂
						} else {
							// 删除pc.sendPackets(new S_ServerMessage(481)); //
							// \f1一能力值最大值25。他能力值选择。
							// 变更
							if (pc.getElixirStats() >= Config.BONUS_STATS2)
								pc.sendPackets(new S_ServerMessage(166,
										L1WilliamSystemMessage
												.ShowMessage(1093)));
							else if (pc.getBaseDex() >= Config.BONUS_STATS1)
								pc.sendPackets(new S_SystemMessage(
										L1WilliamSystemMessage
												.ShowMessage(1001))); // 从DB取得讯息
							// 变更 end
						}
					}
						break;
					case 40036: { // :知力
						if (pc.getBaseInt() < Config.BONUS_STATS3
								&& pc.getElixirStats() < Config.BONUS_STATS2) { // 调整能力值上限
							pc.addBaseInt((byte) 1); // 素INT值+1
							pc.setElixirStats(pc.getElixirStats() + 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.sendPackets(new S_OwnCharStatus2(pc));
							pc.save();
							; // DB情报书迂
						} else {
							// 删除pc.sendPackets(new S_ServerMessage(481)); //
							// \f1一能力值最大值25。他能力值选择。
							// 变更
							if (pc.getElixirStats() >= Config.BONUS_STATS2)
								pc.sendPackets(new S_ServerMessage(166,
										L1WilliamSystemMessage
												.ShowMessage(1093)));
							else if (pc.getBaseInt() >= Config.BONUS_STATS1)
								pc.sendPackets(new S_SystemMessage(
										L1WilliamSystemMessage
												.ShowMessage(1001))); // 从DB取得讯息
							// 变更 end
						}
					}
						break;
					case 40037: { // :精神
						if (pc.getBaseWis() < Config.BONUS_STATS3
								&& pc.getElixirStats() < Config.BONUS_STATS2) { // 调整能力值上限
							pc.addBaseWis((byte) 1); // 素WIS值+1
							pc.resetBaseMr();
							pc.setElixirStats(pc.getElixirStats() + 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.sendPackets(new S_OwnCharStatus2(pc));
							pc.save();
							; // DB情报书迂
						} else {
							// 删除pc.sendPackets(new S_ServerMessage(481)); //
							// \f1一能力值最大值25。他能力值选择。
							// 变更
							if (pc.getElixirStats() >= Config.BONUS_STATS2)
								pc.sendPackets(new S_ServerMessage(166,
										L1WilliamSystemMessage
												.ShowMessage(1093)));
							else if (pc.getBaseWis() >= Config.BONUS_STATS1)
								pc.sendPackets(new S_SystemMessage(
										L1WilliamSystemMessage
												.ShowMessage(1001))); // 从DB取得讯息
							// 变更 end
						}
					}
						break;
					case 40038: { // :魅力
						if (pc.getBaseCha() < Config.BONUS_STATS3
								&& pc.getElixirStats() < Config.BONUS_STATS2) { // 调整能力值上限
							pc.addBaseCha((byte) 1); // 素CHA值+1
							pc.setElixirStats(pc.getElixirStats() + 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.sendPackets(new S_OwnCharStatus2(pc));
							pc.save();
							; // DB情报书迂
						} else {
							// 删除pc.sendPackets(new S_ServerMessage(481)); //
							// \f1一能力值最大值25。他能力值选择。
							// 变更
							if (pc.getElixirStats() >= Config.BONUS_STATS2)
								pc.sendPackets(new S_ServerMessage(166,
										L1WilliamSystemMessage
												.ShowMessage(1093)));
							else if (pc.getBaseCha() >= Config.BONUS_STATS1)
								pc.sendPackets(new S_SystemMessage(
										L1WilliamSystemMessage
												.ShowMessage(1001))); // 从DB取得讯息
							// 变更 end
						}
					}
						break;
					case L1ItemId.POTION_OF_HEALING:
					case L1ItemId.CONDENSED_POTION_OF_HEALING:
					case 40029: {// 、浓缩体力回复剂、象牙塔体力回复剂
						UseHeallingPotion(pc, 15, 189, itemId);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 40022: { // 古代体力回复剂
						UseHeallingPotion(pc, 20, 189, itemId);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case L1ItemId.POTION_OF_EXTRA_HEALING:
					case L1ItemId.CONDENSED_POTION_OF_EXTRA_HEALING: {
						UseHeallingPotion(pc, 45, 194, itemId);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 40023: { // 古代高级体力回复剂
						UseHeallingPotion(pc, 30, 194, itemId);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case L1ItemId.POTION_OF_GREATER_HEALING:
					case L1ItemId.CONDENSED_POTION_OF_GREATER_HEALING: {
						UseHeallingPotion(pc, 75, 197, itemId);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 40024: { // 古代强力体力回复剂
						UseHeallingPotion(pc, 55, 197, itemId);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 40506: { // 实
						UseHeallingPotion(pc, 70, 197, itemId);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 40026:
					case 40027:
					case 40028: { // 
						UseHeallingPotion(pc, 25, 189, itemId);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 40058: { // 色
						UseHeallingPotion(pc, 30, 189, itemId);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 40071: { // 黑
						UseHeallingPotion(pc, 70, 197, itemId);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 40734: { // 信赖
						UseHeallingPotion(pc, 50, 189, itemId);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case L1ItemId.B_POTION_OF_HEALING: {
						UseHeallingPotion(pc, 25, 189, itemId);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case L1ItemId.C_POTION_OF_HEALING: {
						UseHeallingPotion(pc, 10, 189, itemId);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case L1ItemId.B_POTION_OF_EXTRA_HEALING: { // 祝福
						// 
						UseHeallingPotion(pc, 55, 194, itemId);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case L1ItemId.B_POTION_OF_GREATER_HEALING: { // 祝福
						// 
						UseHeallingPotion(pc, 85, 197, itemId);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 140506: { // 祝福实
						UseHeallingPotion(pc, 80, 197, itemId);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 40858: { // liquor（酒）
						pc.setDrink(true);
						pc.sendPackets(new S_Liquor(pc.getId()));
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case L1ItemId.POTION_OF_CURE_POISON:
					case 40507: { // 、枝
						if (pc.hasSkillEffect(71) == true) { // 状态
							pc.sendPackets(new S_ServerMessage(698)); // 魔力何饮。
						} else {
							cancelAbsoluteBarrier(pc); //  解除
							pc.sendPackets(new S_SkillSound(pc.getId(), 192));
							pc.broadcastPacket(new S_SkillSound(pc.getId(), 192));
							if (itemId == L1ItemId.POTION_OF_CURE_POISON) {
								pc.getInventory().removeItem(useItem, 1);
							} else if (itemId == 40507) {
								pc.getInventory().removeItem(useItem, 1);
							}

							pc.curePoison();
						}
					}
						break;
					case L1ItemId.POTION_OF_HASTE_SELF:
					case L1ItemId.B_POTION_OF_HASTE_SELF:
					case 40018: // 强化 
					case 140018: // 祝福强化 
					case 40039: // 
					case 40040: // 
					case 40030:
					case Item_EPU_17: // 饭团
					case Item_EPU_18: // 鸡肉串烧
					case Item_EPU_24: // 小比萨
					case Item_EPU_25: // 烤玉米
					case Item_EPU_26: // 爆米花
					case Item_EPU_27: // 甜不拉
					case Item_EPU_28: // 松饼
					{ // 象牙塔 
						useGreenPotion(pc, itemId);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case L1ItemId.POTION_OF_EMOTION_BRAVERY:
					case L1ItemId.B_POTION_OF_EMOTION_BRAVERY: {
						// 
						if (pc.isKnight()) {
							useBravePotion(pc, itemId);
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
						}
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case L1ItemId.C_POTION_OF_EMOTION_BRAVERY: { // 新手双速药水
						useBravePotion(pc, itemId);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 40031: { //  
						if (pc.isCrown() && pc.getTempCharGfx() != 6080
								&& pc.getTempCharGfx() != 6094) {
							// 补上判断是否变身军马状态
							useBravePotion(pc, itemId);
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
						}
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 40733: { // 名誉
						// 补上判断是否变身军马状态
						if (pc.getTempCharGfx() != 6080
								&& pc.getTempCharGfx() != 6094) {
							useBravePotion(pc, itemId);
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
						}
						// 补上判断是否变身军马状态 end
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 40068:
					case 140068: { // 精饼
						if (pc.isElf()) {
							useBravePotion(pc, itemId);
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
						}
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 40066: { // 饼
						pc.sendPackets(new S_ServerMessage(338, "$1084")); // %0回复。
						pc.setCurrentMp(pc.getCurrentMp() + 20);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 40067: { // 饼
						pc.sendPackets(new S_ServerMessage(338, "$1084")); // %0回复。
						pc.setCurrentMp(pc.getCurrentMp() + 50);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 40735: { // 勇气
						pc.sendPackets(new S_ServerMessage(338, "$1084")); // %0回复。
						pc.setCurrentMp(pc.getCurrentMp() + 60);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 40042: { // 
						pc.sendPackets(new S_ServerMessage(338, "$1084")); // %0回复。
						pc.setCurrentMp(pc.getCurrentMp() + 50);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 40043: { // 兔肝
						UseHeallingPotion(pc, 600, 189, itemId);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 40032:
					case 40041: { // 祝福＆鳞
						useBlessOfEva(pc, itemId);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case L1ItemId.POTION_OF_MANA: //  
					case L1ItemId.B_POTION_OF_MANA: // 祝福
						// 
					case 40736: { //
						useBluePotion(pc, itemId);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case L1ItemId.POTION_OF_EMOTION_WISDOM: // 
						// 
					case L1ItemId.B_POTION_OF_EMOTION_WISDOM: { // 祝福
						// 
						if (pc.isWizard()) {
							useWisdomPotion(pc, itemId);
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
						}
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case L1ItemId.POTION_OF_BLINDNESS: { // 
						useBlindPotion(pc);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 40088: // 变身
					case 40096: // 象牙塔
					case 40410: // 修正 40410
					case 140088: { // 祝福变身
						// 龙骑士觉醒后无法使用变卷功能
						final int awakeSkillId = pc.getAwakeSkillId();
						if ((awakeSkillId == AWAKEN_ANTHARAS)
								|| (awakeSkillId == AWAKEN_FAFURION)
								|| (awakeSkillId == AWAKEN_VALAKAS)) {
							pc.sendPackets(new S_ServerMessage(1384)); // 目前状态中无法变身。
							return;
						}
						// 龙骑士觉醒后无法使用变卷功能
						// 限制地图不可变身
						if (pc.getMapId() == 5124) {
							pc.sendPackets(new S_ServerMessage(1170));
							return;
						}
						// 限制地图不可变身 end
						if (usePolyScroll(pc, itemId, s)) {
							pc.getInventory().removeItem(useItem, 1);
						} else {
							pc.sendPackets(new S_ServerMessage(181)); // \f1变身。
						}
					}
						break;
					case 41154: // 闇鳞
					case 41155: // 烈火鳞
					case 41156: // 背德者鳞
					case 41157: // 憎恶鳞
					{
						// 限制地图不可变身
						if (pc.getMapId() == 5124) {
							pc.sendPackets(new S_ServerMessage(1170));
							return;
						}
						// 限制地图不可变身 end
						usePolyScale(pc, itemId);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 40317: { // 砥石
						// 武器防具场合
						if (l1iteminstance1.getItem().getType2() != 0
								&& l1iteminstance1.get_durability() > 0) {
							String msg0;
							pc.getInventory().recoveryDamage(l1iteminstance1);
							msg0 = l1iteminstance1.getLogName();
							if (l1iteminstance1.get_durability() == 0) {
								pc.sendPackets(new S_ServerMessage(464, msg0)); // %0%s新品同态。
							} else {
								pc.sendPackets(new S_ServerMessage(463, msg0)); // %0态良。
							}
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
						}
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 40097:
					case 40119:
					case 140119:
					case 140329: { // 解咒、原住民
						for (L1ItemInstance eachItem : pc.getInventory()
								.getItems()) {
							if (eachItem.getBless() != 2) {
								continue;
							}
							if (!eachItem.isEquipped()
									&& (itemId == 40119 || itemId == 40097)) {
								// n解备解
								continue;
							}
							int id_normal = eachItem.getItemId() - 200000;
							L1Item template = ItemTable.getInstance()
									.getTemplate(id_normal);
							if (template == null) {
								continue;
							}
							if (pc.getInventory().checkItem(id_normal)
									&& template.isStackable()) {
								pc.getInventory().storeItem(id_normal,
										eachItem.getCount());
								pc.getInventory().removeItem(eachItem,
										eachItem.getCount());
							} else {
								eachItem.setItem(template);
								pc.getInventory().updateItem(eachItem,
										L1PcInventory.COL_ITEMID);
								pc.getInventory().saveItem(eachItem,
										L1PcInventory.COL_ITEMID);
							}
						}
						pc.getInventory().removeItem(useItem, 1);
						pc.sendPackets(new S_ServerMessage(155)); // \f1谁助。
					}
						break;
					case 40126:
					case 40098: { // 确认
						if (!l1iteminstance1.isIdentified()) {
							l1iteminstance1.setIdentified(true);
							pc.getInventory().updateItem(l1iteminstance1,
									L1PcInventory.COL_IS_ID);
						}
						pc.sendPackets(new S_IdentifyDesc(l1iteminstance1));
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 41036: {// 糊
						int diaryId = l1iteminstance1.getItem().getItemId();
						if (diaryId >= 41038 && 41047 >= diaryId) {
							if (_random.nextInt(100) < 33) {
								createNewItem(pc, diaryId + 10, 1);
							}
							pc.getInventory().removeItem(l1iteminstance1, 1);
							pc.getInventory().removeItem(useItem, 1);
						}
					}
						break;
					case 40090:
					case 40091:
					case 40092:
					case 40093:
					case 40094: { //  (Lv1)～
						// (Lv5)
						if (pc.isWizard()) { // 
							if (itemId == 40090 && blanksc_skillid <= 7 || // 
									// (Lv1)1以下魔法
									itemId == 40091 && blanksc_skillid <= 15 || // 
									// (Lv2)2以下魔法
									itemId == 40092 && blanksc_skillid <= 22 || // 
									// (Lv3)3以下魔法
									itemId == 40093 && blanksc_skillid <= 31 || // 
									// (Lv4)4以下魔法
									itemId == 40094 && blanksc_skillid <= 39) { // 
								// (Lv5)5以下魔法
								L1ItemInstance spellsc = ItemTable
										.getInstance().createItem(
												40859 + blanksc_skillid);
								if (spellsc != null) {
									if (pc.getInventory().checkAddItem(spellsc,
											1) == L1Inventory.OK) {
										L1Skills l1skills = SkillsTable
												.getInstance().getTemplate(
														blanksc_skillid + 1); // blanksc_skillid0始
										if (pc.getCurrentHp() + 1 < l1skills
												.getHpConsume() + 1) {
											pc.sendPackets(new S_ServerMessage(
													279)); // \f1HP不足魔法使。
											return;
										}
										if (pc.getCurrentMp() < l1skills
												.getMpConsume()) {
											pc.sendPackets(new S_ServerMessage(
													278)); // \f1MP不足魔法使。
											return;
										}
										if (l1skills.getItemConsumeId() != 0) { // 材料必要
											if (!pc.getInventory()
													.checkItem(
															l1skills.getItemConsumeId(),
															l1skills.getItemConsumeCount())) { // 必要材料
												pc.sendPackets(new S_ServerMessage(
														299)); // \f1魔法咏唱材料足。
												return;
											}
										}
										pc.setCurrentHp(pc.getCurrentHp()
												- l1skills.getHpConsume());
										pc.setCurrentMp(pc.getCurrentMp()
												- l1skills.getMpConsume());
										int lawful = pc.getLawful()
												+ l1skills.getLawful();
										if (lawful > 32767) {
											lawful = 32767;
										}
										if (lawful < -32767) {
											lawful = -32767;
										}
										pc.setLawful(lawful);
										if (l1skills.getItemConsumeId() != 0) { // 材料必要
											pc.getInventory()
													.consumeItem(
															l1skills.getItemConsumeId(),
															l1skills.getItemConsumeCount());
										}
										pc.getInventory()
												.removeItem(useItem, 1);
										pc.getInventory().storeItem(spellsc);
									}
								}
							} else {
								pc.sendPackets(new S_ServerMessage(591)); // \f1强魔法记弱。
							}
						} else {
							pc.sendPackets(new S_ServerMessage(264)); // \f1使用。
						}
					}
						break;
					case 40859:
					case 40860:
					case 40861:
					case 40862:
					case 40864:
					case 40865:
					case 40866:
					case 40867:
					case 40868:
					case 40869: /* 删除case 40870:删除 */
					case 40871:
					case 40872:
					case 40873:
					case 40874:
					case 40875:
					case 40876:
					case 40877:
					case 40878:
						/* 删除case 40879:删除 */
					case 40880:
					case 40881:
					case 40882:
					case 40883:
					case 40884:
					case 40885:
					case 40886:
					case 40887:
					case 40888:
					case 40889:
					case 40890:
					case 40891:
					case 40892:
					case 40893:
					case 40894:
					case 40895:
					case 40896:
					case 40897:
					case 40898: { // 40863理
						if (spellsc_objid == pc.getId()
								&& useItem.getItem().getUseType() != 30) { // spell_buff
							pc.sendPackets(new S_ServerMessage(281)); // \f1魔法无佅。
							return;
						}
						pc.getInventory().removeItem(useItem, 1);
						if (spellsc_objid == 0
								&& useItem.getItem().getUseType() != 0
								&& useItem.getItem().getUseType() != 26
								&& useItem.getItem().getUseType() != 27) {
							return;
							// 场合handleCommands送return
							// handleCommands判＆理部分
						}
						cancelAbsoluteBarrier(pc); //  解除
						int skillid = itemId - 40858;
						// 攻态确认
						if (pc.getInventory().getWeight240() >= 180) { // 重量
							pc.sendPackets(new S_ServerMessage(110)); //
							if (skillid == TELEPORT || skillid == MASS_TELEPORT) {
								pc.sendPackets(new S_Paralysis(
										S_Paralysis.TYPE_TELEPORT_UNLOCK, 0,
										false));
							}
							return;
						}

						// 要求间隔
						if (Config.CHECK_SPELL_INTERVAL) {
							int result;
							if (SkillsTable.getInstance().getTemplate(skillid)
									.getActid() == 18) {
								result = pc.speed_Attack()
										.checkIntervalskilldir();
							} else {
								result = pc.speed_Attack()
										.checkIntervalskillnodir();
							}
							if (result == AcceleratorChecker.R_DISPOSED) {
								return;
							}
						}
						L1SkillUse l1skilluse = new L1SkillUse();
						l1skilluse.handleCommands(_client.getActiveChar(),
								skillid, spellsc_objid, spellsc_x, spellsc_y,
								null, 0, L1SkillUse.TYPE_SPELLSC);

					}
						break;
					case 40870: { // 修正 魔法卷轴(拟似魔法武器)
						cancelAbsoluteBarrier(pc); //  解除
						int skillid = 12;
						L1SkillUse l1skilluse = new L1SkillUse();
						l1skilluse.handleCommands(_client.getActiveChar(),
								skillid, l, 0, 0, null, 0,
								L1SkillUse.TYPE_SPELLSC);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 40879: { // 修正 魔法卷轴(铠甲护持)
						cancelAbsoluteBarrier(pc); //  解除
						int skillid = 21;
						L1SkillUse l1skilluse = new L1SkillUse();
						l1skilluse.handleCommands(_client.getActiveChar(),
								skillid, l, 0, 0, null, 0,
								L1SkillUse.TYPE_SPELLSC);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					/*
					 * case 40373: case 40374: case 40375: case 40376: case
					 * 40377: case 40378: case 40379: case 40380: case 40381:
					 * case 40382: case 40385: case 40386: case 40387: case
					 * 40388: case 40389: case 40390: case 40383: case 40384:
					 * //隐谷、歌唱地图改回台版 { // 地图各种 pc.sendPackets(new S_UseMap(pc,
					 * useItem.getId(), useItem.getItem().getItemId())); }break;
					 */
					case 40310:
					case 40730:
					case 40731:
					case 40732: { // 便笺(未使用)
						if (writeLetter(itemId, pc, letterCode, letterReceiver,
								letterText)) {
							pc.getInventory().removeItem(useItem, 1);
						}
					}
						break;
					case 40311: { // 血盟便笺(未使用)
						if (writeClanLetter(itemId, pc, letterCode,
								letterReceiver, letterText)) {
							pc.getInventory().removeItem(useItem, 1);
						}
					}
						break;
					case 49016:
					case 49018:
					case 49020:
					case 49022:
					case 49024: { // 便笺(未开封)
						pc.sendPackets(new S_Letter(useItem));
						useItem.setItemId(itemId + 1);
						pc.getInventory().updateItem(useItem,
								L1PcInventory.COL_ITEMID);
						pc.getInventory().saveItem(useItem,
								L1PcInventory.COL_ITEMID);
					}
						break;
					case 49017:
					case 49019:
					case 49021:
					case 49023:
					case 49025: { // 便笺(开封济)
						pc.sendPackets(new S_Letter(useItem));
					}
						break;
					case 40314:
					case 40316: { // 
						if (pc.getInventory().checkItem(41160)) { // 召唤笛
							if (withdrawPet(pc, itemObjid)) {
								pc.getInventory().consumeItem(41160, 1);
							}
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
						}
					}
						break;
					case 40315: { // 笛
						pc.sendPackets(new S_Sound(437));
						pc.broadcastPacket(new S_Sound(437));
						Object[] petList = pc.getPetList().values().toArray();
						for (Object petObject : petList) {
							if (petObject instanceof L1PetInstance) { // 
								L1PetInstance pet = (L1PetInstance) petObject;
								pet.call();
							}
						}
					}
						break;
					case 40493: { // 
						pc.sendPackets(new S_Sound(165));
						pc.broadcastPacket(new S_Sound(165));
						for (L1Object visible : pc.getKnownObjects()) {
							if (visible instanceof L1GuardianInstance) {
								L1GuardianInstance guardian = (L1GuardianInstance) visible;
								if (guardian.getNpcTemplate().get_npcId() == 70850) { // 
									if (createNewItem(pc, 88, 1)) {
										pc.getInventory()
												.removeItem(useItem, 1);
									}
								}
							}
						}
					}
						break;
					case 40325: { // 2面
						if (pc.getInventory().checkItem(40318, 1)) {
							int gfxid = 3237 + _random.nextInt(2);
							pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
							pc.broadcastPacket(new S_SkillSound(pc.getId(),
									gfxid));
							pc.getInventory().consumeItem(40318, 1);
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
						}
					}
						break;
					case 40326: { // 3方向
						if (pc.getInventory().checkItem(40318, 1)) {
							int gfxid = 3229 + _random.nextInt(3); // 3241 修正为
																	// 3229
							pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
							pc.broadcastPacket(new S_SkillSound(pc.getId(),
									gfxid));
							pc.getInventory().consumeItem(40318, 1);
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
						}
					}
						break;
					case 40327: { // 4方向
						if (pc.getInventory().checkItem(40318, 1)) {
							int gfxid = 3241 + _random.nextInt(4);
							pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
							pc.broadcastPacket(new S_SkillSound(pc.getId(),
									gfxid));
							pc.getInventory().consumeItem(40318, 1);
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
						}
					}
						break;
					case 40328: { // 6面
						if (pc.getInventory().checkItem(40318, 1)) {
							int gfxid = 3204 + _random.nextInt(6);
							pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
							pc.broadcastPacket(new S_SkillSound(pc.getId(),
									gfxid));
							pc.getInventory().consumeItem(40318, 1);
						} else {
							// \f1何起。
							pc.sendPackets(new S_ServerMessage(79));
						}
					}
						break;
					case 40089:
					case 140089: { // 复活、祝福复活
						L1Character resobject = (L1Character) L1World
								.getInstance().findObject(resid);
						if (resobject != null) {
							if (resobject instanceof L1PcInstance) {
								L1PcInstance target = (L1PcInstance) resobject;
								if (pc.getId() == target.getId()) {
									return;
								}
								if (target.getCurrentHp() == 0
										&& target.isDead() == true) {
									/*
									 * target.setTempID(pc.getId()); if (itemId
									 * == 40089) { // 复活？（Y/N）
									 * target.sendPackets(new S_Message_YN( 321,
									 * "")); } else if (itemId == 140089) { //
									 * 复活？（Y/N） target.sendPackets(new
									 * S_Message_YN( 322, "")); }
									 */
									if (pc.getMap().isUseResurrection()) {
										target.setTempID(pc.getId());
										if (itemId == 40089) {
											// 复活？（Y/N）
											target.sendPackets(new S_Message_YN(
													321, ""));
										} else if (itemId == 140089) {
											// 复活？（Y/N）
											target.sendPackets(new S_Message_YN(
													322, ""));
										}
									} else {
										return;
									}
								}
							} else if (resobject instanceof L1NpcInstance) {
								if (!(resobject instanceof L1TowerInstance)
										&& !(resobject instanceof L1DoorInstance)
										&& !(resobject instanceof L1CastleGuardInstance)) {// 补上门
																							// &
																							// 守城警卫的判断
									L1NpcInstance npc = (L1NpcInstance) resobject;
									if (npc.getCurrentHp() == 0
											&& npc.isDead()
											&& npc.getNpcTemplate()
													.get_IsErase() == true) { // 判断可以复活
										npc.resurrect(npc.getMaxHp() / 4);
										npc.setResurrect(true);
										// 修正复活后回血回血
										npc.stopHpRegeneration();
										if (npc.getMaxHp() > npc.getCurrentHp()) {
											npc.startHpRegeneration();
										}
										npc.stopMpRegeneration();
										if (npc.getMaxMp() > npc.getCurrentMp()) {
											npc.startMpRegeneration();
										}
										// 修正复活后回血回血 end
									}
								}
							}
						}
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 40079:
					case 40095: { // 扫还
						if (pc.getMap().isEscapable() || pc.isGm()) {
							int[] loc = Getback.GetBack_Location(pc, true);
							L1Teleport.teleport(pc, loc[0], loc[1],
									(short) loc[2], 5, true);
							pc.getInventory().removeItem(useItem, 1);
							// 使用隐藏之谷、歌唱之岛传送卷
							if ((pc.getMapId() == 68 || pc.getMapId() == 69
									|| pc.getMapId() == 85 || pc.getMapId() == 86)
									&& (pc.getCurrentHp() <= (pc.getMaxHp() / 2))) {
								pc.setCurrentHp(pc.getMaxHp());
								pc.setCurrentMp(pc.getMaxMp());
								pc.sendPackets(new S_ServerMessage(77));
								pc.sendPackets(new S_SkillSound(pc.getId(), 830));
								pc.sendPackets(new S_HPUpdate(
										pc.getCurrentHp(), pc.getMaxHp()));
								pc.sendPackets(new S_MPUpdate(
										pc.getCurrentMp(), pc.getMaxMp()));
							}
							// 使用隐藏之谷、歌唱之岛传送卷轴恢复血魔 end
						} else {
							pc.sendPackets(new S_ServerMessage(647));
							// pc.sendPackets(new
							// S_CharVisualUpdate(pc));
						}
						cancelAbsoluteBarrier(pc); //  解除
					}
						break;
					case 40124: { // 血盟扫还
						if (pc.getMap().isEscapable() || pc.isGm()) {
							int castle_id = 0;
							int house_id = 0;
							if (pc.getClanid() != 0) { // 所属
								L1Clan clan = L1World.getInstance().getClan(
										pc.getClanname());
								if (clan != null) {
									castle_id = clan.getCastleId();
									house_id = clan.getHouseId();
								}
							}
							if (castle_id != 0) { // 城主员
								if (pc.getMap().isEscapable() || pc.isGm()) {
									int[] loc = new int[3];
									loc = L1CastleLocation
											.getCastleLoc(castle_id);
									int locx = loc[0];
									int locy = loc[1];
									short mapid = (short) (loc[2]);
									L1Teleport.teleport(pc, locx, locy, mapid,
											5, true);
									pc.getInventory().removeItem(useItem, 1);
								} else {
									pc.sendPackets(new S_ServerMessage(647));
								}
							} else if (house_id != 0) { // 所有员
								if (pc.getMap().isEscapable() || pc.isGm()) {
									int[] loc = new int[3];
									loc = L1HouseLocation.getHouseLoc(house_id);
									int locx = loc[0];
									int locy = loc[1];
									short mapid = (short) (loc[2]);
									L1Teleport.teleport(pc, locx, locy, mapid,
											5, true);
									pc.getInventory().removeItem(useItem, 1);
								} else {
									pc.sendPackets(new S_ServerMessage(647));
								}
							} else {
								if (pc.getHomeTownId() > 0) {
									int[] loc = L1TownLocation.getGetBackLoc(pc
											.getHomeTownId());
									int locx = loc[0];
									int locy = loc[1];
									short mapid = (short) (loc[2]);
									L1Teleport.teleport(pc, locx, locy, mapid,
											5, true);
									pc.getInventory().removeItem(useItem, 1);
								} else {
									int[] loc = Getback.GetBack_Location(pc,
											true);
									L1Teleport.teleport(pc, loc[0], loc[1],
											(short) loc[2], 5, true);
									pc.getInventory().removeItem(useItem, 1);
								}
							}
						} else {
							pc.sendPackets(new S_ServerMessage(647));
						}
						cancelAbsoluteBarrier(pc); //  解除
					}
						break;
					case 40080:
					case 40081:
					case 40082:
					case 40083:
					case 40084:
					case 40085:
					case 40102:
					case 40101: // 补上隐藏之谷传送卷轴
					case 40103:
					case 40104:
					case 40105:
					case 40106:
					case 40107:
					case 40108:
					case 40109:
					case 40110:
					case 40111:
					case 40112:
					case 40113:
					case 40114:
					case 40115:
					case 40116:
					case 40117:
					case 40118:
					case 40120:
					case 40121:
					case 40122:
					case 40123:
					case 40125:
					case 40801:
					case 40802:
					case 40803:
					case 40804:
					case 40805:
					case 40806:
					case 40807:
					case 40808:
					case 40809:
					case 40810:
					case 40811:
					case 40812:
					case 40813:
					case 40814:
					case 40815:
					case 40816:
					case 40817:
					case 40818:
					case 40819:
					case 40820:
					case 40821:
					case 40822:
					case 40823:
					case 40824:
					case 40825:
					case 40826:
					case 40827:
					case 40828:
					case 40829:
					case 40830:
					case 40831:
					case 40832:
					case 40833:
					case 40834:
					case 40835:
					case 40836:
					case 40837:
					case 40838:
					case 40839:
					case 40840:
					case 40841:
					case 40842:
					case 40843:
					case 40844:
					case 40845:
					case 40846:
					case 40847:
					case 40848:
					case 40849:
					case 40850:
					case 40851:
					case 40852:
					case 40853:
					case 40854:
					case 40855:
					case 40856:
					case 40857:
					case 42001:
					case 42002:
					case 42003:
					case 42004:
					case 42005:
					case 42006:
					case 42007:
					case 42008:
					case 42009:
					case 42010:
					case 42011:
					case 42012:
					case 42013:
					case 42014:
					case 42015:
					case 42016:
					case 42017:
					case 42018:
					case 42019:
					case 42020:
					case 42021:
					case 42022:
					case 42023:
					case 42024:
					case 42025:
					case 42026:
					case 42027:
					case 42028:
					case 42029:
					case 42030:
					case 42031:
					case 42032:
					case 42033:
					case 42035:
					case 42036:
					case 42037:
					case 42038:
					case 42039:
					case 42040:
					case 42041:
					case 42042:
					case 42043:
					case 42044:
					case 42045:
					case 42046:
					case 42047:
					case 42048:
					case 42049:
					case 42050:
					case 42051:
					case 42052:
					case 42053:
					case 42054:
					case 42055:
					case 42056:
					case 42057:
					case 42058:
					case 42059:
					case 42060:
					case 42061:
					case 42062:
					case 42063:
					case 42064:
					case 42065:
					case 42066:
					case 42067:
					case 42068:
					case 42069:
					case 42070:
					case 42071:
					case 42072:
					case 42073:
					case 42074:
					case 42075:
					case 42076:
					case 42077:
					case 42078:
					case 42079:
					case 42080:
					case 42081:
					case 42082:
					case 42083:
					case 42084:
					case 42085:
					case 42086:
					case 42087:
					case 42088:
					case 42089:
					case 42090:
					case 42091:
					case 42092:
					case 42093:
					case 42094:
					case 42095:
					case 42096:
					case 42097:
					case 42098:
					case 42099:
					case 42100: { // 各种
						if (pc.getMap().isEscapable() || pc.isGm()) {
							L1Teleport
									.teleport(pc, ((L1EtcItem) useItem
											.getItem()).get_locx(),
											((L1EtcItem) useItem.getItem())
													.get_locy(),
											((L1EtcItem) useItem.getItem())
													.get_mapid(), 5, true);
							pc.getInventory().removeItem(useItem, 1);
							// 使用隐藏之谷、歌唱之岛传送卷轴恢复血魔
							if ((itemId == 40082 || itemId == 40101)
									&& (pc.getCurrentHp() <= (pc.getMaxHp() / 2))) {
								pc.setCurrentHp(pc.getMaxHp());
								pc.setCurrentMp(pc.getMaxMp());
								pc.sendPackets(new S_ServerMessage(77));
								pc.sendPackets(new S_SkillSound(pc.getId(), 830));
								pc.sendPackets(new S_HPUpdate(
										pc.getCurrentHp(), pc.getMaxHp()));
								pc.sendPackets(new S_MPUpdate(
										pc.getCurrentMp(), pc.getMaxMp()));
							}
							// 使用隐藏之谷、歌唱之岛传送卷轴恢复血魔 end
						} else {
							pc.sendPackets(new S_ServerMessage(647));
						}
						cancelAbsoluteBarrier(pc); //  解除
					}
						break;
					case 40901:
					case 40902:
					case 40903:
					case 40904:
					case 40905:
					case 40906:
					case 40907:
					case 40908: { // 各种
						L1PcInstance partner = null;
						boolean partner_stat = false;
						if (pc.getPartnerId() != 0) { // 结婚中
							partner = (L1PcInstance) L1World.getInstance()
									.findObject(pc.getPartnerId());
							if (partner != null && partner.getPartnerId() != 0
									&& pc.getPartnerId() == partner.getId()
									&& partner.getPartnerId() == pc.getId()) {
								partner_stat = true;
							}
						} else {
							pc.sendPackets(new S_ServerMessage(662)); // \f1结婚。
							return;
						}

						if (partner_stat) {
							boolean castle_area = L1CastleLocation
									.checkInAllWarArea(
											// 城
											partner.getX(), partner.getY(),
											partner.getMapId());
							if ((partner.getMapId() == 0
									|| partner.getMapId() == 4 || partner
									.getMapId() == 304) && castle_area == false) {
								L1Teleport.teleport(pc, partner.getX(),
										partner.getY(), partner.getMapId(), 5,
										true);
							} else {
								pc.sendPackets(new S_ServerMessage(547)); // \f1今行
							}
						} else {
							pc.sendPackets(new S_ServerMessage(546)); // \f1今。
						}
					}
						break;
					case 40555: { // 秘密部屋
						if (pc.isKnight()
								&& (pc.getX() >= 32806 && // 部屋
								pc.getX() <= 32814)
								&& (pc.getY() >= 32798 && pc.getY() <= 32807)
								&& pc.getMapId() == 13) {
							short mapid = 13;
							L1Teleport.teleport(pc, 32815, 32810, mapid, 5,
									false);
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
						}
					}
						break;
					case 40417: { // 
						if ((pc.getX() >= 32665 && // 海贼岛
						pc.getX() <= 32674)
								&& (pc.getY() >= 32976 && pc.getY() <= 32985)
								&& pc.getMapId() == 440) {
							short mapid = 430;
							L1Teleport.teleport(pc, 32922, 32812, mapid, 5,
									true);
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
						}
					}
						break;
					case 40566: { //  
						if (pc.isElf()
								&& (pc.getX() >= 33971 && // 象牙塔村南魔方阵座标
								pc.getX() <= 33975)
								&& (pc.getY() >= 32324 && pc.getY() <= 32328)
								&& pc.getMapId() == 4
								&& !pc.getInventory().checkItem(40548)) { // 亡灵袋
							boolean found = false;
							for (L1Object obj : L1World.getInstance()
									.getObject()) {
								if (obj instanceof L1MonsterInstance) {
									L1MonsterInstance mob = (L1MonsterInstance) obj;
									if (mob != null) {
										if (mob.getNpcTemplate().get_npcId() == 45300) {
											found = true;
											break;
										}
									}
								}
							}
							if (found) {
								pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
							} else {
								L1SpawnUtil.spawnmob(pc, 45300, 0, 0);
								/*
								 * GMCommands.getInstance().mobspawn(client,
								 * 45300, 0, false); // 古代人亡灵
								 */}
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
						}
					}
						break;
					case 40006:
					case 40412:
					case 140006: {
						// 
						if (pc.getMap().isUsePainwand()) {
							S_DoActionGFX s_attackStatus = new S_DoActionGFX(
									pc.getId(), ActionCodes.ACTION_Wand);
							pc.sendPackets(s_attackStatus);
							pc.broadcastPacket(s_attackStatus);
							int chargeCount = useItem.getChargeCount();
							if (chargeCount <= 0 && itemId != 40412) {
								// \f1何起。
								pc.sendPackets(new S_ServerMessage(79));
								return;
							}
							int[] mobArray = { 45008, 45140, 45016, 45021,
									45025, 45033, 45099, 45147, 45123, 45130,
									45046, 45092, 45138, 45098, 45127, 45143,
									45149, 45171, 45040, 45155, 45192, 45173,
									45213, 45079, 45144 };
							// 鹿
							// 
							// 
							// 
							// 
							/*
							 * 45005, 45008, 45009, 45016, 45019, 45043, 45060,
							 * 45066, 45068, 45082, 45093, 45101, 45107, 45126,
							 * 45129, 45136, 45144, 45157, 45161, 45173, 45184,
							 * 45223 }; // 、、、、 // 
							 * 、、、、 // 、 、 //
							 *  、、、 //  、 、
							 * //  、、、 //
							 * 、、 、 // 
							 */
							int rnd = _random.nextInt(mobArray.length);
							L1SpawnUtil.spawnmob(pc, mobArray[rnd], 0, 0);
							/*
							 * GMCommands.getInstance().mobspawn(client,
							 * mobArray[rnd], 0, true);
							 */
						} else {
							// \f1何起。
							pc.sendPackets(new S_ServerMessage(79));
						}
						// 修正扣除问题
						if (itemId != 40412) {
							// 修正扣除问题 end
							useItem.setChargeCount(useItem.getChargeCount() - 1);
							pc.getInventory().updateItem(useItem,
									L1PcInventory.COL_CHARGE_COUNT);
							// 修正扣除问题
						} else {
							pc.getInventory().removeItem(useItem, 1);
						}
						// 修正扣除问题 end
					}
						break;
					case 40007: { //  
						// 电杖功能变更
						int chargeCount = useItem.getChargeCount();
						if (chargeCount <= 0) {
							cancelAbsoluteBarrier(pc); //  解除
							// pc.sendPackets(new
							// S_DoActionGFX(pc.getId(),ActionCodes.ACTION_Wand));
							// pc.broadcastPacket(new
							// S_DoActionGFX(pc.getId(),ActionCodes.ACTION_Wand));
							// \f1何起。
							pc.sendPackets(new S_ServerMessage(79));
							return;
						}

						L1Object target = L1World.getInstance().findObject(
								spellsc_objid);
						int Dmg = 0;
						if (target != null
								&& ((target instanceof L1MonsterInstance) || (target instanceof L1PcInstance))) {
							Dmg = doWandAction(pc, target);
						}

						// S_UseAttackSkill packet = new S_UseAttackSkill(
						// pc, spellsc_objid, 10, spellsc_x, spellsc_y,
						// ActionCodes.ACTION_Walk, Dmg);//ACTION_Wand
						//
						S_SkillSound packet = new S_SkillSound(spellsc_objid,
								10);
						pc.sendPackets(packet);
						pc.broadcastPacket(packet);
						if (Dmg != 0) {
							if (target instanceof L1Character) {
								if (target instanceof L1PcInstance) {
									((L1PcInstance) target)
											.sendPackets(new S_DoActionGFX(
													spellsc_objid,
													ActionCodes.ACTION_Damage));
								}
								((L1Character) target)
										.broadcastPacket(new S_DoActionGFX(
												spellsc_objid,
												ActionCodes.ACTION_Damage));
							}
						}

						useItem.setChargeCount(useItem.getChargeCount() - 1);
						pc.getInventory().updateItem(useItem,
								L1PcInventory.COL_CHARGE_COUNT);
						// 电杖功能变更 end
					}
						break;
					case 40008: /* 删除case 40410:删除 */
					case 140008: { // 变杖
						pc.sendPackets(new S_DoActionGFX(pc.getId(),
								ActionCodes.ACTION_Wand));
						pc.broadcastPacket(new S_DoActionGFX(pc.getId(),
								ActionCodes.ACTION_Wand));
						// System.out.println("变杖动作执行完毕");
						int chargeCount = useItem.getChargeCount();
						if (chargeCount <= 0 && itemId != 40410) {
							// \f1何起。
							pc.sendPackets(new S_ServerMessage(79));
							return;
						}
						// System.out.println("施展物品正确");
						if (pc.getId() != spellsc_objid) {
							pc.sendPackets(new S_SystemMessage(
									"目标错误，只能对自己进行变身！"));
							return;
						}
						L1Object target = pc;
						// 判断变身套装无法使用变卷
						// System.out.println("对象不为null");
						if (target != null && (target instanceof L1PcInstance)) {
							L1PcInstance target_pc = (L1PcInstance) target;
							if (target_pc.hasSkillEffect(Skill_AJ_0_10)) { // 装备变身套装
								if (target_pc.getId() == pc.getId()) { // 目标是自己
									pc.sendPackets(new S_ServerMessage(79));
								}
								cancelAbsoluteBarrier(pc); //  解除
								useItem.setChargeCount(useItem.getChargeCount() - 1);
								pc.getInventory().updateItem(useItem,
										L1PcInventory.COL_CHARGE_COUNT);
								return;
							}
						}
						// System.out.println("对象没穿变身套装，执行正确");
						// 判断变身套装无法使用变卷 end
						if (target != null) {
							L1Character cha = (L1Character) target;
							polyAction(pc, cha);
							cancelAbsoluteBarrier(pc); //  解除
							useItem.setChargeCount(useItem.getChargeCount() - 1);
							pc.getInventory().updateItem(useItem,
									L1PcInventory.COL_CHARGE_COUNT);
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
						}
						// if (pc.getId() == target.getId()) { // 自分
						// ;
						// } else if (target instanceof L1PcInstance) { //
						// PC
						// L1PcInstance targetpc = (L1PcInstance) target;
						// if (pc.getClanid() != 0
						// && pc.getClanid() == targetpc.getClanid()) { //
						// 同
						// ;
						// }
						// } else { // 他（NPC他PC）
						// }
					}
						break;
					case 40289:
					case 40290:
					case 40291:
					case 40292:
					case 40293:
					case 40294:
					case 40295:
					case 40296:
					case 40297: { // 傲慢塔11~91阶
						useToiTeleportAmulet(pc, itemId, useItem);
					}
						break;
					case 40280:
					case 40281:
					case 40282:
					case 40283:
					case 40284:
					case 40285:
					case 40286:
					case 40287:
					case 40288: {
						// 封印傲慢塔11～91阶
						pc.getInventory().removeItem(useItem, 1);
						L1ItemInstance item = pc.getInventory().storeItem(
								itemId + 9, 1);
						if (item != null) {
							pc.sendPackets(new S_ServerMessage(403, item
									.getLogName()));
						}
					}
						break;
					// 肉类
					case 40056:
					case 40057:
					case 40059:
					case 40060:
					case 40061:
					case 40062:
					case 40063:
					case 40064:
					case 40065:
					case 40069:
					case 40072:
					case 40073:
					case 140061:
					case 140062:
					case 140065:
					case 140069:
					case 140072:
					case 41296:
					case 41297: {
						pc.getInventory().removeItem(useItem, 1);
						if (pc.get_food() < 225) {
							final int foodvolume = pc.get_food() + 5;
							pc.set_food(foodvolume);
							pc.sendPackets(new S_PacketBox(S_PacketBox.FOOD, pc
									.get_food()));
						}
						if (itemId == 40057) { // 肉
							pc.setSkillEffect(STATUS_FLOATING_EYE, 0);
						}
						pc.sendPackets(new S_ServerMessage(76, useItem
								.getItem().getNameId()));
					}
						break;
					case 41298: { // 
						UseHeallingPotion(pc, 4, 189, itemId);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 41299: { // 
						UseHeallingPotion(pc, 15, 189, itemId);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 41300: { // 
						UseHeallingPotion(pc, 35, 189, itemId);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 41301: { // 
						int chance = _random.nextInt(10);
						if (chance >= 0 && chance < 5) {
							UseHeallingPotion(pc, 15, 189, itemId);
						} else if (chance >= 5 && chance < 9) {
							createNewItem(pc, 40019, 1);
						} else if (chance >= 9) {
							int gemChance = _random.nextInt(3);
							if (gemChance == 0) {
								createNewItem(pc, 40045, 1);
							} else if (gemChance == 1) {
								createNewItem(pc, 40049, 1);
							} else if (gemChance == 2) {
								createNewItem(pc, 40053, 1);
							}
						}
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 41302: { // 
						int chance = _random.nextInt(3);
						if (chance >= 0 && chance < 5) {
							UseHeallingPotion(pc, 15, 189, itemId);
						} else if (chance >= 5 && chance < 9) {
							createNewItem(pc, 40018, 1);
						} else if (chance >= 9) {
							int gemChance = _random.nextInt(3);
							if (gemChance == 0) {
								createNewItem(pc, 40047, 1);
							} else if (gemChance == 1) {
								createNewItem(pc, 40051, 1);
							} else if (gemChance == 2) {
								createNewItem(pc, 40055, 1);
							}
						}
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 41303: { // 
						int chance = _random.nextInt(3);
						if (chance >= 0 && chance < 5) {
							UseHeallingPotion(pc, 15, 189, itemId);
						} else if (chance >= 5 && chance < 9) {
							createNewItem(pc, 40015, 1);
						} else if (chance >= 9) {
							int gemChance = _random.nextInt(3);
							if (gemChance == 0) {
								createNewItem(pc, 40046, 1);
							} else if (gemChance == 1) {
								createNewItem(pc, 40050, 1);
							} else if (gemChance == 2) {
								createNewItem(pc, 40054, 1);
							}
						}
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 41304: { // 
						int chance = _random.nextInt(3);
						if (chance >= 0 && chance < 5) {
							UseHeallingPotion(pc, 15, 189, itemId);
						} else if (chance >= 5 && chance < 9) {
							createNewItem(pc, 40021, 1);
						} else if (chance >= 9) {
							int gemChance = _random.nextInt(3);
							if (gemChance == 0) {
								createNewItem(pc, 40044, 1);
							} else if (gemChance == 1) {
								createNewItem(pc, 40048, 1);
							} else if (gemChance == 2) {
								createNewItem(pc, 40052, 1);
							}
						}
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 40136:
					case 40137:
					case 40138:
					case 40139:
					case 40140:
					case 40141:
					case 40142:
					case 40143:
					case 40144:
					case 40145:
					case 40146:
					case 40147:
					case 40148:
					case 40149:
					case 40150:
					case 40151:
					case 40152:
					case 40153:
					case 40154:
					case 40155:
					case 40156:
					case 40157:
					case 40158:
					case 40159:
					case 40160:
					case 40161: { // 花火
						int soundid = 3198;
						if (itemId == 40154) {
							soundid = 3198;
						} else if (itemId == 40152) {
							soundid = 2031;
						} else if (itemId == 40141) {
							soundid = 2028;
						} else if (itemId == 40160) {
							soundid = 2030;
						} else if (itemId == 40145) {
							soundid = 2029;
						} else if (itemId == 40159) {
							soundid = 2033;
						} else if (itemId == 40151) {
							soundid = 2032;
						} else if (itemId == 40161) {
							soundid = 2037;
						} else if (itemId == 40142) {
							soundid = 2036;
						} else if (itemId == 40146) {
							soundid = 2039;
						} else if (itemId == 40148) {
							soundid = 2043;
						} else if (itemId == 40143) {
							soundid = 2041;
						} else if (itemId == 40156) {
							soundid = 2042;
						} else if (itemId == 40139) {
							soundid = 2040;
						} else if (itemId == 40137) {
							soundid = 2048;
						} else if (itemId == 40136) {
							soundid = 2046;
						} else if (itemId == 40138) {
							soundid = 2047;
						} else if (itemId == 40140) {
							soundid = 2051;
						} else if (itemId == 40144) {
							soundid = 2053;
						} else if (itemId == 40147) {
							soundid = 2045;
						} else if (itemId == 40149) {
							soundid = 2034;
						} else if (itemId == 40150) {
							soundid = 2055;
						} else if (itemId == 40153) {
							soundid = 2038;
						} else if (itemId == 40155) {
							soundid = 2044;
						} else if (itemId == 40157) {
							soundid = 2035;
						} else if (itemId == 40158) {
							soundid = 2049;
						} else {
							soundid = 3198;
						}

						S_SkillSound s_skillsound = new S_SkillSound(
								pc.getId(), soundid);
						pc.sendPackets(s_skillsound);
						pc.broadcastPacket(s_skillsound);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 41357:
					case 41358:
					case 41359:
					case 41360:
					case 41361:
					case 41362:
					case 41363:
					case 41364:
					case 41365:
					case 41366:
					case 41367:
					case 41368:
					case 41369:
					case 41370:
					case 41371:
					case 41372:
					case 41373:
					case 41374:
					case 41375:
					case 41376:
					case 41377:
					case 41378:
					case 41379:
					case 41380:
					case 41381:
					case 41382: { // 花火
						int soundid = itemId - 34946;
						S_SkillSound s_skillsound = new S_SkillSound(
								pc.getId(), soundid);
						pc.sendPackets(s_skillsound);
						pc.broadcastPacket(s_skillsound);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 40615: { // 影神殿2阶键
						if ((pc.getX() >= 32701 && pc.getX() <= 32705)
								&& (pc.getY() >= 32894 && pc.getY() <= 32898)
								&& pc.getMapId() == 522) { // 影神殿1F
							L1Teleport
									.teleport(pc, ((L1EtcItem) useItem
											.getItem()).get_locx(),
											((L1EtcItem) useItem.getItem())
													.get_locy(),
											((L1EtcItem) useItem.getItem())
													.get_mapid(), 5, true);
						} else {
							// \f1何起。
							pc.sendPackets(new S_ServerMessage(79));
						}
					}
						break;
					case 40616:
					case 40782:
					case 40783: {// 影神殿3阶键
						if ((pc.getX() >= 32698 && pc.getX() <= 32702)
								&& (pc.getY() >= 32894 && pc.getY() <= 32898)
								&& pc.getMapId() == 523) { // 影神殿2阶
							L1Teleport
									.teleport(pc, ((L1EtcItem) useItem
											.getItem()).get_locx(),
											((L1EtcItem) useItem.getItem())
													.get_locy(),
											((L1EtcItem) useItem.getItem())
													.get_mapid(), 5, true);
						} else {
							// \f1何起。
							pc.sendPackets(new S_ServerMessage(79));
						}
					}
						break;
					case 40692: { // 完成宝地图
						if (pc.getInventory().checkItem(40621)) {
							// \f1何起。
							pc.sendPackets(new S_ServerMessage(79));
						} else if ((pc.getX() >= 32856 && pc.getX() <= 32858)
								&& (pc.getY() >= 32857 && pc.getY() <= 32858)
								&& pc.getMapId() == 443) { // 海贼岛３阶
							L1Teleport
									.teleport(pc, ((L1EtcItem) useItem
											.getItem()).get_locx(),
											((L1EtcItem) useItem.getItem())
													.get_locy(),
											((L1EtcItem) useItem.getItem())
													.get_mapid(), 5, true);
						} else {
							// \f1何起。
							pc.sendPackets(new S_ServerMessage(79));
						}
					}
						break;
					case 41146: {// 招待状
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei001"));
					}
						break;
					case 40641: {// 
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"tscrollo"));
					}
						break;
					/*
					 * 删除case 40383: {// 地图：歌岛 pc.sendPackets(new
					 * S_NPCTalkReturn(pc.getId(), "ei035")); } break; case
					 * 40384: {//地图：隐溪谷 pc.sendPackets(new
					 * S_NPCTalkReturn(pc.getId(), "ei036")); } break; case
					 * 40101: {// 隐溪谷扫还 pc.sendPackets(new
					 * S_NPCTalkReturn(pc.getId(), "ei037")); } break;删除
					 */
					case 41209: {// 依赖书
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei002"));
					}
						break;
					case 41210: {// 研磨材
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei003"));
					}
						break;
					case 41211: {// 
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei004"));
					}
						break;
					case 41212: {// 特制
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei005"));
					}
						break;
					case 41213: {// 
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei006"));
					}
						break;
					case 41214: {// 运证
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei012"));
					}
						break;
					case 41215: {// 知证
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei010"));
					}
						break;
					case 41216: {// 力证
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei011"));
					}
						break;
					case 41222: {// 
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei008"));
					}
						break;
					case 41223: {// 武具破片
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei007"));
					}
						break;
					case 41224: {// 
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei009"));
					}
						break;
					case 41225: {// 发注书
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei013"));
					}
						break;
					case 41226: {// 药
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei014"));
					}
						break;
					case 41227: {// 绍介状
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei033"));
					}
						break;
					case 41228: {// 守
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei034"));
					}
						break;
					case 41229: {// 头
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei025"));
					}
						break;
					case 41230: {// 手纸
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei020"));
					}
						break;
					case 41231: {// 手纸
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei021"));
					}
						break;
					case 41233: {// 手纸
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei019"));
					}
						break;
					case 41234: {// 骨入袋
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei023"));
					}
						break;
					case 41235: {// 材料表
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei024"));
					}
						break;
					case 41236: {// 骨
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei026"));
					}
						break;
					case 41237: {// 骨
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei027"));
					}
						break;
					case 41239: {// 手纸
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei018"));
					}
						break;
					case 41240: {// 手纸
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei022"));
					}
						break;
					case 41060: { // 推荐书
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"nonames"));
					}
						break;
					case 41061: { // 调查团证书：地域-
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "kames"));
					}
						break;
					case 41062: { // 调查团证书：人间地域
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"bakumos"));
					}
						break;
					case 41063: { // 调查团证书：精灵地域-
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "bukas"));
					}
						break;
					case 41064: { // 调查团证书：地域
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"huwoomos"));
					}
						break;
					case 41065: { // 调查团证书：调查团长
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "noas"));
					}
						break;
					case 40701: { // 小宝地图
						if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 1) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
									"firsttmap"));
						} else if (pc.getQuest()
								.get_step(L1Quest.QUEST_LUKEIN1) == 2) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
									"secondtmapa"));
						} else if (pc.getQuest()
								.get_step(L1Quest.QUEST_LUKEIN1) == 3) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
									"secondtmapb"));
						} else if (pc.getQuest()
								.get_step(L1Quest.QUEST_LUKEIN1) == 4) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
									"secondtmapc"));
						} else if (pc.getQuest()
								.get_step(L1Quest.QUEST_LUKEIN1) == 5) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
									"thirdtmapd"));
						} else if (pc.getQuest()
								.get_step(L1Quest.QUEST_LUKEIN1) == 6) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
									"thirdtmape"));
						} else if (pc.getQuest()
								.get_step(L1Quest.QUEST_LUKEIN1) == 7) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
									"thirdtmapf"));
						} else if (pc.getQuest()
								.get_step(L1Quest.QUEST_LUKEIN1) == 8) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
									"thirdtmapg"));
						} else if (pc.getQuest()
								.get_step(L1Quest.QUEST_LUKEIN1) == 9) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
									"thirdtmaph"));
						} else if (pc.getQuest()
								.get_step(L1Quest.QUEST_LUKEIN1) == 10) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
									"thirdtmapi"));
						}
					}
						break;
					case 40663: { // 息子手纸
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"sonsletter"));
					}
						break;
					case 40630: { // 古日记
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"diegodiary"));
					}
						break;
					case 41208: {// 散魂
						if ((pc.getX() >= 32844 && pc.getX() <= 32845)
								&& (pc.getY() >= 32693 && pc.getY() <= 32694)
								&& pc.getMapId() == 550) { // 船墓场:地上层
							L1Teleport
									.teleport(pc, ((L1EtcItem) useItem
											.getItem()).get_locx(),
											((L1EtcItem) useItem.getItem())
													.get_locy(),
											((L1EtcItem) useItem.getItem())
													.get_mapid(), 5, true);
						} else {
							// \f1何起。
							pc.sendPackets(new S_ServerMessage(79));
						}
					}
						break;
					case 40700: { // 
						pc.sendPackets(new S_Sound(10));
						pc.broadcastPacket(new S_Sound(10));
						if ((pc.getX() >= 32619 && pc.getX() <= 32623)
								&& (pc.getY() >= 33120 && pc.getY() <= 33124)
								&& pc.getMapId() == 440) { // 海贼岛前半魔方阵座标
							boolean found = false;
							for (L1Object obj : L1World.getInstance()
									.getObject()) {
								if (obj instanceof L1MonsterInstance) {
									L1MonsterInstance mob = (L1MonsterInstance) obj;
									if (mob != null) {
										if (mob.getNpcTemplate().get_npcId() == 45875) {
											found = true;
											break;
										}
									}
								}
							}
							if (found) {
							} else {
								L1SpawnUtil.spawnmob(pc, 45875, 0, 0);
								/*
								 * GMCommands.getInstance().mobspawn(client,
								 * 45875, 0, false); // 
								 */}
						}
					}
						break;
					case 42501: { // 
						if (pc.getCurrentMp() < 10) {
							pc.sendPackets(new S_ServerMessage(278)); // \f1MP不足魔法使。
							return;
						}
						pc.setCurrentMp(pc.getCurrentMp() - 10);
						// pc.sendPackets(new S_CantMove()); //
						// 后移动不可能场合
						L1Teleport.teleport(pc, spellsc_x, spellsc_y,
								pc.getMapId(), pc.getHeading(), true,
								L1Teleport.CHANGE_POSITION);
					}
						break;
					case 41293:
					case 41294: { // 钓竿
						if (pc.getMapId() != 5124) {
							// 钓竿投。
							pc.sendPackets(new S_ServerMessage(1138));
							return;
						}
						startFishing(pc, itemId, fishX, fishY);
					}
						break;
					case 41255:
					case 41256:
					case 41257:
					case 41258:
					case 41259: { // 料理本
						/*
						 * 删除if (cookLevel == 0) { pc.sendPackets(new
						 * S_PacketBox(S_PacketBox .COOK_WINDOW)); } else {
						 * makeCooking(pc, cookLevel, cookNo); }删除
						 */
						// 料理书修正
						if (cookLevel == 0) { // 未开启
							pc.sendPackets(new S_PacketBox(
									S_PacketBox.COOK_WINDOW));
							return;
						}
						boolean ishasCampfire = false;
						// 判断周围是否有火
						for (L1Object obj : L1World.getInstance()
								.getVisibleObjects(pc, 2)) {
							if (obj instanceof L1EffectInstance) {
								L1EffectInstance npc = (L1EffectInstance) obj;
								if (npc.getNpcTemplate().get_npcId() == 81170) {
									ishasCampfire = true;
									break;
								}
							}
						}
						if (!ishasCampfire) {
							// $1160 为了料理所以需要柴火。
							pc.sendPackets(new S_ServerMessage(1160));
							return;
						}
						if (!checkMaterial(pc, cookNo)) {
							return;
						}
						if (pc.hasSkillEffect(Skill_AJ_0_5)) { // 料理中判断
							return;
						}
						pc.setSkillEffect(Skill_AJ_0_5, 1500);
						doFoodMaking(pc, cookNo);
						// 料理书修正 end
					}
						break;
					case 41260: { // 薪
						for (L1Object object : L1World.getInstance()
								.getVisibleObjects(pc)) {
							if (object instanceof L1EffectInstance) {
								if (((L1NpcInstance) object).getNpcTemplate()
										.get_npcId() == 81170) {
									// 周焚火。
									pc.sendPackets(new S_ServerMessage(1162));
									return;
								}
							}
						}
						int[] loc = new int[2];
						loc = pc.getFrontLoc();
						L1EffectSpawn.getInstance().spawnEffect(81170,
								300 * 1000, loc[0], loc[1], pc.getMapId());
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 41277:
					case 41278:
					case 41279:
					case 41280:
					case 41281:
					case 41282:
					case 41283:
					case 41284:
					case 41285:
					case 41286:
					case 41287:
					case 41288:
					case 41289:
					case 41290:
					case 41291:
					case 41292: { // 料理
						L1Cooking.useCookingItem(pc, useItem);
					}
						break;
					case 41383:
					case 41384:
					case 41385:
					case 41386:
					case 41387:
					case 41388:
					case 41389:
					case 41390:
					case 41391:
					case 41392:
					case 41393:
					case 41394:
					case 41395:
					case 41396:
					case 41397:
					case 41398:
					case 41399:
					case 41400: { // 家具
						useFurnitureItem(pc, itemId, itemObjid);
					}
						break;
					case 41401: { // 家具除去
						useFurnitureRemovalWand(pc, spellsc_objid, useItem);
					}
						break;
					case 40922: {// 激励药水
						if (pc.hasSkillEffect(71) == true) { // 状态
							pc.sendPackets(new S_ServerMessage(698)); // \f1魔力何饮。
							return;
						}

						if (pc.hasSkillEffect(42)) {
							pc.removeSkillEffect(42);
						}

						if (pc.hasSkillEffect(STATUS_STR_POISON)) {
							pc.sendPackets(new S_Strup(pc, 5, 600));
							pc.setSkillEffect(STATUS_STR_POISON, 600 * 1000);
							pc.sendPackets(new S_SkillSound(pc.getId(), 751));
							pc.broadcastPacket(new S_SkillSound(pc.getId(), 751));
						} else {
							pc.addStr((byte) 6);
							pc.sendPackets(new S_Strup(pc, 5, 600));
							pc.setSkillEffect(STATUS_STR_POISON, 600 * 1000);
							pc.sendPackets(new S_SkillSound(pc.getId(), 751));
							pc.broadcastPacket(new S_SkillSound(pc.getId(), 751));
						}
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 40923: {// 能力药水
						if (pc.hasSkillEffect(71) == true) { // 状态
							pc.sendPackets(new S_ServerMessage(698)); // \f1魔力何饮。
							return;
						}

						if (pc.hasSkillEffect(26)) {
							pc.removeSkillEffect(26);
						}

						if (pc.hasSkillEffect(STATUS_DEX_POISON)) {
							pc.sendPackets(new S_Dexup(pc, 5, 600));
							pc.setSkillEffect(STATUS_DEX_POISON, 600 * 1000);
							pc.sendPackets(new S_SkillSound(pc.getId(), 750));
							pc.broadcastPacket(new S_SkillSound(pc.getId(), 750));
						} else {
							pc.addDex((byte) 6);
							pc.sendPackets(new S_Dexup(pc, 5, 600));
							pc.setSkillEffect(STATUS_DEX_POISON, 600 * 1000);
							pc.sendPackets(new S_SkillSound(pc.getId(), 750));
							pc.broadcastPacket(new S_SkillSound(pc.getId(), 750));
						}
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 40924: {// 花神药水
						if (pc.hasSkillEffect(71) == true) { // 状态
							pc.sendPackets(new S_ServerMessage(698)); // \f1魔力何饮。
							return;
						}

						if (pc.hasSkillEffect(42)) {
							pc.removeSkillEffect(42);
						}

						if (pc.hasSkillEffect(STATUS_STR_POISON)) {
							pc.sendPackets(new S_Strup(pc, 5, 600));
							pc.setSkillEffect(STATUS_STR_POISON, 600 * 1000);
							pc.sendPackets(new S_SkillSound(pc.getId(), 751));
							pc.broadcastPacket(new S_SkillSound(pc.getId(), 751));
						} else {
							pc.addStr((byte) 6);
							pc.sendPackets(new S_Strup(pc, 5, 600));
							pc.setSkillEffect(STATUS_STR_POISON, 600 * 1000);
							pc.sendPackets(new S_SkillSound(pc.getId(), 751));
							pc.broadcastPacket(new S_SkillSound(pc.getId(), 751));
						}

						if (pc.hasSkillEffect(26)) {
							pc.removeSkillEffect(26);
						}

						if (pc.hasSkillEffect(STATUS_DEX_POISON)) {
							pc.sendPackets(new S_Dexup(pc, 5, 600));
							pc.setSkillEffect(STATUS_DEX_POISON, 600 * 1000);
							pc.sendPackets(new S_SkillSound(pc.getId(), 750));
							pc.broadcastPacket(new S_SkillSound(pc.getId(), 750));
						} else {
							pc.addDex((byte) 6);
							pc.sendPackets(new S_Dexup(pc, 5, 600));
							pc.setSkillEffect(STATUS_DEX_POISON, 600 * 1000);
							pc.sendPackets(new S_SkillSound(pc.getId(), 750));
							pc.broadcastPacket(new S_SkillSound(pc.getId(), 750));
						}
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 41143: {// 海贼骷髅首领变身药水
						// 限制地图不可变身
						if (pc.getMapId() == 5124) {
							pc.sendPackets(new S_ServerMessage(1170));
							return;
						}
						// 限制地图不可变身 end
						L1Skills skillTemp = SkillsTable.getInstance()
								.getTemplate(SHAPE_CHANGE);
						L1PolyMorph.doPoly(pc, 6086,
								skillTemp.getBuffDuration());
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 41144: {// 海贼骷髅士兵变身药水
						// 限制地图不可变身
						if (pc.getMapId() == 5124) {
							pc.sendPackets(new S_ServerMessage(1170));
							return;
						}
						// 限制地图不可变身 end
						L1Skills skillTemp = SkillsTable.getInstance()
								.getTemplate(SHAPE_CHANGE);
						L1PolyMorph.doPoly(pc, 6087,
								skillTemp.getBuffDuration());
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case 41145: {// 海贼骷髅刀手变身药水
						// 限制地图不可变身
						if (pc.getMapId() == 5124) {
							pc.sendPackets(new S_ServerMessage(1170));
							return;
						}
						// 限制地图不可变身 end
						L1Skills skillTemp = SkillsTable.getInstance()
								.getTemplate(SHAPE_CHANGE);
						L1PolyMorph.doPoly(pc, 6088,
								skillTemp.getBuffDuration());
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case Item_EPU_1: { // 溶解剂
						L1WilliamMagicCrystalItem MagicCrystal_Item = MagicCrystalItem
								.getInstance().getTemplate(
										l1iteminstance1.getItem().getItemId());
						if (MagicCrystal_Item != null) {
							if (l1iteminstance1.isEquipped()) {
								pc.sendPackets(new S_ServerMessage(166,
										l1iteminstance1.getLogName(),
										L1WilliamSystemMessage
												.ShowMessage(1140)));
								return;
							}
							int MinCount = L1WilliamMagicCrystalItem
									.getMinCount(l1iteminstance1.getItem()
											.getItemId());
							int MaxCount = L1WilliamMagicCrystalItem
									.getMaxCount(l1iteminstance1.getItem()
											.getItemId());
							int AddCount = MaxCount - MinCount + 1;

							if (AddCount > 1) {
								MinCount += _random.nextInt(AddCount);
							}

							if (MinCount > 0) {// 数量大于 0
								pc.getInventory()
										.removeItem(l1iteminstance1, 1); // 删除被溶解的物品
								if (createNewItem(pc, Item_EPU_2, MinCount)) { // 获得魔法结晶体
									pc.getInventory().removeItem(useItem, 1); // 删除溶解剂
								}
							} else {
								pc.sendPackets(new S_ServerMessage(1161));
							}
						} else {
							pc.sendPackets(new S_ServerMessage(1161));
						}
					}
						break;
					// case 40070:
					case 41252: { // 进化果实 && 珍奇的乌龟
						pc.sendPackets(new S_ServerMessage(76, useItem
								.getLogName()));
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case Item_EPU_63: {// 勇者的南瓜袋子
						int l1 = 0;
						byte l2 = (byte) 1;
						int k3 = _random.nextInt(100) + 1;
						if (k3 < 86) {
							int k = _random.nextInt(30) + 1;
							switch (k) {
							case 1:
							case 11:
							case 18:
							case 25:
							case 27: {
								l1 = 40010;// 治愈药水
								l2 = 15;
							}
								break;
							case 2:
							case 12:
							case 26: {
								l1 = 40010;// 治愈药水
								l2 = 30;
							}
								break;
							case 3:
							case 13:
							case 19:
							case 28: {
								l1 = 40011;// 强力治愈药水
								l2 = 8;
							}
								break;
							case 4:
							case 14:
							case 29: {
								l1 = 40011;// 强力治愈药水
								l2 = 16;
							}
								break;
							case 5:
							case 16: {
								l1 = 40012;// 终极治愈药水
								l2 = 4;
							}
								break;
							case 6:
							case 15:
							case 17:
							case 24: {
								l1 = 40013;// 自我加速药水
								l2 = 8;
							}
								break;
							case 7:
							case 20:
							case 23: {
								l1 = 40318;// 魔法宝石
								l2 = 5;
							}
								break;
							case 8:
							case 30: {
								l1 = 40318;// 魔法宝石
								l2 = 10;
							}
								break;
							case 9:
							case 22: {
								l1 = 40319;// 精灵玉
								l2 = 10;
							}
								break;
							case 10:
							case 21: {
								l1 = 40088;// 变形卷轴
								l2 = 2;
							}
								break;
							}
						} else if (k3 > 85 && k3 < 92) {
							l1 = 40074;// 对盔甲施法的卷轴6%
						} else if (k3 > 91 && k3 < 97) {
							l1 = 40087;// 对武器施法的卷轴5%
						} else if (k3 > 96) {
							l1 = Item_EPU_7;// 骷髅圣杯4%
						}
						if (createNewItem(pc, l1, l2)) {
							pc.getInventory().removeItem(useItem, 1);
						}
					}
						break;
					case Item_EPU_66: {// 惊喜箱
						int l1 = 0;
						byte l2 = (byte) 1;
						int k3 = _random.nextInt(100) + 1;
						if (k3 < 48) {
							l1 = 40068;// 精灵饼干47%
							l2 = 10;
						} else if (k3 > 47 && k3 < 95) {
							l1 = 40031;// 恶魔之血47%
							l2 = 10;
						} else if (k3 > 94) {
							l1 = Item_EPU_10;// 胜利的徽章6%
						}
						if (createNewItem(pc, l1, l2)) {
							pc.getInventory().removeItem(useItem, 1);
						}
					}
						break;
					case Item_EPU_72: {// 拉罗森的推荐书
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "rarson"));
					}
						break;
					case Item_EPU_110: {// 波伦的袋子
						int l1 = 0;
						byte l2 = (byte) 1;
						int k3 = _random.nextInt(100) + 1;
						if (k3 < 77) {
							int k = _random.nextInt(27) + 1;
							switch (k) {
							case 1:
							case 12:
							case 18:
							case 20:
							case 26: {
								l1 = 40022;// 古代体力恢复剂
								l2 = 3;
							}
								break;
							case 2:
							case 13:
							case 19: {
								l1 = 40023;// 古代强力体力恢复剂
								l2 = 3;
							}
								break;
							case 3:
							case 14: {
								l1 = 40024;// 古代终极体力恢复剂
								l2 = 3;
							}
								break;
							case 4: {
								l1 = 40410;// 黑暗安特的树皮
							}
								break;
							case 5:
							case 21: {
								l1 = 40307;// 沙哈之石
							}
								break;
							case 6:
							case 22: {
								l1 = 40304;// 马普勒之石
							}
								break;
							case 7:
							case 23: {
								l1 = 40306;// 伊娃之石
							}
								break;
							case 8:
							case 24: {
								l1 = 40305;// 帕格里奥之石
							}
								break;
							case 9:
							case 16:
							case 25:
							case 27: {
								l1 = 40458;// 光明的鳞片
							}
								break;
							case 10:
							case 15: {
								l1 = 40428;// 月光之泪
							}
								break;
							case 11:
							case 17: {
								l1 = 40508;// 奥里哈鲁根
							}
								break;
							}

						} else if (k3 > 76 && k3 < 84) {
							l1 = 40410;// 黑暗安特的树皮7%
							l2 = 5;
						} else if (k3 > 83 && k3 < 91) {
							l1 = 40441;// 白金原石7%
						} else if (k3 > 91 && k3 < 96) {
							l1 = 40087;// 对武器施法的卷轴4%
						} else if (k3 > 95) {
							l1 = 40074;// 对盔甲施法的卷轴5%
						}
						if (createNewItem(pc, l1, l2)) {
							pc.getInventory().removeItem(useItem, 1);
						}
					}
						break;
					case Item_EPU_111: {// 波伦的资源清单
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"rparum3"));
					}
						break;
					case Item_AJ_16:
					case Item_AJ_30: { // 瞬间移动戒指
						if (!pc.isGm() && itemId == Item_AJ_16) {
							pc.sendPackets(new S_ServerMessage(1053)); // 无使用权限
							return;
						}
						if (pc.glanceCheck(spellsc_x, spellsc_y) == false) {
							return; // 直线上障害物
						}
						if (pc.getCurrentMp() < 10) {
							pc.sendPackets(new S_ServerMessage(278)); // \f1MP不足魔法使。
							return;
						}
						if (itemId == Item_AJ_30) {
							int chargeCount = useItem.getChargeCount();
							if (chargeCount <= 0) {
								pc.sendPackets(new S_ServerMessage(79));
								return;
							}
							useItem.setChargeCount(useItem.getChargeCount() - 1);
							pc.getInventory().updateItem(useItem,
									L1PcInventory.COL_CHARGE_COUNT);
						}

						pc.setHeading(pc.targetDirection(spellsc_x, spellsc_y));
						pc.setCurrentMp(pc.getCurrentMp() - 10);
						L1Teleport.teleport(pc, spellsc_x, spellsc_y,
								pc.getMapId(), pc.getHeading(), true,
								L1Teleport.CHANGE_POSITION);
					}
						break;
					case Item_AJ_31: { // 重生药水
						if (pc.getLevel() <= 10) {
							pc.sendPackets(new S_ServerMessage(79));
							return;
						}
						pc.sendPackets(new S_SkillSound(pcObjid, 6505));
						pc.broadcastPacket(new S_SkillSound(pcObjid, 6505));
						pc.getInventory().takeoffEquip(945);// 用来脱掉全身装备
						pc.setExp(10000);
						pc.resExp();
						pc.addBaseMaxHp((short) (-1 * (int) ((double) pc
								.getBaseMaxHp() - 12)));
						pc.addBaseMaxMp((short) (-1 * (int) ((double) pc
								.getBaseMaxMp() - 12)));
						pc.resetBaseAc();
						pc.resetBaseMr();
						pc.resetBaseHitup();
						pc.resetBaseDmgup();
						pc.sendPackets(new S_OwnCharStatus(pc));
						pc.sendPackets(new S_ServerMessage(822));
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case Item_AJ_17: { // 宠物指环
						L1MonsterInstance Target = (L1MonsterInstance) L1World
								.getInstance().findObject(spellsc_objid);
						if (Target == null) {
							pc.sendPackets(new S_ServerMessage(328));
							return;
						}
						pc.setPetTarget(Target);
					}
						break;
					case Item_AJ_19: {// 宠物活力药水
						pc.sendPackets(new S_SystemMessage(
								L1WilliamSystemMessage.ShowMessage(1002))); // 从DB取得讯息
					}
						break;
					case Item_AJ_20: {// 宠物魔力药水
						pc.sendPackets(new S_SystemMessage(
								L1WilliamSystemMessage.ShowMessage(1003))); // 从DB取得讯息
					}
						break;
					case Item_AJ_21: { // 魔法徽章
						pc.sendPackets(new S_SystemMessage(
								L1WilliamSystemMessage.ShowMessage(1004))); // 从DB取得讯息
					}
						break;
					case Item_EPU_73: { // 可恩的便条纸
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "kuen"));
					}
						break;
					case Item_EPU_84: { // 标本制作委托书
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"anirequest"));
					}
						break;
					case Item_EPU_101: { // 罗宾孙的便条纸1
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"robinscroll"));
					}
						break;
					case Item_EPU_102: { // 罗宾孙的便条纸2
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"robinscroll2"));
					}
						break;
					case Item_EPU_103: { // 罗宾孙的推荐书
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"robinhood"));
					}
						break;
					case Item_EPU_70: { // 圣水
						if (pc.hasSkillEffect(Skill_AJ_0_9)) {
							// 伊娃的圣水效果
							pc.sendPackets(new S_ServerMessage(79));
							return;
						}
						if (pc.hasSkillEffect(Skill_AJ_0_8)) { // 移除神圣的米索莉粉效果
							pc.removeSkillEffect(Skill_AJ_0_8);
						}
						pc.setSkillEffect(Skill_AJ_0_7, 900 * 1000); // 给予圣水效果
						pc.sendPackets(new S_SkillSound(pc.getId(), 190));
						pc.broadcastPacket(new S_SkillSound(pc.getId(), 190));
						pc.sendPackets(new S_ServerMessage(1141));
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case Item_EPU_71: { // 神圣的米索莉粉
						if (pc.hasSkillEffect(Skill_AJ_0_9)) {
							// 伊娃的圣水效果
							pc.sendPackets(new S_ServerMessage(79));
							return;
						}
						if (pc.hasSkillEffect(Skill_AJ_0_7)) { // 移除圣水效果
							pc.removeSkillEffect(Skill_AJ_0_7);
						}
						pc.setSkillEffect(Skill_AJ_0_8, 900 * 1000); // 给予神圣的米索莉粉效果
						pc.sendPackets(new S_SkillSound(pc.getId(), 190));
						pc.broadcastPacket(new S_SkillSound(pc.getId(), 190));
						pc.sendPackets(new S_ServerMessage(1142));
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case Item_EPU_109: { // 伊娃的圣水
						if (pc.hasSkillEffect(Skill_AJ_0_7)
								|| pc.hasSkillEffect(Skill_AJ_0_8)) {
							// 圣水效果、神圣的米索莉粉效果
							pc.sendPackets(new S_ServerMessage(79));
							return;
						}
						pc.setSkillEffect(Skill_AJ_0_9, 900 * 1000); // 给予伊娃的圣水效果
						pc.sendPackets(new S_SkillSound(pc.getId(), 190));
						pc.broadcastPacket(new S_SkillSound(pc.getId(), 190));
						pc.sendPackets(new S_ServerMessage(1140));
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case Item_AJ_2_1: { // 精灵的祝福御守
						if (createNewItem(pc, Armor_AJ_1_1, 1)) {// 布拉斯耳环
							pc.getInventory().removeItem(useItem, 1);
						}
					}
						break;
					case Item_AJ_2_2: { // 精灵的祝福签诗
						if (createNewItem(pc, Armor_AJ_1_2, 1)) {// 阿门项链
							pc.getInventory().removeItem(useItem, 1);
						}
					}
						break;
					case 40582: { // 安迪亚之袋
						if (createNewItem(pc, 40583, 1)) {// 安迪亚之信
							pc.getInventory().removeItem(useItem, 1);
						}
					}
						break;
					case 40600: { // 堕落钥匙
						if (((pc.getX() == 32619 && pc.getY() == 32909) || (pc
								.getX() == 32619 && pc.getY() == 32907))
								&& pc.getMapId() == 306) {
							L1Teleport.teleport(pc, 32620, 32897, (short) 306,
									5, true);
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
						}
					}
						break;
					case 40606: { // 混沌钥匙
						if (pc.getX() >= 32620 && pc.getX() <= 32622
								&& pc.getY() >= 32897 && pc.getY() <= 32898
								&& pc.getMapId() == 306) {
							L1Teleport.teleport(pc, 32591, 32813, (short) 306,
									5, true);
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
						}
					}
						break;
					case 40557: { // 暗杀名单(古鲁丁村)
						if (pc.getX() == 32620 && pc.getY() == 32641
								&& pc.getMapId() == 4) {
							for (L1NpcInstance npc : L1World.getInstance()
									.getAllNpcs()) {
								if (npc.getNpcTemplate().get_npcId() == Npc_AJ_2_15) { // 力比存在
									pc.sendPackets(new S_ServerMessage(79));
									return;
								}
							}
							L1SpawnUtil.spawnmob(pc, Npc_AJ_2_15, 0, 0);
							// GMCommands.getInstance().mobspawn(client,
							// Npc_AJ_2_15, 0, false); // 召唤力
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
						}
					}
						break;
					case 40563: { // 暗杀名单(燃柳村)
						if (pc.getX() == 32730 && pc.getY() == 32426
								&& pc.getMapId() == 4) {
							for (L1NpcInstance npc : L1World.getInstance()
									.getAllNpcs()) {
								if (npc.getNpcTemplate().get_npcId() == Npc_AJ_2_16) { // 戴度普存在
									pc.sendPackets(new S_ServerMessage(79));
									return;
								}
							}
							L1SpawnUtil.spawnmob(pc, Npc_AJ_2_16, 0, 0);
							// GMCommands.getInstance().mobspawn(client,
							// Npc_AJ_2_16, 0, false); // 召唤戴度普
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
						}
					}
						break;
					case 40561: { // 暗杀名单(肯特村)
						if (pc.getX() == 33046 && pc.getY() == 32806
								&& pc.getMapId() == 4) {
							for (L1NpcInstance npc : L1World.getInstance()
									.getAllNpcs()) {
								if (npc.getNpcTemplate().get_npcId() == Npc_AJ_2_17) { // 依卡存在
									pc.sendPackets(new S_ServerMessage(79));
									return;
								}
							}
							L1SpawnUtil.spawnmob(pc, Npc_AJ_2_17, 0, 0);
							// GMCommands.getInstance().mobspawn(client,
							// Npc_AJ_2_17, 0, false); // 召唤依卡
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
						}
					}
						break;
					case 40560: { // 暗杀名单(风木村)
						if (pc.getX() == 32580 && pc.getY() == 33260
								&& pc.getMapId() == 4) {
							for (L1NpcInstance npc : L1World.getInstance()
									.getAllNpcs()) {
								if (npc.getNpcTemplate().get_npcId() == Npc_AJ_2_18) { // 托得存在
									pc.sendPackets(new S_ServerMessage(79));
									return;
								}
							}
							L1SpawnUtil.spawnmob(pc, Npc_AJ_2_18, 0, 0);
							// GMCommands.getInstance().mobspawn(client,
							// Npc_AJ_2_18, 0, false); // 召唤托得
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
						}
					}
						break;
					case 40562: { // 暗杀名单(海音村)
						if (pc.getX() == 33447 && pc.getY() == 33476
								&& pc.getMapId() == 4) {
							for (L1NpcInstance npc : L1World.getInstance()
									.getAllNpcs()) {
								if (npc.getNpcTemplate().get_npcId() == Npc_AJ_2_19) { // 罗德存在
									pc.sendPackets(new S_ServerMessage(79));
									return;
								}
							}
							L1SpawnUtil.spawnmob(pc, Npc_AJ_2_19, 0, 0);
							// GMCommands.getInstance().mobspawn(client,
							// Npc_AJ_2_19, 0, false); // 召唤罗德
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
						}
					}
						break;
					case 40559: { // 暗杀名单(亚丁城镇)
						if (pc.getX() == 34215 && pc.getY() == 33195
								&& pc.getMapId() == 4) {
							for (L1NpcInstance npc : L1World.getInstance()
									.getAllNpcs()) {
								if (npc.getNpcTemplate().get_npcId() == Npc_AJ_2_20) { // 亚特辛存在
									pc.sendPackets(new S_ServerMessage(79));
									return;
								}
							}
							L1SpawnUtil.spawnmob(pc, Npc_AJ_2_20, 0, 0);
							// GMCommands.getInstance().mobspawn(client,
							// Npc_AJ_2_20, 0, false); // 召唤亚特辛
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
						}
					}
						break;
					case 40558: { // 暗杀名单(奇岩村)
						if (pc.getX() == 33513 && pc.getY() == 32890
								&& pc.getMapId() == 4) {
							for (L1NpcInstance npc : L1World.getInstance()
									.getAllNpcs()) {
								if (npc.getNpcTemplate().get_npcId() == Npc_AJ_2_21) { // 托达存在
									pc.sendPackets(new S_ServerMessage(79));
									return;
								}
							}
							L1SpawnUtil.spawnmob(pc, Npc_AJ_2_21, 0, 0);
							// GMCommands.getInstance().mobspawn(client,
							// Npc_AJ_2_21, 0, false); // 召唤托达
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
						}
					}
						break;
					case 40572: { // 刺客之证
						if (pc.getX() == 32778 && pc.getY() == 32738
								&& pc.getMapId() == 21) {
							L1Teleport.teleport(pc, 32781, 32728, (short) 21,
									5, false);
						} else if (pc.getX() == 32781 && pc.getY() == 32728
								&& pc.getMapId() == 21) {
							L1Teleport.teleport(pc, 32778, 32738, (short) 21,
									5, false);
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
						}
					}
						break;
					case Item_AJ_25: { // 装备鉴定卷轴
						装备鉴定(pc, l1iteminstance1);
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					case Item_AJ_26: { // 神秘的强化水晶
						L1WilliamArmorUpgrade Armor_Upgrade = ArmorUpgrade
								.getInstance().getTemplate(
										l1iteminstance1.getItem().getItemId());
						if (Armor_Upgrade != null
								&& l1iteminstance1.getItem().getType2() == 2) { // 有可强化的装备

							if (l1iteminstance1.isEquipped()) {
								pc.sendPackets(new S_ServerMessage(166,
										l1iteminstance1.getLogName()
												+ " "
												+ L1WilliamSystemMessage
														.ShowMessage(1019)));
								return;
							}

							强化装备(pc, l1iteminstance1);
							pc.getInventory().removeItem(useItem, 1);
						} else {
							pc.sendPackets(new S_ServerMessage(79));
						}
					}
						break;
					case Item_AJ_28: { // 偷窥卡
						L1Object target = L1World.getInstance().findObject(
								spellsc_objid);
						if (target != null) {
							String msg0 = "";
							String msg1 = "";
							String msg2 = "";
							String msg3 = "";
							String msg4 = "";
							String msg5 = "";
							String msg6 = "";
							String msg7 = "";
							String msg8 = "";
							String msg9 = "";
							String msg10 = "";

							if (target instanceof L1PcInstance) {
								L1PcInstance target_pc = (L1PcInstance) target;
								if (!target_pc.getInventory().checkItem(
										Item_AJ_29, 1)) {
									msg0 = target_pc.getName();
									msg1 = "" + target_pc.getLevel();
									msg2 = "" + target_pc.getCurrentHp()
											+ " / " + target_pc.getMaxHp();
									msg3 = "" + target_pc.getCurrentMp()
											+ " / " + +target_pc.getMaxMp();
									msg4 = "" + target_pc.getAc();
									msg5 = "" + target_pc.getEr();
									msg6 = "" + target_pc.getMr() + " %";
									msg7 = "" + target_pc.getFire() + " %";
									msg8 = "" + target_pc.getWater() + " %";
									msg9 = "" + target_pc.getWind() + " %";
									msg10 = "" + target_pc.getEarth() + " %";
									String msg[] = { msg0, msg1, msg2, msg3,
											msg4, msg5, msg6, msg7, msg8, msg9,
											msg10 };
									pc.sendPackets(new S_NPCTalkReturn(pc
											.getId(), "ajplayer", msg));
								} else {
									int deleitem = Item_AJ_29;
									target_pc
											.sendPackets(new S_SystemMessage(pc
													.getName()
													+ " "
													+ L1WilliamSystemMessage
															.ShowMessage(1017))); // 从DB取得讯息
									pc.sendPackets(new S_SystemMessage(
											target_pc.getName()
													+ " "
													+ L1WilliamSystemMessage
															.ShowMessage(1018))); // 从DB取得讯息
									target_pc.getInventory().consumeItem(
											deleitem, 1);
								}
							} else if (target instanceof L1MonsterInstance) {
								L1MonsterInstance target_npc = (L1MonsterInstance) target;
								msg0 = target_npc.getName();
								msg1 = "" + target_npc.getLevel();
								msg2 = "" + target_npc.getCurrentHp() + " / "
										+ target_npc.getMaxHp();
								msg3 = "" + target_npc.getCurrentMp() + " / "
										+ +target_npc.getMaxMp();
								msg4 = "" + target_npc.getAc();
								msg5 = "0";
								msg6 = "" + target_npc.getMr() + " %";
								msg7 = "" + target_npc.getFire() + " %";
								msg8 = "" + target_npc.getWater() + " %";
								msg9 = "" + target_npc.getWind() + " %";
								msg10 = "" + target_npc.getEarth() + " %";
								String msg[] = { msg0, msg1, msg2, msg3, msg4,
										msg5, msg6, msg7, msg8, msg9, msg10 };
								pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
										"ajplayer", msg));
							} else {
								pc.sendPackets(new S_ServerMessage(79));
							}
						}
						pc.getInventory().removeItem(useItem, 1);
					}
						break;
					default: {
						// 传送卷轴、召唤道具、魔法道具
						if (itemId == L1WilliamItemMagic.checkItemId(itemId)) {
							L1WilliamItemMagic Item_Magic = ItemMagic
									.getInstance().getTemplate(itemId);

							if (Item_Magic.getCheckClass() != 0) { // 职业判断
								byte class_id = (byte) 0;
								String msg = "";

								if (pc.isCrown()) { // 王族
									class_id = 1;
								} else if (pc.isKnight()) { // 骑士
									class_id = 2;
								} else if (pc.isWizard()) { // 法师
									class_id = 3;
								} else if (pc.isElf()) { // 妖精
									class_id = 4;
								} else if (pc.isDarkelf()) { // 黑妖
									class_id = 5;
								} else if (pc.isDragonKnight()) { // 黑妖
									class_id = 6;
								} else if (pc.isIllusionist()) { // 黑妖
									class_id = 7;
								}

								switch (Item_Magic.getCheckClass()) {
								case 1:
									msg = "王族";
									break;
								case 2:
									msg = "骑士";
									break;
								case 3:
									msg = "法师";
									break;
								case 4:
									msg = "妖精";
									break;
								case 5:
									msg = "黑暗妖精";
									break;
								case 6:
									msg = "龙骑士";
									break;
								case 7:
									msg = "幻术师";
									break;
								}

								if (Item_Magic.getCheckClass() != class_id) { // 职业不符
									pc.sendPackets(new S_ServerMessage(166,
											L1WilliamSystemMessage
													.ShowMessage(1097), msg,
											L1WilliamSystemMessage
													.ShowMessage(1114)));
									return;
								}
							}

							if (Item_Magic.getCheckItem() != 0) { // 携带物品判断
								if (!pc.getInventory().checkItem(
										Item_Magic.getCheckItem())) {
									L1Item temp = ItemTable.getInstance()
											.getTemplate(
													Item_Magic.getCheckItem());
									pc.sendPackets(new S_ServerMessage(166,
											L1WilliamSystemMessage
													.ShowMessage(1113), " ("
													+ temp.getName() + ") ",
											L1WilliamSystemMessage
													.ShowMessage(1114)));
									return;
								}
							}

							if (spellsc_objid == pc.getId()
									&& useItem.getItem().getUseType() != 30) { // spell_buff
								pc.sendPackets(new S_ServerMessage(281)); // \f1魔法无佅。
								return;
							}
							if (spellsc_objid == 0
									&& useItem.getItem().getUseType() != 0
									&& useItem.getItem().getUseType() != 26
									&& useItem.getItem().getUseType() != 27) {
								return;
								// 场合handleCommands送return
								// handleCommands判＆理部分
							}

							cancelAbsoluteBarrier(pc);

							L1SkillUse l1skilluse = new L1SkillUse();
							if (Item_Magic.getRemoveItem() == 0) { // 删除道具判断
								switch (Item_Magic.getSkillId()) {
								case 12:
								case 21:
								case 107:
									l1skilluse.handleCommands(
											_client.getActiveChar(),
											Item_Magic.getSkillId(), l, 0, 0,
											null, 0, L1SkillUse.TYPE_NORMAL);
									break;

								default:
									l1skilluse.handleCommands(
											_client.getActiveChar(),
											Item_Magic.getSkillId(),
											spellsc_objid, spellsc_x,
											spellsc_y, null, 0,
											L1SkillUse.TYPE_NORMAL);
									break;
								}
							} else {
								switch (Item_Magic.getSkillId()) {
								case 12:
								case 21:
								case 107:
									l1skilluse.handleCommands(
											_client.getActiveChar(),
											Item_Magic.getSkillId(), l, 0, 0,
											null, 0, L1SkillUse.TYPE_SPELLSC);
									break;

								default:
									l1skilluse.handleCommands(
											_client.getActiveChar(),
											Item_Magic.getSkillId(),
											spellsc_objid, spellsc_x,
											spellsc_y, null, 0,
											L1SkillUse.TYPE_SPELLSC);
									break;
								}
								pc.getInventory().removeItem(useItem, 1);
							}
						} else if (itemId == L1WilliamItemSummon
								.checkItemId(itemId)) {
							L1WilliamItemSummon.getItemSummon(pc, useItem,
									itemId);
						} else if (itemId == L1WilliamTeleportScroll
								.checkItemId(itemId)) {
							L1WilliamTeleportScroll.getTeleportScroll(pc,
									useItem, itemId);
						} else {
							// 传送卷轴、召唤道具、魔法道具 end
							if (useItem.getCount() < 1) {
								// 得？
								pc.sendPackets(new S_ServerMessage(329, useItem
										.getLogName())); // \f1%0持
							} else {
								pc.sendPackets(new S_ServerMessage(74, useItem
										.getLogName())); // \f1%0使用。
							}

							// 传送卷轴DB化
						}
						// 传送卷轴DB化 end
					}
						break;
					}
				// 改写成switch 2 end
			} else if (useItem.getItem().getType2() == 1) {
				// 种别：武器
				int min = useItem.getItem().getMinLevel();
				int max = useItem.getItem().getMaxLevel();
				if (min != 0 && min > pc.getLevel()) {
					// %0以上使用。
					pc.sendPackets(new S_ServerMessage(318, String.valueOf(min)));
				} else if (max != 0 && max < pc.getLevel()) {
					// %d以下使用。
					// S_ServerMessage引表示
					if (max < 50) {
						pc.sendPackets(new S_PacketBox(
								S_PacketBox.MSG_LEVEL_OVER, max));
					} else {
						pc.sendPackets(new S_SystemMessage(
								L1WilliamSystemMessage.ShowMessage(1027) // 从DB取得讯息
										+ max
										+ L1WilliamSystemMessage
												.ShowMessage(1028))); // 从DB取得讯息
					}
				} else {
					if (pc.isCrown() && useItem.getItem().isUseRoyal()
							|| pc.isKnight() && useItem.getItem().isUseKnight()
							|| pc.isElf() && useItem.getItem().isUseElf()
							|| pc.isWizard() && useItem.getItem().isUseMage()
							|| pc.isDarkelf()
							&& useItem.getItem().isUseDarkelf()
							|| pc.isDragonKnight()
							&& useItem.getItem().isUseDragonknight()
							|| pc.isIllusionist()
							&& useItem.getItem().isUseIllusionist()) {
						UseWeapon(pc, useItem);
					} else {
						// \f1使用。
						pc.sendPackets(new S_ServerMessage(264));
					}
				}
			} else if (useItem.getItem().getType2() == 2) { // 种别：防具
				if (pc.isCrown() && useItem.getItem().isUseRoyal()
						|| pc.isKnight() && useItem.getItem().isUseKnight()
						|| pc.isElf() && useItem.getItem().isUseElf()
						|| pc.isWizard() && useItem.getItem().isUseMage()
						|| pc.isDarkelf() && useItem.getItem().isUseDarkelf()
						|| pc.isDragonKnight()
						&& useItem.getItem().isUseDragonknight()
						|| pc.isIllusionist()
						&& useItem.getItem().isUseIllusionist()) {

					int min = ((L1Armor) useItem.getItem()).getMinLevel();
					int max = ((L1Armor) useItem.getItem()).getMaxLevel();
					if (min != 0 && min > pc.getLevel()) {
						// %0以上使用。
						pc.sendPackets(new S_ServerMessage(318, String
								.valueOf(min)));
					} else if (max != 0 && max < pc.getLevel()) {
						// %d以下使用。
						// S_ServerMessage引表示
						if (max < 50) {
							pc.sendPackets(new S_PacketBox(
									S_PacketBox.MSG_LEVEL_OVER, max));
						} else {
							pc.sendPackets(new S_SystemMessage(
									L1WilliamSystemMessage.ShowMessage(1027) // 从DB取得讯息
											+ max
											+ L1WilliamSystemMessage
													.ShowMessage(1028))); // 从DB取得讯息
						}
					} else {
						UseArmor(pc, useItem);
					}
				} else {
					// \f1使用。
					pc.sendPackets(new S_ServerMessage(264));
				}
			}

			// 佅果场合现在时间
			if (isDelayEffect) {
				Timestamp ts = new Timestamp(System.currentTimeMillis());
				useItem.setLastUsed(ts);
				pc.getInventory().updateItem(useItem,
						L1PcInventory.COL_DELAY_EFFECT);
				pc.getInventory().saveItem(useItem,
						L1PcInventory.COL_DELAY_EFFECT);
			}

			L1ItemDelay.onItemUse(_client, useItem); // 开始
		}
	}

	private void SuccessEnchant(L1PcInstance pc, L1ItemInstance item,
			LineageClient _client, int i) {
		new EnchantExecutor().successEnchant(pc, item, i);
	}

	private void FailureEnchant(L1PcInstance pc, L1ItemInstance item,
			LineageClient _client) {
		new EnchantExecutor().failureEnchant(pc, item);
	}

	private void UseHeallingPotion(final L1PcInstance pc, int healHp,
			final int gfxid, final int itemId) {
		if (pc.hasSkillEffect(71) == true) { //  状态
			pc.sendPackets(new S_ServerMessage(698)); // 魔力何饮。
			return;
		}
		pc.setSelHealHpPotion(itemId, healHp, gfxid);
		if (pc.getHealHPAI()) {
			return;
		}
		//  解除
		cancelAbsoluteBarrier(pc);

		pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
		pc.broadcastPacket(new S_SkillSound(pc.getId(), gfxid));
		if (pc.IsShowHealMessage()) {
			pc.sendPackets(new S_ServerMessage(77)); // \f1气分良
		}
		healHp *= (_random.nextGaussian() / 5.0D) + 1.0D;
		if (pc.hasSkillEffect(POLLUTE_WATER)) { // 中回复量1/2倍
			healHp /= 2;
		}
		if (pc.getHeallingPotion() > 0) {
			healHp *= ((double) pc.getHeallingPotion() / 100.0D + 1.0D);
		}
		pc.setCurrentHp(pc.getCurrentHp() + healHp);
	}

	private void useGreenPotion(L1PcInstance pc, int itemId) {
		if (pc.hasSkillEffect(71) == true) { // 状态
			pc.sendPackets(new S_ServerMessage(698)); // \f1魔力何饮。
			return;
		}

		//  解除
		cancelAbsoluteBarrier(pc);

		int time = 0;
		if (itemId == L1ItemId.POTION_OF_HASTE_SELF) { //  
			time = 300;
		} else if (itemId == L1ItemId.B_POTION_OF_HASTE_SELF) { // 祝福
			// 
			time = 350;
		} else if (itemId == 40018) { // 强化 
			time = 1800;
		} else if (itemId == 140018) { // 祝福强化 
			time = 2100;
		} else if (itemId == 40039) { // 
			time = 600;
		} else if (itemId == 40040) { // 
			time = 900;
		} else if (itemId == 40030) { //
			time = 300;
		} else if (itemId == Item_EPU_17) { // 饭团 商店食品
			time = 30;
		} else if (itemId == Item_EPU_18) { // 鸡肉串烧
			time = 30;
		} else if (itemId == Item_EPU_24) { // 小比萨
			time = 30;
		} else if (itemId == Item_EPU_25) { // 烤玉米
			time = 30;
		} else if (itemId == Item_EPU_26) { // 爆米花
			time = 30;
		} else if (itemId == Item_EPU_27) { // 甜不拉
			time = 30;
		} else if (itemId == Item_EPU_28) { // 松饼
			time = 30;
		}

		pc.sendPackets(new S_SkillSound(pc.getId(), 191));
		pc.broadcastPacket(new S_SkillSound(pc.getId(), 191));
		// XXX:装备时、醉状态解除不明
		if (pc.getHasteItemEquipped() > 0) {
			return;
		}
		// 醉状态解除
		pc.setDrink(false);

		// 、重复
		if (pc.hasSkillEffect(HASTE)) {
			pc.killSkillEffectTimer(HASTE);
			pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
			pc.setMoveSpeed(0);
		} else if (pc.hasSkillEffect(GREATER_HASTE)) {
			pc.killSkillEffectTimer(GREATER_HASTE);
			pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
			pc.setMoveSpeed(0);
		}
		// 补充绿水、强绿、食品类效果不重叠
		else if (pc.hasSkillEffect(STATUS_HASTE)) {
			pc.killSkillEffectTimer(STATUS_HASTE);
			pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
			pc.setMoveSpeed(0);
		}
		// 补充绿水、强绿、食品类效果不重叠 end

		// 、 、中状态解除
		if (pc.hasSkillEffect(SLOW)) { // 
			pc.killSkillEffectTimer(SLOW);
			pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
			pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
		} else if (pc.hasSkillEffect(MASS_SLOW)) { //  
			pc.killSkillEffectTimer(MASS_SLOW);
			pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
			pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
		} else if (pc.hasSkillEffect(ENTANGLE)) { // 
			pc.killSkillEffectTimer(ENTANGLE);
			pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
			pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
		} else {
			pc.sendPackets(new S_SkillHaste(pc.getId(), 1, time));
			pc.setMoveSpeed(1);
			pc.setSkillEffect(STATUS_HASTE, time * 1000);
		}
	}

	private void useBravePotion(L1PcInstance pc, int item_id) {
		if (pc.hasSkillEffect(71) == true) { // 状态
			pc.sendPackets(new S_ServerMessage(698)); // \f1魔力何饮。
			return;
		}

		//  解除
		cancelAbsoluteBarrier(pc);

		int time = 0;
		if (item_id == L1ItemId.POTION_OF_EMOTION_BRAVERY) { //  
			time = 300;
		} else if (item_id == L1ItemId.B_POTION_OF_EMOTION_BRAVERY) { // 祝福
			// 
			time = 350;
		} else if (item_id == 40068) { //  
			time = 600;
			if (pc.hasSkillEffect(WIND_WALK)) { // 重复
				pc.killSkillEffectTimer(WIND_WALK);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}
		} else if (item_id == 140068) { // 祝福 
			time = 700;
			if (pc.hasSkillEffect(WIND_WALK)) { // 
				pc.killSkillEffectTimer(WIND_WALK);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}
		} else if (item_id == 11000) { // 新手双速药水
			time = 600;
		} else if (item_id == 40031) { //  
			time = 600;
		} else if (item_id == 40733) { // 名誉
			time = 600;
			if (pc.hasSkillEffect(HOLY_WALK)) { // 重复
				pc.killSkillEffectTimer(HOLY_WALK);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}
			if (pc.hasSkillEffect(MOVING_ACCELERATION)) { // 重复
				pc.killSkillEffectTimer(MOVING_ACCELERATION);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}
			if (pc.hasSkillEffect(WIND_WALK)) { // 重复
				pc.killSkillEffectTimer(WIND_WALK);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}
			/*
			 * 删除if (pc.hasSkillEffect(STATUS_WISDOM_POTION)) { //
			 * 重复 pc.killSkillEffectTimer(STATUS_WISDOM_POTION);
			 * pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0)); pc.addSp(-2);
			 * }删除
			 */
		}
		pc.sendPackets(new S_SkillSound(pc.getId(), 751));
		pc.broadcastPacket(new S_SkillSound(pc.getId(), 751));
		if (item_id == 40068 || item_id == 140068) { // 名誉使用
			pc.sendPackets(new S_SkillBrave(pc.getId(), 3, time));
			pc.setBraveSpeed(3);
			pc.setSkillEffect(STATUS_ELFBRAVE, time * 1000);
		} else {
			pc.sendPackets(new S_SkillBrave(pc.getId(), 1, time));
			pc.setBraveSpeed(1);
			pc.setSkillEffect(STATUS_BRAVE, time * 1000);
		}
		// 变更
		// pc.sendPackets(new S_SkillBrave(pc.getId(), 1, time));
		// 变更 end
	}

	private void useBluePotion(L1PcInstance pc, int item_id) {
		if (pc.hasSkillEffect(DECAY_POTION)) { // 状态
			pc.sendPackets(new S_ServerMessage(698)); // \f1魔力何饮。
			return;
		}

		//  解除
		cancelAbsoluteBarrier(pc);

		int time = 0;
		if (item_id == 40015 || item_id == 40736) { // 、知惠
			time = 600;
		} else if (item_id == 140015) { // 祝福 
			time = 700;
		} else {
			return;
		}

		pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_BLUEPOTION, time));
		pc.sendPackets(new S_SkillSound(pc.getId(), 190));
		pc.broadcastPacket(new S_SkillSound(pc.getId(), 190));

		pc.setSkillEffect(STATUS_BLUE_POTION, time * 1000);

		pc.sendPackets(new S_ServerMessage(1007)); // MP回复速度速。
	}

	private void useWisdomPotion(L1PcInstance pc, int item_id) {
		if (pc.hasSkillEffect(71) == true) { // 状态
			pc.sendPackets(new S_ServerMessage(698)); // \f1魔力何饮。
			return;
		}

		//  解除
		cancelAbsoluteBarrier(pc);

		int time = 0;
		if (item_id == L1ItemId.POTION_OF_EMOTION_WISDOM) { //  
			time = 300;
		} else if (item_id == L1ItemId.B_POTION_OF_EMOTION_WISDOM) { // 祝福
			// 
			time = 350;
		}

		/*
		 * 删除if (pc.hasSkillEffect(HOLY_WALK)) { // 重复
		 * pc.killSkillEffectTimer(HOLY_WALK); pc.sendPackets(new
		 * S_SkillBrave(pc.getId(), 0, 0)); pc.setBraveSpeed(0); } if
		 * (pc.hasSkillEffect(STATUS_BRAVE)) { // 重复
		 * pc.killSkillEffectTimer(STATUS_BRAVE); pc.sendPackets(new
		 * S_SkillBrave(pc.getId(), 0, 0)); pc.setBraveSpeed(0); }删除
		 */

		if (!pc.hasSkillEffect(STATUS_WISDOM_POTION)) {
			pc.addSp(2);
			// SP画面更新
			// pc.sendPackets(new S_SPMR(pc));
			// SP画面更新 end
		}

		// 删除pc.sendPackets(new S_SkillBrave(pc.getId(), 2, time));
		pc.sendPackets(new S_SkillSound(pc.getId(), 750));
		pc.broadcastPacket(new S_SkillSound(pc.getId(), 750));
		pc.sendPackets(new S_PacketBox(57, 44, time / 4));
		pc.setSkillEffect(STATUS_WISDOM_POTION, time * 1000);

		// pc.sendPackets(new S_ServerMessage(348)); // 气分、集中力高感。
	}

	private void useBlessOfEva(L1PcInstance pc, int item_id) {
		if (pc.hasSkillEffect(71) == true) { // 状态
			pc.sendPackets(new S_ServerMessage(698)); // \f1魔力何饮。
			return;
		}

		//  解除
		cancelAbsoluteBarrier(pc);

		int time = 0;
		if (item_id == 40032) { // 祝福
			time = 1800;
		} else if (item_id == 40041) { // 鳞
			time = 300;
		} else {
			return;
		}

		if (pc.hasSkillEffect(STATUS_UNDERWATER_BREATH)) {
			int timeSec = pc.getSkillEffectTimeSec(STATUS_UNDERWATER_BREATH);
			time += timeSec;
			if (time > 3600) {
				time = 3600;
			}
		}
		pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(), time));
		pc.sendPackets(new S_SkillSound(pc.getId(), 190));
		pc.broadcastPacket(new S_SkillSound(pc.getId(), 190));
		pc.setSkillEffect(STATUS_UNDERWATER_BREATH, time * 1000);
	}

	private void useBlindPotion(L1PcInstance pc) {
		if (pc.hasSkillEffect(DECAY_POTION)) {
			pc.sendPackets(new S_ServerMessage(698)); // \f1魔力何饮。
			return;
		}

		//  解除
		cancelAbsoluteBarrier(pc);

		int time = 16;
		if (pc.hasSkillEffect(CURSE_BLIND)) {
			pc.killSkillEffectTimer(CURSE_BLIND);
		} else if (pc.hasSkillEffect(DARKNESS)) {
			pc.killSkillEffectTimer(DARKNESS);
		}

		if (pc.hasSkillEffect(STATUS_FLOATING_EYE)) {
			pc.sendPackets(new S_CurseBlind(2));
		} else {
			pc.sendPackets(new S_CurseBlind(1));
		}

		pc.setSkillEffect(CURSE_BLIND, time * 1000);
	}

	private boolean usePolyScroll(L1PcInstance pc, int item_id, String s) {
		int time = 0;
		if (item_id == 40088 || item_id == 40096 || item_id == 40410) { // 变身、象牙塔变身、追加40410
			time = 1800;
		} else if (item_id == 140088) { // 祝福变身
			time = 2100;
		}

		updatePoly(pc, s);
		System.out.println("更新" + s + "完成！");

		L1PolyMorph poly = PolyTable.getInstance().getTemplate(s);

		if (poly != null || s.equals("")) {
			// 判断变身套装无法使用变卷
			if (s.equals("") && pc.hasSkillEffect(Skill_AJ_0_10)) {
				return true;
			} else if (pc.hasSkillEffect(Skill_AJ_0_10)) {
				return false;
			} else
			// 判断变身套装无法使用变卷 end
			if (s.equals("")) {
				pc.removeSkillEffect(SHAPE_CHANGE);
				return true;
			} else if (poly.getPolyId() >= 12000) {
				if (pc.getInventory().consumeItem(40308, 10000)) {
					L1PolyMorph.doPoly(pc, poly.getPolyId(), time);
				} else {
					pc.sendPackets(new S_SystemMessage("金币不足1万呢..这么帅的变身要点代价哦"));
				}
				return true;
			} else if (poly.getMinLevel() <= pc.getLevel() || pc.isGm()) {
				L1PolyMorph.doPoly(pc, poly.getPolyId(), time);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private void updatePoly(L1PcInstance pc, String s) {
		if (!pc.isGm()) {
			return;
		}
		if (pc.getBianshen().equals("没写")) {
			return;
		}
		pc.sendPackets(new S_SystemMessage("变身名字" + pc.getBianshen()
				+ "已经更新到变身清单，请使用变卷进行下一个变身！"));
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("UPDATE polymorphs SET note=? WHERE name=?");
			pstm.setString(1, pc.getBianshen());
			pstm.setString(2, s);
			pstm.execute();
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		pc.setBianshen("没写");
	}

	private void usePolyScale(L1PcInstance pc, int itemId) {
		int polyId = 0;
		if (itemId == 41154) { // 闇鳞
			polyId = 3101;
		} else if (itemId == 41155) { // 烈火鳞
			polyId = 3126;
		} else if (itemId == 41156) { // 背德者鳞
			polyId = 3888;
		} else if (itemId == 41157) { // 憎恶鳞
			polyId = 3784;
		}
		L1PolyMorph.doPoly(pc, polyId, 600);
	}

	private void UseArmor(L1PcInstance activeChar, L1ItemInstance armor) {
		int itemid = armor.getItem().getItemId();
		int type = armor.getItem().getType();
		L1PcInventory pcInventory = activeChar.getInventory();
		boolean equipeSpace; // 装备个所空
		if (type == 9) { // 场合
			equipeSpace = pcInventory.getTypeEquipped(2, 9) <= 1;
		} else {
			equipeSpace = pcInventory.getTypeEquipped(2, type) <= 0;
		}
		if (equipeSpace && !armor.isEquipped()) { // 使用防具装备、装备个所空场合（装着试）
			int polyid = activeChar.getTempCharGfx();

			if (!L1PolyMorph.isEquipableArmor(polyid, type)) { // 变身装备不可
				return;
			}

			if (type == 7 && activeChar.getWeapon() != null) { // 场合、武器装备两手武器
				if (activeChar.getWeapon().getItem().isTwohandedWeapon()
						&& itemid != 25063) { // 手武器
					activeChar.sendPackets(new S_ServerMessage(129)); // \f1两手武器武装着用
					return;
				}
			}

			if (type == 3 && pcInventory.getTypeEquipped(2, 4) >= 1) { // 场合、着确认
				activeChar
						.sendPackets(new S_ServerMessage(126, "$224", "$225")); // \f1%1上%0着。
				return;
			} else if ((type == 3) && pcInventory.getTypeEquipped(2, 2) >= 1) { // 场合、着确认
				activeChar
						.sendPackets(new S_ServerMessage(126, "$224", "$226")); // \f1%1上%0着。
				return;
			} else if ((type == 2) && pcInventory.getTypeEquipped(2, 4) >= 1) { // 场合、着确认
				activeChar
						.sendPackets(new S_ServerMessage(126, "$226", "$225")); // \f1%1
				return;
			}

			cancelAbsoluteBarrier(activeChar); //  解除

			pcInventory.setEquipped(armor, true);
		} else if (armor.isEquipped()) { // 使用防具装备场合（脱着试）
			if (armor.getBless() == 2) { // 咒场合
				activeChar.sendPackets(new S_ServerMessage(150)); // \f1。咒。
				return;
			}
			if (type == 3 && pcInventory.getTypeEquipped(2, 2) >= 1) { // 场合、着确认
				activeChar.sendPackets(new S_ServerMessage(127)); // \f1脱。
				return;
			} else if ((type == 2 || type == 3)
					&& pcInventory.getTypeEquipped(2, 4) >= 1) { // 场合、着确认
				activeChar.sendPackets(new S_ServerMessage(127)); // \f1脱。
				return;
			}

			pcInventory.setEquipped(armor, false);
		} else {
			activeChar.sendPackets(new S_ServerMessage(124)); // \f1何装备。
		}
		// 装备用HP、MP、MR更
		activeChar.setCurrentHp(activeChar.getCurrentHp());
		activeChar.setCurrentMp(activeChar.getCurrentMp());
		activeChar.sendPackets(new S_OwnCharAttrDef(activeChar));
		activeChar.sendPackets(new S_OwnCharStatus(activeChar));
		activeChar.sendPackets(new S_SPMR(activeChar));
	}

	private void UseWeapon(L1PcInstance activeChar, L1ItemInstance weapon) {
		L1PcInventory pcInventory = activeChar.getInventory();
		if (activeChar.getWeapon() == null
				|| !activeChar.getWeapon().equals(weapon)) { // 指定武器装备武器违场合、装备确认
			int weapon_type = weapon.getItem().getType();
			int polyid = activeChar.getTempCharGfx();

			if (!L1PolyMorph.isEquipableWeapon(polyid, weapon_type)) { // 变身装备不可
				return;
			}
			if (weapon.getItem().isTwohandedWeapon()
					&& pcInventory.getTypeEquipped(2, 7) >= 1
					&& pcInventory.getItemEquipped(2, 7).getItemId() != 25063) { // 两手武器场合、装备确认
				activeChar.sendPackets(new S_ServerMessage(128)); // \f1装备时两手持武器使。
				return;
			}
		}

		cancelAbsoluteBarrier(activeChar); //  解除

		if (activeChar.getWeapon() != null) { // 既何装备场合、前装备
			if (activeChar.getWeapon().getBless() == 2) { // 咒
				activeChar.sendPackets(new S_ServerMessage(150)); // \f1。咒。
				return;
			}
			if (activeChar.getWeapon().equals(weapon)) {
				// 装备交换外
				pcInventory.setEquipped(activeChar.getWeapon(), false, false,
						false);
				return;
			} else {
				pcInventory.setEquipped(activeChar.getWeapon(), false, false,
						true);
			}
		}

		if (weapon.getItemId() == 200002) { // 咒
			activeChar
					.sendPackets(new S_ServerMessage(149, weapon.getLogName())); // \f1%0手。
		}
		pcInventory.setEquipped(weapon, true, false, false);
	}

	private int RandomELevel(L1ItemInstance item, int itemId, L1PcInstance pc) {
		if (itemId == L1ItemId.B_SCROLL_OF_ENCHANT_ARMOR
				|| itemId == L1ItemId.B_SCROLL_OF_ENCHANT_WEAPON) {
			if (item.getEnchantLevel() <= 2) {
				int j = _random.nextInt(100) + 1;
				if (j < 32) {
					return 1;
				} else if (j >= 33 && j <= 76) {
					return 2;
				} else if (j >= 77 && j <= 100) {
					return 3;
				}
			} else if (item.getEnchantLevel() >= 3
					&& item.getEnchantLevel() <= 5) {
				final L1ServerBlessEnchant serverBlessEnchant = ServerBlessEnchantTable
						.get().getItem(item.getItem().getItemId());
				int old_enchantCount = 0;
				int new_enchantCount = 0;
				if (serverBlessEnchant != null) {
					old_enchantCount = pc.getBlessEnchant().getEnchantCount(
							item.getItem().getItemId());
					new_enchantCount = old_enchantCount + 1;
					if (old_enchantCount < serverBlessEnchant.get_minCount()) {
						pc.getBlessEnchant().updateEnchantCount(
								item.getItem().getItemId(), new_enchantCount);
						return 1;
					} else if (old_enchantCount >= serverBlessEnchant
							.get_maxCount()) {
						pc.getBlessEnchant().updateEnchantCount(
								item.getItem().getItemId(), 0);
						return 2;
					}
				}
				int j = _random.nextInt(100) + 1;
				if (j < Config.BLESSENCHANT) {
					if (serverBlessEnchant != null) {
						pc.getBlessEnchant().updateEnchantCount(
								item.getItem().getItemId(), 0);
					}
					return 2;
				} else {
					if (serverBlessEnchant != null) {
						pc.getBlessEnchant().updateEnchantCount(
								item.getItem().getItemId(), new_enchantCount);
					}
					return 1;
				}
			} else {
				return 1;
			}
		}
		if (itemId == 140129 || itemId == 140130) {
			if (item.getEnchantLevel() <= 2) {
				return 3;
			} else if (item.getEnchantLevel() >= 3
					&& item.getEnchantLevel() <= 5) {
				return 2;
			}
			{
				return 1;
			}
		} else {
			return 1;
		}
	}

	/*
	 * 删除private void doWandAction(L1PcInstance user, L1Object target) { if
	 * (user.getId() == target.getId()) { return; // 自分自身当 } if
	 * (user.glanceCheck(target.getX(), target.getY()) == false) { return; //
	 * 直线上障害物 }
	 * 
	 * // XXX 适当计算、要修正 int dmg = (_random.nextInt(11) - 5) + user.getStr();
	 * dmg = Math.max(1, dmg);
	 * 
	 * if (target instanceof L1PcInstance) { L1PcInstance pc = (L1PcInstance)
	 * target; if (pc.getMap().isSafetyZone(pc.getLocation()) ||
	 * user.checkNonPvP(user, pc)) { // 攻击 return; } if
	 * (pc.hasSkillEffect(50) == true || pc.hasSkillEffect(78) == true ||
	 * pc.hasSkillEffect(157) == true) { //  、、  状态
	 * return; }
	 * 
	 * int newHp = pc.getCurrentHp() - dmg; if (newHp > 0) { pc.sendPackets(new
	 * S_AttackStatus(pc, 0, ActionCodes.ACTION_Damage)); pc.broadcastPacket(new
	 * S_AttackStatus(pc, 0, ActionCodes.ACTION_Damage));
	 * pc.setCurrentHp(newHp); } else if (newHp <= 0 && pc.isGm()) {
	 * pc.setCurrentHp(pc.getMaxHp()); } else if (newHp <= 0 && !pc.isGm()) {
	 * pc.death(user); } } else if (target instanceof L1MonsterInstance) {
	 * L1MonsterInstance mob = (L1MonsterInstance) target;
	 * mob.broadcastPacket(new S_AttackPacket(user, mob.getId(), 2));
	 * mob.receiveDamage(user, dmg); } else if (target instanceof L1NpcInstance)
	 * { L1NpcInstance npc = (L1NpcInstance) target; npc.broadcastPacket(new
	 * S_DoActionGFX(npc.getId(), ActionCodes.ACTION_Damage)); } }删除
	 */

	// 电杖伤害
	private int doWandAction(L1PcInstance user, L1Object target) {
		if (user.getId() == target.getId()) {
			return 0; // 自分自身当
		}
		if (user.glanceCheck(target.getX(), target.getY()) == false) {
			return 0; // 直线上障害物
		}

		// XXX 适当计算、要修正
		int dmg = (_random.nextInt(11) - 5) + user.getStr();
		dmg = Math.max(1, dmg);

		if (target instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) target;
			if (pc.getMap().isSafetyZone(pc.getLocation())
					|| user.checkNonPvP(user, pc)) {
				// 攻击
				return 0;
			}
			if (pc.hasSkillEffect(50) == true || pc.hasSkillEffect(78) == true
					|| pc.hasSkillEffect(157) == true) {
				//  、、  状态
				return 0;
			}
			pc.receiveDamage(user, dmg,false);
		} else if (target instanceof L1MonsterInstance) {
			L1MonsterInstance mob = (L1MonsterInstance) target;
			mob.receiveDamage(user, dmg);
		}
		return dmg;
	}

	// 电杖伤害 end

	private void polyAction(L1PcInstance attacker, L1Character cha) {
		boolean isSameClan = false;
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			if (pc.getClanid() != 0 && attacker.getClanid() == pc.getClanid()) {
				isSameClan = true;
			}
		}
		if (attacker.getId() != cha.getId() && !isSameClan) { // 自分以外违
			final int probability = 3 * (attacker.getLevel() - cha.getLevel())
					+ 100 - cha.getMr();
			final int rnd = _random.nextInt(100) + 1;
			if (rnd > probability) {
				return;
			}
		}
		// 限制地图不可变身
		if (cha.getMapId() == 5124) {
			return;
		}
		// 限制地图不可变身 end

		int[] polyArray = { 29, 945, 947, 979, 1037, 1039, 3860, 3861, 3862,
				3863, 3864, 3865, 3904, 3906, 95, 146, 2374, 2376, 2377, 2378,
				3866, 3867, 3868, 3869, 3870, 3871, 3872, 3873, 3874, 3875,
				3876 };

		int pid = _random.nextInt(polyArray.length);
		int polyId = polyArray[pid];

		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			if (pc.getInventory().checkEquipped(20281)) {
				pc.setPring(true);
				pc.sendPackets(new S_ShowPolyList(pc.getId()));
				pc.sendPackets(new S_ServerMessage(966)); // string-j.tbl:968行目
				// System.out.println("由于拥有变戒，所以弹出变身菜单");
				// 魔法力保护。
				//
			} else {
				// System.out.println("由于没有变戒，随机变身");
				L1Skills skillTemp = SkillsTable.getInstance().getTemplate(
						SHAPE_CHANGE);

				L1PolyMorph.doPoly(pc, polyId, skillTemp.getBuffDuration());
				// XXX 把你变身
				if (attacker.getId() != pc.getId())
					pc.sendPackets(new S_ServerMessage(241, attacker.getName()));
				// XXX 把你变身 end
			}
		} else if (cha instanceof L1MonsterInstance) {
			L1MonsterInstance mob = (L1MonsterInstance) cha;
			if (mob.getLevel() < 50) {
				mob.broadcastPacket(new S_ChangeShape(mob.getId(), polyId));
			}
		}
	}

	private void cancelAbsoluteBarrier(L1PcInstance pc) { //  解除
		if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
			pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
			pc.startHpRegeneration();
			pc.startMpRegeneration();
			// pc.startMpRegenerationByDoll();
		}
	}

	private boolean createNewItem(L1PcInstance pc, int item_id, int count) {
		L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
		if (item != null) {
			item.setCount(count);
			if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
				pc.getInventory().storeItem(item);
			} else { // 持场合地面落 处理（不正防止）
				L1World.getInstance()
						.getInventory(pc.getX(), pc.getY(), pc.getMapId())
						.storeItem(item);
			}
			pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0手入
			return true;
		}
		return false;
	}

	private void useToiTeleportAmulet(L1PcInstance pc, int itemId,
			L1ItemInstance item) {
		boolean isTeleport = false;
		/*
		 * if (itemId == 40289 || itemId == 40293) { // 11,51Famulet if
		 * (pc.getX() >= 32816 && pc.getX() <= 32821 && pc.getY() >= 32778 &&
		 * pc.getY() <= 32783 && pc.getMapId() == 101) { isTeleport = true; } }
		 * else if (itemId == 40290 || itemId == 40294) { // 21,61Famulet if
		 * (pc.getX() >= 32815 && pc.getX() <= 32820 && pc.getY() >= 32815 &&
		 * pc.getY() <= 32820 && pc.getMapId() == 101) { isTeleport = true; } }
		 * else if (itemId == 40291 || itemId == 40295) { // 31,71Famulet if
		 * (pc.getX() >= 32779 && pc.getX() <= 32784 && pc.getY() >= 32778 &&
		 * pc.getY() <= 32783 && pc.getMapId() == 101) { isTeleport = true; } }
		 * else if (itemId == 40292 || itemId == 40296) { // 41,81Famulet if
		 * (pc.getX() >= 32779 && pc.getX() <= 32784 && pc.getY() >= 32815 &&
		 * pc.getY() <= 32820 && pc.getMapId() == 101) { isTeleport = true; } }
		 * else if (itemId == 40297) { // 91Famulet if (pc.getX() >= 32706 &&
		 * pc.getX() <= 32710 && pc.getY() >= 32909 && pc.getY() <= 32913 &&
		 * pc.getMapId() == 190) { isTeleport = true; } if (pc.getX() >= 32779
		 * && pc.getX() <= 32784 && pc.getY() >= 32815 && pc.getY() <= 32820 &&
		 * pc.getMapId() == 101) { isTeleport = true; } }
		 */
		if (pc.getMap().isSafetyZone(pc.getX(), pc.getY())) {
			isTeleport = true;
		}

		if (isTeleport) {
			L1Teleport.teleport(pc, item.getItem().get_locx(), item.getItem()
					.get_locy(), item.getItem().get_mapid(), 5, true);
		} else {
			pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
		}
	}

	private boolean writeLetter(int itemId, L1PcInstance pc, int letterCode,
			String letterReceiver, byte[] letterText) {

		int newItemId = 0;
		if (itemId == 40310) {
			newItemId = 49016;
		} else if (itemId == 40730) {
			newItemId = 49020;
		} else if (itemId == 40731) {
			newItemId = 49022;
		} else if (itemId == 40732) {
			newItemId = 49024;
		}
		L1ItemInstance item = ItemTable.getInstance().createItem(newItemId);
		if (item == null) {
			return false;
		}
		item.setCount(1);
		if (sendLetter(pc, letterReceiver, item, true)) {
			saveLetter(item.getId(), letterCode, pc.getName(), letterReceiver,
					letterText);
		} else {
			return false;
		}
		return true;
	}

	private boolean writeClanLetter(int itemId, L1PcInstance pc,
			int letterCode, String letterReceiver, byte[] letterText) {
		L1Clan targetClan = null;
		for (L1Clan clan : L1World.getInstance().getAllClans()) {
			if (clan.getClanName().toLowerCase()
					.equals(letterReceiver.toLowerCase())) {
				targetClan = clan;
				break;
			}
		}
		if (targetClan == null) {
			pc.sendPackets(new S_ServerMessage(434)); // 受信者。
			return false;
		}

		String memberName[] = targetClan.getAllMemberNames();
		for (int i = 0; i < memberName.length; i++) {
			L1ItemInstance item = ItemTable.getInstance().createItem(49016);
			if (item == null) {
				return false;
			}
			item.setCount(1);
			if (sendLetter(pc, memberName[i], item, false)) {
				saveLetter(item.getId(), letterCode, pc.getName(),
						memberName[i], letterText);
			}
		}
		return true;
	}

	private boolean sendLetter(L1PcInstance pc, String name,
			L1ItemInstance item, boolean isFailureMessage) {
		L1PcInstance target = L1World.getInstance().getPlayer(name);
		if (target != null) {
			if (target.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
				target.getInventory().storeItem(item);
				target.sendPackets(new S_SkillSound(target.getId(), 1091));
				target.sendPackets(new S_ServerMessage(428)); // 手纸。
			} else {
				if (isFailureMessage) {
					// 相手重、以上。
					pc.sendPackets(new S_ServerMessage(942));
				}
				return false;
			}
		} else {
			if (CharacterTable.doesCharNameExist(name)) {
				try {
					int targetId = CharacterTable.getInstance()
							.restoreCharacter(name).getId();
					CharactersItemStorage storage = CharactersItemStorage
							.create();
					if (storage.getItemCount(targetId) < 180) {
						storage.storeItem(targetId, item);
					} else {
						if (isFailureMessage) {
							// 相手重、以上。
							pc.sendPackets(new S_ServerMessage(942));
						}
						return false;
					}
				} catch (Exception e) {
					_log.error(e.getLocalizedMessage(), e);
				}
			} else {
				if (isFailureMessage) {
					pc.sendPackets(new S_ServerMessage(109, name)); // %0名前人。
				}
				return false;
			}
		}
		return true;
	}

	private void saveLetter(int itemObjectId, int code, String sender,
			String receiver, byte[] text) {
		// 日付取得
		SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
		TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		String date = sdf.format(Calendar.getInstance(tz).getTime());

		// subjectcontent切(0x00 0x00)位置见
		int spacePosition1 = 0;
		int spacePosition2 = 0;
		for (int i = 0; i < text.length; i += 2) {
			if (text[i] == 0 && text[i + 1] == 0) {
				if (spacePosition1 == 0) {
					spacePosition1 = i;
				} else if (spacePosition1 != 0 && spacePosition2 == 0) {
					spacePosition2 = i;
					break;
				}
			}
		}

		// letter书
		int subjectLength = spacePosition1 + 2;
		int contentLength = spacePosition2 - spacePosition1;
		byte[] subject = new byte[subjectLength];
		byte[] content = new byte[contentLength];
		System.arraycopy(text, 0, subject, 0, subjectLength);
		System.arraycopy(text, subjectLength, content, 0, contentLength);
		LetterTable.getInstance().writeLetter(itemObjectId, code, sender,
				receiver, date, 0, subject, content);
	}

	private boolean withdrawPet(L1PcInstance pc, int itemObjectId) {
		if (!pc.getMap().isTakePets()) {
			pc.sendPackets(new S_ServerMessage(563)); // \f1使。
			return false;
		}

		int petCost = 0;
		Object[] petList = pc.getPetList().values().toArray();
		for (Object pet : petList) {
			if (pet instanceof L1PetInstance) {
				if (((L1PetInstance) pet).getItemObjId() == itemObjectId) { // 既引出
					return false;
				}
			}
			petCost += ((L1NpcInstance) pet).getPetcost();
		}
		int charisma = pc.getCha();
		if (pc.isCrown()) { // 君主
			charisma += 6;
		} else if (pc.isElf()) { // 
			charisma += 12;
		} else if (pc.isWizard()) { // WIZ
			charisma += 6;
		} else if (pc.isDarkelf()) { // DE
			charisma += 6;
		}
		charisma -= petCost;
		int petCount = charisma / 6;
		if (petCount <= 0) {
			pc.sendPackets(new S_ServerMessage(489)); // 引取多。
			return false;
		}

		L1Pet l1pet = PetTable.getInstance().getTemplate(itemObjectId);
		if (l1pet != null) {
			L1Npc npcTemp = NpcTable.getInstance().getTemplate(
					l1pet.get_npcid());
			L1PetInstance pet = new L1PetInstance(npcTemp, pc, l1pet);
			pet.setPetcost(6);
		}
		return true;
	}

	private void startFishing(L1PcInstance pc, int itemId, int fishX, int fishY) {
		int rodLength = 0;
		if (itemId == 41293) {
			rodLength = 5;
		} else if (itemId == 41294) {
			rodLength = 3;
		}
		if (pc.getMap().isFishingZone(fishX, fishY)) {
			if (pc.getMap().isFishingZone(fishX + 1, fishY)
					&& pc.getMap().isFishingZone(fishX - 1, fishY)
					&& pc.getMap().isFishingZone(fishX, fishY + 1)
					&& pc.getMap().isFishingZone(fishX, fishY - 1)) {
				if (fishX > pc.getX() + rodLength
						|| fishX < pc.getX() - rodLength) {
					// 钓竿投。
					pc.sendPackets(new S_ServerMessage(1138));
				} else if (fishY > pc.getY() + rodLength
						|| fishY < pc.getY() - rodLength) {
					// 钓竿投。
					pc.sendPackets(new S_ServerMessage(1138));
				} else if (pc.getInventory().consumeItem(41295, 1)) { // 
					pc.sendPackets(new S_Fishing(pc.getId(),
							ActionCodes.ACTION_Fishing, fishX, fishY));
					pc.broadcastPacket(new S_Fishing(pc.getId(),
							ActionCodes.ACTION_Fishing, fishX, fishY));
					pc.setFishing(true);
					long time = System.currentTimeMillis() + 10000
							+ _random.nextInt(5) * 1000;
					pc.setFishingTime(time);
					FishingTimeController.getInstance().addMember(pc);
				} else {
					// 钓必要。
					pc.sendPackets(new S_ServerMessage(1137));
				}
			} else {
				// 钓竿投。
				pc.sendPackets(new S_ServerMessage(1138));
			}
		} else {
			// 钓竿投。
			pc.sendPackets(new S_ServerMessage(1138));
		}
	}

	private void useFurnitureItem(L1PcInstance pc, int itemId, int itemObjectId) {
		if (!L1HouseLocation.isInHouse(pc.getX(), pc.getY(), pc.getMapId())) {
			pc.sendPackets(new S_ServerMessage(563)); // \f1使。
			return;
		}

		boolean isAppear = true;
		L1FurnitureInstance furniture = null;
		for (L1Object l1object : L1World.getInstance().getObject()) {
			if (l1object instanceof L1FurnitureInstance) {
				furniture = (L1FurnitureInstance) l1object;
				if (furniture.getItemObjId() == itemObjectId) { // 既引出家具
					isAppear = false;
					break;
				}
			}
		}

		if (isAppear) {
			if (pc.getHeading() != 0 && pc.getHeading() != 2) {
				return;
			}
			int npcId = 0;
			if (itemId == 41383) { // 
				npcId = 80109;
			} else if (itemId == 41384) { // 制
				npcId = 80110;
			} else if (itemId == 41385) { // 制
				npcId = 80113;
			} else if (itemId == 41386) { // 制
				npcId = 80114;
			} else if (itemId == 41387) { // 鹿制
				npcId = 80115;
			} else if (itemId == 41388) { // 制
				npcId = 80124;
			} else if (itemId == 41389) { // 
				npcId = 80118;
			} else if (itemId == 41390) { // 
				npcId = 80119;
			} else if (itemId == 41391) { // 烛台
				npcId = 80120;
			} else if (itemId == 41392) { // 
				npcId = 80121;
			} else if (itemId == 41393) { // 火
				npcId = 80126;
			} else if (itemId == 41394) { // 
				npcId = 80125;
			} else if (itemId == 41395) { // 君主用立台
				npcId = 80111;
			} else if (itemId == 41396) { // 旗
				npcId = 80112;
			} else if (itemId == 41397) { // 用椅子(右)
				npcId = 80116;
			} else if (itemId == 41398) { // 用椅子(左)
				npcId = 80117;
			} else if (itemId == 41399) { // (右)
				npcId = 80122;
			} else if (itemId == 41400) { // (左)
				npcId = 80123;
			}

			try {
				L1Npc l1npc = NpcTable.getInstance().getTemplate(npcId);
				if (l1npc != null) {
					try {
						String s = l1npc.getImpl();
						Constructor<?> constructor = Class.forName(
								"l1j.server.server.model.Instance." + s
										+ "Instance").getConstructors()[0];
						Object aobj[] = { l1npc };
						furniture = (L1FurnitureInstance) constructor
								.newInstance(aobj);
						furniture.setId(IdFactory.getInstance().nextId());
						furniture.setMap(pc.getMapId());
						if (pc.getHeading() == 0) {
							furniture.setX(pc.getX());
							furniture.setY(pc.getY() - 1);
						} else if (pc.getHeading() == 2) {
							furniture.setX(pc.getX() + 1);
							furniture.setY(pc.getY());
						}
						furniture.setHomeX(furniture.getX());
						furniture.setHomeY(furniture.getY());
						furniture.setHeading(0);
						furniture.setItemObjId(itemObjectId);

						L1World.getInstance().storeWorldObject(furniture);
						L1World.getInstance().addVisibleObject(furniture);
						FurnitureSpawnTable.getInstance().insertFurniture(
								furniture);
					} catch (Exception e) {
						_log.error(e.getLocalizedMessage(), e);
					}
				}
			} catch (Exception exception) {
			}
		} else {
			furniture.deleteMe();
		}
	}

	private void useFurnitureRemovalWand(L1PcInstance pc, int targetId,
			L1ItemInstance item) {
		S_DoActionGFX s_attackStatus = new S_DoActionGFX(pc.getId(),
				ActionCodes.ACTION_Wand);
		pc.sendPackets(s_attackStatus);
		pc.broadcastPacket(s_attackStatus);
		int chargeCount = item.getChargeCount();
		if (chargeCount <= 0) {
			return;
		}

		L1Object target = L1World.getInstance().findObject(targetId);
		if (target != null && target instanceof L1FurnitureInstance) {
			L1FurnitureInstance furniture = (L1FurnitureInstance) target;
			furniture.deleteMe();
			FurnitureSpawnTable.getInstance().deleteFurniture(furniture);
			item.setChargeCount(item.getChargeCount() - 1);
			pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
		}
	}

	// 亮度变化
	private void 灯(L1PcInstance pc, L1ItemInstance item) {
		item.startLight(pc, item); // 消耗计时

		if (item.getEnchantLevel() != 0) {
			item.setEnchantLevel(0);
		} else {
			item.setEnchantLevel(1);
		}
		pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
		pc.sendPackets(new S_ItemName(item));

		if (pc.hasSkillEffect(2)) { // 日光术
			pc.setPcLight(14);
		}

		for (Object Light : pc.getInventory().getItems()) {
			L1ItemInstance OwnLight = (L1ItemInstance) Light;
			if ((OwnLight.getItem().getItemId() == 40001
					|| OwnLight.getItem().getItemId() == 40002
					|| OwnLight.getItem().getItemId() == 40004 || OwnLight
					.getItem().getItemId() == 40005)
					&& OwnLight.getEnchantLevel() != 0) {
				if (pc.getPcLight() < OwnLight.getItem().getLightRange()) {
					pc.setPcLight(OwnLight.getItem().getLightRange());
				}
			}
		}

		pc.sendPackets(new S_Light(pc.getId(), pc.getPcLight()));
		if (!pc.isInvisble() && item.getItem().getItemId() != 40004) { // 非隐身中跟魔法灯笼除外
			pc.broadcastPacket(new S_Light(pc.getId(), pc.getPcLight()));
		}
	}

	// 亮度变化

	// 复数文字
	private static Object getArray(String s, String sToken, int iType) {
		StringTokenizer st = new StringTokenizer(s, sToken);
		int iSize = st.countTokens();
		String sTemp = null;
		if (iType == 1) { // int
			int[] iReturn = new int[iSize];
			for (int i = 0; i < iSize; i++) {
				sTemp = st.nextToken();
				iReturn[i] = Integer.parseInt(sTemp);
			}
			return iReturn;
		}

		if (iType == 2) { // String
			String[] sReturn = new String[iSize];
			for (int i = 0; i < iSize; i++) {
				sTemp = st.nextToken();
				sReturn[i] = sTemp;
			}
			return sReturn;
		}

		if (iType == 3) { // String
			String sReturn = null;
			for (int i = 0; i < iSize; i++) {
				sTemp = st.nextToken();
				sReturn = sTemp;
			}
			return sReturn;
		}
		return null;
	}

	// 复数文字 end

	// 装备鉴定
	private void 装备鉴定(L1PcInstance pc, L1ItemInstance item) {
		L1WilliamArmorUpgrade Armor_Upgrade = ArmorUpgrade.getInstance()
				.getTemplate(item.getItem().getItemId());
		if (Armor_Upgrade != null && item.getItem().getType2() == 2) { // 有可强化的装备
			int armor_id = 0;
			int rnd = 0;
			String msg0 = " ";
			String msg1 = " ";
			String msg2 = "0";
			String msg3 = " ";
			String msg4 = " ";
			String msg5 = " ";
			String msg6 = " ";
			String msg7 = " ";
			String msg8 = " ";
			String msg9 = " ";
			String msg10 = " ";
			String msg11 = " ";
			String msg12 = " ";
			String msg13 = " ";
			String msg14 = " ";
			String msg15 = " ";
			String msg16 = " ";
			String msg17 = " ";
			String msg18 = " ";
			String msg19 = " ";
			String msg20 = " ";
			String msg21 = " ";
			String msg22 = " ";

			int[] materials = (int[]) getArray(Armor_Upgrade.getMaterials(),
					",", 1);
			int[] counts = (int[]) getArray(Armor_Upgrade.getCounts(), ",", 1);

			for (int j = 0; j < materials.length; j++) { // 显示强化材料
				L1ItemInstance item1 = ItemTable.getInstance().createItem(
						materials[j]);
				if (item1 != null) {
					armor_id++;
					switch (armor_id) {
					case 1:
						msg3 = item1.getLogName() + ":";
						msg4 = " " + counts[j] + "个";
						rnd++;
						break;
					case 2:
						msg5 = item1.getLogName() + ":";
						msg6 = " " + counts[j] + "个";
						rnd++;
						break;
					case 3:
						msg7 = item1.getLogName() + ":";
						msg8 = " " + counts[j] + "个";
						rnd++;
						break;
					case 4:
						msg9 = item1.getLogName() + ":";
						msg10 = " " + counts[j] + "个";
						rnd++;
						break;
					case 5:
						msg11 = item1.getLogName() + ":";
						msg12 = " " + counts[j] + "个";
						rnd++;
						break;
					case 6:
						msg13 = item1.getLogName() + ":";
						msg14 = " " + counts[j] + "个";
						rnd++;
						break;
					case 7:
						msg15 = item1.getLogName() + ":";
						msg16 = " " + counts[j] + "个";
						rnd++;
						break;
					case 8:
						msg17 = item1.getLogName() + ":";
						msg18 = " " + counts[j] + "个";
						rnd++;
						break;
					case 9:
						msg19 = item1.getLogName() + ":";
						msg20 = " " + counts[j] + "个";
						rnd++;
						break;
					case 10:
						msg21 = item1.getLogName() + ":";
						msg22 = " " + counts[j] + "个";
						rnd++;
						break;
					}
				}
			}

			msg0 = item.getLogName();
			msg1 = "" + Armor_Upgrade.getUpgradeRnd(); // 基本机率
			msg2 = "" + (rnd * 10); // 材料附加机率

			// 显示装备资讯
			String msg[] = { msg0, msg1, msg2, msg3, msg4, msg5, msg6, msg7,
					msg8, msg9, msg10, msg11, msg12, msg13, msg14, msg15,
					msg16, msg17, msg18, msg19, msg20, msg21, msg22 };
			pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "armorup2", msg));
			// 显示装备资讯
		} else {
			pc.sendPackets(new S_NPCTalkReturn(pc.getId(), ""));
			pc.sendPackets(new S_ServerMessage(79));
		}
	}

	// 装备

	// 强化装备
	private void 强化装备(L1PcInstance pc, L1ItemInstance item) {
		L1WilliamArmorUpgrade Armor_Upgrade = ArmorUpgrade.getInstance()
				.getTemplate(item.getItem().getItemId());
		if (Armor_Upgrade != null) { // 有可强化的装备

			byte need = (byte) 0;
			int[] materials = (int[]) getArray(Armor_Upgrade.getMaterials(),
					",", 1);
			int[] counts = (int[]) getArray(Armor_Upgrade.getCounts(), ",", 1);

			for (int j = 0; j < materials.length; j++) {
				if (pc.getInventory().checkItem(materials[j], counts[j])) { // 有强化材料
					need++;
					pc.getInventory().consumeItem(materials[j], counts[j]); // 删除强化材料
				}
			}

			if (need > 0) { // 有强化材料
				if ((Armor_Upgrade.getUpgradeRnd() + need * 10) >= (_random
						.nextInt(100) + 1)) { // 强化成功
					L1ItemInstance new_item = ItemTable.getInstance()
							.createItem(Armor_Upgrade.getUpgradeArmorId());
					new_item.setCount(1);
					new_item.setEnchantLevel(item.getEnchantLevel());

					if (new_item != null) {
						pc.getInventory().removeItem(item, 1); // 删除原装备

						if (pc.getInventory().checkAddItem(new_item, 1) == L1Inventory.OK) {
							pc.getInventory().storeItem(new_item);
						} else {
							L1World.getInstance()
									.getInventory(pc.getX(), pc.getY(),
											pc.getMapId()).storeItem(new_item);
						}
						pc.sendPackets(new S_SkillSound(pc.getId(), 763));
						pc.sendPackets(new S_ServerMessage(166, item
								.getLogName()
								+ " "
								+ L1WilliamSystemMessage.ShowMessage(1020)
								+ " " + new_item.getLogName()));
					}
				} else {
					pc.getInventory().removeItem(item, 1); // 删除原装备
					pc.sendPackets(new S_ServerMessage(166, item.getLogName()
							+ " " + L1WilliamSystemMessage.ShowMessage(1021)));
				}
			} else { // 无强化材料
				pc.getInventory().removeItem(item, 1); // 删除原装备
				pc.sendPackets(new S_ServerMessage(166, item.getLogName() + " "
						+ L1WilliamSystemMessage.ShowMessage(1021)));
			}
		} else {
			pc.sendPackets(new S_ServerMessage(79));
		}
	}

	// 强化装备 end

	// TODO 检查料理材料
	private boolean checkMaterial(L1PcInstance pc, int food_select) {
		int[] materials = null;
		int[] counts = null;
		switch (food_select) {
		case 0: // 制作漂浮之眼肉排
			materials = new int[] { 40057 }; // 材料
			counts = new int[] { 1 }; // 材料数量
			break;
		case 1: // 制作烤熊肉
			materials = new int[] { Item_EPU_30 }; // 材料
			counts = new int[] { 1 }; // 材料数量
			break;
		case 2:// 制作煎饼
			materials = new int[] { Item_EPU_19, Item_EPU_21 }; // 材料
			counts = new int[] { 1, 1 }; // 材料数量
			break;
		case 3: // 制作烤蚂蚁腿起司
			materials = new int[] { Item_EPU_29, Item_EPU_23 }; // 材料
			counts = new int[] { 1, 1 }; // 材料数量
			break;
		case 4:// 制作水果沙拉
			materials = new int[] { 40064, 40062, 40069 }; // 材料
			counts = new int[] { 1, 1, 1 }; // 材料数量
			break;
		case 5: // 制作水果糖醋肉
			materials = new int[] { 40056, 40061, 40060 }; // 材料
			counts = new int[] { 1, 1, 1 }; // 材料数量
			break;
		case 6: // 制作烤山猪肉串
			materials = new int[] { Item_EPU_31 }; // 材料
			counts = new int[] { 1 }; // 材料数量
			break;
		case 7: // 制作蘑菇汤
			materials = new int[] { 40499, 40060 }; // 材料
			counts = new int[] { 1, 1 }; // 材料数量
			break;
		}

		// 确认材料
		for (int j = 0; j < materials.length; j++) {
			if (!pc.getInventory().checkItem(materials[j], counts[j])) {
				// $1102 不够材料可以烹饪...
				pc.sendPackets(new S_ServerMessage(1102));
				return false;
			}
		}
		return true;
	}

	// 检查料理材料 end

	// TODO 进行料理
	private void doFoodMaking(L1PcInstance pc, int cookNo) {
		int[] materials = null;
		int[] counts = null;
		int[] createitem = null;

		switch (cookNo) {
		case 0: // 制作漂浮之眼肉排
			materials = new int[] { 40057 }; // 材料
			counts = new int[] { 1 }; // 材料数量
			createitem = new int[] { Item_EPU_32, Item_EPU_40 }; // 料理
			break;
		case 1: // 制作烤熊肉
			materials = new int[] { Item_EPU_30 }; // 材料
			counts = new int[] { 1 }; // 材料数量
			createitem = new int[] { Item_EPU_33, Item_EPU_41 }; // 料理
			break;
		case 2: // 制作煎饼
			materials = new int[] { Item_EPU_19, Item_EPU_21 }; // 材料
			counts = new int[] { 1, 1 }; // 材料数量
			createitem = new int[] { Item_EPU_34, Item_EPU_42 }; // 料理
			break;
		case 3: // 制作烤蚂蚁腿起司
			materials = new int[] { Item_EPU_29, Item_EPU_23 }; // 材料
			counts = new int[] { 1, 1 }; // 材料数量
			createitem = new int[] { Item_EPU_35, Item_EPU_43 }; // 料理
			break;
		case 4: // 制作水果沙拉
			materials = new int[] { 40064, 40062, 40069 }; // 材料
			counts = new int[] { 1, 1, 1 }; // 材料数量
			createitem = new int[] { Item_EPU_36, Item_EPU_44 }; // 料理
			break;
		case 5: // 制作水果糖醋肉
			materials = new int[] { 40056, 40061, 40060 }; // 材料
			counts = new int[] { 1, 1, 1 }; // 材料数量
			createitem = new int[] { Item_EPU_37, Item_EPU_45 }; // 料理
			break;
		case 6: // 制作烤山猪肉串
			materials = new int[] { Item_EPU_31 }; // 材料
			counts = new int[] { 1 }; // 材料数量
			createitem = new int[] { Item_EPU_38, Item_EPU_46 }; // 料理
			break;
		case 7: // 制作蘑菇汤
			materials = new int[] { 40499, 40060 }; // 材料
			counts = new int[] { 1, 1 }; // 材料数量
			createitem = new int[] { Item_EPU_39, Item_EPU_47 }; // 料理
			break;
		}

		// 容量重量计算
		int create_weight = 0;
		L1Item temp = ItemTable.getInstance().getTemplate(createitem[0]);
		create_weight += temp.getWeight() / 1000;

		// 重量确认+容量确认
		if (pc.getMaxWeight() < pc.getInventory().getWeight() + create_weight
				|| pc.getInventory().getSize() + 1 > 180) {

			// $1103 身上无法再携带烹饪的物品
			pc.sendPackets(new S_ServerMessage(1103));
			return;
		}
		// 材料消费
		for (int j = 0; j < materials.length; j++) {
			pc.getInventory().consumeItem(materials[j], counts[j]);
		}

		// 终于开始做了
		byte k = -1;
		int chance = _random.nextInt(10) + 1;
		if (chance >= 1 && chance <= 2) { // 1.2 失败

			// $1101 执行失败...(附动作真贴心)
			pc.sendPackets(new S_ServerMessage(1101));
			pc.sendPackets(new S_SkillSound(pc.getId(), 6394));
			pc.broadcastPacket(new S_SkillSound(pc.getId(), 6394));
			return;

		} else if (chance >= 3 && chance <= 8) { // 3~8 一般
			k = 0;
			pc.sendPackets(new S_SkillSound(pc.getId(), 6392));
			pc.broadcastPacket(new S_SkillSound(pc.getId(), 6392));

		} else if (chance >= 9 && chance <= 10) { // 9.10 特别
			k = 1;
			pc.sendPackets(new S_SkillSound(pc.getId(), 6390));
			pc.broadcastPacket(new S_SkillSound(pc.getId(), 6390));
		}
		L1ItemInstance item = pc.getInventory().storeItem(createitem[k], 1);

		if (item == null) {
			return;
		}
		String itemName = ItemTable.getInstance().getTemplate(createitem[k])
				.getName();
		// $403 获得%1%s %0%o
		pc.sendPackets(new S_ServerMessage(403, itemName));
	}

	// 进行料理 end
	/**
	 * 武器防具的使用<BR>
	 * 
	 * @param pc
	 * @param useItem
	 * @return 该职业可用传回:true
	 */
	private boolean useItem(final L1PcInstance pc, final L1ItemInstance useItem) {
		boolean isEquipped = false;
		// 职业与物件的使用限制
		if (pc.isCrown()) {// 王族
			if (useItem.getItem().isUseRoyal()) {
				isEquipped = true;
			}
		} else if (pc.isKnight()) {// 骑士
			if (useItem.getItem().isUseKnight()) {
				isEquipped = true;
			}
		} else if (pc.isElf()) {// 精灵
			if (useItem.getItem().isUseElf()) {
				isEquipped = true;
			}
		} else if (pc.isWizard()) {// 法师
			if (useItem.getItem().isUseMage()) {
				isEquipped = true;
			}
		} else if (pc.isDarkelf()) {// 黑暗精灵;
			if (useItem.getItem().isUseDarkelf()) {
				isEquipped = true;
			}
		} else if (pc.isDragonKnight()) {// 龙骑士
			if (useItem.getItem().isUseDragonknight()) {
				isEquipped = true;
			}
		} else if (pc.isIllusionist()) {// 幻术师
			if (useItem.getItem().isUseIllusionist()) {
				isEquipped = true;
			}
		}

		if (!isEquipped) {
			// 264 \f1你的职业无法使用此装备。
			pc.sendPackets(new S_ServerMessage(264));
		} else {
			if (pc.hasSkillEffect(COUNTER_BARRIER)) {
				pc.removeSkillEffect(COUNTER_BARRIER);
			}
		}
		return isEquipped;
	}

	@Override
	public String getType() {
		return C_ITEM_USE;
	}
}
