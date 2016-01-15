package org.akigrafsoft.jdbckonnector;

import com.akigrafsoft.knetthreads.ExceptionAuditFailed;
import com.akigrafsoft.knetthreads.konnector.SessionBasedClientKonnectorConfiguration;

public class JdbcClientConfig extends SessionBasedClientKonnectorConfiguration {

	public String dbHostName;
	public int dbHostPort = -1;
	public String dbName;
	public String dbLogin;
	public String dbPassword;
	public int connectTimeoutMilliseconds = 3000;

	@Override
	public void audit() throws ExceptionAuditFailed {
		super.audit();

		if (numberOfSessions <= 0) {
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
			throw new ExceptionAuditFailed("connectTimeoutMilliseconds must be > 0");
		}
	}
}
