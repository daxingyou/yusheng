package l1j.server.server.serverpackets;


import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1SummonInstance;

public class S_OpCode_Test extends ServerBasePacket {

	public S_OpCode_Test(int OpCodeID, int TestLevel, L1PcInstance Player) {
		_opcodeid = OpCodeID;
		_testlevel = TestLevel;
		_gm = Player;
	}

	@Override
	public byte[] getContent() {
		writeC(_opcode[_testlevel][_opcodeid]);
		// ■■■■ jump鲭等调用 ■■■■
		int objid = 0;
		Object[] petList = _gm.getPetList().values().toArray();
		for (Object pet : petList) {
			if (pet instanceof L1SummonInstance) {
				L1SummonInstance summon = (L1SummonInstance) pet;
				objid = summon.getId();
				break;
			}
		}
		writeD(objid);
		writeC(25);
		// ■■■■■■■■■■■■■■■■■■■■■■■■
		return getBytes();
	}

	public String getInfo() {
		StringBuilder info = new StringBuilder();
		info.append(".opc ID 入力下。\n");
		info.append("[Ver] ").append(_version);
		info.append(" [Level] ").append(_testlevel);
		info.append(" [IdRange] 0 - ").append(_opcode[_testlevel].length - 1)
				.append("\n");
		info.append("[直前行动] ").append(_action).append("\n");
		info.append("[予想状态] ").append(_status).append("\n");
		return info.toString();
	}

	public String getCode() {
		StringBuilder info = new StringBuilder();
		info.append("[OpCodeId] ").append(_opcodeid).append(" [OpCode] ")
				.append(_opcode[_testlevel][_opcodeid]);
		return info.toString();
	}

	public String getCodeList() {
		StringBuilder info = new StringBuilder();
		info.append(".opcid ID 入力下。\n");
		info.append("Lv").append(_testlevel).append(
				"　0　　1　　2　　3　　4　　5　　6　　7　　8　　9\n");
		int t = 0;
		int tc = 10;
		for (int i = 0; i < _opcode[_testlevel].length; i++) {
			if (tc == 10) {
				if (t > 0) {
					info.append("\n");
				}
				info.append(padt(t));
				t++;
				tc = 0;
			}
			info.append(pad(_opcode[_testlevel][i]));
			tc++;
		}
		return info.toString();
	}

	private String pad(int i) {
		if (i < 10) {
			return (new StringBuilder()).append(" 00").append(i).toString();
		} else if (i < 100) {
			return (new StringBuilder()).append(" 0").append(i).toString();
		}
		return (new StringBuilder()).append(" ").append(i).toString();
	}

	private String padt(int i) {
		if (i < 10) {
			return (new StringBuilder()).append("0").append(i).append(" ")
					.toString();
		}
		return (new StringBuilder()).append(i).append(" ").toString();
	}

	public String getType() {
		return "[S]  S_OpCode_Test";
	}


	private int _opcodeid;
	private int _testlevel;
	private L1PcInstance _gm;

	// ■■■■ 报告样为 ■■■■
	private final String _version = "S_HPMeter1.0";

	// ■■■■ 直前行动 ■■■■
	private final String _action = "１匹出";

	// ■■■■ 正送予想状态 ■■■■
	private final String _status = "HP25%变动";

	// 既解明济下段设定
	// 上段(Level0)现在137鲭使 .opc .opcid 用
	// 中段(Level1)现在137鲭定义本当用确认
	// "etc/参考资料.txt"书
	// 130~139(0~129间可能性高思)领域 .opc2 .opcid2 用
	// 下段(Level2)现在137鲭利用动作(念为用意) .opc3 .opcid3 用
	// 上段场合中段、中段场合下段感
	int[][] _opcode = {
			{ 2, 3, 4, 6, 8, 16, 17, 18, 19, 22, 24, 27, 31, 33, 34, 35, 37,
					38, 40, 43, 47, 48, 49, 52, 54, 62, 65, 70, 72, 73, 74, 75,
					76, 78, 80, 83, 84, 86, 87, 88, 89, 90, 91, 92, 93, 95, 98,
					99, 101, 102, 104, 105, 107, 110, 112, 113, 114, 116, 117,
					118, 119, 120, 121, 122, 124, 127, 128 },

			{ 0, 5, 9, 13,

			42, 44, 50, 53, 55, 58, 60, 64, 77,

			111, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139 },

			{ 1, 7, 10, 11, 12, 15, 20, 21, 23, 25, 26, 28, 29, 30, 32, 36, 39,
					41, 45, 46, 51, 56, 57, 59, 61, 63, 66, 67, 68, 69, 71, 79,
					81, 82, 85, 94, 96, 97, 100, 103, 106, 108, 109, 115, 123,
					125, 126 } };
}
