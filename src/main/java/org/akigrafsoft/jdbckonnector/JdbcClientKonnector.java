package org.akigrafsoft.jdbckonnector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import com.akigrafsoft.knetthreads.ExceptionDuplicate;
import com.akigrafsoft.knetthreads.konnector.ExceptionCreateSessionFailed;
import com.akigrafsoft.knetthreads.konnector.KonnectorConfiguration;
import com.akigrafsoft.knetthreads.konnector.KonnectorDataobject;
import com.akigrafsoft.knetthreads.konnector.SessionBasedClientKonnector;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * A session based JDBC client Konnector
 * 
 * @author kmoyse
 * 
 */
public class JdbcClientKonnector extends SessionBasedClientKonnector {

	String m_dbLogin;
	String m_dbPassword;

	MysqlDataSource m_ds;

	public JdbcClientKonnector(String name) throws ExceptionDuplicate {
		super(name);
	}
	
	@Override
	public Class<? extends KonnectorConfiguration> getConfigurationClass() {
		return JdbcClientConfig.class;
	}

	@Override
	protected void doLoadConfig(KonnectorConfiguration config) {
		super.doLoadConfig(config);
		JdbcClientConfig l_config = (JdbcClientConfig) config;

		m_ds = new MysqlDataSource();
		m_ds.setServerName(l_config.getDbHostName());
		m_ds.setPort(l_config.getDbHostPort());
		m_ds.setPortNumber(l_config.getDbHostPort());
		try {
			m_ds.setConnectTimeout(l_config.getConnectTimeoutMilliseconds());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		m_ds.setDatabaseName(l_config.getDbName());
		// m_ds.setDescription("JdbcClientKonnector");

		m_dbLogin = l_config.getDbLogin();
		m_dbPassword = l_config.getDbPassword();
	}

	@Override
	protected void createSession(Session session)
			throws ExceptionCreateSessionFailed {
		Connection l_con;
		try {
			l_con = m_ds.getConnection(m_dbLogin, m_dbPassword);
			l_con.setAutoCommit(false);
			session.setUserObject(l_con);
		} catch (SQLException e) {
			throw new ExceptionCreateSessionFailed(e.getMessage());
		}
	}

	@Override
	public void async_startSession(Session session) {
		this.sessionStarted(session);
	}

	@Override
	protected void execute(KonnectorDataobject dataobject, Session session) {
		JdbcClientDataobject l_dataobject = (JdbcClientDataobject) dataobject;
		Connection l_con = (Connection) session.getUserObject();
		Statement l_stmt;
		try {
			l_stmt = l_con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			this.notifyNetworkError(l_dataobject, session, e.getMessage());
			return;
		}

		switch (l_dataobject.statementKind) {
		case Query:
			ResultSet l_rs;
			try {
				l_rs = l_stmt.executeQuery(l_dataobject.outboundBuffer);
				l_dataobject.queryResults = new ArrayList<HashMap<String, Object>>();
				int i = 0;
				while (l_rs.next()) {
					HashMap<String, Object> l_map = new HashMap<String, Object>();
					for (String columnLabel : l_dataobject.columnLabels) {
						l_map.put(columnLabel, l_rs.getObject(columnLabel));
					}
					l_dataobject.queryResults.add(i, l_map);
					i += 1;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				this.notifyNetworkError(l_dataobject, session, e.getMessage());
				try {
					l_stmt.close();
				} catch (SQLException ex) {
				}
				dieConnection(session);
				return;
			}
			break;
		case Update:
			try {
				int l_result = l_stmt
						.executeUpdate(l_dataobject.outboundBuffer);
				l_dataobject.updateResult = l_result;
				l_con.commit();
			} catch (SQLTimeoutException ex_timeout) {
				try {
					l_stmt.close();
				} catch (SQLException ex) {
				}
				this.notifyNetworkError(l_dataobject, session,
						ex_timeout.getMessage());
				dieConnection(session);
				return;
			} catch (SQLException e) {
				try {
					l_stmt.close();
				} catch (SQLException ex) {
				}
				e.printStackTrace();
				this.notifyFunctionalError(l_dataobject, e.getMessage());
				// seems the connection must be killed after
				// such error because it seems not valid anymore
				dieConnection(session);
				return;
			}
			break;
		default:
			break;
		}

		try {
			l_stmt.close();
		} catch (SQLException e) {
		}

		this.notifyExecuteCompleted(l_dataobject);
	}

	private void dieConnection(Session session) {
		Connection l_con = (Connection) session.getUserObject();
		try {
			l_con.close();
		} catch (SQLException e) {
		}
		this.sessionDied(session);
	}

	@Override
	protected void async_stopSession(Session session) {
		Connection l_con = (Connection) session.getUserObject();
		try {
			l_con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.sessionStopped(session);
		}
	}

}
