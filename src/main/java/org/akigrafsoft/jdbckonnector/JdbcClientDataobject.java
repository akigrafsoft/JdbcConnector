package org.akigrafsoft.jdbckonnector;

import java.util.ArrayList;
import java.util.HashMap;

import com.akigrafsoft.knetthreads.Message;
import com.akigrafsoft.knetthreads.konnector.KonnectorDataobject;

public class JdbcClientDataobject extends KonnectorDataobject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5681708635811549024L;

	public enum StatementKind {
		Query, Update
	}

	public StatementKind statementKind;

	public ArrayList<String> columnLabels;

	public ArrayList<HashMap<String, Object>> queryResults;
	public int updateResult;

	public JdbcClientDataobject(Message message) {
		super(message);
	}
}
