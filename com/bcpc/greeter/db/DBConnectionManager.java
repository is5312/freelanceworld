package com.bcpc.greeter.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bcpc.greeter.PropertyManager;
import com.google.inject.Singleton;

@Singleton
public class DBConnectionManager {

	private static final Logger log = LoggerFactory.getLogger(DBConnectionManager.class);

	private PropertyManager props;

	@Inject
	DBConnectionManager(PropertyManager props) {
		this.props = props;
	}

	public Connection createConnection() {
		
		Connection con = null;
		
		String url = props.getStringProp("dburl");
		String dbport = props.getStringProp("dbport");
		String dbname = props.getStringProp("dbname");

		String connectionString = String.format("jdbc:mysql://%s:%s/%s", url, dbport, dbname);
		log.info(String.format("creating connection using %s",connectionString));
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(connectionString, props.getStringProp("dbuname"),
					props.getStringProp("dbpwd"));
		} catch (ClassNotFoundException e) {
			log.error(e.getMessage(), e);
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
		
		return con;
	}
}
