package l1j.server.server.model.Instance;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import l1j.server.server.IdFactory;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_BiaoChePack;
import l1j.server.server.serverpackets.S_HPMeter;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.world.L1World;

public class L1BiaoCheInstance extends L1NpcInstance {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Random _random = new Random();
	private int _time = 3600;//1小时 测试改为60秒
	public L1BiaoCheInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(L1PcInstance player) {
		if (player.getId() == _master.getId()){
			//攻击是自己主人
			L1Attack attack_mortion = new L1Attack(player, this); // 攻击送信
			attack_mortion.action();
			return;
		}
		if (_master instanceof L1PcInstance){
			if (player.getClanid() == ((L1PcInstance)_master).getClanid()){
				//攻击是自己血盟成员 攻击无效
				L1Attack attack_mortion = new L1Attack(player, this); // 攻击送信
				attack_mortion.action();
				return;
			}
		}
		
		if (this.getZoneType() == 1){
			//安全区
			L1Attack attack_mortion = new L1Attack(player, this); // 攻击送信
			attack_mortion.action();
			return;
		}
		L1Attack attack = new L1Attack(player, this);
		if (attack.calcHit()) {
			attack.calcDamage();
		}
		attack.action();
		attack.commit();
	}
	
	private long _callTime = 0;
	// 攻击ＨＰ减使用
	@Override
	public void receiveDamage(L1Character attacker, int damage) {
		if (getCurrentHp() > 0) {
			if (damage > 0) { // 回复场合攻击。
				removeSkillEffect(L1SkillId.FOG_OF_SLEEPING);
			}
			int newHp = getCurrentHp() - damage;
			if (newHp <= 0) {
				death(attacker);
			} else {
				setCurrentHp(newHp);
			}
			if (attacker instanceof L1PcInstance){
				((L1PcInstance)attacker).sendPackets(new S_HPMeter(this));
			}
			//间隔60秒 召唤一次血盟成员协助
			if (System.currentTimeMillis() - _callTime >= 60 * 1000){
				_callTime = System.currentTimeMillis();
				if (_master instanceof L1PcInstance){//镖车被攻击 召唤血盟成员协助
					final L1PcInstance pc = (L1PcInstance)_master;
					if (pc.getOnlineStatus() != 0){//在线
						final L1Clan clan = pc.getClan();
						if (pc.getClanid() !=0 && clan != null){
							for(final L1PcInstance tagpc : clan.getOnlineClanMember()){
								//自己主人
								if (tagpc.getId() == pc.getId()){
									continue;
								}
								//血盟成员已在镖车附近
								if (tagpc.getMapId() == pc.getMapId() && tagpc.getLocation().getLineDistance(getLocation()) < 20){
									continue;
								}
								//该血盟成员已死亡
								if (tagpc.isDead()){
									continue;
								}
								tagpc.setTempBiaoCheMapId(getMapId());
								tagpc.setTempBiaoCheLocX(getX());
								tagpc.setTempBiaoCheLocY(getY());
								tagpc.sendPackets(new S_Message_YN(4532, pc.getName()));
							}
						} 
					}
				}
			}
		} else if (!isDead()) { // 念
			death(attacker);
		}
	}
	class checkTimer extends TimerTask {

		public checkTimer() {
		}

		public void run() {
			_time --;
			if (_time <= 0){
				if (_master instanceof L1PcInstance){
					final L1PcInstance pc = (L1PcInstance)_master;
					if (pc.getOnlineStatus() != 0){//在线
						pc.setBiaoChe(null);
						pc.sendPackets(new S_SystemMessage("\\F1你的镖已超时."));
					}
				}
				deleteMe();//直接删除
			}
		}
	}
	private checkTimer _checkTimer = null;
	
	public final void startcheckTimer(){
		if (_checkTimer == null){
			_checkTimer = new checkTimer();
			(new Timer()).schedule(_checkTimer, 1000,1000);
		}
	}
	
	public final void stopcheckTimer(){
		if (_checkTimer != null){
			_checkTimer.cancel();
			_checkTimer = null;
		}
	}
	
