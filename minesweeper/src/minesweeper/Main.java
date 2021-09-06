package minesweeper;

import java.util.*;

class Game {
  static char[][] minefield;
  static char[][] playerMinefield;
  static int mines;
  static boolean firstClaim = true;
  static boolean keepPlaying = true;

  static {
    minefield = new char[9][9];
    for (var row : minefield) Arrays.fill(row, '.');
  }

  private Game() {}

  static void initMinefield() {
    System.out.println("How many mines do you want on the field?");
    final var input = new Scanner(System.in);
    Game.mines = input.nextInt();
    setMines(Game.mines);
    setHints();
  }

  private static void setMines(int mines) {
    for (var mine = 0; mine < mines; mine++) {
      int row;
      int col;
      do {
        row = new Random().nextInt(minefield.length);
        col = new Random().nextInt(minefield[row].length);
      } while (minefield[row][col] == 'X');
      minefield[row][col] = 'X';
    }
  }

  private static void setMines(int mines, int x, int y) {
    for (var mine = 0; mine < mines; mine++) {
      int row;
      int col;
      do {
        row = new Random().nextInt(minefield.length);
        col = new Random().nextInt(minefield[row].length);
      } while (minefield[row][col] == 'X' || (row == x && col == y));
      minefield[row][col] = 'X';
    }
  }

  private static void setHints() {
    for (var row = 0; row < minefield.length; row++)
      for (var col = 0; col < minefield[row].length; col++) {
        if (minefield[row][col] != 'X') {
          var counter = countMines(row, col);
          if (counter > 0) minefield[row][col] = Character.forDigit(counter, 10);
        }
      }
  }

  private static int countMines(int x, int y) {
    var counter = 0;
    final var rows = minefield.length;
    final var cols = minefield[x].length;

    for (var row = x - 1; row <= x + 1; ++row)
      for (var col = y - 1; col <= y + 1; ++col)
        if (!(row < 0 || row >= rows || col < 0 || col >= cols) && minefield[row][col] == 'X')
          ++counter;

    return counter;
  }

  static void initPlayerMinefield() {
    final var minefield = new ArrayList<char[]>();
    minefield.add(new char[] {' ', '|', '1', '2', '3', '4', '5', '6', '7', '8', '9', '|'});
    minefield.add(new char[] {'-', '|', '-', '-', '-', '-', '-', '-', '-', '-', '-', '|'});
    minefield.addAll(List.of(Game.minefield));
    minefield.add(new char[] {'-', '|', '-', '-', '-', '-', '-', '-', '-', '-', '-', '|'});

    for (var row = 2; row < minefield.size() - 1; row++) {
      var cols = new ArrayList<Character>();
      cols.add(Character.forDigit(row - 1, 10));
      cols.add('|');
      var fieldRow = minefield.get(row);
      for (var col : fieldRow) cols.add(col == 'X' || Character.isDigit(col) ? '.' : col);
      cols.add('|');
      var array = new char[cols.size()];
      for (var index = 0; index < array.length; index++) array[index] = cols.get(index);
      minefield.set(row, array);
    }

    playerMinefield = new char[minefield.size()][minefield.get(0).length];

    for (var row = 0; row < playerMinefield.length; row++)
      playerMinefield[row] = minefield.get(row);
  }

  static void play() {
    do {
      printPlayerMinefield();
      setCoordinates(getCoordinates());
    } while (Game.keepPlaying);
  }

  private static void printPlayerMinefield() {
    System.out.println();
    for (var row : playerMinefield) {
      for (var col : row) System.out.print(col);
      System.out.println();
    }
  }

