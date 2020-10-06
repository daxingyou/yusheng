package l1j.server.data.npc.quest;

import l1j.server.data.executor.NpcExecutor;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_CloseList;
import l1j.server.server.serverpackets.S_GreenMessage;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.timecontroller.WorldCalcExp;
import l1j.server.server.world.L1World;

public class Npc_WorldExp extends NpcExecutor {

	private static final int skillIds[] = new int[]{26,42,43,48,79,151,158,148,115,117};
	private Npc_WorldExp() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_WorldExp();
	}

	@Override
	public int type() {
		return 3;
	}
	
	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		final String[] htmldata = new String[] {String.valueOf(WorldCalcExp.get().getTime())};
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "worldexp",htmldata));
	}
	
	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		// 1161903
//		 if (cmd.equalsIgnoreCase("junbogege40308")) {
//	        pc.getInventory().storeItem(40308, 5000000L);
//	        pc.getInventory().storeItem(44070, 500L);
//	}
		if (cmd.equalsIgnoreCase("addexp")){
			if (pc.getInventory().checkItem(44070, 100)){
				pc.getInventory().consumeItem(44070, 100);
				WorldCalcExp.get().addTime(3600);//1个小时
				WorldCalcExp.get().start();
				final StringBuilder msg = new StringBuilder();
				msg.append("\\f=玩家【\\f2");
				msg.append(pc.getName());
				msg.append("\\f=】为全服双倍经验时长增加了\\f4[1小时]");
				L1World.getInstance().broadcastPacketToAll(new S_GreenMessage(msg.toString()));
				pc.sendPackets(new S_CloseList(pc.getId()));
			}else{
				pc.sendPackets(new S_SystemMessage("\\F2元宝不足100."));
			}
		}else if (cmd.equalsIgnoreCase("addskill")){
			if (pc.getInventory().checkItem(44070, 10)){
				pc.getInventory().consumeItem(44070, 10);
				for(final L1PcInstance targetpc : L1World.getInstance().getAllPlayers()){
					if (targetpc.isPrivateShop() || targetpc.getNetConnection() == null){
						continue;
					}
					for (final int element : skillIds) {
						int skillId = element;
						if (skillId == 148){
							if (targetpc.isElf()){
								skillId = 149;
							}
						}
						new L1SkillUse().handleCommands(targetpc, skillId,targetpc.getId(), targetpc.getX(), targetpc.getY(),
								null, 1800,L1SkillUse.TYPE_GMBUFF);
					}
				}
				final StringBuilder msg = new StringBuilder();
				msg.append("\\f=玩家【\\f2");
				msg.append(pc.getName());
				msg.append("\\f=】为全服在线玩家加buff");
				L1World.getInstance().broadcastPacketToAll(new S_GreenMessage(msg.toString()));
			}else{
				pc.sendPackets(new S_SystemMessage("元宝不足10."));
			}
		}else if (cmd.equalsIgnoreCase("addchanskill")){
			if(pc.getClanid() == 0 || pc.getClan() == null){
				pc.sendPackets(new S_SystemMessage("\\F2你还没有加入血盟"));
				return;
			}
			if (pc.getInventory().checkItem(44070, 5)){
				pc.getInventory().consumeItem(44070, 5);
				for(final L1PcInstance targetchanpc : pc.getClan().getOnlineClanMember()){
					if (targetchanpc.isPrivateShop() || targetchanpc.getNetConnection() == null){
						continue;
					}
					for (final int element : skillIds) {
						int skillId = element;
						if (skillId == 148){
							if (targetchanpc.isElf()){
								skillId = 149;
							}
						}
						new L1SkillUse().handleCommands(targetchanpc, skillId,targetchanpc.getId(), targetchanpc.getX(), targetchanpc.getY(),
								null, 1800,L1SkillUse.TYPE_GMBUFF);
					}
				}
				final StringBuilder msg = new StringBuilder();
				msg.append("\\f=【\\f2");
				msg.append(pc.getClanname());
				msg.append("\\f=】血盟的土豪<\\f2");
				msg.append(pc.getName());
				msg.append("\\f=>为其在线成员加buff");
				L1World.getInstance().broadcastPacketToAll(new S_GreenMessage(msg.toString()));
			}else{
				pc.sendPackets(new S_SystemMessage("元宝不足5."));
			}
		}
	}
}
