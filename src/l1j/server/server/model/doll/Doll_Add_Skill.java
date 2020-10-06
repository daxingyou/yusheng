package l1j.server.server.model.doll;

import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.templates.L1Skills;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Doll_Add_Skill extends L1DollExecutor {

    private static final Log _log = LogFactory.getLog(Doll_Add_Skill.class);

    private int _int1;// 值1


    public Doll_Add_Skill() {
    }

    public static L1DollExecutor get() {
        return new Doll_Add_Skill();
    }

    @Override
    public void set_power(int int1, int int2, int int3) {
        try {
            _int1 = int1;

        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void setDoll(L1PcInstance pc) {
    	L1ItemInstance tgItem = null;
        switch (_int1) {
            case L1SkillId.HOLY_WEAPON:
            case L1SkillId.ENCHANT_WEAPON:
            case L1SkillId.BLESS_WEAPON:
            case L1SkillId.SHADOW_FANG:
                tgItem = pc.getWeapon();
                if (tgItem == null) {
                    return;
                }
                break;
            case L1SkillId.BLESSED_ARMOR:// 铠甲护持
                tgItem = pc.getInventory().getItemEquipped(2, 2);// 盔甲
                if (tgItem == null) {
                    return;
                }
                break;
            default:
                break;
        }
        boolean is = false;
        if (tgItem != null) {
            if (!tgItem.isRunning()) {
                is = true;
            }
        } else {
            if (!pc.hasSkillEffect(_int1)) {
                is = true;
            }
        }
        if (is) {
            final L1Skills skill = SkillsTable.getInstance().getTemplate(_int1);
            final L1SkillUse skillUse = new L1SkillUse();
            skillUse.handleCommands(pc, _int1, pc.getId(), pc.getX(),
                    pc.getY(),null, skill.getBuffDuration(),
                    L1SkillUse.TYPE_GMBUFF);
        }
    }

    @Override
    public void removeDoll(L1PcInstance pc) {
    	if (pc.hasSkillEffect(_int1)) {
            pc.removeSkillEffect(_int1);
        }
    }
    
    @Override
    public boolean is_reset() {
        return false;
    }
    
    @Override
    public int getValue1(){
    	return _int1;
    }
}
