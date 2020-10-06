package l1j.server.server.model.Instance;

import java.util.List;
import java.util.Random;//加入机率 
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import static l1j.server.server.model.skill.L1SkillId.*;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_AttackMissPacket;//祭司主动补血功能 
import l1j.server.server.serverpackets.S_HierarchPack;//祭司功能 
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.world.L1World;

public class L1HierarchInstance extends L1NpcInstance {
	private static final long serialVersionUID = 1L;


	private ScheduledFuture<?> _summonFuture;
	private static final long SUMMON_TIME = 3600;
	private boolean _tamed;
	private static Random _random = new Random();

	// 场合处理
	@Override
	public boolean noTarget() {
		/*
		 * if(getHierarch() == 2) { Death(null); } else if (_master != null &&
		 * (_master.getCurrentHp() > 0) && (_master.getCurrentHp() <=
		 * ((_master.getMaxHp() * _master.getHierarch()) / 10)) &&
		 * (getCurrentMp() > 15) &&
		 * (getLocation().getTileLineDistance(_master.getLocation()) < 7) &&
		 * (_master.glanceCheck(_master.getX(), _master.getY()) == true) &&
		 * (getHierarch() == 1)) { // 祭司主动补血 if (_master instanceof
		 * L1PcInstance) { Random random = new Random(); byte chance = (byte)
		 * (random.nextInt(75) + 1); L1PcInstance player = (L1PcInstance)
		 * _master; broadcastPacket(new S_SkillSound(player.getId(), 830));
		 * broadcastPacket(new S_AttackMissPacket(this, player.getId(), 19));
		 * player.setCurrentHp(player.getCurrentHp() + (getLawful() + chance));
		 * player.sendPackets(new S_ServerMessage(77, ""));//你觉得舒服多了。
		 * player.sendPackets(new S_HPUpdate(player.getCurrentHp(),
		 * player.getMaxHp())); setCurrentMp(getCurrentMp() - 16);
		 * 
		 * //player.sendPackets(new S_OwnCharStatus(player)); if
		 * (player.isInParty()) { // 中
		 * player.getParty().updateMiniHP(player); }
		 * setSleepTime(calcSleepTime(getAtkexspeed())); } } else if (_master !=
		 * null && ((!_master.hasSkillEffect(PHYSICAL_ENCHANT_STR) &&
		 * getNpcTemplate().get_npcId() != l1j.william.New_Id.Npc_AJ_1_1) ||
		 * (!_master.hasSkillEffect(PHYSICAL_ENCHANT_DEX) &&
		 * getNpcTemplate().get_npcId() != l1j.william.New_Id.Npc_AJ_1_1) ||
		 * !_master.hasSkillEffect(48)) && (_master.getCurrentHp() > 0) &&
		 * (getCurrentMp() > 9) && (getHierarch() == 1)) {// 加辅助效果 L1PcInstance
		 * player = (L1PcInstance) _master; if
		 * (!player.hasSkillEffect(PHYSICAL_ENCHANT_DEX) &&
		 * (getNpcTemplate().get_npcId()== l1j.william.New_Id.Npc_AJ_1_5 ||
		 * getNpcTemplate().get_npcId()== l1j.william.New_Id.Npc_AJ_1_6)) {
		 * player.addDex((byte) 5); player.sendPackets(new S_Dexup(player, 5,
		 * 600)); player.setSkillEffect(26, 600 * 1000); broadcastPacket(new
		 * S_SkillSound(player.getId(), 750)); L1Skills skill =
		 * SkillsTable.getInstance().getTemplate( PHYSICAL_ENCHANT_DEX); new
		 * L1SkillUse().handleCommands(player, PHYSICAL_ENCHANT_DEX,
		 * player.getId(), player.getX(), player.getY(), null,
		 * skill.getBuffDuration(), L1SkillUse.TYPE_GMBUFF);
		 * setCurrentMp(getCurrentMp() - 10); broadcastPacket(new
		 * S_AttackMissPacket(this, player.getId(), 19));
		 * setSleepTime(calcSleepTime(getAtkexspeed())); }
		 * 
		 * if (!player.hasSkillEffect(PHYSICAL_ENCHANT_STR) &&
		 * (getNpcTemplate().get_npcId()== l1j.william.New_Id.Npc_AJ_1_5 ||
		 * getNpcTemplate().get_npcId()== l1j.william.New_Id.Npc_AJ_1_6)) {
		 * player.addStr((byte) 5); player.sendPackets(new S_Strup(player, 5,
		 * 600)); player.setSkillEffect(42, 600 * 1000); broadcastPacket(new
		 * S_SkillSound(player.getId(), 751)); L1Skills skill =
		 * SkillsTable.getInstance().getTemplate( PHYSICAL_ENCHANT_STR); new
		 * L1SkillUse().handleCommands(player, PHYSICAL_ENCHANT_STR,
		 * player.getId(), player.getX(), player.getY(), null,
		 * skill.getBuffDuration(), L1SkillUse.TYPE_GMBUFF);
		 * setCurrentMp(getCurrentMp() - 10); broadcastPacket(new
		 * S_AttackMissPacket(this, player.getId(), 19));
		 * setSleepTime(calcSleepTime(getAtkexspeed())); } L1ItemInstance weapon
		 * = player.getWeapon(); if (weapon!=null) { if (weapon.getEnchant()==0)
		 * { L1Skills skill = SkillsTable.getInstance().getTemplate(
		 * BLESS_WEAPON); new L1SkillUse().handleCommands(player, BLESS_WEAPON,
		 * player.getId(), player.getX(), player.getY(), null,
		 * skill.getBuffDuration(), L1SkillUse.TYPE_GMBUFF);
		 * setCurrentMp(getCurrentMp() - 10); broadcastPacket(new
		 * S_AttackMissPacket(this, player.getId(), 19));
		 * setSleepTime(calcSleepTime(getAtkexspeed())); } }
		 * 
		 * if (!player.hasSkillEffect(48)) { player.addDmgup(2);
		 * player.addHitup(2); player.setSkillEffect(48, 1200 * 1000);
		 * broadcastPacket(new S_SkillSound(player.getId(), 2176)); } } else
		 */if (_master != null && _master.getMapId() == getMapId()) {
			if (getLocation().getTileLineDistance(_master.getLocation()) > 2) {
				setDirectionMove(moveDirection(_master.getX(), _master.getY()));
				setSleepTime(calcSleepTime(getPassispeed()));
			}
		}
		return false;
	}

