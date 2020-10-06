package l1j.server.server.model.doll;

import l1j.server.server.model.Instance.L1PcInstance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Doll_MprT extends L1DollExecutor {

    private static final Log _log = LogFactory.getLog(Doll_MprT.class);

    private int _int1;// 值1
    private int _int2;// 值1

    public Doll_MprT() {
    }

    public static L1DollExecutor get() {
        return new Doll_MprT();
    }

    @Override
    public void set_power(int int1, int int2, int int3) {
        try {
            _int1 = int1;
            _int2 = int2;
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void setDoll(L1PcInstance pc) {
       
    }

    @Override
    public void removeDoll(L1PcInstance pc) {
        
    }

    @Override
    public boolean is_reset() {
        return false;
    }
    @Override
    public int getValue1(){
    	return _int1;
    }
    @Override
    public int getValue2(){
    	return _int2;
    }
}
