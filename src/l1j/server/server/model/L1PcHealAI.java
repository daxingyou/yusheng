package l1j.server.server.model;

import java.util.Random;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1PcHealAI implements Runnable {
	private static final Log _log = LogFactory.getLog(L1PcHealAI.class);
	private static final Random _random = new Random();
	private final L1PcInstance _pc;

	public L1PcHealAI(final L1PcInstance pc){
		_pc = pc;
	}
	
	public void startAI() {
		GeneralThreadPool.getInstance().execute(this);
        //NpcAiThreadPool.get().execute(this);
    }
	
	@Override
	public void run() {
		try {
			while (true) {
				if (AIProcess()) {
                    break;
                }
				UseHeallingPotion(_pc);
				final int delay_time  = _pc.getSelHealHpPotion()[3];
				Thread.sleep(delay_time <= 0  ? 300 : delay_time);
			}
			Thread.sleep(10);
			_pc.setHealAIProcess(false);
			_pc.setHealAI(false);
		} catch (Exception e) {
			_log.error(_pc.getName() + "\r\n" + e.getLocalizedMessage(), e);
		}
	}

	private void UseHeallingPotion(final L1PcInstance pc) {
		if (pc.isGhost())
			return;
		if (pc.isDead())
			return;
		if (pc.isStop())
			return;
		if (pc.isPrivateShop())
			return;
		if (pc.isTeleport())
			return;
		if (pc.hasSkillEffect(71)) {
			pc.sendPackets(new S_ServerMessage(698));
			return;
		}
		if (pc.isParalyzed()){
			return;
		}
		if (pc.getSelHealHpPotion()[0] <=0 || pc.getSelHealHpPotion()[1] <= 0){
			return;
		}
		//1000 50 500
		if (pc.getCurrentHp() > pc.getHealpersentHP() * 0.01 * pc.getMaxHp()){
			return;
		}
		if (!pc.getInventory().consumeItem(pc.getSelHealHpPotion()[0], 1)){
			return;
		}
		cancelAbsoluteBarrier(pc);
		pc.sendPackets(new S_SkillSound(pc.getId(), pc.getSelHealHpPotion()[2]));
		pc.broadcastPacket(new S_SkillSound(pc.getId(), pc.getSelHealHpPotion()[2]));
		if (pc.IsShowHealMessage()){
			pc.sendPackets(new S_ServerMessage(77));
		}
		int newhealHp = (int) ((double) pc.getSelHealHpPotion()[1] * (_random.nextGaussian() / 5D + 1.0D));
		if (pc.hasSkillEffect(173))
			newhealHp /= 2;
		if (pc.getHeallingPotion() > 0){
			newhealHp *= ((double)pc.getHeallingPotion() / 100.0D + 1.0D);
		}
		pc.setCurrentHp(pc.getCurrentHp() + newhealHp);
	}
	
	private void cancelAbsoluteBarrier(final L1PcInstance pc) {
		if (pc.hasSkillEffect(78)) {
			pc.killSkillEffectTimer(78);
			pc.startHpRegeneration();
			pc.startMpRegeneration();
		}
	}
	private boolean AIProcess() {
		if (_pc.getOnlineStatus() == 0){
			return true;
		}
		if (!_pc.getHealHPAI()){
			return true;
		}
		return false;
	}
}
