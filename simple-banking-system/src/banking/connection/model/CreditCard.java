package banking.connection.model;

public class CreditCard {
  private static int idCounter = 0;
  private final int id;
  private final String number;
  private final String pin;
  private int balance;

  public CreditCard(String number, String pin) {
    this.id = ++idCounter;
    this.number = number;
    this.pin = pin;
    System.out.println(
        "\n"
            + "Your card has been created."
            + "\n"
            + "Your card number:"
            + "\n"
            + this.number
            + "\n"
            + "Your card PIN:"
            + "\n"
            + this.pin
            + "\n");
  }

  public CreditCard(int id, String number, String pin, int balance) {
    this.id = id;
    this.number = number;
    this.pin = pin;
    this.balance = balance;
  }

  public int getId() {
    return this.id;
  }

  public String getNumber() {
    return this.number;
  }

  public String getPin() {
    return this.pin;
  }

  public int getBalance() {
    return this.balance;
  }

  public void addBalance(int amount) {
    this.balance += amount;
  }

  public void subtractBalance(int amount) {
    this.balance -= amount;
  }
}