	private void check() {
		if (_master != null) {
			if ((_master.getCurrentHp() > 0)
					&& (getCurrentMp() > 15)
					&& (getLocation()
							.getTileLineDistance(_master.getLocation()) < 7)
					&& (_master.glanceCheck(_master.getX(), _master.getY()) == true)) {
				L1PcInstance player = (L1PcInstance) _master;
				switch (getHierarch()) {
				case 1:
					if (_master.getCurrentHp() <= ((_master.getMaxHp() * _master
							.getHierarch()) / 10)) {
						byte chance = (byte) (_random.nextInt(75) + 1);
						broadcastPacket(new S_SkillSound(player.getId(), 830));
						/*broadcastPacket(new S_AttackMissPacket(this,
								player.getId(), 19));*/
						player.setCurrentHp(player.getCurrentHp()
								+ (getLawful() + chance));
						player.sendPackets(new S_ServerMessage(77, ""));// 你觉得舒服多了。
						player.sendPackets(new S_HPUpdate(
								player.getCurrentHp(), player.getMaxHp()));
						setCurrentMp(getCurrentMp() - 16);
						// player.sendPackets(new S_OwnCharStatus(player));
						if (player.isInParty()) { // 中
							player.getParty().updateMiniHP(player);
						}
						setSleepTime(calcSleepTime(getAtkexspeed()));
					}
					if (!player.hasSkillEffect(PHYSICAL_ENCHANT_DEX)
							&& (getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_1_5 || getNpcTemplate()
									.get_npcId() == l1j.william.New_Id.Npc_AJ_1_6)) {
						/*
						 * player.addDex((byte) 5); player.sendPackets(new
						 * S_Dexup(player, 5, 600)); player.setSkillEffect(26,
						 * 600 * 1000); broadcastPacket(new
						 * S_SkillSound(player.getId(), 750));
						 */
						L1Skills skill = SkillsTable.getInstance().getTemplate(
								PHYSICAL_ENCHANT_DEX);
						new L1SkillUse()
								.handleCommands(player, PHYSICAL_ENCHANT_DEX,
										player.getId(), player.getX(),
										player.getY(), null,
										skill.getBuffDuration(),
										L1SkillUse.TYPE_GMBUFF);
						setCurrentMp(getCurrentMp() - 10);
						/*broadcastPacket(new S_AttackMissPacket(this,
								player.getId(), 19));*/
						setSleepTime(calcSleepTime(getAtkexspeed()));
					}

					if (!player.hasSkillEffect(PHYSICAL_ENCHANT_STR)
							&& (getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_1_5 || getNpcTemplate()
									.get_npcId() == l1j.william.New_Id.Npc_AJ_1_6)) {
						/*
						 * player.addStr((byte) 5); player.sendPackets(new
						 * S_Strup(player, 5, 600)); player.setSkillEffect(42,
						 * 600 * 1000); broadcastPacket(new
						 * S_SkillSound(player.getId(), 751));
						 */
						L1Skills skill = SkillsTable.getInstance().getTemplate(
								PHYSICAL_ENCHANT_STR);
						new L1SkillUse()
								.handleCommands(player, PHYSICAL_ENCHANT_STR,
										player.getId(), player.getX(),
										player.getY(), null,
										skill.getBuffDuration(),
										L1SkillUse.TYPE_GMBUFF);
						setCurrentMp(getCurrentMp() - 10);
						/*broadcastPacket(new S_AttackMissPacket(this,
								player.getId(), 19));*/
						setSleepTime(calcSleepTime(getAtkexspeed()));
					}
					L1ItemInstance weapon = player.getWeapon();
					if (weapon != null) {
						if (weapon.getEnchant() == 0) {
							L1Skills skill = SkillsTable.getInstance()
									.getTemplate(BLESS_WEAPON);
							new L1SkillUse().handleCommands(player,
									BLESS_WEAPON, player.getId(),
									player.getX(), player.getY(), null,
									skill.getBuffDuration(),
									L1SkillUse.TYPE_GMBUFF);
							setCurrentMp(getCurrentMp() - 10);
							/*broadcastPacket(new S_AttackMissPacket(this,
									player.getId(), 19));*/
							setSleepTime(calcSleepTime(getAtkexspeed()));
						}
					}
					break;
				case 2:
					Death(null);
					break;
				default:
					break;
				}
			}

		}
	}

