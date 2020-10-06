package l1j.server.data.npc.power;

import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.Config;
import l1j.server.data.executor.NpcExecutor;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

public class Npc_Balrog extends NpcExecutor {

	/**
	 * 转生尊者
	 */
	private Npc_Balrog() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Balrog();
	}

	@Override
	public int type() {
		return 3;
	}
	
	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		String[] htmldata = new String[] { npc.getName()+
				":哦，忠诚的奴隶，你是来兑换耳环的吗？您如果对炎魔大人比较忠诚可以来找我兑换耳环。",
				 "贡献1个灵魂石碎片", "贡献10个灵魂石碎片", "贡献100个灵魂石碎片", "想要得到暗影神殿2楼的钥匙","想要得到暗影神殿3楼的钥匙","我想来兑换耳环"," "," "," ",
				 " "," "," "," "," "," "," "
};
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "a2_1",htmldata));
	}
	
	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		String htmlid = "";
		if (cmd.equalsIgnoreCase("a2_1_1")) {
			if (pc.getInventory().consumeItem(40678, 1)) {
				pc.addKarma((int) (100 * Config.RATE_KARMA));
				pc.sendPackets(new S_ServerMessage(1078));
			} 

		} else if (cmd.equalsIgnoreCase("a2_1_2")) {
			if (pc.getInventory().consumeItem(40678, 10)) {
				pc.addKarma((int) (1000 * Config.RATE_KARMA));
				pc.sendPackets(new S_ServerMessage(1078));
			} 
		} else if (cmd.equalsIgnoreCase("a2_1_3")) {
			if (pc.getInventory().consumeItem(40678, 100)) {
				pc.addKarma((int) (10000 * Config.RATE_KARMA));
				pc.sendPackets(new S_ServerMessage(1078));
			} 
		} else if (cmd.equalsIgnoreCase("a2_1_4")) {
			if (pc.getInventory().checkItem(40678, 1000)) {
				if (pc.getInventory().checkItem(40719, 1)) {
					if (pc.getInventory().consumeItem(40678, 1000)&&pc.getInventory().consumeItem(40719, 1)) {
						L1ItemInstance item = pc.getInventory().storeItem(40615, 1);			
						pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(),item.getLogName()));
						WriteLogTxt.Recording("兑换影子神殿钥匙记录", "玩家 "+pc.getName()+"兑换"+item+"成功！");
					}
				}else {
					pc.sendPackets(new S_SystemMessage("混沌首级不足！"));
				}
			}else {
				pc.sendPackets(new S_SystemMessage("灵魂石不足！"));
			}
		} else if (cmd.equalsIgnoreCase("a2_1_5")) {
			if (pc.getInventory().checkItem(40678, 1000)) {
				if (pc.getInventory().checkItem(40707, 1)) {
					if (pc.getInventory().consumeItem(40678, 1000)&&pc.getInventory().consumeItem(40707, 1)) {
						L1ItemInstance item = pc.getInventory().storeItem(40616, 1);
						pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(),item.getLogName()));
						WriteLogTxt.Recording("兑换影子神殿钥匙记录", "玩家 "+pc.getName()+"兑换"+item+"成功！");
					}
				}else {
					pc.sendPackets(new S_SystemMessage("死亡首级不足！"));
				}
			}else {
				pc.sendPackets(new S_SystemMessage("灵魂石不足！"));
			}
		} else if (cmd.equalsIgnoreCase("a2_1_6")) {
			int earringLevel = 0;
			if (pc.getInventory().checkItem(21020)) { // 踊躍のイアリング
				earringLevel = 1;
			} else if (pc.getInventory().checkItem(21021)) { // 双子のイアリング
				earringLevel = 2;
			} else if (pc.getInventory().checkItem(21022)) { // 友好のイアリング
				earringLevel = 3;
			} else if (pc.getInventory().checkItem(21023)) { // 極知のイアリング
				earringLevel = 4;
			} else if (pc.getInventory().checkItem(21024)) { // 暴走のイアリング
				earringLevel = 5;
			} else if (pc.getInventory().checkItem(21025)) { // 従魔のイアリング
				earringLevel = 6;
			} else if (pc.getInventory().checkItem(21026)) { // 血族のイアリング
				earringLevel = 7;
			} else if (pc.getInventory().checkItem(21027)) { // 奴隷のイアリング
				earringLevel = 8;
			}
			if (pc.getKarmaLevel() == 1) {
				if (earringLevel >= 1) {
					htmlid = "lringd";
				} else {
					htmlid = "lring1";
				}
			} else if (pc.getKarmaLevel() == 2) {
				if (earringLevel >= 2) {
					htmlid = "lringd";
				} else {
					htmlid = "lring2";
				}
			} else if (pc.getKarmaLevel() == 3) {
				if (earringLevel >= 3) {
					htmlid = "lringd";
				} else {
					htmlid = "lring3";
				}
			} else if (pc.getKarmaLevel() == 4) {
				if (earringLevel >= 4) {
					htmlid = "lringd";
				} else {
					htmlid = "lring4";
				}
			} else if (pc.getKarmaLevel() == 5) {
				if (earringLevel >= 5) {
					htmlid = "lringd";
				} else {
					htmlid = "lring5";
				}
			} else if (pc.getKarmaLevel() == 6) {
				if (earringLevel >= 6) {
					htmlid = "lringd";
				} else {
					htmlid = "lring6";
				}
			} else if (pc.getKarmaLevel() == 7) {
				if (earringLevel >= 7) {
					htmlid = "lringd";
				} else {
					htmlid = "lring7";
				}
			} else if (pc.getKarmaLevel() == 8) {
				if (earringLevel >= 8) {
					htmlid = "lringd";
				} else {
					htmlid = "lring8";
				}
			} else {
				htmlid = "lring0";
			}	
		}else {
			htmlid = getBarlogEarring(pc, npc, cmd);
		}
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), htmlid));	
	}
	
	
	private String getBarlogEarring(final L1PcInstance pc,
			final L1NpcInstance npc, final String s) {
		final int[] earringIdList = { 21020, 21021, 21022, 21023, 21024, 21025,
				21026, 21027 };
		final int[] karmalist = { 10000, 20000, 100000, 500000, 1500000,
				3000000, 5000000, 10000000 };
		int earringId = 0;
		int karma = 0;
		String htmlid = null;
		L1ItemInstance item = null;
		if (s.equalsIgnoreCase("1")) {
			earringId = earringIdList[0];
			karma = karmalist[0];
		} else if (s.equalsIgnoreCase("2")) {
			earringId = earringIdList[1];
			karma = karmalist[1];
		} else if (s.equalsIgnoreCase("3")) {
			earringId = earringIdList[2];
			karma = karmalist[2];
		} else if (s.equalsIgnoreCase("4")) {
			earringId = earringIdList[3];
			karma = karmalist[3];
		} else if (s.equalsIgnoreCase("5")) {
			earringId = earringIdList[4];
			karma = karmalist[4];
		} else if (s.equalsIgnoreCase("6")) {
			earringId = earringIdList[5];
			karma = karmalist[5];
		} else if (s.equalsIgnoreCase("7")) {
			earringId = earringIdList[6];
			karma = karmalist[6];
		} else if (s.equalsIgnoreCase("8")) {
			earringId = earringIdList[7];
			karma = karmalist[7];
		}
		WriteLogTxt.Recording("耳环兑换记录", "当前需要友好度为" + karma + "玩家" + pc.getName() + "当前友好度为"
				+ pc.getKarma() + "试图兑换" + earringId);
		if (earringId != 0 && pc.getKarma() >= karma) {
			WriteLogTxt.Recording("耳环兑换记录","当前需要友好度为" + karma + "玩家" + pc.getName() + "当前友好度为"
					+ pc.getKarma() + "兑换" + earringId + "成功");
			item = pc.getInventory().storeItem(earringId, 1);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.get_name(), item.getLogName())); // \f1%0が%1をくれました。
			}
			for (final int id : earringIdList) {
				if (id == earringId) {
					break;
				}
				if (pc.getInventory().checkItem(id)) {
					pc.getInventory().consumeItem(id, 1);
				}
			}
			htmlid = "";
		}
		return htmlid;
	}
}
