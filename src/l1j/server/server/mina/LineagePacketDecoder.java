package l1j.server.server.mina;

import l1j.server.Config;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;


public class LineagePacketDecoder extends CumulativeProtocolDecoder{
	
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		LineageClient client = (LineageClient) session
				.getAttribute(LineageClient.CLIENT_KEY);
		if (!session.isClosing()&&client!=null) {
			if (in.remaining() < 4)//
			{
				return false;
			}
			// TGG
			in.mark();//标记当前位置，以便reset 
			byte hiByte = in.get();
			byte loByte = in.get();

			// TODO 伺服器綑綁
			if (Config.LOGINS_TO_AUTOENTICATION) {
				hiByte ^= client._xorByte;
				loByte ^= client._xorByte;
			}

			/*
			 * if (loByte < 0) { throw new RuntimeException(); }
			 */
			int dataLength = hiByte & 0xff;
			dataLength |= loByte << 8 & 0xff00;
			//int dataLength = (loByte * 256 + hiByte) - 2;
			dataLength -= 2;
			if (dataLength > 1440) {
				client.kick();
				return true;
			}
			
			if (dataLength <= 0) {
//				System.out.println("长度为负数或者0");
				client.kick();
				return true;
			}

			byte data[] = new byte[dataLength];
			if (in.remaining() < dataLength) {
				in.reset();
				return false;
			}
			in.get(data, 0, dataLength);
			// TGG
			if (Config.LOGINS_TO_AUTOENTICATION) {
				for (int i = 0; i < dataLength; i++) {
					data[i] = (byte) (data[i] ^ client._xorByte);
				}
			}
			// TGG
			data = client.getCipher().decrypt(data);
			out.write(data);
			return true;
		}
		return false;
		
	}

/*	@Override
	public void decode(IoSession session, IoBuffer buffer,
			ProtocolDecoderOutput out) {
		try {
			// ����ȭ �ؾ���
			LineageClient client = (LineageClient) session
					.getAttribute(LineageClient.CLIENT_KEY);
			// if(client!=null) client.encryptD(buffer);
			if (client != null) {
				int size = buffer.limit();
				if (size > 0 && size < 1448) {
//					if(!client.doAutoPacket()){
						byte[] data = new byte[size];
						buffer.get(data);
						if (Config.LOGINS_TO_AUTOENTICATION) {
							for (int i = 0; i < data.length; i++) {
								data[i] = (byte) (data[i] ^ client._xorByte);
							}
						}
						synchronized(client.PacketD){
							System.arraycopy(data, 0, client.PacketD, client.PacketIdx,size);
							client.PacketIdx += size;
						}					
						out.write(data);
//					}
				} else {
					// ������ ���ٷ� �����ع���.
					client.kick();
				}
			}
			client = null;
		} catch (Exception e) {
		}
	}*/

	public void dispose(IoSession client) throws Exception {

	}

	public void finishDecode(IoSession client, ProtocolDecoderOutput output)
			throws Exception {

	}

	// ��Ŷũ�� �� ����.
/*	private int PacketSize(byte[] data) {
		int length = data[0] & 0xff;
		length |= data[1] << 8 & 0xff00;
		return length;
	}*/

}
