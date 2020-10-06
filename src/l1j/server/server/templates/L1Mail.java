package l1j.server.server.templates;

import java.sql.Timestamp;

public class L1Mail {
	private int _id;
	private int _type;
	private String _senderName;
	private String _receiverName;
	private String _date = null;

	private Timestamp _dateTime = null;

	private int _readStatus = 0;

	private byte[] _subject = null;

	private byte[] _content = null;

	private boolean _reMail = false;

	public int getId() {
		return _id;
	}

	public void setId(int i) {
		_id = i;
	}

	public int getType() {
		return _type;
	}

	public void setType(int i) {
		_type = i;
	}

	public String getSenderName() {
		return _senderName;
	}

	public void setSenderName(String s) {
		_senderName = s;
	}

	public String getReceiverName() {
		return _receiverName;
	}

	public void setReceiverName(String s) {
		_receiverName = s;
	}

	public String getDate() {
		return _date;
	}

	public void setDate(String s) {
		_date = s;
	}

	public Timestamp getDateTime() {
		return _dateTime;
	}

	public void setDateTime(Timestamp t) {
		_dateTime = t;
	}

	public int getReadStatus() {
		return _readStatus;
	}

	public void setReadStatus(int i) {
		_readStatus = i;
	}

	public byte[] getSubject() {
		return _subject;
	}

	public void setSubject(byte[] arg) {
		byte[] newarg = new byte[arg.length - 2];
		System.arraycopy(arg, 0, newarg, 0, newarg.length);
		_subject = arg;
	}

	public byte[] getContent() {
		return _content;
	}

	public void setContent(byte[] arg) {
		_content = arg;
	}

	public boolean isReMail() {
		return _reMail;
	}

	public void setReMail(boolean flag) {
		_reMail = flag;
	}
}