package l1j.server.server.model.doll;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_HPUpdate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Doll_Hp extends L1DollExecutor {

    private static final Log _log = LogFactory.getLog(Doll_Hp.class);

    private int _int1;// 值1

    public Doll_Hp() {
    }

    public static L1DollExecutor get() {
        return new Doll_Hp();
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
        try {
        	 pc.addMaxHp(_int1);
             pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void removeDoll(L1PcInstance pc) {
        try {
        	 pc.addMaxHp(-_int1);
             pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
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
