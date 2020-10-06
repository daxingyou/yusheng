package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.datatables.storage.MailStorage;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.L1Mail;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;


public class MailTable implements MailStorage {
	private static final Log _log = LogFactory.getLog(MailTable.class);

	private static final Map<Integer, L1Mail> _allMail = new HashMap<Integer, L1Mail>();

	@Override
	public void load() {
		PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `mail`");
			rs = pstm.executeQuery();
			while (rs.next()) {
				L1Mail mail = new L1Mail();
				int id = rs.getInt("id");
				mail.setId(id);
				mail.setType(rs.getInt("type"));
				String sender = rs.getString("sender");
				mail.setSenderName(sender);

				String receiver = rs.getString("receiver");
				mail.setReceiverName(receiver);
				mail.setDateTime(rs.getTimestamp("date"));
				mail.setReadStatus(rs.getInt("read_status"));
				mail.setSubject(rs.getBytes("subject"));
				mail.setContent(rs.getBytes("content"));
				int isReMail = rs.getInt("Is_ReMail");
				mail.setReMail(isReMail != 0);
				L1PcInstance restorePc = CharacterTable.getInstance()
						.restoreCharacter(receiver);
				if (restorePc != null) {
					_allMail.put(Integer.valueOf(id), mail);
				}
				else 
				{
					deleteMail(receiver);
				}		
/*				if (CharacterTable.getInstance().isChar(objid)) {
					
				}

				if (CharObjidTable.get().charObjid(receiver) != 0)
					_allMail.put(Integer.valueOf(id), mail);
				else
					deleteMail(receiver);*/
			}
		} catch (Exception  e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入信件資料數量: " + _allMail.size() + "(" + timer.get() + "ms)");
	}

	private void deleteMail(String receiver) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("DELETE FROM `mail` WHERE `receiver`=?");
			pstm.setString(1, receiver);
			pstm.execute();
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	@Override
	public void setReadStatus(int mailId) {
		L1Mail mail = (L1Mail) _allMail.get(Integer.valueOf(mailId));
		if (mail != null) {
			mail.setReadStatus(1);

			Connection con = null;
			PreparedStatement pstm = null;
			ResultSet rs = null;
			try {
				con = L1DatabaseFactory.getInstance().getConnection();
				rs = con.createStatement().executeQuery(
						"SELECT * FROM `mail` WHERE `id`=" + mailId);
				if ((rs != null) && (rs.next())) {
					pstm = con
							.prepareStatement("UPDATE `mail` SET `read_status`=? WHERE `id`="
									+ mailId);
					pstm.setInt(1, 1);
					pstm.execute();
				}
			} catch (SQLException e) {
				_log.error(e.getLocalizedMessage(), e);
			} finally {
				SQLUtil.close(rs);
				SQLUtil.close(pstm);
				SQLUtil.close(con);
			}
		}
	}

	@Override
	public void setMailType(int mailId, int type) {
		L1Mail mail = (L1Mail) _allMail.get(Integer.valueOf(mailId));
		if (mail != null) {
			mail.setType(type);

			Connection con = null;
			PreparedStatement pstm = null;
			ResultSet rs = null;
			try {
				con = L1DatabaseFactory.getInstance().getConnection();
				rs = con.createStatement().executeQuery(
						"SELECT * FROM `mail` WHERE `id`=" + mailId);
				if ((rs != null) && (rs.next())) {
					pstm = con
							.prepareStatement("UPDATE `mail` SET `type`=? WHERE `id`="
									+ mailId);
					pstm.setInt(1, type);
					pstm.execute();
				}
			} catch (SQLException e) {
				_log.error(e.getLocalizedMessage(), e);
			} finally {
				SQLUtil.close(rs);
				SQLUtil.close(pstm);
				SQLUtil.close(con);
			}
		}
	}

	@Override
	public void deleteMail(int mailId) {
		L1Mail mail = (L1Mail) _allMail.remove(Integer.valueOf(mailId));
		if (mail != null) {
			Connection con = null;
			PreparedStatement pstm = null;
			try {
				con = L1DatabaseFactory.getInstance().getConnection();
				pstm = con.prepareStatement("DELETE FROM `mail` WHERE `id`=?");
				pstm.setInt(1, mailId);
				pstm.execute();
			} catch (SQLException e) {
				_log.error(e.getLocalizedMessage(), e);
			} finally {
				SQLUtil.close(pstm);
				SQLUtil.close(con);
			}
		}
	}

	@Override
	public void writeMail(L1Mail mail, int type, String receiver,
			L1PcInstance writer, byte[] text, int isReMail) {
		Timestamp date = new Timestamp(System.currentTimeMillis());

		int spacePosition1 = 0;
		int spacePosition2 = 0;
		for (int i = 0; i < text.length; i += 2) {
			if ((text[i] == 0) && (text[(i + 1)] == 0)) {
				if (spacePosition1 == 0) {
					spacePosition1 = i;
				} else if ((spacePosition1 != 0) && (spacePosition2 == 0)) {
					spacePosition2 = i;
					break;
				}
			}

		}

		int subjectLength = spacePosition1 + 2;
		int contentLength = spacePosition2 - spacePosition1;
		if (contentLength <= 0) {
			contentLength = 1;
		}
		byte[] subject = new byte[subjectLength];
		byte[] content = new byte[contentLength];
		System.arraycopy(text, 0, subject, 0, subjectLength);
		System.arraycopy(text, subjectLength, content, 0, contentLength);

		Connection con = null;
		PreparedStatement pstm2 = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm2 = con
					.prepareStatement("INSERT INTO `mail` SET `id`=?,`type`=?,`sender`=?,`receiver`=?,`date`=?,`read_status`=?,`subject`=?,`content`=?,`Is_ReMail`=?");

			int id = mail.getId();
			pstm2.setInt(1, id);
			pstm2.setInt(2, type);
			pstm2.setString(3, writer.getName());
			pstm2.setString(4, receiver);
			pstm2.setTimestamp(5, date);
			pstm2.setInt(6, 0);
			pstm2.setBytes(7, subject);
			pstm2.setBytes(8, content);
			pstm2.setInt(9, isReMail);
			pstm2.execute();

			mail.setId(id);
			mail.setType(type);
			mail.setSenderName(writer.getName());
			mail.setReceiverName(receiver);
			mail.setDateTime(date);
			mail.setSubject(subject);
			mail.setContent(content);
			mail.setReadStatus(0);
			mail.setReMail(isReMail != 0);
			_allMail.put(Integer.valueOf(id), mail);
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm2);
			SQLUtil.close(con);
		}
	}

	@Override
	public Map<Integer, L1Mail> getAllMail() {
		return _allMail;
	}

	@Override
	public L1Mail getMail(int mailId) {
		return (L1Mail) _allMail.get(Integer.valueOf(mailId));
	}
}