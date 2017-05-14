package com.cryo.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

import com.cryo.Website;
import com.cryo.utils.Logger;
import com.mysql.jdbc.Statement;

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
			String ip = prop.getProperty("db-host");
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

	public void set(String database, String update, String clause, Object... params) {
		if(params == null || params.length == 0) {
			String query = "UPDATE " + database + " SET " + update + " WHERE " + clause + ";";
			execute(query);
			return;
		}
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("UPDATE ").append(database).append(" SET ")
					.append(update).append(" WHERE ").append(clause+";");
			PreparedStatement stmt = connection.prepareStatement(builder.toString());
			setParams(stmt, params);
			stmt.execute();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setParams(PreparedStatement stmt, Object[] params) {
		try {
			int index = 0;
			for (int i = 0; i < params.length; i++) {
				Object obj = params[i];
				index++;
				if (obj instanceof String) {
					String string = (String) obj;
					if (string.equals("DEFAULT")) {
						index--;
						continue;
					}
					stmt.setString(index, (String) obj);
				} else if (obj instanceof Integer)
					stmt.setInt(index, (int) obj);
				else if(obj instanceof Double)
					stmt.setDouble(index, (double) obj);
				else if(obj instanceof Long)
					stmt.setTimestamp(index, new Timestamp((long) obj));
				else if(obj instanceof Timestamp)
					stmt.setTimestamp(index, (Timestamp) obj);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ResultSet select(String database, String condition, Object... values) {
		try {
			if (connection.isClosed() || !connection.isValid(5))
				connect();
			StringBuilder builder = new StringBuilder();
			builder.append("SELECT * FROM "+database);
			if(values.length == 0) {
				if(condition != null && !condition.equals("")) {
					builder.append(" WHERE ");
					builder.append(condition);
				}
				PreparedStatement stmt = connection.prepareStatement(builder.toString());
				return stmt.executeQuery();
			}
			builder.append(" WHERE ");
			builder.append(condition);
			PreparedStatement stmt = connection.prepareStatement(builder.toString());
			for(int i = 0; i < values.length; i++) {
				Object obj = values[i];
				int index = i+1;
				if (obj instanceof String)
					stmt.setString(index, (String) obj);
				else if (obj instanceof Integer)
					stmt.setInt(index, (int) obj);
				else if(obj instanceof Double)
					stmt.setDouble(index, (double) obj);
				else if(obj instanceof Long)
					stmt.setTimestamp(index, new Timestamp((long) obj));
			}
			return stmt.executeQuery();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ResultSet selectCount(String database, String condition, Object... values) {
		try {
			if (connection.isClosed() || !connection.isValid(5))
				connect();
			StringBuilder builder = new StringBuilder();
			builder.append("SELECT COUNT(*) FROM "+database);
			if(values.length == 0) {
				if(condition != null && !condition.equals("")) {
					builder.append(" WHERE ");
					builder.append(condition);
				}
				PreparedStatement stmt = connection.prepareStatement(builder.toString());
				return stmt.executeQuery();
			}
			builder.append(" WHERE ");
			builder.append(condition);
			PreparedStatement stmt = connection.prepareStatement(builder.toString());
			for(int i = 0; i < values.length; i++) {
				Object obj = values[i];
				int index = i+1;
				if (obj instanceof String)
					stmt.setString(index, (String) obj);
				else if (obj instanceof Integer)
					stmt.setInt(index, (int) obj);
				else if(obj instanceof Double)
					stmt.setDouble(index, (double) obj);
				else if(obj instanceof Long)
					stmt.setTimestamp(index, new Timestamp((long) obj));
				else if(obj instanceof Timestamp)
					stmt.setTimestamp(index, (Timestamp) obj);
			}
			return stmt.executeQuery();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int insert(String database, Object... objects) {
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
			PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			int index = 0;
			for (int i = 0; i < inserts; i++) {
				Object obj = objs[i];
				index++;
				if (obj instanceof String) {
					String string = (String) obj;
					if (string.equals("DEFAULT")) {
						index--;
						continue;
					}
					stmt.setString(index, (String) obj);
				} else if (obj instanceof Integer)
					stmt.setInt(index, (int) obj);
				else if(obj instanceof Double)
					stmt.setDouble(index, (double) obj);
				else if(obj instanceof Long)
					stmt.setTimestamp(index, new Timestamp((long) obj));
				else if(obj instanceof Timestamp)
					stmt.setTimestamp(index, (Timestamp) obj);
			}
			stmt.execute();
			ResultSet set = stmt.getGeneratedKeys();
			if(set.next())
				return set.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public ResultSet selectAll(String database, String condition) {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT * FROM ").append(database).append(" "+condition);
		return executeQuery(builder.toString());
	}
	
	public void delete(String database, String condition, Object...values) {
		StringBuilder builder = new StringBuilder();
		builder.append("DELETE FROM ").append(database).append(" WHERE ").append(condition);
		try {
			PreparedStatement stmt = connection.prepareStatement(builder.toString());
			for(int i = 0; i < values.length; i++) {
				Object obj = values[i];
				int index = i+1;
				if (obj instanceof String)
					stmt.setString(index, (String) obj);
				else if (obj instanceof Integer)
					stmt.setInt(index, (int) obj);
				else if(obj instanceof Double)
					stmt.setDouble(index, (double) obj);
				else if(obj instanceof Long)
					stmt.setTimestamp(index, new Timestamp((long) obj));
			}
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
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
	}
	
	public void execute(String query, Object... values) {
		try {
			if (connection.isClosed() || !connection.isValid(5))
				connect();
			PreparedStatement stmt = connection.prepareStatement(query);
			for(int i = 0; i < values.length; i++) {
				Object obj = values[i];
				int index = i+1;
				if (obj instanceof String)
					stmt.setString(index, (String) obj);
				else if (obj instanceof Integer)
					stmt.setInt(index, (int) obj);
				else if(obj instanceof Double)
					stmt.setDouble(index, (double) obj);
				else if(obj instanceof Long)
					stmt.setTimestamp(index, new Timestamp((long) obj));
			}
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
	
	public int getFetchSize(ResultSet set) {
		try {
			return set.getFetchSize();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
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

	public int getInt(ResultSet set, int index) {
		try {
			return set.getInt(index);
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

	public Timestamp getTimestamp(ResultSet set, String string) {
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
	
	public int getRow(ResultSet set) {
		try {
			return set.getRow();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public boolean last(ResultSet set) {
		try {
			return set.last();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
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
		if(set == null)
			return true;
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
