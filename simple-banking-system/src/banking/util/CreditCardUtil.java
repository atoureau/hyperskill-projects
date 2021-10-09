package banking.util;

import java.util.Random;

public class CreditCardUtil {
  private static final Random RANDOM_GENERATOR = new Random();
  private static final String BIN = "400000";

  private CreditCardUtil() {}

  public static String generateNumber() {
    var number = BIN;

    while (number.length() < 15) {
      var digit = RANDOM_GENERATOR.nextInt(10);
      number = number.concat(String.valueOf(digit));
    }

    number = number.concat(String.valueOf(generateChecksum(number)));
    return number;
  }

  public static String generatePin() {
    return String.format("%04d", RANDOM_GENERATOR.nextInt(10000));
  }

  public static boolean isValid(String number) {
    final var checksum = Character.digit(number.charAt(number.length() - 1), 10);
    return checksum == generateChecksum(number.substring(0, number.length() - 1));
  }

  private static int generateChecksum(String number) {
    var checksum = 0;
    var sum = 0;

    for (var index = 0; index < number.length(); index++) {
      var digit = Character.digit(number.charAt(index), 10);
      if ((index + 1) % 2 != 0) digit *= 2;
      if (digit > 9) digit -= 9;
      sum += digit;
    }

    while ((checksum + sum) % 10 != 0) ++checksum;

    return checksum;
  }
}
