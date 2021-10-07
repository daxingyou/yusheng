package l1j.server.data.npc.quest;

import l1j.server.Config;
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
		if (cmd.equalsIgnoreCase("addexp")){//全服双倍经验
			int count = 10;
			if (pc.getInventory().checkItem(44070, count)){
				pc.getInventory().consumeItem(44070, count);
				WorldCalcExp.get().addTime(3600);//1个小时
				WorldCalcExp.get().start();
				final StringBuilder msg = new StringBuilder();
				msg.append("\\f=玩家【\\f2");
				msg.append(pc.getName());
				msg.append("\\f=】为全服双倍经验时长增加了\\f4[1小时]");
				L1World.getInstance().broadcastPacketToAll(new S_GreenMessage(msg.toString()));
				pc.sendPackets(new S_CloseList(pc.getId()));
			}else{
				pc.sendPackets(new S_SystemMessage("\\F2元宝不足"+count));
			}
		}else if (cmd.equalsIgnoreCase("clanDoubleExperience")) {//全盟经验加倍
			int count = 5;
			if (pc.getInventory().checkItem(44070, count)) {
				if (pc.getClanid() == 0 || pc.getClan() == null) {
					pc.sendPackets(new S_SystemMessage("\\F2你还没有加入血盟"));
					return;
				}
				pc.getInventory().consumeItem(44070, count);
				for(final L1PcInstance tagpc : pc.getClan().getOnlineClanMember()){
					if (tagpc.isPrivateShop() || tagpc.getNetConnection() == null) {
						continue;
					}
					if (pc.getLevel() >= Config.MAXLV) {// 已达最大等级终止计算
						pc.sendPackets(new S_SystemMessage("已经达到最高使用等级,无法开启双倍经验"));
					}else {
						pc.setSkillEffect(l1j.william.New_Id.Skill_AJ_0_3, 3600);
					}
				}
				final StringBuilder msg = new StringBuilder();
				msg.append("\\f=玩家【\\f2");
				msg.append(pc.getName());
				msg.append("\\f=】为自己血盟"+pc.getClanname()+"双倍经验时长增加了\\f4[1小时]");
				L1World.getInstance().broadcastPacketToAll(
						new S_GreenMessage(msg.toString()));
				pc.sendPackets(new S_CloseList(pc.getId()));
			} else {
				pc.sendPackets(new S_SystemMessage("\\F2元宝不足"+count));
			}
		}else if (cmd.equalsIgnoreCase("addskill")){//全服在线玩家加buff
			int count = 5;
			if (pc.getInventory().checkItem(44070, count)){
				pc.getInventory().consumeItem(44070, count);
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
				pc.sendPackets(new S_SystemMessage("元宝不足"+count));
			}
		} else if (cmd.equalsIgnoreCase("addchanskill")){//血盟玩家加buff
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
		} else if (cmd.equalsIgnoreCase("addpcskill")) {//添加个人状态
			if (pc.getInventory().checkItem(40308, 20000)) {
				pc.getInventory().consumeItem(44070, 1);
				if (pc.isPrivateShop() || pc.getNetConnection() == null) {
					return;
				}
				for (final int element : skillIds) {
					int skillId = element;
					if (skillId == 148) {
						if (pc.isElf()) {
							skillId = 149;
						}
					}
					new L1SkillUse().handleCommands(pc, skillId,
							pc.getId(), pc.getX(), pc.getY(), null,
							1800, L1SkillUse.TYPE_GMBUFF);
				}
			} else {
				pc.sendPackets(new S_SystemMessage("金币不足2万."));
			}

		}
		else if (cmd.equalsIgnoreCase("addmxahpmp")) {//个人血魔全恢复
			if (pc.getInventory().checkItem(44070, 1)) {
				pc.getInventory().consumeItem(44070, 1);
				if (pc.isPrivateShop() || pc.getNetConnection() == null) {
					return;
				}
				if(pc.getCurrentHp() == pc.getMaxHp() && pc.getCurrentMp() == pc.getMaxMp()){
					pc.sendPackets(new S_SystemMessage("血魔已是满状态."));
				}else{
					pc.setCurrentHp(pc.getMaxHp());
					pc.setCurrentMp(pc.getMaxMp());
					pc.sendPackets(new S_SystemMessage("血魔恢复成功！扣除1元宝."));
				}


			} else {
				pc.sendPackets(new S_SystemMessage("元宝不足1."));
			}

		}
	}
}
