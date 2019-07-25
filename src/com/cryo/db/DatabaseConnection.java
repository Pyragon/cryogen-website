package com.cryo.db;

import com.cryo.Website;
import com.cryo.entities.MySQLRead;
import com.cryo.utils.Logger;
import com.google.common.base.CaseFormat;
import com.mysql.jdbc.Statement;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
			if(connection == null) {
				System.out.println("The SQL server needs to be started.");
				System.exit(-1);
				return;
			}
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
	
	public void set(String database, String update, String clause, SQLQuery query_inter, Object...params) {
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
			try {
				setParams(stmt, params);
				stmt.execute();
			} finally {
				stmt.close();
			}
		} catch(Exception e) {
			
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
				else if (obj instanceof Boolean)
					stmt.setBoolean(index, (Boolean) obj);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Object[] select(String database, String condition, String orderClause, SQLQuery query, Object... values) {
		try {
			if(connection.isClosed() || !connection.isValid(5))
				connect();
			StringBuilder builder = new StringBuilder();
			builder.append("SELECT * FROM "+database);
			if(condition != null && !condition.equals(""))
				builder.append(" WHERE ").append(condition);
			if(orderClause != null && !orderClause.equals(""))
				builder.append(" "+orderClause);
			Object[] data = getResults(builder.toString(), query, values);
			return data;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Object[] select(String database, String condition, SQLQuery query, Object... values) {
		try {
			if(connection.isClosed() || !connection.isValid(5))
				connect();
			StringBuilder builder = new StringBuilder();
			builder.append("SELECT * FROM "+database);
			if(condition != null && !condition.equals(""))
				builder.append(" WHERE ").append(condition);
			Object[] data = getResults(builder.toString(), query, values);
			return data;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public <T> ArrayList<T> selectList(String table, String condition, Class<T> c, Object... values) {
		return selectList(table, condition, null, c, values);
	}

	public <T> ArrayList<T> selectList(String table, Class<T> c, Object... values) {
		return selectList(table, null, null, c, values);
	}

	public <T> ArrayList<T> selectList(String table, String condition, String order, Class<T> c, Object... values) {
		try {
			if (connection.isClosed() || !connection.isValid(5)) connect();
			StringBuilder builder = new StringBuilder();
			builder.append("SELECT * FROM " + table);
			if (condition != null && !condition.equals("")) builder.append(" WHERE ").append(condition);
			if (order != null && !order.equals("")) builder.append(" " + order);
			PreparedStatement stmt = connection.prepareStatement(builder.toString());
			setParams(stmt, values);
			ResultSet set = stmt.executeQuery();
			ArrayList<T> list = new ArrayList<>();
			if (wasNull(set)) return list;
			while (next(set))
				list.add(loadClass(set, c));
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public <T> T selectClass(String table, String condition, Class<T> c, Object... values) {
		return selectClass(table, condition, null, c, values);
	}

	public <T> T selectClass(String table, Class<T> c, Object... values) {
		return selectClass(table, null, null, c, values);
	}

	public <T> T selectClass(String table, String condition, String order, Class<T> c, Object... values) {
		try {
			if (connection.isClosed() || !connection.isValid(5)) connect();
			StringBuilder builder = new StringBuilder();
			builder.append("SELECT * FROM " + table);
			if (condition != null && !condition.equals("")) builder.append(" WHERE ").append(condition);
			if (order != null && !order.equals("")) builder.append(" " + order);
			PreparedStatement stmt = connection.prepareStatement(builder.toString());
			setParams(stmt, values);
			ResultSet set = stmt.executeQuery();
			if (empty(set)) return null;
			return loadClass(set, c);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public <T> T loadClass(ResultSet set, Class<T> c) {
		try {
			List<Class<?>> types = new ArrayList<>();
			List<Object> cValues = new ArrayList<>();
			for (Field field : c.getDeclaredFields()) {
				if (!Modifier.isFinal(field.getModifiers()) && !field.isAnnotationPresent(MySQLRead.class)) continue;
				types.add(field.getType());
				String name = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());
				if (field.isAnnotationPresent(MySQLRead.class)) {
					String value = field.getAnnotation(MySQLRead.class).value();
					if (!value.equals("null")) name = value;
				}
				switch (field.getType().getSimpleName().toLowerCase()) {
					case "int":
						cValues.add(getInt(set, name));
						break;
					case "string":
						cValues.add(getString(set, name));
						break;
					case "boolean":
						cValues.add(getBoolean(set, name));
						break;
					case "timestamp":
						cValues.add(getTimestamp(set, name));
						break;
					case "time":
						cValues.add(getTime(set, name));
						break;
					case "double":
						cValues.add(getDouble(set, name));
						break;
					case "long":
						cValues.add(getLongInt(set, name));
						break;
					case "date":
						cValues.add(getDate(set, name));
						break;
					default:
						System.out.println("Missing type: " + field.getType().getName().toLowerCase());
						break;
				}
			}
			Constructor<T> constructor = c.getConstructor(types.toArray(new Class<?>[types.size()]));
			T obj = constructor.newInstance(cValues.toArray());
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public int selectCount(String database, String condition, Object...values) {
		try {
			if(connection.isClosed() || !connection.isValid(5))
				connect();
			StringBuilder builder = new StringBuilder();
			builder.append("SELECT COUNT(*) FROM "+database);
			if(condition != null && !condition.equals(""))
				builder.append(" WHERE ").append(condition);
			PreparedStatement stmt = connection.prepareStatement(builder.toString());
			setParams(stmt, values);
			ResultSet set = stmt.executeQuery();
			if(!set.next()) return 0;
			int count = set.getInt(1);
			set.close();
			stmt.close();
			return count;
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public Object[] getResults(String builder, SQLQuery query, Object... values) {
		try {
			PreparedStatement stmt = null;
			ResultSet set = null;
			Object[] result;
			try {
				stmt = connection.prepareStatement(builder.toString());
				setParams(stmt, values);
				set = stmt.executeQuery();
				result = query.handleResult(set);
			} finally {
				if(stmt != null)
					stmt.close();
				if(set != null)
					set.close();
			}
			return result;
		} catch(Exception e) {
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
				if(obj == null) {
					insert.append("NULL");
					if (i != inserts - 1)
						insert.append(", ");
					continue;
				} else if (obj instanceof String) {
					String string = (String) obj;
					if (string.equals("DEFAULT") || string.equals("NULL")) {
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
				if(obj == null) continue;
				index++;
				if (obj instanceof String) {
					String string = (String) obj;
					if (string.equals("DEFAULT") || string.equals("NULL")) {
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
				else if (obj instanceof Boolean)
					stmt.setBoolean(index, (Boolean) obj);
			}
//			System.out.println(stmt);
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
			statement.close();
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

	public ResultSet executeQuery(String query, Object...values) {
		try {
			if (connection.isClosed() || !connection.isValid(5))
				connect();
			PreparedStatement statement = connection.prepareStatement(query);
			for(int i = 0; i < values.length; i++) {
				Object obj = values[i];
				int index = i+1;
				if (obj instanceof String)
					statement.setString(index, (String) obj);
				else if (obj instanceof Integer)
					statement.setInt(index, (int) obj);
				else if(obj instanceof Double)
					statement.setDouble(index, (double) obj);
				else if(obj instanceof Long)
					statement.setTimestamp(index, new Timestamp((long) obj));
			}
			ResultSet set = statement.executeQuery();
			if (set != null)
				return set;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public final SQLQuery GET_READ = (set) -> {
		return empty(set) ? null : new Object[] { getString(set, "read") };
	};
	
	public boolean containsRow(ResultSet set, String row) {
		try {
			ResultSetMetaData rsMetaData = set.getMetaData();
			int numberOfColumns = rsMetaData.getColumnCount();
	
			// get the column names; column indexes start from 1
			for (int i = 1; i < numberOfColumns + 1; i++) {
			    String columnName = rsMetaData.getColumnName(i);
			    // Get the name of the column's table name
			    if (row.equals(columnName))
			    	return true;
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
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

	public boolean getBoolean(ResultSet set, String string) {
		try {
			return set.getBoolean(string);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
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

	public Date getDate(ResultSet set, String string) {
		try {
			return set.getDate(string);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Time getTime(ResultSet set, String string) {
		try {
			return set.getTime(string);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
