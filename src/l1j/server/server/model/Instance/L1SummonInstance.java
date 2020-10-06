package l1j.server.server.model.Instance;

import static l1j.william.New_Id.Npc_AJ_6_11;
import static l1j.william.New_Id.Npc_AJ_6_12;
import static l1j.william.New_Id.Npc_AJ_6_13;
import static l1j.william.New_Id.Npc_AJ_6_14;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;

import l1j.server.server.IdFactory;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1PinkName;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_HPMeter;
import l1j.server.server.serverpackets.S_PetMenuPacket;
import l1j.server.server.serverpackets.S_PinkName;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SummonPack;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.world.L1World;

public class L1SummonInstance extends L1NpcInstance {
	private static final long serialVersionUID = 1L;

	private ScheduledFuture<?> _summonFuture;
	private static final int  _summonTime = 3600;
	private int _currentPetStatus;
	private boolean _tamed;
	private static Random _random = new Random();

	// 场合处理
	@Override
	public boolean noTarget() {
		if (_currentPetStatus == 3) {
			// ● 休憩场合
			return true;
		} else if (_currentPetStatus == 4) {
			// ● 配备场合
			if (_master != null
					&& _master.getMapId() == getMapId()
					&& getLocation().getTileLineDistance(_master.getLocation()) < 5) {
				if (L1CastleLocation.checkInWarAreaIsNowWar(_master.getLocation())){
					_currentPetStatus = 3;
					return true;
				}
				int dir = targetReverseDirection(_master.getX(), _master.getY());
				dir = checkObject(getX(), getY(), getMapId(), dir);
				setDirectionMove(dir);
				setSleepTime(calcSleepTime(getPassispeed()));
			} else {
				// 主人见失５以上休憩状态
				_currentPetStatus = 3;
				return true;
			}
		} else if (_currentPetStatus == 5) {
			// ● 警戒场合
			if (Math.abs(getHomeX() - getX()) > 1
					|| Math.abs(getHomeY() - getY()) > 1) {
				int dir = moveDirection(getHomeX(), getHomeY());
				if (dir == -1) {
					// 离现在地
					setHomeX(getX());
					setHomeY(getY());
				} else {
					setDirectionMove(dir);
					setSleepTime(calcSleepTime(getPassispeed()));
				}
			}
		} else if (_master != null && _master.getMapId() == getMapId()) {
			if (L1CastleLocation.checkInWarAreaIsNowWar(_master.getLocation())){
				_currentPetStatus = 3;
				return true;
			}
			// ●主人追尾
			if (getLocation().getTileLineDistance(_master.getLocation()) > 2) {
				int dir = moveDirection(_master.getX(), _master.getY());
				/*删除if (dir == -1) {
					// 主人离休憩状态
					_currentPetStatus = 3;
					return true;
				} else {删除*/
					setDirectionMove(dir);
					setSleepTime(calcSleepTime(getPassispeed()));
				/*删除}*/
			}
		} /*删除else {
			// ● 主人见失休憩状态
			_currentPetStatus = 3;
			return true;
		}删除*/
		return false;
	}

/*	// １时间计测用
	class SummonTimer implements Runnable {
		@Override
		public void run() {
			if (_destroyed) { // 既破弃
				return;
			}
			if (_tamed) {
				// 、解放
				liberate();
			} else {
				// 解散
				Death(null);
			}
		}
	}*/

