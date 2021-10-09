package banking.connection.operation.impl;

import banking.connection.model.CreditCard;
import banking.connection.operation.Operation;
import banking.connection.util.SQLiteConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CreditCardOperationImpl implements Operation<CreditCard> {
  private final String database;

  public CreditCardOperationImpl(String database) {
    this.database = database;
  }

  public void createCardTable() {
    try (var statement = this.getConnection().createStatement()) {
      statement.executeUpdate(
          "CREATE TABLE IF NOT EXISTS card ("
              + "id      INTEGER,"
              + "number  TEXT,"
              + "pin     TEXT,"
              + "balance INTEGER DEFAULT 0"
              + ");");
    } catch (SQLException exception) {
      System.out.println(exception.getMessage());
    }
  }

  public void addBalance(String number, int amount) {
    final var sql = "UPDATE card SET balance = balance + ? WHERE number = ?;";
    try (var statement = this.getConnection().prepareStatement(sql)) {
      statement.setInt(1, amount);
      statement.setString(2, number);
      statement.executeUpdate();
    } catch (SQLException exception) {
      System.out.println(exception.getMessage());
    }
  }

  private Connection getConnection() throws SQLException {
    return SQLiteConnection.getConnection(this.database);
  }

  @Override
  public List<CreditCard> readByCriteria(int option, String... criteria) {
    final var creditCards = new ArrayList<CreditCard>();
    var id = criteria[0];
    var number = criteria[1];
    var pin = criteria[2];
    var sql = "";

    try (var statement = this.getConnection().createStatement()) {
      var hasResultSet = false;

      switch (option) {
        case 1:
          sql = "SELECT * FROM card WHERE number = " + number + ";";
          hasResultSet = statement.execute(sql);
          break;
        case 2:
          sql =
              "SELECT * FROM card WHERE number = "
                  + number
                  + " AND pin = "
                  + String.format("'%s'", pin)
                  + ";";
          hasResultSet = statement.execute(sql);
          break;
        default:
          sql =
              "SELECT * FROM card WHERE id = "
                  + id
                  + " AND number = "
                  + number
                  + " AND pin = "
                  + String.format("'%s'", pin)
                  + ";";
          hasResultSet = statement.execute(sql);
      }

      if (hasResultSet) {
        var resultSet = statement.getResultSet();

        while (resultSet.next()) {
          var cardId = resultSet.getInt("id");
          var cardNumber = resultSet.getString("number");
          var cardPin = resultSet.getString("pin");
          var cardBalance = resultSet.getInt("balance");
          creditCards.add(new CreditCard(cardId, cardNumber, cardPin, cardBalance));
        }
      }
    } catch (SQLException exception) {
      System.out.println(exception.getMessage());
    }

    return creditCards;
  }

  @Override
  public void create(CreditCard creditCard) {
    try (var statement = this.getConnection().createStatement()) {
      statement.executeUpdate(
          "INSERT INTO card(id, number, pin)"
              + "VALUES ("
              + creditCard.getId()
              + ", "
              + creditCard.getNumber()
              + ", "
              + String.format("'%s'", creditCard.getPin())
              + ");");
    } catch (SQLException exception) {
      System.out.println(exception.getMessage());
    }
  }

  @Override
  public void delete(CreditCard creditCard) {
    final var sql = "DELETE FROM card WHERE number = ?;";

    try (var statement = this.getConnection().prepareStatement(sql)) {
      statement.setString(1, creditCard.getNumber());
      statement.executeUpdate();
    } catch (SQLException exception) {
      System.out.println(exception.getMessage());
    }
  }

  @Override
  public void deleteAll() {
    try (var statement = this.getConnection().createStatement()) {
      statement.executeUpdate("DELETE FROM card WHERE id IS NOT NULL;");
    } catch (SQLException exception) {
      System.out.println(exception.getMessage());
    }
  }

  @Override
  public void transfer(CreditCard originator, CreditCard beneficiary, int amount) {
    final var sql =
        "UPDATE card SET balance = balance + ? WHERE id = ? AND number = ? AND pin = ?;";

    try (var connection = getConnection();
        var statement = connection.prepareStatement(sql)) {
      connection.setAutoCommit(false);

      statement.setInt(1, amount);
      statement.setInt(2, beneficiary.getId());
      statement.setString(3, beneficiary.getNumber());
      statement.setString(4, beneficiary.getPin());
      statement.executeUpdate();

      amount = -amount;
      statement.setInt(1, amount);
      statement.setInt(2, originator.getId());
      statement.setString(3, originator.getNumber());
      statement.setString(4, originator.getPin());
      statement.executeUpdate();

      connection.commit();
      connection.setAutoCommit(true);
    } catch (SQLException exception) {
      System.out.println(exception.getMessage());
    }
  }
}
