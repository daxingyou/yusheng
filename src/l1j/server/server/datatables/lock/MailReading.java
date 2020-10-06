package l1j.server.server.datatables.lock;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import l1j.server.server.datatables.MailTable;
import l1j.server.server.datatables.storage.MailStorage;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.L1Mail;

public class MailReading {
	private final Lock _lock;
	private final MailStorage _storage;
	private static MailReading _instance;

	private MailReading() {
		_lock = new ReentrantLock(true);
		_storage = new MailTable();
	}

	public static MailReading get() {
		if (_instance == null) {
			_instance = new MailReading();
		}
		return _instance;
	}

	public void load() {
		_lock.lock();
		try {
			_storage.load();
		} finally {
			_lock.unlock();
		}
	}

	public void setReadStatus(int mailId) {
		_lock.lock();
		try {
			_storage.setReadStatus(mailId);
		} finally {
			_lock.unlock();
		}
	}

	public void setMailType(int mailId, int type) {
		_lock.lock();
		try {
			_storage.setMailType(mailId, type);
		} finally {
			_lock.unlock();
		}
	}

	public void deleteMail(int mailId) {
		_lock.lock();
		try {
			_storage.deleteMail(mailId);
		} finally {
			_lock.unlock();
		}
	}

	public void writeMail(L1Mail mail, int type, String receiver,
			L1PcInstance writer, byte[] text, int isReMail) {
		_lock.lock();
		try {
			_storage.writeMail(mail, type, receiver, writer, text, isReMail);
		} finally {
			_lock.unlock();
		}
	}

	public Map<Integer, L1Mail> getAllMail() {
		_lock.lock();
		Map<Integer, L1Mail> tmp;
		try {
			tmp = _storage.getAllMail();
		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	public L1Mail getMail(int mailId) {
		_lock.lock();
		L1Mail tmp;
		try {
			tmp = _storage.getMail(mailId);
		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	public ArrayList<L1Mail> getMails(String receiverName) {
		ArrayList<L1Mail> mailList = new ArrayList<L1Mail>();
		for (L1Mail mail : getAllMail().values()) {
			if ((mail.getReceiverName().equalsIgnoreCase(receiverName))
					&& (mail.getType() == 0) && (!mail.isReMail())) {
				mailList.add(mail);
			}

			if ((mail.getSenderName().equalsIgnoreCase(receiverName))
					&& (mail.getType() == 0) && (mail.isReMail())) {
				mailList.add(mail);
			}
		}

		return mailList;
	}

	public ArrayList<L1Mail> getPMails(String receiverName) {
		ArrayList<L1Mail> mailList = new ArrayList<L1Mail>();
		for (L1Mail mail : getAllMail().values()) {
			if ((mail.getReceiverName().equalsIgnoreCase(receiverName))
					&& (mail.getType() == 1) && (!mail.isReMail())) {
				mailList.add(mail);
			}
		}

		return mailList;
	}

	public ArrayList<L1Mail> getKMails(String receiverName) {
		ArrayList<L1Mail> mailList = new ArrayList<L1Mail>();
		for (L1Mail mail : getAllMail().values()) {
			if ((mail.getReceiverName().equalsIgnoreCase(receiverName))
					&& (mail.getType() == 2) && (!mail.isReMail())) {
				mailList.add(mail);
			}

			if ((mail.getSenderName().equalsIgnoreCase(receiverName))
					&& (mail.getType() == 2) && (mail.isReMail())) {
				mailList.add(mail);
			}
		}

		return mailList;
	}

	public int getMailSizeByReceiver(String receiverName, int type) {
		ArrayList<L1Mail> mailList = new ArrayList<L1Mail>();
		for (L1Mail mail : getAllMail().values()) {
			if ((mail.getReceiverName().equalsIgnoreCase(receiverName))
					&& (mail.getType() == type) && (!mail.isReMail())) {
				mailList.add(mail);
			}
		}

		if (mailList.size() > 0) {
			return mailList.size();
		}
		return 0;
	}
}