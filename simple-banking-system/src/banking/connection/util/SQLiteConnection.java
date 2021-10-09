package banking.connection.util;

import org.sqlite.SQLiteConfig;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLiteConnection {
  private SQLiteConnection() {}

  public static Connection getConnection(String fileName) throws SQLException {
    return new SQLiteConfig().createConnection("jdbc:sqlite:".concat(fileName));
  }
}
