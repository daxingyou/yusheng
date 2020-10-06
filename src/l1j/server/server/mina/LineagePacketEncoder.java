package l1j.server.server.mina;

import l1j.server.server.serverpackets.S_Key;
import l1j.server.server.serverpackets.ServerBasePacket;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class LineagePacketEncoder implements ProtocolEncoder {

	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		try {
			if (session.isClosing()) {
				return;
			}
			if (message != null) {
				LineageClient client = (LineageClient) session
						.getAttribute(LineageClient.CLIENT_KEY);
				if (client!=null) {
					IoBuffer buffer = null;
					if (message instanceof ServerBasePacket) {
						ServerBasePacket bp = (ServerBasePacket) message;
						if (!(bp instanceof S_Key)) {
							if (client.getLanguage() == 3){
								buffer = buffer(client.getCipher().encrypt(bp.getBIG5Bytes()),bp.getLengthBIG5());
							}else{
								buffer = buffer(client.getCipher().encrypt(bp.getBytes()),bp.getLength());
							}
						} else {
							buffer = buffer(bp.getBytes(false), bp.getLength());
						}
						out.write(buffer);
					} else {
						out.write(message);
					}
				}else {
					out.write(message);
				}			
			}
		} catch (Exception e) {
				// Logger.getInstance().error(getClass().toString()+" putClient(LineageClient c)\r\n"+e.toString(),
				// Config.LOG.error);
		}
	}

	public void dispose(IoSession client) throws Exception {

	}

	private IoBuffer buffer(byte[] data, int length) {
		byte[] size = new byte[2];
		size[0] |= length & 0xff;
		size[1] |= length >> 8 & 0xff;
		IoBuffer buffer = IoBuffer.allocate(length, false);
		buffer.put(size);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
}