	// 用
	public L1SummonInstance(L1Npc template, L1Character master) {
		super(template);
		if (L1CastleLocation.checkInWarAreaIsNowWar(master.getLocation())){
			if (master instanceof L1PcInstance){
				((L1PcInstance)master).sendPackets(new S_SystemMessage("战争区域内不能召唤."));
			}
			return;
		}
		setId(IdFactory.getInstance().nextId());

		// 变更召唤时间 
		if (getNpcTemplate().get_npcId() == Npc_AJ_6_11 
		 || getNpcTemplate().get_npcId() == Npc_AJ_6_12 
		 || getNpcTemplate().get_npcId() == Npc_AJ_6_13 
		 || getNpcTemplate().get_npcId() == Npc_AJ_6_14) { // 风龙、地龙、水龙、火龙
//				TIME = 60000L; // 60秒
				setMagicDmg(master.getInt()); // 魔法攻击力
		}
		/*_summonFuture = GeneralThreadPool.getInstance().schedule(
				new SummonTimer(), TIME);*/
		this.set_time(_summonTime);
		// 变更召唤时间  end

		setMaster(master);
		setX(master.getX() + _random.nextInt(5) - 2);
		setY(master.getY() + _random.nextInt(5) - 2);
		setMap(master.getMapId());
		setHeading(5);
		setLightSize(template.getLightSize());
		setLawful(0);

		_currentPetStatus = 3;
		_tamed = false;

		// 风龙、地龙、水龙、火龙 
		if (getNpcTemplate().get_npcId() == Npc_AJ_6_11 
		 || getNpcTemplate().get_npcId() == Npc_AJ_6_12 
		 || getNpcTemplate().get_npcId() == Npc_AJ_6_13 
		 || getNpcTemplate().get_npcId() == Npc_AJ_6_14) {
			_currentPetStatus = 1;
			if (!isAiRunning()) {
				startAI();
			}
		}
		// 风龙、地龙、水龙、火龙  end

		// 更新怪物武器状态 
		if (getNpcTemplate().getAttStatus() != 0) {
			setStatus(getNpcTemplate().getAttStatus());
		}
		// 更新怪物武器状态  end

		L1World.getInstance().storeWorldObject(this);
		L1World.getInstance().addVisibleObject(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			onPerceive(pc);
		}
		master.addPet(this);
	}

	// 、用
	public L1SummonInstance(L1NpcInstance target, L1Character master,
			boolean createZombie) {
		super(null);
		if (L1CastleLocation.checkInWarAreaIsNowWar(master.getLocation())){
			if (master instanceof L1PcInstance){
				((L1PcInstance)master).sendPackets(new S_SystemMessage("战争区域内不能召唤."));
			}
			return;
		}
		setId(IdFactory.getInstance().nextId());

		if (createZombie) // 
		{
			// 删除L1Npc template = NpcTable.getInstance().getTemplate(Mob).clone(); // 普通作成
			// 造尸术修正 
			int Mob = 45065;
			L1PcInstance pc = (L1PcInstance) master;
			if (pc.isWizard()) { // 法师
				if (master.getLevel() >= 24 && master.getLevel() <= 31) {
					Mob = l1j.william.New_Id. Npc_AJ_6_5;
				} else if (master.getLevel() >= 32 && master.getLevel() <= 39) {
					Mob = l1j.william.New_Id. Npc_AJ_6_6;
				} else if (master.getLevel() >= 40 && master.getLevel() <= 43) {
					Mob = l1j.william.New_Id. Npc_AJ_6_7;
				} else if (master.getLevel() >= 44 && master.getLevel() <= 47) {
					Mob = l1j.william.New_Id. Npc_AJ_6_8;
				} else if (master.getLevel() >= 48 && master.getLevel() <= 51) {
					Mob = l1j.william.New_Id. Npc_AJ_6_9;
				} else if (master.getLevel() >= 52) {
					Mob = l1j.william.New_Id. Npc_AJ_6_10;
				}
			} else if (pc.isElf()) { // 妖精
				if (master.getLevel() >= 48) {
					Mob = l1j.william.New_Id. Npc_AJ_6_5;
				}
			}
			L1Npc template = NpcTable.getInstance().getTemplate(Mob).clone();
			// 造尸术修正  end
			// template.set_level((int) Math.ceil(master.get_level() * 0.66));
			// //
			// 咏唱者３分２
			// template.set_hp(template.get_level() * 10); // １０倍
			// template.set_hp(template.get_level() * 5); // ５倍
			// template.set_ac(-(master.get_level() - 10)); // 咏唱者ＡＣ
			setting_template(template);
		} else // 
		{
			setting_template(target.getNpcTemplate());
			setCurrentHpDirect(target.getCurrentHp());
			setCurrentMpDirect(target.getCurrentMp());
		}

/*		_summonFuture = GeneralThreadPool.getInstance().schedule(
				new SummonTimer(), SUMMON_TIME);*/
		this.set_time(_summonTime);

		setMaster(master);
		setX(target.getX());
		setY(target.getY());
		setMap(target.getMapId());
		setHeading(target.getHeading());
		setLightSize(target.getLightSize());
		setPetcost(6);
		setLawful(0);
		if (target instanceof L1MonsterInstance
				&& !((L1MonsterInstance) target).is_storeDroped()) {
			DropTable.getInstance().setDrop(target, target.getInventory());
		}
		setInventory(target.getInventory());
		target.setInventory(null);

		_currentPetStatus = 3;
		
		// 造尸术修正 
		if (createZombie)
		_tamed = false;
		else
		// 造尸术修正  end
		_tamed = true;

		// 攻中场合止
		for (L1NpcInstance each : master.getPetList().values()) {
			each.targetRemove(target);
		}

		target.deleteMe();
		L1World.getInstance().storeWorldObject(this);
		L1World.getInstance().addVisibleObject(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			onPerceive(pc);
		}
		master.addPet(this);
	}

