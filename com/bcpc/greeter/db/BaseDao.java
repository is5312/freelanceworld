package com.bcpc.greeter.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseDao {

	private Logger log = LoggerFactory.getLogger(BaseDao.class);

	public void closeConnection(Connection con) {
		try {
			if (con != null) {
				con.close();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public void closeStatement(Statement st) {
		try {
			if (st != null) {
				st.close();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public void closeResultSet(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
