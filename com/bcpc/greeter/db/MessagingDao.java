package com.bcpc.greeter.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.inject.Inject;

public class MessagingDao extends BaseDao {

	private Logger log = LoggerFactory.getLogger(MessagingDao.class);
	private DBConnectionManager manager;

	@Inject
	MessagingDao(DBConnectionManager manager) {
		this.manager = manager;
	}

	public boolean checkIfmessageInDb(String fromNumber) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		boolean isPresent = false;

		try {
			conn = manager.createConnection();
			pst = conn.prepareStatement(Query.SELECT_MSG_SQL);
			
			pst.setString(1, fromNumber);
			
			rs = pst.executeQuery();
			
			if(rs.next())
			{
				int count = rs.getInt(1);
				if(count > 0)
				{
					isPresent = true;
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}finally {
			closeResultSet(rs);
			closeStatement(pst);
			closeConnection(conn);
		}
		
		return isPresent;
	}

	public boolean insertMessageToDb(String fromNumber, String fromName, String emailId) {
		Connection conn = null;
		PreparedStatement pst = null;
		boolean isSuccess = false;
		try {
			conn = manager.createConnection();
			pst = conn.prepareStatement(Query.INSERT_MSG_SQL);
			pst.setString(1, fromName);
			pst.setString(2, emailId);
			pst.setString(3, fromNumber);
			pst.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

			pst.execute();
			
			if(pst.getUpdateCount() ==  1)
			{
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
		public static final String INSERT_MSG_SQL = "INSERT INTO MESSAGE_STORE (MSG_FROM,MSG_EMAIL,MSG_FROM_NUM,CREATED_TS) VALUES (?,?,?,?) ";
		public static final String SELECT_MSG_SQL = "SELECT count(*), MSG_FROM from MESSAGE_STORE where MSG_FROM_NUM = ?";
	}
}
