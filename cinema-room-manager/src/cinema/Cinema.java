package cinema;

import java.util.Scanner;

class Ticket {
  static int row;
  static int seat;
  static int price;

  private Ticket() {}
}

public class Cinema {
  static char[][] room;
  static int purchasedTickets = 0;
  static double percentage = 0.0;
  static int currentIncome = 0;
  static int totalIncome = 0;

  public static void main(String[] args) {
    // Write your code here
    final var input = new Scanner(System.in);
    createCinema(input);
    calculateTotalIncome();
    int option;

    do {
      printMenu();
      option = input.nextInt();
      switch (option) {
        case 1:
          showSeats();
          break;
        case 2:
          buyTicket(input);
          break;
        case 3:
          printStatistics();
          break;
        default:
      }
    } while (option > 0);
  }

  public static void createCinema(Scanner input) {
    System.out.println("Enter the number of rows:");
    final var rows = input.nextInt() + 1;
    System.out.println("Enter the number of seats in each row:");
    final var seats = input.nextInt() + 1;

    Cinema.room = new char[rows][seats];
    for (var row = 0; row < Cinema.room.length; row++)
      for (var seat = 0; seat < Cinema.room[row].length; seat++)
        if (row == 0) {
          Cinema.room[row][seat] = seat > 0 ? Character.forDigit(seat, 10) : ' ';
        } else Cinema.room[row][seat] = seat > 0 ? 'S' : Character.forDigit(row, 10);
  }

  public static void calculateTotalIncome() {
    final var totalSeats = (Cinema.room.length - 1) * (Cinema.room[0].length - 1);
    final var isMoreThan60 = totalSeats > 60;

    if (isMoreThan60) {
      for (var row = 0; row < Cinema.room.length - 1; row++) {
        for (var seat = 0; seat < Cinema.room[row].length - 1; seat++)
          Cinema.totalIncome += row < (Cinema.room.length - 1) / 2 ? 10 : 8;
      }
    } else Cinema.totalIncome = totalSeats * 10;
  }

  public static void printMenu() {
    System.out.println(
        "1. Show the seats" + "\n" + "2. Buy a ticket" + "\n" + "3. Statistics" + "\n" + "0. Exit");
  }

  public static void showSeats() {
    System.out.println("Cinema:");
    for (var row : Cinema.room) {
      for (var seat : row) System.out.print(seat + " ");
      System.out.println();
    }
  }

  public static void askSeat(Scanner input) {
    do {
      System.out.println("Enter a row number:");
      Ticket.row = input.nextInt();
      System.out.println("Enter a seat number in that row:");
      Ticket.seat = input.nextInt();
    } while (!(checkSeat()));
  }

  public static boolean checkSeat() {
    if ((Ticket.row < 0 || Ticket.row >= Cinema.room.length)
        || (Ticket.seat < 0 || Ticket.seat >= Cinema.room[Ticket.row].length)) {
      System.out.println("Wrong input!");
      return false;
    } else if (Cinema.room[Ticket.row][Ticket.seat] == 'B') {
      System.out.println("That ticket has already been purchased!");
      return false;
    }

    return true;
  }

  public static void buyTicket(Scanner scanner) {
    askSeat(scanner);

    final var totalSeats = (Cinema.room.length - 1) * (Cinema.room[0].length - 1);
    final var isMoreThan60 = totalSeats > 60;

    if (!(isMoreThan60)) {
      Ticket.price = 10;
    } else Ticket.price = Ticket.row <= (Cinema.room.length - 1) / 2 ? 10 : 8;

    Cinema.room[Ticket.row][Ticket.seat] = 'B';
    Cinema.purchasedTickets += 1;
    Cinema.percentage = (Cinema.purchasedTickets / (double) totalSeats) * 100;
    Cinema.currentIncome += Ticket.price;
    System.out.println("Ticket price: $" + Ticket.price);
  }

  public static void printStatistics() {
    System.out.printf(
        "Number of purchased tickets: %d%nPercentage: %.2f%%%nCurrent income: $%d%nTotal income: $%d%n",
        Cinema.purchasedTickets, Cinema.percentage, Cinema.currentIncome, Cinema.totalIncome);
  }
}
