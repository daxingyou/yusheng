package l1j.server.server.mina;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import l1j.server.server.serverpackets.ServerBasePacket;

public class LineageSend implements Runnable{
	
	private final Queue<ServerBasePacket> _queue; // 隊列
	
	private final LineageClient _client;
	
	public LineageSend(final LineageClient client)
	{
		this._client = client;
		this._queue = new ConcurrentLinkedQueue<ServerBasePacket>();
	}
	
	public void requestWork(final ServerBasePacket packet) {
		if (packet != null) {
			this._queue.offer(packet);
		}
	}

	@Override
	public void run() {
		while (!this._client.isClosed()) {
			final ServerBasePacket packet = this._queue.poll();
			// 取得要處理的封包
			if (packet != null) {
				try {
					this._client.sendPacket(packet);
				} catch (final Exception e) {
					// LOG.error(e.getLocalizedMessage(), e);
				}
			} else {
				try {
					Thread.sleep(10);
				} catch (final Exception e) {
					// LOG.error(e.getLocalizedMessage(), e);
				}
			}
		}
		this._queue.clear(); // 移除此 collection 中的所有元素
		
	}

}
