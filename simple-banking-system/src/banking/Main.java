package banking;

import banking.connection.model.CreditCard;
import banking.connection.operation.impl.CreditCardOperationImpl;
import banking.util.CreditCardUtil;

import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    final var input = new Scanner(System.in);
    final var fileName = args[1];
    final var creditCardOperation = new CreditCardOperationImpl(fileName);
    int option;
    creditCardOperation.createCardTable();

    do {
      printMenu();
      option = input.nextInt();

      switch (option) {
        case 0:
          creditCardOperation.deleteAll();
          System.out.println("\n" + "Bye!");
          break;
        case 1:
          var number = CreditCardUtil.generateNumber();
          var pin = CreditCardUtil.generatePin();
          creditCardOperation.create(new CreditCard(number, pin));
          break;
        case 2:
          login(creditCardOperation);
          break;
        default:
      }
    } while (option != 0);
  }

  private static void printMenu() {
    System.out.println("1. Create an account\n" + "2. Log into account\n" + "0. Exit");
  }

  private static void printCardMenu() {
    System.out.println(
        "1. Balance\n"
            + "2. Add income\n"
            + "3. Do transfer\n"
            + "4. Close account\n"
            + "5. Log out\n"
            + "0. Exit");
  }

  private static void login(CreditCardOperationImpl creditCardOperation) {
    final var input = new Scanner(System.in);
    System.out.println("\n" + "Enter your card number:");
    final var number = input.next();
    System.out.println("Enter your PIN:");
    final var pin = input.next();
    final var creditCards = creditCardOperation.readByCriteria(2, null, number, pin, null);

    if (creditCards.isEmpty()) {
      System.out.println("\n" + "Wrong card number or PIN!" + "\n");
    } else {
      System.out.println("\n" + "You have successfully logged in!" + "\n");
      manageCreditCardMenu(creditCardOperation, creditCards.get(0));
    }
  }

  private static void manageCreditCardMenu(
      CreditCardOperationImpl creditCardOperation, CreditCard creditCard) {
    final var input = new Scanner(System.in);
    int option;

    do {
      printCardMenu();
      option = input.nextInt();

      switch (option) {
        case 0:
          creditCardOperation.deleteAll();
          System.out.println("\n" + "Bye!");
          System.exit(0);
          break;
        case 1:
          System.out.println("\n" + "Balance: " + creditCard.getBalance() + "\n");
          break;
        case 2:
          System.out.println("\n" + "Enter income:");
          var income = input.nextInt();
          creditCard.addBalance(income);
          creditCardOperation.addBalance(creditCard.getNumber(), income);
          System.out.println("\n" + "Income was added!" + "\n");
          break;
        case 3:
          System.out.println("\n" + "Transfer" + "\n" + "Enter card number:");
          manageTransfer(creditCardOperation, creditCard, input.next());
          break;
        case 4:
          creditCardOperation.delete(creditCard);
          System.out.println("\n" + "The account has been closed!" + "\n");
          option = 5;
          break;
        case 5:
          System.out.println("\n" + "You have successfully logged out!" + "\n");
          break;
        default:
      }
    } while (option != 5);
  }

  private static void manageTransfer(
      CreditCardOperationImpl creditCardOperation, CreditCard creditCard, String number) {
    final var input = new Scanner(System.in);

    if (CreditCardUtil.isValid(number)) {
      var creditCards = creditCardOperation.readByCriteria(1, null, number, null, null);

      if (creditCards.isEmpty()) {
        System.out.println("\n" + "Such a card does not exist." + "\n");
      } else {
        var beneficiary = creditCards.get(0);

        System.out.println("\n" + "Enter how much money you want to transfer:");
        var amount = input.nextInt();

        if (creditCard.getBalance() >= amount) {
          creditCard.subtractBalance(amount);
          creditCardOperation.transfer(creditCard, beneficiary, amount);
          System.out.println("\n" + "Success!" + "\n");
        } else System.out.println("\n" + "Not enough money!" + "\n");
      }
    } else
      System.out.println(
          "\n" + "Probably you made a mistake in the card number. Please try again!" + "\n");
  }
}
