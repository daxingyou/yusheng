package l1j.server.server.command.executor;

import java.util.StringTokenizer;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1TestOp implements L1CommandExecutor {

	private L1TestOp() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1TestOp();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			final StringTokenizer tok = new StringTokenizer(arg);
			final int upclan = Integer.parseInt(tok.nextToken());
//			Opcodes.S_OPCODE_CHARRESET = upclan;
			pc.sendPackets(new S_SystemMessage("当前测试ID变更为"+upclan+"，请测试！"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		// S_PacketBoxActiveSpells._testop =upclan;
		// pc.sendPackets(new S_RedMessage(pc.getAccountName(),upclan));
		//pc.sendPackets(new S_PacketBoxMima(upclan, 30));
	}

}
