package l1j.server.server.datatables.storage;

import java.util.Map;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.L1Mail;

public abstract interface MailStorage {
	public abstract void load();

	public abstract void setReadStatus(int paramInt);

	public abstract void setMailType(int paramInt1, int paramInt2);

	public abstract void deleteMail(int paramInt);

	public abstract void writeMail(L1Mail paramL1Mail, int paramInt1,
			String paramString, L1PcInstance paramL1PcInstance,
			byte[] paramArrayOfByte, int paramInt2);

	public abstract Map<Integer, L1Mail> getAllMail();

	public abstract L1Mail getMail(int paramInt);
}