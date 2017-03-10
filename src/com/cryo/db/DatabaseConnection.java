package com.cryo.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

import com.cryo.Website;
import com.cryo.utils.Logger;

public abstract class DatabaseConnection {

	private Connection connection;

	private final String database;

	public DatabaseConnection(String database) {
		this.database = database;
		connect();
		ping();
	}

	public void connect() {
		try {
			Properties prop = Website.getProperties();
			String user = prop.getProperty("db-user");
			String pass = prop.getProperty("db-pass");
			String ip = "localhost";
			connection = DriverManager.getConnection("jdbc:mysql://" + ip + "/" + database,
					user, pass);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public abstract Object[] handleRequest(Object... data);

	public void ping() {
		try {
			if (connection.isClosed())
				return;
			long start = System.currentTimeMillis();
			connection.createStatement().execute("/* ping */ SELECT 1");
			Logger.log(this.getClass(),
					"Connection to " + database + " database took " + (System.currentTimeMillis() - start) + "ms");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void set(String database, String update, String clause) {
		String query = "UPDATE " + database + " SET " + update + " WHERE " + clause + ";";
		execute(query);
	}

	public void insert(String database, Object... objects) {
		try {
			if (connection.isClosed() || !connection.isValid(5))
				connect();
			int inserts = objects.length;
			Object[] objs = objects;
			StringBuilder insert = new StringBuilder();
			for (int i = 0; i < inserts; i++) {
				Object obj = objs[i];
				if (obj instanceof String) {
					String string = (String) obj;
					if (string.equals("DEFAULT")) {
						insert.append(string);
						if (i != inserts - 1)
							insert.append(", ");
						continue;
					}
				}
				insert.append("?");
				if (i != inserts - 1)
					insert.append(", ");
			}
			String query = "INSERT INTO `" + database + "` VALUES(" + insert.toString() + ")";
			PreparedStatement stmt = connection.prepareStatement(query);
			for (int i = 0; i < inserts; i++) {
				Object obj = objs[i];
				int index = i + 1;
				if (obj instanceof String) {
					String string = (String) obj;
					if (string.equals("DEFAULT")) {
						continue;
					}
					stmt.setString(index, (String) obj);
				} else if (obj instanceof Integer)
					stmt.setInt(index, (int) obj);
				else if(obj instanceof Double)
					stmt.setDouble(index, (double) obj);
			}
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ResultSet selectAll(String database, String condition) {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT * FROM ").append(database).append(" "+condition);
		return executeQuery(builder.toString());
	}

	public ResultSet select(String database, String condition) {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT * FROM ").append(database).append(condition != null ? " WHERE " : "")
				.append(condition != null ? condition : "");
		return executeQuery(builder.toString());
	}

	public void delete(String database, String condition) {
		StringBuilder builder = new StringBuilder();
		builder.append("DELETE FROM ").append(database).append(condition != null ? " WHERE " : "")
				.append(condition != null ? condition : "");
		execute(builder.toString());
	}

	public void execute(String query) {
		try {
			if (connection.isClosed() || !connection.isValid(5))
				connect();
			PreparedStatement statement = connection.prepareStatement(query);
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return;
	}

	public ResultSet executeQuery(String query) {
		try {
			if (connection.isClosed() || !connection.isValid(5))
				connect();
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet set = statement.executeQuery();
			if (set != null)
				return set;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public long getLongInt(ResultSet set, String string) {
		try {
			return set.getLong(string);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public int getInt(ResultSet set, String string) {
		try {
			return set.getInt(string);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public double getDouble(ResultSet set, String string) {
		try {
			return set.getDouble(string);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public Date getTimeStamp(ResultSet set, String string) {
		try {
			return set.getTimestamp(string);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getString(ResultSet set, String string) {
		try {
			return set.getString(string);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean next(ResultSet set) {
		try {
			return set.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean wasNull(ResultSet set) {
		try {
			return set.wasNull();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean empty(ResultSet set) {
		return set == null || wasNull(set) || !next(set);
	}

}
