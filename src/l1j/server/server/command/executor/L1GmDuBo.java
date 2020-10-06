package l1j.server.server.command.executor;

import java.util.StringTokenizer;

import l1j.server.server.model.L1DuBo;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1GmDuBo implements L1CommandExecutor {

    private static final Log _log = LogFactory.getLog(L1Buff.class);

    private L1GmDuBo() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1GmDuBo();
    }

    @Override
    public void execute(final L1PcInstance pc, final String cmdName,
            final String arg) {
        try {
            final StringTokenizer tok = new StringTokenizer(arg);
            String s = tok.nextToken();
            if (s.equals("a")) {
               L1DuBo.get(null).setGmBaoz(true);
               pc.sendPackets(new S_SystemMessage("\\F2设置成功 开住必出豹子"));
            } else if (s.equals("b")) {
            	L1DuBo.get(null).setGmDa(true);
            	pc.sendPackets(new S_SystemMessage("\\F2设置成功 开住必出大"));
            } else if (s.equals("c")) {
            	L1DuBo.get(null).setGmXiao(true);
            	pc.sendPackets(new S_SystemMessage("\\F2设置成功 开住必出小"));
            }else{
            	pc.sendPackets(new S_SystemMessage("\\F2请正确输入: .dubo a(a必出豹子,b必出大,c必出小)"));
            }
        } catch (final Exception e) {
            _log.error("错误的GM指令格式: " + this.getClass().getSimpleName()
                    + " 执行的GM:" + pc.getName());
            // 261 \f1指令错误。
            pc.sendPackets(new S_SystemMessage(" 请正确输入: .dubo a(a必出豹子,b必出大,c必出小)"));
        }
    }
}
