package org.akigrafsoft.jdbckonnector;

import java.io.Serializable;

import com.akigrafsoft.knetthreads.ExceptionAuditFailed;
import com.akigrafsoft.knetthreads.konnector.SessionBasedClientKonnectorConfiguration;

/**
 * Configuration class for {@link JdbcClientKonnector}
 * <p>
 * <b>This is a Java bean and all extension classes MUST be Java beans.</b>
 * </p>
 * 
 * @author kmoyse
 * 
 */
public class JdbcClientConfig extends SessionBasedClientKonnectorConfiguration
		implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7745676981069718041L;

	private String dbHostName;
	private int dbHostPort = -1;
	private String dbName;
	private String dbLogin;
	private String dbPassword;
	private int connectTimeoutMilliseconds = 3000;

	// ------------------------------------------------------------------------
	// Java Bean

	public String getDbHostName() {
		return dbHostName;
	}

	public void setDbHostName(String dbHostName) {
		this.dbHostName = dbHostName;
	}

	public int getDbHostPort() {
		return dbHostPort;
	}

	public void setDbHostPort(int dbHostPort) {
		this.dbHostPort = dbHostPort;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbLogin() {
		return dbLogin;
	}

	public void setDbLogin(String dbLogin) {
		this.dbLogin = dbLogin;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public int getConnectTimeoutMilliseconds() {
		return connectTimeoutMilliseconds;
	}

	public void setConnectTimeoutMilliseconds(int connectTimeoutMilliseconds) {
		this.connectTimeoutMilliseconds = connectTimeoutMilliseconds;
	}

	// ------------------------------------------------------------------------
	// Configuration

	@Override
	public void audit() throws ExceptionAuditFailed {
		super.audit();

		if (this.getNumberOfSessions() <= 0) {
			throw new ExceptionAuditFailed("numberOfSessions must be > 0");
		}

		if (dbHostName == null || dbHostName.equals("")) {
			throw new ExceptionAuditFailed("dbHostName must be configured");
		}
		if (dbHostPort <= 0) {
			throw new ExceptionAuditFailed("dbHostPort must be configured");
		}
		if (dbName == null || dbName.equals("")) {
			throw new ExceptionAuditFailed("dbName must be configured");
		}
		if (connectTimeoutMilliseconds <= 0) {
			throw new ExceptionAuditFailed(
					"connectTimeoutMilliseconds must be > 0");
		}
	}
}
