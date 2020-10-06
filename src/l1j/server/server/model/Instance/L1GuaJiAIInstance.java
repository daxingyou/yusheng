package l1j.server.server.model.Instance;

import java.util.Random;

import l1j.server.Config;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_GuaJiAINpcPack;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.world.L1World;

public class L1GuaJiAIInstance extends L1NpcInstance {
	private static final long serialVersionUID = 1L;

	private static Random _random = new Random();
	
	@Override
	public void onAction(L1PcInstance pc) {
		final L1Attack attack = new L1Attack(pc, this);
		attack.action();
		pc.addGuaJiAIAttackCount(1);
		if (!pc.getGuaJiAI()){
			final int attack_count0 = pc.getGuaJiAIAttackCount();
			if (attack_count0 >= 20){
				if (_random.nextInt(51) > attack_count0){//强制攻击 20~50刀成功
					pc.setGuaJiAIAttackCount(0);
					pc.setGuaJiAI(true);
					pc.setGuaJiAITime(System.currentTimeMillis());
					if (pc.hasSkillEffect(L1SkillId.GUAJI_AI_SKILLID)){
						pc.removeSkillEffect(L1SkillId.GUAJI_AI_SKILLID);
					}
					setDeleteTime(30);//成功后30秒删除NPC
					pc.sendPackets(new S_SystemMessage("**验证成功 无需再攻击 否则掉线处理**"));
					if (Config.GUAJI_AI_OK_ITEMID > 0 && Config.GUAJI_AI_OK_ITEMCOUNT > 0){
						final L1Item item = ItemTable.getInstance().getTemplate(Config.GUAJI_AI_OK_ITEMID);
						if (item != null){
							pc.sendPackets(new S_SystemMessage("获得挂机验证奖励:" + item.getName() + "(" + Config.GUAJI_AI_OK_ITEMCOUNT + ")"));
							pc.getInventory().storeItem(Config.GUAJI_AI_OK_ITEMID,Config.GUAJI_AI_OK_ITEMCOUNT);
						}
					}
				}
			}
		}else{
			pc.sendPackets(new S_SystemMessage("**已经验证成功 无需再攻击 否则掉线处理**"));
			final int attack_count1 = pc.getGuaJiAIAttackCount();
			if (attack_count1 >= 20){
				//如果已经成功再攻击 掉线处理
				pc.getNetConnection().kick();
			}
		}
	}
	
	private void setDeleteTime(final int time){
		GeneralThreadPool.getInstance().schedule(new GuaJiAINpcTimer(), 30 * 1000);
	}
	// 时间计测用
	class GuaJiAINpcTimer implements Runnable {
			@Override
			public void run() {
				if (_destroyed) {
					return;
				}
				deleteMe();
			}
		
	}
	
	@Override
	public boolean noTarget() {
		if (_master instanceof L1PcInstance){
			final L1PcInstance pc = (L1PcInstance)_master;
			if (pc.getOnlineStatus() == 0){
				deleteMe();
				return true;
			}
			if (pc.getGuaJiAI()){//如果已经验证成功 停止AI
				return true;
			}
		}
		if (_master != null) {
			if (_master.getMapId() == getMapId()){
				final int range = getLocation().getTileLineDistance(_master.getLocation());
				if (range > 2 && range < 15) {
					int dir = moveDirection(_master.getX(), _master.getY());
					if (dir == -1) {
						if (!isAiRunning()) {
							startAI();
						}
						return true;
					} else {
						setDirectionMove(dir);
						setSleepTime(calcSleepTime(getPassispeed()));
					}
				}else if(range >= 15){
					if (!nearTeleport(_master.getX(), _master.getY())){
						qzteleport(this,_master.getX(), _master.getY(), _master.getMapId(),5);
					}
				}
			}else{
				qzteleport(this,_master.getX(), _master.getY(), _master.getMapId(),5);
			}
		} else {
			deleteMe();
			return true;
		}
		return false;
	}
	
	private void qzteleport(final L1Character npc,final int x,final int y,final short map,final int head) {
		L1World.getInstance().moveVisibleObject(npc, map);

		L1WorldMap.getInstance().getMap(npc.getMapId()).setPassable(npc.getX(),
				npc.getY(), true);
		npc.setX(x);
		npc.setY(y);
		npc.setMap(map);
		npc.setHeading(head);
		L1WorldMap.getInstance().getMap(npc.getMapId()).setPassable(npc.getX(),
				npc.getY(), false);
	}
	
	public L1GuaJiAIInstance(final L1Npc template,final L1PcInstance master) {
		super(template);
		setId(IdFactory.getInstance().nextId());
		setMaster(master);


		setX(master.getX() + _random.nextInt(5) - 2);
		setY(master.getY() + _random.nextInt(5) - 2);
		setMap(master.getMapId());
		setHeading(5);
		setLightSize(template.getLightSize());

		master.setGuaJiAI(false);
		master.setGuaJiAIAttackCount(0);

		L1World.getInstance().storeWorldObject(this);
		L1World.getInstance().addVisibleObject(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			onPerceive(pc);
		}
		if (!isAiRunning()) {
			startAI();
		}
		if (Config.GUAJI_AI_NO_TIME > 0){
			master.setSkillEffect(L1SkillId.GUAJI_AI_SKILLID, Config.GUAJI_AI_NO_TIME * 60 * 1000);
		}
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		if (_master != null && perceivedFrom.getId() == _master.getId()){//只有自己能看得到
			perceivedFrom.addKnownObject(this);
			perceivedFrom.sendPackets(new S_GuaJiAINpcPack(this, perceivedFrom));
		}
	}

	@Override
	public void onItemUse() {

	}

	@Override
	public void onGetItem(L1ItemInstance item) {
		
	}
}
