package l1j.server.data.item_etcitem;

import l1j.server.Config;
import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.world.L1World;

public class TouKui extends ItemExecutor {

    /**
	 *
	 */
    private TouKui() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new TouKui();
    }

	@Override
	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		final int spellsc_objid = data[0];
		final L1Object target = L1World.getInstance().findObject(spellsc_objid);
		if (target == null)
			return;
		if (!pc.getInventory().checkItem(40308, 500)){
			pc.sendPackets(new S_SystemMessage("\\f2偷窥一次需要金币500."));
			return;
		}
		final String[] msg = new String[17];
		if (target instanceof L1PcInstance) {
			if (pc.getMapId() == Config.HUODONGMAPID){
				pc.sendPackets(new S_SystemMessage("此地图不能对人物使用."));
				return;
			}
			L1PcInstance target_pc = (L1PcInstance) target;
			pc.getInventory().consumeItem(40308, 500);
			msg[0] = target_pc.getName();
			msg[1] = String.valueOf(target_pc.getLevel());
			msg[2] = String.valueOf(target_pc.getCurrentHp()) + " / " + String.valueOf(target_pc.getMaxHp());
			msg[3] = String.valueOf(target_pc.getCurrentMp()) + " / " + String.valueOf(target_pc.getMaxMp());
			msg[4] = String.valueOf(target_pc.getStr());
			msg[5] = String.valueOf(target_pc.getCon());
			msg[6] = String.valueOf(target_pc.getDex());
			msg[7] = String.valueOf(target_pc.getWis());
			msg[8] = String.valueOf(target_pc.getInt());
			msg[9] = String.valueOf(target_pc.getCha());
			msg[10] = String.valueOf(target_pc.getAc());
			msg[11] = String.valueOf(target_pc.getEr());
			msg[12] = String.valueOf(target_pc.getMr()) + " %";
			msg[13] = String.valueOf(target_pc.getFire()) + " %";
			msg[14] = String.valueOf(target_pc.getWater()) + " %";
			msg[15] = String.valueOf(target_pc.getWind()) + " %";
			msg[16] = String.valueOf(target_pc.getEarth()) + " %";
			pc.setTouKuiName(target_pc.getName());
			pc.set_tuokui_objId(spellsc_objid);
			pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "toukuipc", msg));
		} else if (target instanceof L1MonsterInstance) {
			final L1MonsterInstance target_npc = (L1MonsterInstance) target;
			pc.getInventory().consumeItem(40308, 500);
			msg[0] = target_npc.getName();
			msg[1] = String.valueOf(target_npc.getLevel());
			msg[2] = String.valueOf(target_npc.getCurrentHp()) + " / " + String.valueOf(target_npc.getMaxHp());
			msg[3] = String.valueOf(target_npc.getCurrentMp()) + " / " + String.valueOf(target_npc.getMaxMp());
			msg[4] = String.valueOf(target_npc.getStr());
			msg[5] = String.valueOf(target_npc.getCon());
			msg[6] = String.valueOf(target_npc.getDex());
			msg[7] = String.valueOf(target_npc.getWis());
			msg[8] = String.valueOf(target_npc.getInt());
			msg[9] = String.valueOf(target_npc.getCha());
			msg[10] = String.valueOf(target_npc.getAc());
			msg[11] = "0";
			msg[12] = String.valueOf(target_npc.getMr()) + " %";
			msg[13] = String.valueOf(target_npc.getFire()) + " %";
			msg[14] = String.valueOf(target_npc.getWater()) + " %";
			msg[15] = String.valueOf(target_npc.getWind()) + " %";
			msg[16] = String.valueOf(target_npc.getEarth()) + " %";
            pc.setTouKuiName(target_npc.getName());
            pc.set_tuokui_objId(target_npc.getNpcId());
			pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "toukuimob", msg));
		} else {
			pc.sendPackets(new S_ServerMessage(79));
		}
	}

}

