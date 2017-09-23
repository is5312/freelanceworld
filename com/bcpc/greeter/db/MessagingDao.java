package com.bcpc.greeter.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bcpc.greeter.MessageStoreBean;
import com.bcpc.greeter.exceptions.GreeterException.ErrorCode;
import com.google.inject.Inject;

public class MessagingDao extends BaseDao {

	private Logger log = LoggerFactory.getLogger(MessagingDao.class);
	private DBConnectionManager manager;

	@Inject
	MessagingDao(DBConnectionManager manager) {
		this.manager = manager;
	}

	public MessageStoreBean checkIfmessageInDb(String fromNumber) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		MessageStoreBean bean = null;

		try {
			conn = manager.createConnection();
			pst = conn.prepareStatement(Query.SELECT_MSG_SQL);

			pst.setString(1, fromNumber);

			rs = pst.executeQuery();

			if (rs.next()) {
				bean = new MessageStoreBean();
				bean.setpNumber(fromNumber);
				bean.setErrorFlag(rs.getString("IS_ERROR_FLAG"));
				bean.setErrorCd(rs.getInt("ERROR_CD"));
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			closeResultSet(rs);
			closeStatement(pst);
			closeConnection(conn);
		}

		return bean;
	}

	public boolean insertMessageToDb(String fromNumber, String fromName, String emailId, ErrorCode code) {
		Connection conn = null;
		PreparedStatement pst = null;
		boolean isSuccess = false;
		try {
			conn = manager.createConnection();
			pst = conn.prepareStatement(Query.INSERT_MSG_SQL);
			pst.setString(1, fromName);
			pst.setString(2, emailId);
			pst.setString(3, fromNumber);
			if (code != null) {
				pst.setString(4, "Y");
				pst.setString(5, code.toString());
				pst.setInt(6, code.getId());
			} else {
				pst.setNull(4, Types.VARCHAR);
				pst.setNull(5, Types.VARCHAR);
				pst.setNull(6, Types.NUMERIC);
			}
			pst.setTimestamp(7, new Timestamp(System.currentTimeMillis()));

			pst.execute();

			if (pst.getUpdateCount() == 1) {
				isSuccess = true;
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			closeStatement(pst);
			closeConnection(conn);
		}

		return isSuccess;
	}

	public boolean updateMessageToDb(String fromNumber, String fromName, String emailId, String errFlg) {
		Connection conn = null;
		PreparedStatement pst = null;
		boolean isSuccess = false;
		try {
			conn = manager.createConnection();
			pst = conn.prepareStatement(Query.UPDATE_MSG_SQL);
			pst.setString(1, fromName);
			pst.setString(2, emailId);

			if (errFlg == null) {
				pst.setNull(3, Types.VARCHAR);
			} else {
				pst.setString(3, errFlg);
			}
			pst.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
			pst.setString(5, fromNumber);

			pst.execute();

			if (pst.getUpdateCount() == 1) {
				isSuccess = true;
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			closeStatement(pst);
			closeConnection(conn);
		}

		return isSuccess;
	}

	static interface Query {
		public static final String UPDATE_MSG_SQL = "UPDATE MESSAGE_STORE set MSG_FROM_NAME = ? , MSG_FROM_EMAIL = ?, IS_ERROR_FLAG = ?, UPDATED_TS = ? WHERE MSG_FROM_NUM = ?";
		public static final String INSERT_MSG_SQL = "INSERT INTO MESSAGE_STORE (MSG_FROM_NAME,MSG_FROM_EMAIL,MSG_FROM_NUM,IS_ERROR_FLAG,ERROR_MSG,ERROR_CD,CREATED_TS) VALUES (?,?,?,?,?,?,?) ";
		public static final String SELECT_MSG_SQL = "SELECT count(*), MSG_FROM_NAME, IS_ERROR_FLAG, ERROR_CD from MESSAGE_STORE where MSG_FROM_NUM = ? group by MSG_FROM_NAME, IS_ERROR_FLAG";
	}
}
