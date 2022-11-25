package DupFinder.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//Singleton class
public class DbExecutor {

  private static DbExecutor instance;
  private Connection connection;

  private static final String readMeta = "SELECT * FROM file_meta WHERE filename = ?";
  private static final String insertMeta = "INSERT INTO file_meta (filename, md5, last_modified) VALUES (?, ?, ?) on CONFLICT(filename) do UPDATE SET md5 = excluded.md5, last_modified = excluded.last_modified";

  public static DbExecutor getInstance() {
    if (instance == null) {
      instance = new DbExecutor();
    }
    return instance;
  }

  private DbExecutor() {
    try {
      Class.forName("org.sqlite.JDBC");
      connection = DriverManager.getConnection("jdbc:sqlite:dupfinder.db");
    } catch (ClassNotFoundException | SQLException e) {
      e.printStackTrace();
      System.exit(0);
    }
    if (!doesMetaExist()) {
      createMetaTable();
    }
  }

  private void createMetaTable() {
    try {
      connection.createStatement()
          .execute("CREATE TABLE FILE_META (filename TEXT PRIMARY KEY, md5 TEXT, last_modified INTEGER)");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private boolean doesMetaExist() {
    try {
      DatabaseMetaData dmd = connection.getMetaData();
      return dmd.getTables(null, null, "FILE_META", null).next();
    } catch (SQLException e) {
      e.printStackTrace();
      System.exit(0);
    }
    return false;
  }

  public FileMeta readFileMeta(String filename) {
    synchronized (connection) {
      try {
        PreparedStatement readStatement = connection.prepareStatement(readMeta);
        readStatement.setString(1, filename);
        ResultSet rs = readStatement.executeQuery();
        if (rs.next()) {
          FileMeta meta = new FileMeta();
          meta.filename = rs.getString("filename");
          meta.md5 = rs.getString("md5");
          meta.lastModified = rs.getLong("last_modified");
          return meta;
        }
        return null;
      } catch (SQLException e) {
        e.printStackTrace();
      }
      return null;
    }
  }

  public void insertFileMeta(FileMeta meta) {
    synchronized (connection) {
      try {
        PreparedStatement insertStatement = connection.prepareStatement(insertMeta);
        insertStatement.setString(1, meta.filename);
        insertStatement.setString(2, meta.md5);
        insertStatement.setLong(3, meta.lastModified);
        insertStatement.execute();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }
}
