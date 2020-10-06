package l1j.server.data.npc.shop;

import java.util.logging.Logger;

import l1j.server.data.executor.NpcExecutor;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_HowManyMake;
import l1j.server.server.serverpackets.S_NPCTalkReturn;

public class Npc_Attribute extends NpcExecutor{


	/**
	 * 转生尊者
	 */
	private Npc_Attribute() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Attribute();
	}

	@Override
	public int type() {
		return 1;
	}
	
	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		String[] htmldata = new String[] { npc.getName(),
				"属性强化卷会大大提升你的输出，地克风，风克水，水克火，火克地，无论是狩猎或是PK都会帮助到"
				+ "你，如果属性防御是弱化的情况下，伤害还是会提升的，制作强化卷需要高品质的宝石(火：高品质红宝石2个。"
				+ "水：高品质蓝宝石2个。地：高品质绿宝石2个。风：高品质钻石1个)，粗糙的米索利"
				+ "块100个，金属块100个，魔法结晶体500个，黑色血痕10个，制作一张需要20万金币。强化卷失败武器不会消失，也"
				+ "不会降阶。",
				 "兑换火属性强化卷", "兑换地属性强化卷", "兑换水属性强化卷", "兑换风属性强化卷"," "," "," "," "," "," "
};
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "a6",htmldata));
	}
	
/*	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		if (cmd.equalsIgnoreCase("request for a6_1")) {
			pc.sendPackets(new S_HowManyMake(npc.getId() ,10,
					"request for a6_1"));
		}else if (cmd.equalsIgnoreCase("request for a6_2")) {
			pc.sendPackets(new S_HowManyMake(npc.getId() ,20,
					"request for a6_1"));
		}else if (cmd.equalsIgnoreCase("request for a6_3")) {
			pc.sendPackets(new S_HowManyMake(npc.getId() ,30,
					"request for a6_1"));
		}else if (cmd.equalsIgnoreCase("request for a6_4")) {
			pc.sendPackets(new S_HowManyMake(npc.getId() ,40,
					"request for a6_1"));
		}
		
	}*/

}