	@Override
	public void receiveDamage(L1Character attacker, int damage) // 攻击ＨＰ减使用
	{
		if (getCurrentHp() > 0) {
			if (damage > 0) {
				setHate(attacker, 0); // 无
				removeSkillEffect(L1SkillId.FOG_OF_SLEEPING);
//				System.out.println("召唤兽攻击者:"+attacker.getName());
				L1PinkName.onAction(this, attacker);
/*				if (attacker.isVdmg()) {
					if (attacker instanceof L1PcInstance){
						L1PcInstance player = (L1PcInstance) attacker;
						String msg = "输出->"+damage;
						S_ChatPacket s_chatpacket = new S_ChatPacket(this, msg,
								Opcodes.S_OPCODE_NORMALCHAT);
						player.sendPackets(s_chatpacket);
					}
				}*/
				
			}

			int newHp = getCurrentHp() - damage;
			if (newHp <= 0) {
				Death(attacker);
			} else {
				setCurrentHp(newHp);
			}
		} else if (!isDead()) // 念
		{
			System.out.println("警告：ＨＰ减少处理正行个所。※最初ＨＰ０");
			Death(attacker);
		}
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
			S_SkillSound packet = new S_SkillSound(
					getId(), 169);
			broadcastPacket(packet);
			deleteMe();

/*			if (_tamed) {
				broadcastPacket(new S_DoActionGFX(getId(),
						ActionCodes.ACTION_Die));
				startDeleteTimer();
			} else {
				// 风龙、地龙、水龙、火龙
				if (getNpcTemplate().get_npcId() == Npc_AJ_6_11 
				 || getNpcTemplate().get_npcId() == Npc_AJ_6_12 
				 || getNpcTemplate().get_npcId() == Npc_AJ_6_13 
				 || getNpcTemplate().get_npcId() == Npc_AJ_6_14) {
				 	broadcastPacket(new S_DoActionGFX(getId(),ActionCodes.ACTION_Die));
				 	startDeleteTimer();
				 } else {
				 	deleteMe();
			 	}
			 	// 风龙、地龙、水龙、火龙
			}*/
		}
	}

	// 消去处理
	@Override
	public synchronized void deleteMe() {
		if (_destroyed) {
			return;
		}
		if (!_tamed) {
			// 风龙、地龙、水龙、火龙 
			if (getNpcTemplate().get_npcId() != Npc_AJ_6_11 
				 && getNpcTemplate().get_npcId() != Npc_AJ_6_12 
				 && getNpcTemplate().get_npcId() != Npc_AJ_6_13 
				 && getNpcTemplate().get_npcId() != Npc_AJ_6_14)
			// 风龙、地龙、水龙、火龙  end
			broadcastPacket(new S_SkillSound(getId(), 169));
		}
		_master.getPetList().remove(getId());
		super.deleteMe();

		if (_summonFuture != null) {
			_summonFuture.cancel(false);
			_summonFuture = null;
		}
	}

	// 、时解放处理
	public void liberate() {
		Death(null);
/*		L1MonsterInstance monster = new L1MonsterInstance(getNpcTemplate());
		monster.setId(IdFactory.getInstance().nextId());

		monster.setX(getX());
		monster.setY(getY());
		monster.setMap(getMapId());
		monster.setHeading(getHeading());
		monster.set_storeDroped(true);
		monster.setInventory(getInventory());
		setInventory(null);
		monster.setCurrentHpDirect(getCurrentHp());
		monster.setCurrentMpDirect(getCurrentMp());
		monster.setExp(0);

		deleteMe();
		L1World.getInstance().storeObject(monster);
		L1World.getInstance().addVisibleObject(monster);*/
	}

	public void setTarget(L1Character target) {
		if (target != null
				&& (_currentPetStatus == 1 || _currentPetStatus == 2 || _currentPetStatus == 5)) {
			setHate(target, 0);
			if (!isAiRunning()) {
				startAI();
			}
		}
	}

	public void setMasterTarget(L1Character target) {
		if (target != null
				&& (_currentPetStatus == 1 || _currentPetStatus == 5)) {
			setHate(target, 0);
			if (!isAiRunning()) {
				startAI();
			}
		}
	}

	@Override
	public void onAction(L1PcInstance attacker) {
		// XXX:NullPointerException回避。onAction引数型L1Character良？
		if (attacker == null) {
			return;
		}
		L1Character cha = this.getMaster();
		if (cha == null) {
			return;
		}
		L1PcInstance master = (L1PcInstance) cha;
		if (master.isTeleport()) {
			// 处理中
			return;
		}
		if (getZoneType() == 1 || attacker.getZoneType() == 1) {
			// 攻击侧
			// 攻击送信
			L1Attack attack_mortion = new L1Attack(attacker, this);
			attack_mortion.action();
			return;
		}

		if (attacker.checkNonPvP(attacker, this)) {
			return;
		}

		L1Attack attack = new L1Attack(attacker, this);
		if (attack.calcHit()) {
			attack.calcDamage();
		}
		attack.action();
		attack.commit();
	}

	@Override
	public void onTalkAction(L1PcInstance player) {
		if (isDead()) {
			return;
		}
		if (_master.equals(player)) {
			player.sendPackets(new S_PetMenuPacket(this, 0));
		}
	}

	@Override
	public void onFinalAction(L1PcInstance player, String action) {
		int status = ActionType(action);
		if (status == 0) {
			return;
		}
		if (status == 6) {
			if (_tamed) {
				// 、解放
				liberate();
			} else {
				// 解散
				Death(null);
			}
		} else {
			// 同主人状态更新
			Object[] petList = _master.getPetList().values().toArray();
			for (Object petObject : petList) {
				if (petObject instanceof L1SummonInstance) {
					// 
					L1SummonInstance summon = (L1SummonInstance) petObject;
					summon.set_currentPetStatus(status);
				} else {
					// 
				}
			}
		}
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.addKnownObject(this);
		perceivedFrom.sendPackets(new S_SummonPack(this, perceivedFrom));
		if (getPinkSec()>0) {
			perceivedFrom.sendPackets(new S_PinkName(getId(), getPinkSec()));
		}
		// 风龙、地龙、水龙、火龙 
/*		if (getNpcTemplate().get_npcId() == Npc_AJ_6_11 
			|| getNpcTemplate().get_npcId() == Npc_AJ_6_12 
			|| getNpcTemplate().get_npcId() == Npc_AJ_6_13 
		 || getNpcTemplate().get_npcId() == Npc_AJ_6_14) {
		 	if (isDead()) {
				perceivedFrom.sendPackets(new S_DoActionGFX(getId(), ActionCodes.ACTION_Die));
			}
		 }*/
		 // 风龙、地龙、水龙、火龙  end
	}

	@Override
	public void onItemUse() {
		if (!isActived()) {
			// １００％确率使用
			for (L1ItemInstance item : getInventory().getItems()) {
				if (Arrays
						.binarySearch(haestPotions, item.getItem().getItemId()) >= 0) {
					useItem(item,USEITEM_HASTE, 100);
				}
			}
//			useItem(USEITEM_HASTE, 100);
		}
		if (getCurrentHp() * 100 / getMaxHp() < 40) {
			// ＨＰ４０％
			// １００％确率回复使用
			for (L1ItemInstance item : getInventory().getItems()) {
				if (Arrays.binarySearch(healPotions, item.getItem().getItemId()) >= 0) {
					useItem(item,USEITEM_HEAL, 100);
				}
			}
//			useItem(USEITEM_HEAL, 100);
		}
	}

	@Override
	public void onGetItem(L1ItemInstance item) {
		if (getNpcTemplate().get_digestitem() > 0) {
			setDigestItem(item);
		}
		Arrays.sort(healPotions);
		Arrays.sort(haestPotions);
		if (Arrays.binarySearch(healPotions, item.getItem().getItemId()) >= 0) {
			if (getCurrentHp() != getMaxHp()) {
				useItem(item,USEITEM_HEAL, 100);
			}
		} else if (Arrays
				.binarySearch(haestPotions, item.getItem().getItemId()) >= 0) {
			useItem(item,USEITEM_HASTE, 100);
		}
	}

	private int ActionType(String action) {
		int status = 0;
		if (action.equalsIgnoreCase("aggressive")) { // 攻击态势
			status = 1;
		} else if (action.equalsIgnoreCase("defensive")) { // 防御态势
			status = 2;
		} else if (action.equalsIgnoreCase("stay")) { // 休憩
			status = 3;
		} else if (action.equalsIgnoreCase("extend")) { // 配备
			status = 4;
		} else if (action.equalsIgnoreCase("alert")) { // 警戒
			status = 5;
		} else if (action.equalsIgnoreCase("dismiss")) { // 解散
			status = 6;
		}
		return status;
	}

	@Override
	public void setCurrentHp(int i) {
		int currentHp = i;
		if (currentHp >= getMaxHp()) {
			currentHp = getMaxHp();
		}
		setCurrentHpDirect(currentHp);

		if (getMaxHp() > getCurrentHp()) {
			startHpRegeneration();
		}

		if (_master instanceof L1PcInstance) {
			int HpRatio = 100 * currentHp / getMaxHp();
			L1PcInstance Master = (L1PcInstance) _master;
			Master.sendPackets(new S_HPMeter(getId(), HpRatio));
		}
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

	public void set_currentPetStatus(int i) {
		_currentPetStatus = i;
		if (_currentPetStatus == 5) {
			setHomeX(getX());
			setHomeY(getY());
		}

		if (_currentPetStatus == 3) {
			allTargetClear();
		} else {
			if (!isAiRunning()) {
				startAI();
			}
		}
	}

	public int get_currentPetStatus() {
		return _currentPetStatus;
	}

	public boolean destroyed() {
		return _destroyed;
	}

	private int _time = 0;

	/**
	 * 设置剩余时间
	 * 
	 * @return
	 */
	public int get_time() {
		return _time;
	}

	/**
	 * 剩余使用时间
	 * 
	 * @param time
	 */
	public void set_time(final int time) {
		this._time = time;
	}
}