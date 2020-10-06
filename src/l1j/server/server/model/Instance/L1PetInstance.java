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

package l1j.server.server.model.Instance;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import l1j.server.server.ActionCodes;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.PetItemTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.datatables.PetTypeTable;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1PinkName;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_HPMeter;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_PetAttr;
import l1j.server.server.serverpackets.S_PetMenuPacket;
import l1j.server.server.serverpackets.S_PetPack;
import l1j.server.server.serverpackets.S_PinkName;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillHaste;//宠物加速效果 
import l1j.server.server.serverpackets.S_SkillSound;//宠物进化效果 
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1Pet;
import l1j.server.server.templates.L1PetItem;
import l1j.server.server.templates.L1PetType;
import l1j.server.server.utils.IntRange;
import l1j.server.server.world.L1World;

public class L1PetInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;
	private static Random _random = new Random();

	// 场合处理
	@Override
	public boolean noTarget() {
		if (_currentPetStatus == 3) { // ● 休憩场合
			return true;
		} else if (_currentPetStatus == 4) { // ● 配备场合
			if (_petMaster != null
					&& _petMaster.getMapId() == getMapId()
					&& getLocation().getTileLineDistance(
							_petMaster.getLocation()) < 5) {
				if (L1CastleLocation.checkInWarAreaIsNowWar(_petMaster.getLocation())){
					_currentPetStatus = 3;
					return true;
				}
				int dir = targetReverseDirection(_petMaster.getX(), _petMaster
						.getY());
				dir = checkObject(getX(), getY(), getMapId(), dir);
				setDirectionMove(dir);
				setSleepTime(calcSleepTime(getPassispeed()));
			} else { // 主人见失５以上休憩状态
				_currentPetStatus = 3;
				return true;
			}
		} else if (_currentPetStatus == 5) { // ● 警戒场合
			if (Math.abs(getHomeX() - getX()) > 1
					|| Math.abs(getHomeY() - getY()) > 1) {
				int dir = moveDirection(getHomeX(), getHomeY());
				if (dir == -1) { // 离现在地
					setHomeX(getX());
					setHomeY(getY());
				} else {
					setDirectionMove(dir);
					setSleepTime(calcSleepTime(getPassispeed()));
				}
			}
		} else if (_currentPetStatus == 7) { // ● 笛主人元
			if (_petMaster != null
					&& _petMaster.getMapId() == getMapId()
					&& getLocation().getTileLineDistance(
							_petMaster.getLocation()) <= 1) {
				_currentPetStatus = 3;
				return true;
			}
			if (L1CastleLocation.checkInWarAreaIsNowWar(_petMaster.getLocation())){
				_currentPetStatus = 3;
				return true;
			}
			int locx = _petMaster.getX() + _random.nextInt(1);
			int locy = _petMaster.getY() + _random.nextInt(1);
			int dir = moveDirection(locx, locy);
			if (dir == -1) { // 主人见失场休憩状态
				_currentPetStatus = 3;
				return true;
			}
			setDirectionMove(dir);
			setSleepTime(calcSleepTime(getPassispeed()));
		} else if (_petMaster != null && _petMaster.getMapId() == getMapId()) { // ●
			if (L1CastleLocation.checkInWarAreaIsNowWar(_petMaster.getLocation())){
				_currentPetStatus = 3;
				return true;
			}
			// 主人追尾
			if (getLocation().getTileLineDistance(_petMaster.getLocation()) > 2) {
				int dir = moveDirection(_petMaster.getX(), _petMaster.getY());
				/*删除if (dir == -1) { // 主人离休憩状态
					_currentPetStatus = 3;
					return true;
				}删除*/
				setDirectionMove(dir);
				setSleepTime(calcSleepTime(getPassispeed()));
			}
		} /*删除else { // ● 主人见失休憩状态
			_currentPetStatus = 3;
			return true;
		}删除*/
		return false;
	}

	// 领出
	public L1PetInstance(L1Npc template, L1PcInstance master, L1Pet l1pet) {
		super(template);

		_petMaster = master;
		_itemObjId = l1pet.get_itemobjid();
		_type = PetTypeTable.getInstance().get(template.get_npcId());

		// 上书
		setId(l1pet.get_objid());
		setName(l1pet.get_name());
		setLevel(l1pet.get_level());
		// HPMPMAX
		setMaxHp(l1pet.get_hp());
		setCurrentHpDirect(l1pet.get_hp());
		setMaxMp(l1pet.get_mp());
		setCurrentMpDirect(l1pet.get_mp());
		setExp(l1pet.get_exp());
		setLawful(l1pet.get_lawful());
		setTempLawful(l1pet.get_lawful());

		setMaster(master);
		setX(master.getX() + _random.nextInt(5) - 2);
		setY(master.getY() + _random.nextInt(5) - 2);
		setMap(master.getMapId());
		setHeading(5);
		setLawful(0);
		setLightSize(template.getLightSize());
		int expPercentage = ExpTable.getExpPercentage(getLevel(), getExp());
		setExpPercent(expPercentage);

		_currentPetStatus = 3;

		L1World.getInstance().storeWorldObject(this);
		L1World.getInstance().addVisibleObject(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			onPerceive(pc);
		}
		//宠物领出后获得加速术1800秒 
		broadcastPacket(new S_SkillSound(getId(), 3104));
		broadcastPacket(new S_SkillHaste(getId(), 1, 0));
		setMoveSpeed(1);
		setSkillEffect(43, 1800000);
		//宠物领出后获得加速术1800秒  end
		master.addPet(this);
		_pet = l1pet;
	}

	// 抓取
	public L1PetInstance(L1NpcInstance target, L1PcInstance master, int itemid) {
		super(null);

		_petMaster = master;
		_itemObjId = itemid;
		_type = PetTypeTable.getInstance().get(
				target.getNpcTemplate().get_npcId());

		// 上书
		setId(IdFactory.getInstance().nextId());
		setting_template(target.getNpcTemplate());
		setCurrentHpDirect(target.getCurrentHp());
		setCurrentMpDirect(target.getCurrentMp());
		setExp(750); // Lv.5EXP
		setExpPercent(0);
		setLawful(0);
		setTempLawful(0);

		setMaster(master);
		setX(target.getX());
		setY(target.getY());
		setMap(target.getMapId());
		setHeading(target.getHeading());
		setLightSize(target.getLightSize());
		setPetcost(6);
		setInventory(target.getInventory());
		target.setInventory(null);

		_currentPetStatus = 3;
		//修正驯养后回血回血 
		stopHpRegeneration();
		if (getMaxHp() > getCurrentHp()) {
			startHpRegeneration();
		}
		stopMpRegeneration();
		if (getMaxMp() > getCurrentMp()) {
			startMpRegeneration();
		}
		//修正驯养后回血回血  end

		target.deleteMe();
		L1World.getInstance().storeWorldObject(this);
		L1World.getInstance().addVisibleObject(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			onPerceive(pc);
		}

		master.addPet(this);
		PetTable.getInstance().storeNewPet(target, getId(), itemid);
		_pet = PetTable.getInstance().getTemplate(_itemObjId);
	}

	// 攻击ＨＰ减使用
	@Override
	public void receiveDamage(L1Character attacker, int damage) {
		if (getCurrentHp() > 0) {
			if (damage > 0) { // 回复场合攻击。
				setHate(attacker, 0); // 无
				removeSkillEffect(L1SkillId.FOG_OF_SLEEPING);
//				System.out.println("宠物攻击者:"+attacker.getName());
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
				death(attacker);
			} else {
				setCurrentHp(newHp);
			}
		} else if (!isDead()) { // 念
			death(attacker);
		}
	}

	public synchronized void death(L1Character lastAttacker) {
		if (!isDead()) {
			setDead(true);
			setCurrentHp(0);
			setStatus(ActionCodes.ACTION_Die);
			deathPenalty();
//			setExpPercent(getExpPercent() - 5);// 宠物死亡扣除经验
			_currentPetStatus = 3; // 宠物状态改为停留
			getMap().setPassable(getLocation(), true);
			broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_Die));
			if (getPinkSec()<=0) {
				if (lastAttacker instanceof L1PcInstance) {
					final L1PcInstance attackpc = (L1PcInstance)lastAttacker;
					boolean castle_ret = attackpc.castleWarResult(attackpc.getX(),attackpc.getY(),attackpc.getMapId()); // 攻城战
					if (castle_ret == true) { // 攻城战中旗内赤
						return;
					}
					if (getZoneType() != 0)  {
						
					}else {
						if (getLevel()>30) {
							attackpc.addLawful(-10000);
						}
					}							
				}
			}		
		}
	}

	public void evolvePet(int new_itemobjid) {

/*		L1Pet l1pet = PetTable.getInstance().getTemplate(_itemObjId);
		if (l1pet == null) {
			return;
		}
		_pet = l1pet;*/

		int newNpcId = _type.getNpcIdForEvolving();
		// 进化前maxHp,maxMp退避
		int tmpMaxHp = getMaxHp();
		int tmpMaxMp = getMaxMp();

		transform(newNpcId);
		_type = PetTypeTable.getInstance().get(newNpcId);

		//宠物进化特效 
		broadcastPacket(new S_SkillSound(getId(), 2127));
		//宠物进化特效  end
		setLevel(1);
		// HPMP元半分
		setMaxHp(tmpMaxHp / 2);
		setMaxMp(tmpMaxMp / 2);
		setCurrentHpDirect(getMaxHp());
		setCurrentMpDirect(getMaxMp());
		setExp(0);
		setExpPercent(0);

		// 空
		getInventory().clearItems();

		// 古DB消
		PetTable.getInstance().deletePet(_itemObjId);

		// 新DB书迂
/*		_pet.set_itemobjid(new_itemobjid);
		_pet.set_npcid(newNpcId);
		_pet.set_name(getName());
		_pet.set_level(getLevel());
		_pet.set_hp(getMaxHp());
		_pet.set_mp(getMaxMp());
		_pet.set_exp(getExp());*/
		PetTable.getInstance().storeNewPet(this, getId(), new_itemobjid);
		_pet = PetTable.getInstance().getTemplate(new_itemobjid);

		_itemObjId = new_itemobjid;
	}

	// 解放处理
	public void liberate() {
		L1MonsterInstance monster = new L1MonsterInstance(getNpcTemplate());
		monster.setId(IdFactory.getInstance().nextId());

		monster.setX(getX());
		monster.setY(getY());
		monster.setMap(getMapId());
		monster.setHeading(getHeading());
		monster.set_storeDroped(true);
		monster.setInventory(getInventory());
		setInventory(null);
		monster.setLevel(getLevel());
		monster.setMaxHp(getMaxHp());
		monster.setCurrentHpDirect(getCurrentHp());
		monster.setMaxMp(getMaxMp());
		monster.setCurrentMpDirect(getCurrentMp());

		_petMaster.getPetList().remove(getId());
		deleteMe();

		// DBPetTable削除、破弃
		_petMaster.getInventory().removeItem(_itemObjId, 1);
		PetTable.getInstance().deletePet(_itemObjId);

		L1World.getInstance().storeWorldObject(monster);
		L1World.getInstance().addVisibleObject(monster);
		for (L1PcInstance pc : L1World.getInstance()
				.getRecognizePlayer(monster)) {
			onPerceive(pc);
		}
	}

	// 持物收集
	public void collect() {
		L1Inventory targetInventory = _petMaster.getInventory();
		List<L1ItemInstance> items = _inventory.getItems();
		int size = _inventory.getSize();
		for (int i = 0; i < size; i++) {
			L1ItemInstance item = items.get(0);
			//修正宠物洗血洗魔问题
            L1PetItem petitem = PetItemTable.getInstance().getTemplate(item.getItemId());
            if (petitem!=null) {
            	if (item.isEquipped()) {
            		if (petitem.getUseType() == 2) {
						removePetArmor(this, item);
					}
            		if (petitem.getUseType() == 22) {
						removePetWeapon(this, item);
					}
				}   	
			}       
            //~修正宠物洗血洗魔问题
			if (_petMaster.getInventory().checkAddItem( // 容量重量确认及送信
					item, item.getCount()) == L1Inventory.OK) {
				_inventory.tradeItem(item, item.getCount(), targetInventory);
				_petMaster.sendPackets(new S_ServerMessage(143, getName(), item
						.getLogName())); // \f1%0%1。
			} else { // 持足元落
				targetInventory = L1World.getInstance().getInventory(getX(),
						getY(), getMapId());
				_inventory.tradeItem(item, item.getCount(), targetInventory);
			}
		}
	}
	
	/**
	 * 背包內物品的掉落
	 */
	public void dropItem(final L1PcInstance pc) {
		final L1Inventory worldInv = L1World.getInstance().getInventory(this.getX(),
				this.getY(), this.getMapId());
		// 取回背包物件
		final List<L1ItemInstance> items = this._inventory.getItems();

		for (final L1ItemInstance item : items) {
			if (item.isEquipped()) { // 使用中
				final int itemId = item.getItem().getItemId();
				final L1PetItem petItem = PetItemTable.getInstance()
						.getTemplate(itemId);
				// 解除使用狀態
				if (petItem != null) {
					this.setHitByWeapon(0);
					this.setDamageByWeapon(0);
					this.addStr(-petItem.getAddStr());
					this.addCon(-petItem.getAddCon());
					this.addDex(-petItem.getAddDex());
					this.addInt(-petItem.getAddInt());
					this.addWis(-petItem.getAddWis());
					this.addMaxHp(-petItem.getAddHp());
					this.addMaxMp(-petItem.getAddMp());
					this.addSp(-petItem.getAddSp());
					this.addMr(-petItem.getAddMr());

					this.setWeapon(null);
					this.setArmor(null);
					item.setEquipped(false);
				}
			}
			if (pc.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
				this._inventory.tradeItem(item, item.getCount(),
						pc.getInventory());
			} else {
				this._inventory.tradeItem(item, item.getCount(), worldInv);
			}
			// item.setEquipped(false);
		}
		// 存檔
	}

	// 时DROP地面落
	public void dropItem() {
		L1Inventory targetInventory = L1World.getInstance().getInventory(
				getX(), getY(), getMapId());
		List<L1ItemInstance> items = _inventory.getItems();
		int size = _inventory.getSize();
		for (int i = 0; i < size; i++) {
			L1ItemInstance item = items.get(0);
			//修正宠物洗血洗魔问题
            L1PetItem petitem = PetItemTable.getInstance().getTemplate(item.getItemId());
            if (petitem!=null) {
            	if (item.isEquipped()) {
            		if (petitem.getUseType() == 2) {
						removePetArmor(this, item);
					}
            		if (petitem.getUseType() == 22) {
						removePetWeapon(this, item);
					}
				}   	
			}       
            //~修正宠物洗血洗魔问题
			_inventory.tradeItem(item, item.getCount(), targetInventory);
		}
	}

	// 笛使
	public void call() {
		int id = _type.getMessageId(L1PetType.getMessageNumber(getLevel()));
		if (id != 0) {
			broadcastPacket(new S_NpcChatPacket(this, "$" + id, 0));
		}

		setCurrentPetStatus(7); // 主人近休憩状态
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
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.addKnownObject(this);
		perceivedFrom.sendPackets(new S_PetPack(this, perceivedFrom)); // 系认识
		if (getPinkSec()>0) {
			perceivedFrom.sendPackets(new S_PinkName(getId(), getPinkSec()));
		}
/*		if (isDead()) {
			perceivedFrom.sendPackets(new S_DoActionGFX(getId(),
					ActionCodes.ACTION_Die));
		}*/
	}

	@Override
	public void onAction(L1PcInstance player) {
		L1Character cha = this.getMaster();
		L1PcInstance master = (L1PcInstance) cha;
		if (master.isTeleport()) { // 处理中
			return;
		}
		if (getZoneType() == 1) { // 攻击侧
			L1Attack attack_mortion = new L1Attack(player, this); // 攻击送信
			attack_mortion.action();
			return;
		}

		if (player.checkNonPvP(player, this)) {
			return;
		}

		L1Attack attack = new L1Attack(player, this);
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
		if (_petMaster.equals(player)) {
			player.sendPackets(new S_PetMenuPacket(this, getExpPercent()));
/*			player.sendPackets(new S_PetInventory(this));*/
			List<L1ItemInstance> items = _inventory.getItems();
			int size = _inventory.getSize();
			for (int i = 0; i < size; i++) {
				if (getWeapon()!=null) {
					if (getWeapon().getId() == items.get(i).getId()) {
						player.sendPackets(new S_PetAttr(0, this, i));
					}
				}
				if (getArmor()!=null) {
					if (getArmor().getId() == items.get(i).getId()) {
						player.sendPackets(new S_PetAttr(1, this, i));
					}
				}
				
			}
			
//			L1Pet l1pet = PetTable.getInstance().getTemplate(_itemObjId);
			// XXX 话DB书必要
/*			if (_pet != null) {
				_pet.set_exp(getExp());
				_pet.set_level(getLevel());
				_pet.set_hp(getMaxHp());
				_pet.set_mp(getMaxMp());
//				PetTable.getInstance().UpdatePet(l1pet); // DB书迂
			}*/
		}
	}
	
	public void updatePet(){
		if (_pet != null) {
			_pet.set_exp(getExp());
			_pet.set_level(getLevel());
			PetTable.getInstance().UpdatePet(_pet); 
		}	
	}

	@Override
	public void onFinalAction(L1PcInstance player, String action) {
		int status = actionType(action);
		if (status == 0) {
			return;
		}
		if (status == 6) {
			liberate(); // 解放
		} else {
			// 同主人状态更新
			Object[] petList = _petMaster.getPetList().values().toArray();
			for (Object petObject : petList) {
				if (petObject instanceof L1PetInstance) { // 
					L1PetInstance pet = (L1PetInstance) petObject;
					pet.setCurrentPetStatus(status);
				}
			}
		}
	}

	@Override
	public void onItemUse() {
		/*删除if (!isActived()) {
			useItem(USEITEM_HASTE, 100); // １００％确率使用
		}删除*/
		//喝绿判断变更 
		if (!hasSkillEffect(1001) && !hasSkillEffect(43)) {//补上加速术判断 
			for (L1ItemInstance item : getInventory().getItems()) {
				if (Arrays
						.binarySearch(haestPotions, item.getItem().getItemId()) >= 0) {
					useItem(item,USEITEM_HASTE, 100);
				}
			}// １００％确率使用
		}
		//喝绿判断变更  end
		if (getCurrentHp() * 100 / getMaxHp() < 40) { // ＨＰ４０％
			for (L1ItemInstance item : getInventory().getItems()) {
				if (Arrays.binarySearch(healPotions, item.getItem().getItemId()) >= 0) {
					useItem(item,USEITEM_HEAL, 100);
				}
			} // １００％确率回复使用
		}
		//宠物活力、魔力药水 
		if (!hasSkillEffect(l1j.william.New_Id.Skill_AJ_1_6)) // ＨＰ４０％
		{
			useItem(null,USEITEM_PETHPPOTIONS, 100); // １００％确率回复使用
		}
		if (!hasSkillEffect(l1j.william.New_Id.Skill_AJ_1_7)) // ＨＰ４０％
		{
			useItem(null,USEITEM_PETMPPOTIONS, 100); // １００％确率回复使用
		}
		//宠物活力、魔力药水  end
	}

	@Override
	public void onGetItem(L1ItemInstance item) {
		if (getNpcTemplate().get_digestitem() > 0) {
			setDigestItem(item);
		}
		Arrays.sort(healPotions);
		Arrays.sort(haestPotions);
		//宠物活力、魔力药水 
		Arrays.sort(petHpPotions);
		Arrays.sort(petMpPotions);
		//宠物活力、魔力药水  end
		if (Arrays.binarySearch(healPotions, item.getItem().getItemId()) >= 0) {
			if (getCurrentHp() != getMaxHp()) {
				useItem(item,USEITEM_HEAL, 100);
			}
		} else if (Arrays
				.binarySearch(haestPotions, item.getItem().getItemId()) >= 0) {
			useItem(item,USEITEM_HASTE, 100);
		}
		//宠物活力、魔力药水 
		else if (Arrays
				.binarySearch(petHpPotions, item.getItem().getItemId()) >= 0) {
			useItem(item,USEITEM_PETHPPOTIONS, 100);
		} else if (Arrays
				.binarySearch(petMpPotions, item.getItem().getItemId()) >= 0) {
			useItem(item,USEITEM_PETMPPOTIONS, 100);
		}
		//宠物活力、魔力药水  end
	}

	private int actionType(String action) {
		if (isDead()) {
			return 0;
		}
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
		} else if (action.equalsIgnoreCase("getitem")) { // 收集
			collect();
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

		if (_petMaster != null) {
			int HpRatio = 100 * currentHp / getMaxHp();
			L1PcInstance Master = _petMaster;
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

	public synchronized void setCurrentPetStatus(int i) {
		if (isDead()) {
			return;
		}
		_currentPetStatus = i;
		if (_currentPetStatus == 5) {
			setHomeX(getX());
			setHomeY(getY());
		}
		if (_currentPetStatus == 7) {
			allTargetClear();
		}

		if (_currentPetStatus == 3) {
			allTargetClear();
		} else {
			if (!isAiRunning()) {
				startAI();
			}
		}
	}

	public int getCurrentPetStatus() {
		return _currentPetStatus;
	}

	public int getItemObjId() {
		return _itemObjId;
	}

	public void setExpPercent(int expPercent) {
		_expPercent = expPercent;
	}

	public int getExpPercent() {
		return _expPercent;
	}
	
	private L1ItemInstance _weapon;

	public void setWeapon(L1ItemInstance weapon) {
		_weapon = weapon;
	}

	public L1ItemInstance getWeapon() {
		return _weapon;
	}

	private L1ItemInstance _armor;

	public void setArmor(L1ItemInstance armor) {
		_armor = armor;
	}

	public L1ItemInstance getArmor() {
		return _armor;
	}
	
	private int _hitByWeapon;

	public void setHitByWeapon(int i) {
		_hitByWeapon = i;
	}

	public int getHitByWeapon() {
		return _hitByWeapon;
	}

	private int _damageByWeapon;

	public void setDamageByWeapon(int i) {
		_damageByWeapon = i;
	}

	public int getDamageByWeapon() {
		return _damageByWeapon;
	}
	
	public L1Pet getL1Pet(){
		return _pet;
	}
	
	public void deathPenalty() {
		int oldLevel = getLevel();
		int needExp = ExpTable.getNeedExpNextLevel(oldLevel);
		int exp = 0;
		if (oldLevel < 6) {
			exp = 0;
		} else {
			exp = (int) (needExp * 0.1);
		}
		if (exp == 0) {
			return;
		}
		setExp(getExp()-exp);
		onChance();
	}
	
	private void onChance(){
		int oldlevel = getLevel(); 
		setLevel(ExpTable.getLevelByExp(getExp()));
		int newlevel = getLevel();
		int gap = newlevel - oldlevel;
		if (gap > 0) {
			for (int i = 1; i <= gap; i++) {
				IntRange hpUpRange = getPetType().getHpUpRange();
				IntRange mpUpRange = getPetType().getMpUpRange();
				int randomhp = hpUpRange.randomValue();
				int randommp = mpUpRange.randomValue();
				getL1Pet().set_hp(getL1Pet().get_hp() + randomhp);
				getL1Pet().set_mp(getL1Pet().get_mp() + randommp);
				addMaxHp(randomhp);
				addMaxMp(randommp);
			}
		}else {
			for (int i = 0; i > gap; i--) {
				IntRange hpUpRange = getPetType().getHpUpRange();
				IntRange mpUpRange = getPetType().getMpUpRange();
				int randomhp = hpUpRange.randomValue();
				int randommp = mpUpRange.randomValue();
				getL1Pet().set_hp(getL1Pet().get_hp() - randomhp);
				getL1Pet().set_mp(getL1Pet().get_mp() - randommp);
				addMaxHp(-randomhp);
				addMaxMp(-randommp);
			}
		}
		updatePet();
	}
	
	// 使用寵物裝備
	public void usePetWeapon(L1ItemInstance weapon) {
		if (getWeapon() == null) {
			setPetWeapon(this, weapon);
		}
		else { // 既に何かを装備している場合、前の装備をはずす
			if (getWeapon().equals(weapon)) {
				removePetWeapon(this, getWeapon());
			}
			else {
				removePetWeapon(this, getWeapon());
				setPetWeapon(this, weapon);
			}
		}
	}

	public void usePetArmor(L1ItemInstance armor) {
		if (getArmor() == null) {
			setPetArmor(this, armor);
		}
		else { // 既に何かを装備している場合、前の装備をはずす
			if (getArmor().equals(armor)) {
				removePetArmor(this, getArmor());
			}
			else {
				removePetArmor(this, getArmor());
				setPetArmor(this, armor);
			}
		}
	}

	private void setPetWeapon(L1PetInstance pet, L1ItemInstance weapon) {
		int itemId = weapon.getItem().getItemId();
		L1PetItem petItem = PetItemTable.getInstance().getTemplate(itemId);
		if (petItem == null) {
			return;
		}

		pet.setHitByWeapon(petItem.getHitModifier());
		pet.setDamageByWeapon(petItem.getDamageModifier());
		pet.addStr(petItem.getAddStr());
		pet.addCon(petItem.getAddCon());
		pet.addDex(petItem.getAddDex());
		pet.addInt(petItem.getAddInt());
		pet.addWis(petItem.getAddWis());
		pet.addMaxHp(petItem.getAddHp());
		pet.addMaxMp(petItem.getAddMp());
		pet.addSp(petItem.getAddSp());
		pet.addMr(petItem.getAddMr());

		pet.setWeapon(weapon);
		weapon.setEquipped(true);
	}

	private void removePetWeapon(L1PetInstance pet, L1ItemInstance weapon) {
		int itemId = weapon.getItem().getItemId();
		L1PetItem petItem = PetItemTable.getInstance().getTemplate(itemId);
		if (petItem == null) {
			return;
		}

		pet.setHitByWeapon(0);
		pet.setDamageByWeapon(0);
		pet.addStr(-petItem.getAddStr());
		pet.addCon(-petItem.getAddCon());
		pet.addDex(-petItem.getAddDex());
		pet.addInt(-petItem.getAddInt());
		pet.addWis(-petItem.getAddWis());
		pet.addMaxHp(-petItem.getAddHp());
		pet.addMaxMp(-petItem.getAddMp());
		pet.addSp(-petItem.getAddSp());
		pet.addMr(-petItem.getAddMr());

		pet.setWeapon(null);
		weapon.setEquipped(false);
	}

	private void setPetArmor(L1PetInstance pet, L1ItemInstance armor) {
		int itemId = armor.getItem().getItemId();
		L1PetItem petItem = PetItemTable.getInstance().getTemplate(itemId);
		if (petItem == null) {
			return;
		}

		pet.addAc(petItem.getAddAc());
		pet.addStr(petItem.getAddStr());
		pet.addCon(petItem.getAddCon());
		pet.addDex(petItem.getAddDex());
		pet.addInt(petItem.getAddInt());
		pet.addWis(petItem.getAddWis());
		pet.addMaxHp(petItem.getAddHp());
		pet.addMaxMp(petItem.getAddMp());
		pet.addSp(petItem.getAddSp());
		pet.addMr(petItem.getAddMr());

		pet.setArmor(armor);
		armor.setEquipped(true);
	}

	private void removePetArmor(L1PetInstance pet, L1ItemInstance armor) {
		int itemId = armor.getItem().getItemId();
		L1PetItem petItem = PetItemTable.getInstance().getTemplate(itemId);
		if (petItem == null) {
			return;
		}

		pet.addAc(-petItem.getAddAc());
		pet.addStr(-petItem.getAddStr());
		pet.addCon(-petItem.getAddCon());
		pet.addDex(-petItem.getAddDex());
		pet.addInt(-petItem.getAddInt());
		pet.addWis(-petItem.getAddWis());
		pet.addMaxHp(-petItem.getAddHp());
		pet.addMaxMp(-petItem.getAddMp());
		pet.addSp(-petItem.getAddSp());
		pet.addMr(-petItem.getAddMr());

		pet.setArmor(null);
		armor.setEquipped(false);
	}
	
/*	public synchronized void addExp(int exp) {
		_exp += exp;
		if (_exp > ExpTable.MAX_EXP) {
			_exp = ExpTable.MAX_EXP;
		}
//		onChangeExp();
	}*/


	private int _currentPetStatus;
	private L1PcInstance _petMaster;
	private int _itemObjId;
	private L1PetType _type;
	private int _expPercent;
	private L1Pet _pet;

	public L1PetType getPetType() {
		return _type;
	}
}
