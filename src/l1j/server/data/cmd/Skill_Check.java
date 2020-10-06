package l1j.server.data.cmd;

import l1j.server.server.datatables.lock.CharSkillReading;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;

public class Skill_Check {
	/**
     * 技能学习成功与否的判断
     * 
     * @param pc
     * @param item
     * @param skillid
     * @param magicLv
     * @param attribute
     */
    public static void check(final L1PcInstance pc, final L1ItemInstance item,
            final int skillid, final int magicLv, final int attribute) {
        // 检查是否已学习该法术
        if (CharSkillReading.get().spellCheck(pc.getId(), skillid)) {
            // 79 没有任何事情发生
            final S_ServerMessage msg = new S_ServerMessage(79);
            pc.sendPackets(msg);
            System.out.println("1"); 

        } else {
            if (skillid != 0) {
                final Skill_StudyingExecutor addSkill = new Skill_Studying();
                // 执行技能学习结果判断
                addSkill.magic(pc, skillid, magicLv, attribute, item.getId());
                System.out.println("2"); 
            }
        }
    }
}