	public void deleteMe(){
		stopcheckTimer();
		super.deleteMe();
	}
			
	public synchronized void death(L1Character lastAttacker) {
		if (!isDead()) {
			setDead(true);
			setCurrentHp(0);
			getMap().setPassable(getLocation(), true);
			if (_master instanceof L1PcInstance){
				final L1PcInstance pc = (L1PcInstance)_master;
				if (pc.getOnlineStatus() != 0){
					pc.setBiaoChe(null);
					pc.sendPackets(new S_SystemMessage("\\F1你的镖车已被劫杀."));
				}
				// 最后杀、赤
				L1PcInstance player = null;
				if (lastAttacker instanceof L1PcInstance) {
					player = (L1PcInstance) lastAttacker;
				} else if (lastAttacker instanceof L1PetInstance) {
					player = (L1PcInstance) ((L1PetInstance) lastAttacker).getMaster();
				} else if (lastAttacker instanceof L1SummonInstance) {
					player = (L1PcInstance) ((L1SummonInstance) lastAttacker).getMaster();
				}
				if (player != null) {
					player.getInventory().storeItem(40308, 600000);
					player.sendPackets(new S_SystemMessage("\\F1获得金币60万."));
				}
			}
			this.deleteMe();
		}
	}
	
	@Override
	public boolean noTarget() {
		if (_master != null){
			if (_master instanceof L1PcInstance){
				final L1PcInstance pc = (L1PcInstance)_master;
				if (pc.getOnlineStatus() == 0){
					return true;
				}
				if (pc.getMapId() != getMapId()){
					pc.sendPackets(new S_SystemMessage("\\F1你离你的镖车太远。"));
					return true;
				}
				int lineDistance = getLocation().getTileLineDistance(pc.getLocation());
				if (lineDistance >= 15){
					pc.sendPackets(new S_SystemMessage("\\F1你离你的镖车太远。"));
					return true;
				}
				if (lineDistance >= 2){
					int dir = moveDirection(pc.getX(), pc.getY());
					if (dir != -1) {
						setDirectionMove(dir);
						setSleepTime(calcSleepTime(getPassispeed()));
					} else {
						return false;
					}
				}
			}else{
				return true;
			}
		}
		return false;
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.addKnownObject(this);
		perceivedFrom.sendPackets(new S_BiaoChePack(this,perceivedFrom));
		if (_master != null){
			if (perceivedFrom.getId() == _master.getId()){
				if (!isAiRunning()) {
					startAI();
				}
			}
		}
	}
	
	private int _color = 0;
	/**
	 * 0:白色<br>
	 * 1:蓝色<br>
	 * 2:黄色<br>
	 * 3:绿色<br>
	 * 4:紫色<br>
	 * @param type
	 */
	public void setColor(final int type){
		_color = type;
		setNameId(String.format("%s^%s镖车<%s>", getName(),NAME_COLOR[_color],NAME_MSGS[_color]));
	}
	/**
	 * 0:白色<br>
	 * 1:蓝色<br>
	 * 2:黄色<br>
	 * 3:绿色<br>
	 * 4:紫色<br>
	 * @param type
	 */
	public int getColor(){
		return _color;
	}

	private final static String[] NAME_COLOR = {"\\f>","\\f1","\\f=","\\f2","\\f4"};
	private final static String[] NAME_MSGS = {"白色","蓝色","黄色","绿色","紫色"};
	
	public L1BiaoCheInstance(L1Npc template, L1Character master) {
		super(template);
		setId(IdFactory.getInstance().nextId());

		setMaster(master);
		setX(master.getX() + _random.nextInt(5) - 2);
		setY(master.getY() + _random.nextInt(5) - 2);
		setMap(master.getMapId());
		setName(master.getName());
		setColor(0);
		setHeading(5);
		setLightSize(template.getLightSize());

		L1World.getInstance().storeWorldObject(this);
		L1World.getInstance().addVisibleObject(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			onPerceive(pc);
		}
		startcheckTimer();//启动计时
	}
}