  private static String[] getCoordinates() {
    System.out.println("Set/unset mines marks or claim a cell as free:");
    final var input = new Scanner(System.in);
    var coordinates = input.nextLine().split(" ");
    var x = Integer.parseInt(coordinates[1]) - 1;
    var y = Integer.parseInt(coordinates[0]) - 1;
    var value = playerMinefield[x + 2][y + 2];
    var validMarkType = "free".equals(coordinates[2]) || "mine".equals(coordinates[2]);

    while (value == '/' || Character.isDigit(value) || !(validMarkType)) {
      System.out.println(!(validMarkType) ? "Invalid mark type!" : "Cell was already explored!");
      System.out.println("Set/unset mines marks or claim a cell as free:");
      coordinates = input.nextLine().split(" ");
      x = Integer.parseInt(coordinates[1]) - 1;
      y = Integer.parseInt(coordinates[0]) - 1;
      validMarkType = "free".equals(coordinates[2]) || "mine".equals(coordinates[2]);
      value = playerMinefield[x + 2][y + 2];
    }

    coordinates[0] = String.valueOf(Integer.parseInt(coordinates[0]) - 1);
    coordinates[1] = String.valueOf(Integer.parseInt(coordinates[1]) - 1);
    return coordinates;
  }

  private static void setCoordinates(String[] coordinates) {
    final var x = Integer.parseInt(coordinates[1]);
    final var y = Integer.parseInt(coordinates[0]);

    if ("free".equals(coordinates[2])) {
      exploreCell(x, y);
    } else if ("mine".equals(coordinates[2])) {
      markCell(x, y);
    }
  }

  private static void exploreCell(int x, int y) {
    final var value = minefield[x][y];

    if (value == '.') {
      Game.firstClaim = false;
      autoExploreCells(x, y);
    } else if (Character.isDigit(value)) {
      Game.firstClaim = false;
      playerMinefield[x + 2][y + 2] = value;
    } else if (value == 'X' && Game.firstClaim) {
      Game.firstClaim = false;
      regenerateMinefield(x, y);
      autoExploreCells(x, y);
    } else endGame();

    checkVictoryByExploring();
  }

  private static void regenerateMinefield(int x, int y) {
    for (var row : minefield) Arrays.fill(row, '.');
    setMines(Game.mines, x, y);
    setHints();
  }

  private static void autoExploreCells(int x, int y) {
    final var rows = minefield.length;
    final var cols = minefield[x].length;

    for (var row = x - 1; row <= x + 1; ++row)
      for (var col = y - 1; col <= y + 1; ++col)
        if (!(row < 0 || row >= rows || col < 0 || col >= cols)) {
          if (minefield[row][col] == '.') {
            minefield[row][col] = '/';
            playerMinefield[row + 2][col + 2] = '/';
            autoExploreCells(row, col);
          } else if (Character.isDigit(minefield[row][col]))
            playerMinefield[row + 2][col + 2] = minefield[row][col];
        }
  }

  private static void markCell(int x, int y) {
    final var actualValue = playerMinefield[x + 2][y + 2];
    playerMinefield[x + 2][y + 2] = actualValue == '.' ? '*' : '.';
    checkVictoryByMarkingMines();
  }

  private static void checkVictoryByExploring() {
    var victory = true;

    for (var row = 0; row < minefield.length && victory; row++)
      for (var col = 0; col < minefield[row].length; col++)
        if (playerMinefield[row + 2][col + 2] == '.') {
          victory = false;
          break;
        }

    if (victory) {
      printPlayerMinefield();
      Game.keepPlaying = false;
      System.out.println("Congratulations! You found all the mines!");
    }
  }

  private static void checkVictoryByMarkingMines() {
    var victory = true;

    for (var row = 0; row < minefield.length && victory; row++)
      for (var col = 0; col < minefield[row].length; col++) {
        var value = minefield[row][col];
        var actualValue = playerMinefield[row + 2][col + 2];
        if ((value == 'X' && actualValue == '.') || (value != 'X' && actualValue == '*')) {
          victory = false;
          break;
        }
      }

    if (victory) {
      printPlayerMinefield();
      Game.keepPlaying = false;
      System.out.println("Congratulations! You found all the mines!");
    }
  }

  private static void endGame() {
    for (var row = 0; row < minefield.length; row++) {
      for (var col = 0; col < minefield[row].length; col++)
        if (minefield[row][col] == 'X') playerMinefield[row + 2][col + 2] = 'X';
    }

    printPlayerMinefield();
    Game.keepPlaying = false;
    System.out.println("You stepped on a mine and failed!");
  }
}

public class Main {
  public static void main(String[] args) {
    // write your code here
    Game.initMinefield();
    Game.initPlayerMinefield();
    Game.play();
  }
}
