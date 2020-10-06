package l1j.server.data.item_etcitem;
import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;

public class RestPotion extends ItemExecutor{
	
	/**
	 *
	 */
	private RestPotion() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new RestPotion();
	}

	@Override
	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		try {
			short init_hp = 0;
			short init_mp = 0;
			pc.getInventory().takeoffEquip(945);//用来脱掉全身装备
			pc.setExp(1);
			pc.resExp();
			pc.addBaseMaxHp((short)(-1 * (int)((double) pc.getBaseMaxHp())));
    		pc.addBaseMaxMp((short)(-1 * (int)((double) pc.getBaseMaxMp())));
    		if (pc.isCrown()) { // 君主
    			init_hp = 14;
    			switch (pc.getWis()) {
    			case 11:
    				init_mp = 2;
    				break;
    			case 12:
    			case 13:
    			case 14:
    			case 15:
    				init_mp = 3;
    				break;
    			case 16:
    			case 17:
    			case 18:
    				init_mp = 4;
    				break;
    			default:
    				init_mp = 2;
    				break;
    			}
    		} else if (pc.isKnight()) { // 
    			init_hp = 16;
    			switch (pc.getWis()) {
    			case 9:
    			case 10:
    			case 11:
    				init_mp = 1;
    				break;
    			case 12:
    			case 13:
    				init_mp = 2;
    				break;
    			default:
    				init_mp = 1;
    				break;
    			}
    		} else if (pc.isElf()) { // 
    			init_hp = 15;
    			switch (pc.getWis()) {
    			case 12:
    			case 13:
    			case 14:
    			case 15:
    				init_mp = 4;
    				break;
    			case 16:
    			case 17:
    			case 18:
    				init_mp = 6;
    				break;
    			default:
    				init_mp = 4;
    				break;
    			}
    		} else if (pc.isWizard()) { // WIZ
    			init_hp = 12;
    			switch (pc.getWis()) {
    			case 12:
    			case 13:
    			case 14:
    			case 15:
    				init_mp = 6;
    				break;
    			case 16:
    			case 17:
    			case 18:
    				init_mp = 8;
    				break;
    			default:
    				init_mp = 6;
    				break;
    			}
    		} else if (pc.isDarkelf()) { // DE
    			init_hp = 12;
    			switch (pc.getWis()) {
    			case 10:
    			case 11:
    				init_mp = 3;
    				break;
    			case 12:
    			case 13:
    			case 14:
    			case 15:
    				init_mp = 4;
    				break;
    			case 16:
    			case 17:
    			case 18:
    				init_mp = 6;
    				break;
    			default:
    				init_mp = 3;
    				break;
    			}
    		}
    		pc.addBaseMaxHp(init_hp);
    		pc.setCurrentHp(init_hp);
    		pc.addBaseMaxMp(init_mp);
    		pc.setCurrentMp(init_mp);
			//血量调整
			//防御、魔防、命中、攻击重新计算 
			pc.resetBaseAc();
			pc.resetBaseMr();
			pc.resetBaseHitup();
			pc.resetBaseDmgup();
			//防御、魔防、命中、攻击重新计算  end
			pc.sendPackets(new S_SkillSound(pc.getId(), 3944));
			pc.broadcastPacket(new S_SkillSound(pc.getId(), 3944));
			pc.sendPackets(new S_OwnCharStatus(pc));
			pc.getInventory().removeItem(item, 1);
			pc.sendPackets(new S_ServerMessage(822)); // 独自、适当。
			pc.save(); // DB情报书迂
			
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

}