	// １时间计测用
	class SummonTimer implements Runnable {
		@Override
		public void run() {
			try {
				if (_destroyed) { // 既破弃
					return;
				}
				for (int i = 0; i < SUMMON_TIME; i++) {
					check();
					Thread.sleep(1000);
				}
				// 解散
				Death(null);
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	// 用
	public L1HierarchInstance(L1Npc template, L1Character master) {
		super(template);
		setId(IdFactory.getInstance().nextId());

		_summonFuture = GeneralThreadPool.getInstance().schedule(
				new SummonTimer(), SUMMON_TIME);

		setMaster(master);
		setX(master.getX() + _random.nextInt(5) - 2);
		setY(master.getY() + _random.nextInt(5) - 2);
		setMap(master.getMapId());
		setHeading(5);
		setLightSize(template.getLightSize());

		broadcastPacket(new S_SkillHaste(getId(), 1, 0));
		setMoveSpeed(1);
		setSkillEffect(43, 3600 * 1000);

		startAI();

		_tamed = false;

		L1World.getInstance().storeWorldObject(this);
		L1World.getInstance().addVisibleObject(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			onPerceive(pc);
		}
		master.addPet(this);
	}

	public synchronized void Death(L1Character lastAttacker) {
		if (!isDead()) {
			setDead(true);
			setCurrentHp(0);

			getMap().setPassable(getLocation(), true);

			// 解放处理
			L1Inventory targetInventory = _master.getInventory();
			List<L1ItemInstance> items = _inventory.getItems();
			for (L1ItemInstance item : items) {
				if (_master.getInventory().checkAddItem( // 容量重量确认及送信
						item, item.getCount()) == L1Inventory.OK) {
					_inventory
							.tradeItem(item, item.getCount(), targetInventory);
					// \f1%0%1。
					((L1PcInstance) _master).sendPackets(new S_ServerMessage(
							143, getName(), item.getLogName()));
				} else { // 持足元落
					targetInventory = L1World.getInstance().getInventory(
							getX(), getY(), getMapId());
					_inventory
							.tradeItem(item, item.getCount(), targetInventory);
				}
			}

			// 取消判断
			if (_master.hasSkillEffect(l1j.william.New_Id.Skill_AJ_1_1)) {
				_master.killSkillEffectTimer(l1j.william.New_Id.Skill_AJ_1_1);
			}
			// 取消判断 end

			deleteMe();
		}
	}

	// 消去处理
	@Override
	public synchronized void deleteMe() {
		if (_destroyed) {
			return;
		}
		if (!_tamed) {
			broadcastPacket(new S_SkillSound(getId(), 5936));
		}
		_master.getPetList().remove(getId());
		super.deleteMe();

		if (_summonFuture != null) {
			_summonFuture.cancel(false);
			_summonFuture = null;
		}
	}

	@Override
	public void onAction(L1PcInstance player) {
		// System.out.println("攻击步骤1");
		L1Attack attack = new L1Attack(player, this);
		// System.out.println("攻击步骤2");
		// 命中判断
		if (attack.calcHit()) {
			// System.out.println("攻击步骤3");
			attack.calcDamage();
			// System.out.println("攻击步骤4");
			attack.calcStaffOfMana();
			// System.out.println("攻击步骤5");
			attack.addPcPoisonAttack(player, this);
			// System.out.println("攻击步骤6");
		}
		// 命中判断
		// System.out.println("攻击步骤7");
		attack.action();
		attack.commit();
		// System.out.println("攻击步骤8");
	}

	@Override
	public void receiveDamage(L1Character attacker, int damage) {
		// System.out.println("步骤1伤害"+damage);
		if (_master != null) {
			if (attacker.getId() == _master.getId()) {
				return;
			}
		}
		// System.out.println("步骤2伤害"+damage);
		if (getCurrentHp() > 0 && !isDead()) {
			int newHp = getCurrentHp() - damage;
			if (newHp <= 0 && !isDead()) {
				// System.out.println("伤害过量直接死亡一");
				Death(attacker);
			} else {
				if (newHp > 0) {
					setCurrentHp(newHp);
					// System.out.println("当前血量"+newHp);
				} else {
					// System.out.println("伤害过量直接死亡二");
					Death(attacker);
				}
			}
		}
	}

	@Override
	public void onTalkAction(L1PcInstance player) {
		if (isDead()) {
			return;
		}
		if (_master.equals(player)) {
			String[] htmldata = null;
			String msg0 = "";
			String msg1 = String.valueOf(player.getHierarch() * 10);
			if (getHierarch() == 1) {
				msg0 = "辅助";
			} else {
				msg0 = "跟随";
			}
			htmldata = new String[] { getName(),
					String.valueOf(getCurrentMp()), String.valueOf(getMaxMp()),
					msg0, msg1 };
			player.sendPackets(new S_NPCTalkReturn(getId(), "Hierarch",
					htmldata));
		}
	}

	@Override
	public void onFinalAction(L1PcInstance player, String action) {
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.addKnownObject(this);
		perceivedFrom.sendPackets(new S_HierarchPack(this, perceivedFrom));// 变更成S_HierarchPack
	}

	@Override
	public void setCurrentMp(int i) {
		int currentMp = i;
		if (currentMp >= getMaxMp()) {
			currentMp = getMaxMp();
		}
		setCurrentMpDirect(currentMp);
		if (getMaxMp() > getCurrentMp()) {
			startMpRegeneration();
		}
	}
}