package l1j.server.server.serverpackets;

import java.util.logging.Level;
import java.util.logging.Logger;
import l1j.server.server.Opcodes;
import l1j.server.server.model.L1Party;
import l1j.server.server.model.Instance.L1PcInstance;

/**
 * 隊伍UI
 * 
 * @author daien
 * 
 */
public class S_PacketBoxParty extends ServerBasePacket {

	private static Logger _log = Logger.getLogger(S_PacketBoxParty.class
			.getName());
	/**
	 * <font color=#00800>隊伍成員資料(隊員) </font><BR>
	 */
	public static final int MSG_PARTY1 = 0x68;

	/**
	 * <font color=#00800>隊伍成員資料(隊長) </font><BR>
	 */
	public static final int MSG_PARTY2 = 0x69;

	/**
	 * <font color=#00800>隊伍成員資料(更新) </font><BR>
	 */
	public static final int MSG_PARTY3 = 0x6e;

	/**
	 * <font color=#00800>1,698：%s成為了新的隊長。 </font>
	 */
	public static final int MSG_LEADER = 0x6a;

	private byte[] _byte = null;

	public S_PacketBoxParty() {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(MSG_PARTY1);
		writeC(0);
	}
	/**
	 * 隊伍成員資料 <BR>
	 * <font color=#00800> </font>
	 * 
	 * @param pc
	 *            資料接收者
	 * @param party
	 *            隊伍
	 * 
	 */
	public S_PacketBoxParty(final L1PcInstance pc, final L1Party party) {
		try {
//			final Set<Entry<Integer, L1PcInstance>> map = party.partyUsers().entrySet();

			writeC(Opcodes.S_OPCODE_PACKETBOX);
			writeC(MSG_PARTY1);
			writeC(party.getNumOfMembers()-1);//

/*			for (final Iterator<Entry<Integer, L1PcInstance>> iter = map.iterator(); iter.hasNext();) {
				final Entry<Integer, L1PcInstance> info = iter.next();
				final L1PcInstance tgpc = info.getValue();
*/
			for (L1PcInstance tgpc : party.getMembers()) {
				if (tgpc == null) {
					continue;
				}
				if (pc.equals(tgpc)) {// 
					continue;
				}
				writeD(tgpc.getId());
				writeS(tgpc.getName());

				final int nowhp = tgpc.getCurrentHp();
				final int maxhp = tgpc.getMaxHp();

				writeC((int) ((nowhp / maxhp) * 100D));
				writeD(tgpc.getMapId());
				writeH(tgpc.getX());
				writeH(tgpc.getY());
			}

		} catch (final Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 更新隊伍 <BR>
	 * <font color=#00800> </font>
	 * 
	 * @param party
	 * @param pc
	 *            資料接收者
	 */
	public S_PacketBoxParty(final L1Party party, final L1PcInstance pc) {
		try {
//			final Set<Entry<Integer, L1PcInstance>> map = party.partyUsers().entrySet();

			writeC(Opcodes.S_OPCODE_PACKETBOX);
			writeC(MSG_PARTY3);
			writeC(party.getNumOfMembers()-1);// 

			//for (final Iterator<Entry<Integer, L1PcInstance>> iter = map.iterator(); iter.hasNext();) {
				//final Entry<Integer, L1PcInstance> info = iter.next();
				// final int key = info.getKey();
				//final L1PcInstance tgpc = info.getValue();
			for (L1PcInstance tgpc : party.getMembers()) {
				if (tgpc == null) {
					continue;
				}
				if (pc.equals(tgpc)) {
					continue;
				}
				writeD(tgpc.getId());
				writeD(tgpc.getMapId());
				writeH(tgpc.getX());
				writeH(tgpc.getY());
			}

		} catch (final Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 隊伍成員資料(隊長)
	 * 
	 * @param pc
	 * @param map
	 */
	public S_PacketBoxParty(final L1PcInstance tgpc) {
		try {
			writeC(Opcodes.S_OPCODE_PACKETBOX);
			writeC(MSG_PARTY2);
			writeD(tgpc.getId());
			writeS(tgpc.getName());

			final double nowhp = tgpc.getCurrentHp();
			final double maxhp = tgpc.getMaxHp();

			writeC((int) ((nowhp / maxhp) * 100D));
			writeD(tgpc.getMapId());
			writeH(tgpc.getX());
			writeH(tgpc.getY());

		} catch (final Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 成為新的隊長<BR>
	 * <font color=#00800> %s成為了新的隊長 </font>
	 * 
	 * @param
	 */
	public S_PacketBoxParty(final int objid, final String name) {
		try {
			writeC(Opcodes.S_OPCODE_PACKETBOX);
			writeC(MSG_LEADER);
			writeD(objid);
			writeS(name);

		} catch (final Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return getClass().getSimpleName();
	}
}
