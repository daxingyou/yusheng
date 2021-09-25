package l1j.server.server.model.Instance;

import java.util.ArrayList;
import java.util.Random;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.lock.ShouShaReading;
import l1j.server.server.datatables.lock.SpawnBossReading;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1GroundInventory;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.*;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1ShouShaTemp;
import l1j.server.server.types.Point;
import l1j.server.server.utils.CalcExp;
import l1j.server.server.world.L1World;
import l1j.william.MobTalk;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1MonsterInstance extends L1NpcInstance {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Log _log = LogFactory.getLog(L1MonsterInstance.class);


	private boolean _storeDroped; // 读迂完了

	// 使用处理
	@Override
	public void onItemUse() {
		if (!isActived() && _target != null) {
			useItem(null,USEITEM_HASTE, 40); // ４０％确率使用

			// 处理
			if (getNpcTemplate().is_doppel() && _target instanceof L1PcInstance) {
				L1PcInstance targetPc =(L1PcInstance) _target;
				setName(_target.getName());
				setNameId(_target.getName());
				setTitle(_target.getTitle());
				setTempLawful(_target.getLawful());
				setStatus(4);//设定变怪只能拿刀 
				//删除setTempCharGfx(targetPc.getClassId());
				//删除setGfxId(targetPc.getClassId());
				//变怪可直接攻击 
				switch(targetPc.getClassId()) {
					case 0: {
						setTempCharGfx(5853);
						setGfxId(5853);
					}
					break;
					case 1: {
						setTempCharGfx(5854);
						setGfxId(5854);
					}
					break;
					case 61: {
						setTempCharGfx(5855);
						setGfxId(5855);
					}
					break;
					case 48: {
						setTempCharGfx(5856);
						setGfxId(5856);
					}
					break;
					case 138: {
						setTempCharGfx(5857);
						setGfxId(5857);
					}
					break;
					case 37: {
						setTempCharGfx(5858);
						setGfxId(5858);
					}
					break;
					case 734: {
						setTempCharGfx(5859);
						setGfxId(5859);
					}
					break;
					case 1186: {
						setTempCharGfx(5860);
						setGfxId(5860);
					}
					break;
					case 2786: {
						setTempCharGfx(5861);
						setGfxId(5861);
					}
					break;
					case 2796: {
						setTempCharGfx(5862);
						setGfxId(5862);
					}
					break;
				}
				//变怪可直接攻击  end
				setPassispeed(640);
				setAtkspeed(960); // 正确值、变更成960 
				for (L1PcInstance pc : L1World.getInstance()
						.getRecognizePlayer(this)) {
					pc.sendPackets(new S_RemoveObject(this));
					pc.removeKnownObject(this);
					pc.updateObject();
				}
			}
		}
		if (getCurrentHp() * 100 / getMaxHp() < 40) { // ＨＰ４０％
			useItem(null,USEITEM_HEAL, 50); // ５０％确率回复使用
		}
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.addKnownObject(this);
		if (0 < getCurrentHp()) {
			if (getHiddenStatus() == HIDDEN_STATUS_SINK) {
				perceivedFrom.sendPackets(new S_DoActionGFX(getId(),
						ActionCodes.ACTION_Hide));
			} else if (getHiddenStatus() == HIDDEN_STATUS_FLY) {
				perceivedFrom.sendPackets(new S_DoActionGFX(getId(),
						ActionCodes.ACTION_Moveup));
			}
			
			perceivedFrom.sendPackets(new S_NPCPack(this));
			onNpcAI(); // ＡＩ开始
			if (getBraveSpeed() == 1) { // 方法
				perceivedFrom.sendPackets(new S_SkillBrave(getId(), 1, 600000));
			}
		}
	}

	// 探
	public static int[][] _classGfxId = { { 0, 1 }, { 48, 61 }, { 37, 138 },
			{ 734, 1186 }, { 2786, 2796 } };

	@Override
	public void searchTarget() {
		if (!getNpcTemplate().is_agro() && !getNpcTemplate().is_agrososc()
				&& getNpcTemplate().is_agrogfxid1() < 0
				&& getNpcTemplate().is_agrogfxid2() < 0) {
			return; // 完全
		}

		// 搜索
		L1PcInstance targetPlayer = null;

		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(this)) {
			//幽灵坟场特殊功能 
			if (pc.getCurrentHp() > 0 && !pc.isDead() && 
				(getNpcTemplate().get_npcId() == 100001 || 
				 getNpcTemplate().get_npcId() == 100002 || 
				 getNpcTemplate().get_npcId() == 100003)) {
				targetPlayer = pc;
				break;
			}
			//幽灵坟场特殊功能  end
				
			if (pc.getCurrentHp() <= 0 || pc.isDead() || pc.isGm()
					|| pc.isMonitor() || pc.isGhost()) {
				continue;
			}

			// 修正黑暗之影效果 
			double rnd = 2;
			if (((hasSkillEffect(20) == true) 
			 || (hasSkillEffect(40) == true) 
			 || (hasSkillEffect(103) == true)) 
			 && getLocation().getLineDistance(new Point(pc.getX(), pc.getY())) >= rnd) {
				continue;
			}
			// 修正黑暗之影效果  end

			// 条件满场合、友好见先制攻击。
			// 值（侧）PC1以上（友好）
			// 值（侧）PC-1以下（友好）
			if ((getNpcTemplate().getKarma() < 0 && pc.getKarmaLevel() >= 1)
					|| (getNpcTemplate().getKarma() > 0 && pc.getKarmaLevel() <= -1)) {
				continue;
			}

			if (!pc.isInvisble() || getNpcTemplate().is_agrocoi()) { // 
				if (pc.hasSkillEffect(67)) { // 变身
					if (getNpcTemplate().is_agrososc()) { // 变身对
						targetPlayer = pc;
						break;
					}
				} else if (getNpcTemplate().is_agro()) { // 
					targetPlayer = pc;
					break;
				}
				if (getNpcTemplate().getTransformId() == 32) {
					if (pc.getLawful()<0) {
						targetPlayer = pc;
						break;
					}
				}

				// 特定orＩＤ
				if (getNpcTemplate().is_agrogfxid1() >= 0
						&& getNpcTemplate().is_agrogfxid1() <= 4) { // 指定
					if (_classGfxId[getNpcTemplate().is_agrogfxid1()][0] == pc
							.getTempCharGfx()
							|| _classGfxId[getNpcTemplate().is_agrogfxid1()][1] == pc
									.getTempCharGfx()) {
						targetPlayer = pc;
						break;
					}
				} else if (pc.getTempCharGfx() == getNpcTemplate()
						.is_agrogfxid1()) { // ＩＤ指定
					targetPlayer = pc;
					break;
				}

				if (getNpcTemplate().is_agrogfxid2() >= 0
						&& getNpcTemplate().is_agrogfxid2() <= 4) { // 指定
					if (_classGfxId[getNpcTemplate().is_agrogfxid2()][0] == pc
							.getTempCharGfx()
							|| _classGfxId[getNpcTemplate().is_agrogfxid2()][1] == pc
									.getTempCharGfx()) {
						targetPlayer = pc;
						break;
					}
				} else if (pc.getTempCharGfx() == getNpcTemplate()
						.is_agrogfxid2()) { // ＩＤ指定
					targetPlayer = pc;
					break;
				}
			}
		}
		if (targetPlayer != null) {
			_hateList.add(targetPlayer, 0);
			_target = targetPlayer;
			// 怪物发现目标说话 
			MobTalk.forL1MonsterTalking(this, 1);
			// 怪物发现目标说话  end
			if (targetPlayer.getX()>33702&&targetPlayer.getX()<33746
					&&targetPlayer.getY()>32472&&targetPlayer.getY()<32512) {
				L1Location location = new L1Location();
				location.set(getLocation());
				location = location.randomLocation(200, true);
				L1Teleport.teleport(this, location.getX(), location.getY(), getMapId(), getHeading());
//				return;
			}
		}
	}

	// 设定
	@Override
	public void setLink(L1Character cha) {
		if (cha != null && _hateList.isEmpty()) { // 场合追加
			_hateList.add(cha, 0);
			checkTarget();
		}
	}

	public L1MonsterInstance(L1Npc template) {
		super(template);
		_storeDroped = false;
	}

	@Override
	public synchronized void onNpcAI() {
		if (isAiRunning()) {
			return;
		}
		if (!_storeDroped) // 无驮ＩＤ发行
		{
			DropTable.getInstance().setDrop(this, getInventory());
			getInventory().shuffle();
			_storeDroped = true;
		}
		setActived(false);
		startAI();
	}

	@Override
	public void onAction(L1PcInstance pc) {
		if (getCurrentHp() > 0 && !isDead()) {
			if (ATTACK != null) {
				ATTACK.attack(pc, this);
			}
			L1Attack attack = new L1Attack(pc, this);
			if (Config.GUAJI_AI){
				if (!pc.getGuaJiAI()){
					attack.action();//如果没有验证砍怪无伤害 只发送攻击动作
					pc.sendPackets(new S_SystemMessage("**请攻击旁边的验证NPC 否则3分钟后掉线处理**"));
					return;
				}
			}
			if (getNpcTemplate().isCheckAI()){
				attack.action();
				if (Config.AttakMobAIDeath){
					pc.setLawful(-32767);//红名
					pc.death(this);
				}
				pc.sendPackets(new S_ChatPacket(this, "检测挂机 请勿打我",Opcodes.S_OPCODE_NORMALCHAT, 0));
				return;
			}
			if (attack.calcHit()) {
				//System.out.println("步骤1");
				attack.calcDamage();
				//System.out.println("步骤2");
				attack.calcStaffOfMana();
				//System.out.println("步骤3");
				attack.addPcPoisonAttack(pc, this);
				//System.out.println("步骤4");
			}
			//System.out.println("步骤5");
			attack.action();
			//System.out.println("步骤6");
			attack.commit();
			//System.out.println("步骤7");
		}
	}

	@Override
	public void ReceiveManaDamage(L1Character attacker, int mpDamage) { // 攻击ＭＰ减使用
		if (mpDamage > 0 && !isDead()) {
			// int Hate = mpDamage / 10 + 10; // 注意！计算适当 １０分１＋１０
			// setHate(attacker, Hate);
			setHate(attacker, mpDamage);

			onNpcAI();

			if (attacker instanceof L1PcInstance) { // 仲间意识设定
				serchLink((L1PcInstance) attacker, getNpcTemplate()
						.get_family());
			}

			int newMp = getCurrentMp() - mpDamage;
			if (newMp < 0) {
				newMp = 0;
			}
			setCurrentMp(newMp);
		}
	}

	@Override
	public void receiveDamage(L1Character attacker, int damage) { // 攻击ＨＰ减使用
		if (getNpcTemplate().isCheckAI()){
			return;
		}
		if (getCurrentHp() > 0 && !isDead()) {
			if (getHiddenStatus() != HIDDEN_STATUS_NONE) {
				return;
			}
			if (damage >= 0) {
				// int Hate = damage / 10 + 10; // 注意！计算适当 １０分１＋１０
				// setHate(attacker, Hate);
				setHate(attacker, damage);
			}
			if (damage > 0) {
				removeSkillEffect(L1SkillId.FOG_OF_SLEEPING);
			}

			onNpcAI();

			if (attacker instanceof L1PcInstance) { // 仲间意识设定
				serchLink((L1PcInstance) attacker, getNpcTemplate()
						.get_family());
			}
			
			attacker.setAttack(true);
			attacker.setAttacksec(10);

			if (attacker instanceof L1PcInstance && damage > 0) {
				L1PcInstance player = (L1PcInstance) attacker;
				player.setPetTarget(this);
				if (getSpawn()!=null) {
					if (SpawnBossReading.get().getTemplate(getSpawn().getId())!=null) {
						recall(player);
					}
				}else {
					if (SpawnBossReading.get().isBoss(getNpcId())) {
						recall(player);
					}
				}
/*				if (player.isVdmg()) {
					String msg = "输出->"+damage;
					S_ChatPacket s_chatpacket = new S_ChatPacket(this, msg,
							Opcodes.S_OPCODE_NORMALCHAT);
					player.sendPackets(s_chatpacket);
				}*/
				if (getNpcId()>110016&&getNpcId()<=110030) {
					if (!player.getPetList().isEmpty()) {
						Object[] petList = player.getPetList().values().toArray();
						for (Object petObject : petList) {
							if (petObject instanceof L1PetInstance) { // 
								L1PetInstance pet = (L1PetInstance) petObject;
								pet.collect();
								pet.updatePet();
								player.getPetList().remove(pet.getId());
								pet.setDead(true);
								pet.deleteMe();
							}
							if (petObject instanceof L1SummonInstance) {
								L1SummonInstance sum = (L1SummonInstance) petObject;
								sum.setSkillEffect(L1SkillId.RETURN_TO_NATURE, 2000);
							}
						}
					}
				}
				/*if (getNpcTemplate().get_npcId() == 45681 // 
						|| getNpcTemplate().get_npcId() == 45682 // 
						|| getNpcTemplate().get_npcId() == 45683 // 
						|| getNpcTemplate().get_npcId() == 45684 // 
						|| getNpcTemplate().get_npcId() == 45617 // 不死鸟 
						|| getNpcTemplate().get_npcId() == 45610 // 古代巨人 
					)
				{
					recall(player);
				}*/
			}
			int newHp = getCurrentHp() - damage;
			if (newHp <= 0 && !isDead()) {
				int transformId = getNpcTemplate().getTransformId();
				// 变身
				if (transformId == -1) {
					death(attacker);
					/*setCurrentHpDirect(0);
					setDead(true);
					Death death = new Death(attacker);
					GeneralThreadPool.getInstance().execute(death);*/
					// Death(attacker);
				} // 变身
				else {					
					transform(transformId);
				}
			}
			if (newHp > 0) {
				setCurrentHp(newHp);
				if (Config.HPBAR) { // 如果开启显示血条
					L1PcInstance atkpc = whoAttacker(attacker);
					if (atkpc != null) {
						atkpc.sendPackets(new S_HPMeter(this));
					}
				}
				hide();
			}
		} else if (!isDead()) { // 念
			/*setDead(true);
			_log.warning("警告∶ＮＰＣ的ＨＰ减少处理有不正确的地方。※或者从最初开始ＨＰ０");
			Death death = new Death(attacker);
			GeneralThreadPool.getInstance().execute(death);*/
			death(attacker);
			// Death(attacker);
		}
	}


	/**
	 * 判断主要攻击者
	 *
	 * @param  attacker
	 */
	private L1PcInstance  whoAttacker(L1Character attacker){
			// 判断主要攻击者
			L1PcInstance atkpc = null;
			if (attacker instanceof L1PcInstance) { // 攻击者是玩家
				atkpc = (L1PcInstance) attacker;
			} else if (attacker instanceof L1PetInstance) {// 攻击者是宠物传回主人
				atkpc = (L1PcInstance) ((L1PetInstance) attacker)
						.getMaster();
			} else if (attacker instanceof L1SummonInstance) {// 攻击者是召唤兽传回主人
				atkpc = (L1PcInstance) ((L1SummonInstance) attacker)
						.getMaster();
			}
		return atkpc;
	}
	/**
	 * 距离5以上离pc距离3～4位置引寄。
	 * 
	 * @param pc
	 */
	private void recall(L1PcInstance pc) {

		if (getLocation().getTileLineDistance(pc.getLocation()) > 4||!pc.getMap().isPassable2(pc.getLocation())) {
			for (int count = 0; count < 10; count++) {
				L1Location newLoc = getLocation().randomLocation(3, 4, false);
				if (glanceCheck(newLoc.getX(), newLoc.getY())) {
					L1Teleport.teleport(pc, newLoc.getX(), newLoc.getY(),
							getMapId(), 5, true);
					break;
				}
			}
		}
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
	/**
	 * 死亡的处理
	 * @param lastAttacker 攻击致死的攻击者
	 */
	public void death(final L1Character lastAttacker) {
		synchronized (this) {
			if (this.isDead()) {
				return;
			}
			this.setDead(true);
		}
		if (Config.HPBAR) { // 如果开启显示血条
			L1PcInstance atkpc = whoAttacker(lastAttacker);
			if (atkpc != null) {
				atkpc.sendPackets(new S_HPMeter(this.getId(),0xff));//关闭怪物血条
			}
		}
		final Death death = new Death(lastAttacker);
		GeneralThreadPool.getInstance().execute(death);
	}

	class Death implements Runnable {
		// 怪死说话 
		//L1Object object = L1World.getInstance().findObject(getId());
       //	L1MonsterInstance npc = (L1MonsterInstance) object;
       	// 怪死说话  end

		public Death(L1Character lastAttacker) {	
			_lastAttacker = lastAttacker;
		}

		@Override
		public void run() {
			setDeathProcessing(true);
			setCurrentHpDirect(0);
			setDead(true);
			//int targetobjid = getId();

			getMap().setPassable(getLocation(), true);
			setStatus(ActionCodes.ACTION_Die);
			broadcastPacket(new S_DoActionGFX(getId(),ActionCodes.ACTION_Die));
			
			L1PcInstance player = null;
			if (_lastAttacker instanceof L1PcInstance) {
				player = (L1PcInstance) _lastAttacker;
			} else if (_lastAttacker instanceof L1PetInstance) {
				player = (L1PcInstance) ((L1PetInstance) _lastAttacker)
						.getMaster();
			} else if (_lastAttacker instanceof L1SummonInstance) {
				player = (L1PcInstance) ((L1SummonInstance) _lastAttacker)
						.getMaster();
			}
			if (player != null) {
				final L1ShouShaTemp shoushaTmp = ShouShaReading.get().getTemp(L1MonsterInstance.this.getNpcId());
				if (shoushaTmp != null){
					if (shoushaTmp.getObjId() == 0){
						if (ShouShaReading.get().update(shoushaTmp, player.getId(), player.getName())){
							final L1Item item = ItemTable.getInstance().getTemplate(shoushaTmp.getItemId());
							if (item != null){
								player.getInventory().storeItem(shoushaTmp.getItemId(), shoushaTmp.getCount());
								final StringBuilder msg = new StringBuilder();
								msg.append("\\f2恭喜玩家【\\f=");
								msg.append(player.getName());
								msg.append("\\f2】第一个杀死了[\\f3");
								msg.append(L1MonsterInstance.this.getName());
								msg.append("\\f2]获得了首杀奖励:\\f4");
								msg.append(item.getName());
								if (shoushaTmp.getCount() > 1){
									msg.append("(");
									msg.append(shoushaTmp.getCount());
									msg.append(")");
								}
								L1World.getInstance().broadcastPacketToAll(new S_BlueMessage(166,msg.toString()));
							}
						}
					}
				}
				ArrayList<L1Character> targetList = _hateList
						.toTargetArrayList();
				ArrayList<Integer> hateList = _hateList.toHateArrayList();
				long exp = getExp();
				// 怪物死亡说话 
				MobTalk.forL1MonsterTalking(L1MonsterInstance.this, 3);
				// 怪物死亡说话  end
				// 武器升级功能 
       			l1j.william.misc.addWeaponExp(player ,  exp);
       			// 武器升级功能  end
				CalcExp.calcExp(player, L1MonsterInstance.this, targetList, hateList, exp);
				player.addKillMonCount(1);
				try {
					if (L1MonsterInstance.this.getNpcId() == 888888){
                		if (L1MonsterInstance.this._dropWorldList.size() > 0){
                			final long Nowtime = System.currentTimeMillis();
                			for(final L1ItemInstance item : L1MonsterInstance.this._dropWorldList){
                				final L1Location loc = L1MonsterInstance.this.getLocation().randomLocation(8, false);
                				item.setDropTimestamp(Nowtime);
            					final L1GroundInventory ground = L1World.getInstance().getInventory(
            							loc.getX(), loc.getY(), L1MonsterInstance.this.getMapId());
            					if (ground.checkAddItem(item, item.getCount()) == L1Inventory.OK) {
            						ground.storeItem(item);
            					}
                			}
                			L1MonsterInstance.this._dropWorldList.clear();
                		}
                	}else{
                		DropTable.getInstance().dropShare(L1MonsterInstance.this, targetList, hateList);
                	}
				} catch (Exception e) {
					_log.error(e.getLocalizedMessage(), e);
				}
				int karma = getKarma();
				if (karma != 0) {
					int karmaSign = Integer.signum(karma);
					int playerKarmaLevel = player.getKarmaLevel();
					int playerKarmaLevelSign = Integer.signum(playerKarmaLevel);
					// 背信行为5倍
					if (playerKarmaLevelSign != 0
							&& karmaSign != playerKarmaLevelSign) {
						karma *= 5;
					}
					// 止刺设定。or倒场合入。
					player.addKarma((int) (karma * Config.RATE_KARMA));
				}
				if (Config.GUAJI_AI){
					final long nowtime = System.currentTimeMillis();
					if (player.getGuaJiAITime() <= 0){
						player.setGuaJiAITime(nowtime);
					}else if (nowtime - player.getGuaJiAITime() >= Config.GUAJI_AI_TIME * 60 *1000){
						final L1Npc template = NpcTable.getInstance().getTemplate(Config.GUAJI_AI_NPCID);
						if (template != null){
							new L1GuaJiAIInstance(template,player);
						}
					}
				}
				//WriteLogTxt.Recording("调试专用", "怪物"+L1MonsterInstance.this.getName()+"被"+player.getName()+"杀死了！");
			}else {
				//WriteLogTxt.Recording("调试专用", "怪物"+L1MonsterInstance.this.getName()+"被"+_lastAttacker.getName()+"杀死了！");
			}
			setDeathProcessing(false);

			setExp(0);
			setKarma(0);
			allTargetClear();
			
			startDeleteTimer();
		}
	}

	public boolean is_storeDroped() {
		return _storeDroped;
	}

	public void set_storeDroped(boolean flag) {
		_storeDroped = flag;
	}

	private void hide() {
		int npcid = getNpcTemplate().get_npcId();
		//变更成switch 
		switch (npcid)
		{
			case 45061: case 45161: case 45181: case 45455: {
				if (getMaxHp() / 3 > getCurrentHp()) {
					Random random = new Random();
					int rnd = random.nextInt(10);
					if (1 > rnd) {
						allTargetClear();
						setHiddenStatus(HIDDEN_STATUS_SINK);
						broadcastPacket(new S_DoActionGFX(getId(),
								ActionCodes.ACTION_Hide));
						setStatus(13);
						broadcastPacket(new S_NPCPack(this));
					}
				}
			}
			break;
			case 45682: {
				if (getMaxHp() / 3 > getCurrentHp()) {
					Random random = new Random();
					int rnd = random.nextInt(50);
					if (1 > rnd) {
						allTargetClear();
						setHiddenStatus(HIDDEN_STATUS_SINK);
						broadcastPacket(new S_DoActionGFX(getId(),
								ActionCodes.ACTION_AntharasHide));
						setStatus(20);
						broadcastPacket(new S_NPCPack(this));
					}
				}
			}
			break;
			case 45067: case 45264: case 45452: case 45090: case 45321: case 45445: {
				if (getMaxHp() / 3 > getCurrentHp()) {
					Random random = new Random();
					int rnd = random.nextInt(10);
					if (1 > rnd) {
						allTargetClear();
						setHiddenStatus(HIDDEN_STATUS_FLY);
						broadcastPacket(new S_DoActionGFX(getId(),
								ActionCodes.ACTION_Moveup));
						setStatus(4);
						broadcastPacket(new S_NPCPack(this));
					}
				}
			}
			break;
			case 45681: {
				if (getMaxHp() / 3 > getCurrentHp()) {
					Random random = new Random();
					int rnd = random.nextInt(50);
					if (1 > rnd) {
						allTargetClear();
						setHiddenStatus(HIDDEN_STATUS_FLY);
						broadcastPacket(new S_DoActionGFX(getId(),
								ActionCodes.ACTION_Moveup));
						setStatus(11);
						broadcastPacket(new S_NPCPack(this));
					}
				}
			}
			break;
		}
		//变更成switch  end
	}
	private final ArrayList<L1ItemInstance> _dropWorldList = new ArrayList<L1ItemInstance>();
	public void addDropListItem(final L1ItemInstance item) {
		_dropWorldList.add(item);
	}
	public void initHideForMinion(L1NpcInstance leader) {
		// グループに属するモンスターの出現直後の隠れる動作（リーダーと同じ動作にする）
		int npcid = getNpcTemplate().get_npcId();
		if (leader.getHiddenStatus() == HIDDEN_STATUS_SINK) {
			if ((npcid == 45061 // カーズドスパルトイ
					)
					|| (npcid == 45161 // スパルトイ
					) || (npcid == 45181 // スパルトイ
					) || (npcid == 45455)) { // デッドリースパルトイ
				setHiddenStatus(HIDDEN_STATUS_SINK);
				setStatus(11);
			} else if ((npcid == 45045 // クレイゴーレム
					)
					|| (npcid == 45126 // ストーンゴーレム
					) || (npcid == 45134 // ストーンゴーレム
					) || (npcid == 45281)) { // ギランストーンゴーレム
				setHiddenStatus(HIDDEN_STATUS_SINK);
				setStatus(4);
			} else if ((npcid == 46107 // テーベ マンドラゴラ(白)
					)
					|| (npcid == 46108)) { // テーベ マンドラゴラ(黒)
				setHiddenStatus(HIDDEN_STATUS_SINK);
				setStatus(11);
			}
		} else if (leader.getHiddenStatus() == HIDDEN_STATUS_FLY) {
			if ((npcid == 45067 // バレーハーピー
					)
					|| (npcid == 45264 // ハーピー
					) || (npcid == 45452 // ハーピー
					) || (npcid == 45090 // バレーグリフォン
					) || (npcid == 45321 // グリフォン
					) || (npcid == 45445)) { // グリフォン
				setHiddenStatus(HIDDEN_STATUS_FLY);
				setStatus(4);
			} else if (npcid == 45681) { // リンドビオル
				setHiddenStatus(HIDDEN_STATUS_FLY);
				setStatus(11);
			}
		}/* else if ((npcid >= 46125) && (npcid <= 46128)) {
			setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_ICE);
			setStatus(4);
		}*/
	}

	@Override
	public void transform(int transformId) {
		super.transform(transformId);

		// DROP再设定
		getInventory().clearItems();
		DropTable.getInstance().setDrop(this, getInventory());
		getInventory().shuffle();
	}
}
