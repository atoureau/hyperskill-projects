package numbers;

import java.util.Arrays;
import java.util.Scanner;
import java.util.StringJoiner;

class AmazingNumbers {
  String[] params;

  public static boolean isEven(long number) {
    return number % 2 == 0;
  }

  public static boolean isOdd(long number) {
    return number % 2 != 0;
  }

  public static boolean isBuzz(long number) {
    return number % 7 == 0 || String.valueOf(number).endsWith("7");
  }

  public static boolean isDuck(long number) {
    return String.valueOf(number).charAt(0) != '0' && String.valueOf(number).contains("0");
  }

  public static boolean isPalindromic(long number) {
    var stringNumber = String.valueOf(number);
    var reversedDigits = new char[stringNumber.length()];
    var index = 0;

    for (var i = stringNumber.length() - 1; i >= 0; i--)
      reversedDigits[index++] = stringNumber.charAt(i);

    return stringNumber.equals(String.valueOf(reversedDigits));
  }

  public static boolean isGapful(long number) {
    var digits = String.valueOf(number).split("");
    var firstLastDigit = digits[0] + digits[digits.length - 1];
    return digits.length >= 3 && number % Long.parseLong(firstLastDigit) == 0;
  }

  public static boolean isSpy(long number) {
    var stringNumber = String.valueOf(number);
    var sum = 0L;
    var product = 1L;

    for (var i = 0; i < stringNumber.length(); i++) {
      sum += Character.getNumericValue(stringNumber.charAt(i));
      product *= Character.getNumericValue(stringNumber.charAt(i));
    }

    return sum == product;
  }

  public static boolean isSquare(long number) {
    var root = Math.sqrt(number);
    return Math.pow(Math.floor(root), 2) == number;
  }

  public static boolean isSunny(long number) {
    return isSquare(number + 1);
  }

  public static boolean isJumping(long number) {
    var isJumping = true;
    var digits = String.valueOf(number).toCharArray();

    if (digits.length > 1)
      for (var i = 0; i < digits.length; i++) {
        var digit = Character.getNumericValue(digits[i]);
        if (i + 1 < digits.length) {
          var nextDigit = Character.getNumericValue(digits[i + 1]);
          if (!(digit + 1 == nextDigit || digit - 1 == nextDigit)) {
            isJumping = false;
            break;
          }
        }
      }
    return isJumping;
  }

  public static boolean isHappy(long number) {
    if (number == 1 || number == 7) return true;

    var sum = number;
    var x = number;

    while (sum > 9) {
      sum = 0;

      while (x > 0) {
        var d = x % 10;
        sum += d * d;
        x /= 10;
      }

      if (sum == 1) return true;
      x = sum;
    }
    return sum == 7;
  }

  public static boolean isSad(long number) {
    return !(isHappy(number));
  }
}

enum Properties {
  EVEN,
  ODD,
  BUZZ,
  DUCK,
  PALINDROMIC,
  GAPFUL,
  SPY,
  SQUARE,
  SUNNY,
  JUMPING,
  HAPPY,
  SAD
}

public class Main {
  public static void main(String[] args) {
    var az = new AmazingNumbers();
    // Step 1
    welcomeUsers();
    // Step 2
    displayInstructions();
    // Step 3
    askRequest();
    requestParams(az);
    if (Long.parseLong(az.params[0]) != 0) {
      while (Long.parseLong(az.params[0]) != 0) {
        switch (az.params.length) {
          case 1:
            // Step 7
            var param = Long.parseLong(az.params[0]);
            printProperties(param);
            break;
          case 2:
            // Step 8
            var param1 = Long.parseLong(az.params[0]);
            var param2 = Long.parseLong(az.params[1]);
            for (var i = param1; i < param1 + param2; i++) printResumedProperties(i);
            break;
          default:
            // Step 9
            param1 = Long.parseLong(az.params[0]);
            param2 = Long.parseLong(az.params[1]);
            var properties = getProperties(az.params);
            var excludedProperties = getExcludedProperties(az.params);
            printResumedProperties(param1, param2, properties, excludedProperties);
        }
        // Step 11
        askRequest();
        requestParams(az);
      }
    }
    // Step 4
    terminateProgram();
  }

  public static void welcomeUsers() {
    System.out.println("Welcome to Amazing Numbers!");
  }

  public static void displayInstructions() {
    System.out.println(
        "Supported requests:"
            + "\n"
            + "- enter a natural number to know its properties;"
            + "\n"
            + "- enter two natural numbers to obtain the properties of the list:"
            + "\n"
            + "\t* the first parameter represents a starting number;"
            + "\n"
            + "\t* the second parameter shows how many consecutive numbers are to be processed;"
            + "\n"
            + "- two natural numbers and properties to search for;"
            + "\n"
            + "- a property preceded by minus must not be present in numbers;"
            + "\n"
            + "- separate the parameters with one space;"
            + "\n"
            + "- enter 0 to exit.");
  }

  public static void askRequest() {
    System.out.println("Enter a request:");
  }

  public static void requestParams(AmazingNumbers az) {
    var input = new Scanner(System.in);
    var allCorrect = true;

    do {
      az.params = input.nextLine().split(" ");
      switch (az.params.length) {
        case 0:
          allCorrect = false;
          displayInstructions();
          askRequest();
          break;
        case 1:
          allCorrect = checkParam(az.params[0]);
          break;
        case 2:
          allCorrect = checkParams(az.params[0], az.params[1]);
          break;
        case 3:
          allCorrect = checkParams(az.params[0], az.params[1], az.params[2]);
          break;
        default:
          allCorrect = checkAllParams(az.params);
          break;
      }
    } while (!(allCorrect));
  }

  public static boolean checkParam(String param1) {
    var allCorrect = true;

    if (param1.isBlank()) {
      allCorrect = false;
      displayInstructions();
      askRequest();
    } else if (!param1.matches("[0-9]+")) {
      // Step 5
      allCorrect = false;
      System.out.println("The first parameter should be a natural number or zero.");
      askRequest();
    }

    return allCorrect;
  }

  public static boolean checkParams(String param1, String param2) {
    var allCorrect = checkParam(param1);

    if (allCorrect && !(param2.matches("[1-9]+[0]*$"))) {
      // Step 5
      allCorrect = false;
      System.out.println("The second parameter should be a natural number.");
      askRequest();
    }

    return allCorrect;
  }

  public static boolean checkParams(String param1, String param2, String param3) {
    var allCorrect = checkParams(param1, param2);

    if (allCorrect) allCorrect = checkProperty(param3, true);

    return allCorrect;
  }

  public static boolean checkAllParams(String[] params) {
    var allCorrect = checkParams(params[0], params[1]);

    if (allCorrect) allCorrect = checkProperties(params);

    return allCorrect;
  }

  public static boolean checkProperty(String property, boolean printError) {
    var isValid = false;

    property = property.charAt(0) == '-' ? property.substring(1) : property;

    for (var p : Properties.values())
      if (property.equalsIgnoreCase(p.name())) {
        isValid = true;
        break;
      }

    if (!(isValid) && printError) {
      // Step 6
      System.out.println(
          "The property ["
              + property.toUpperCase()
              + "] is wrong."
              + "\n"
              + "Available properties: "
              + Arrays.toString(Properties.values()));
      askRequest();
    }

    return isValid;
  }

  public static boolean checkProperties(String[] properties) {
    var allCorrect = true;
    var hasCorrectProperty = true;
    var wrongProperties = new StringJoiner(" ");
    String[] wrongPropertiesArray;

    for (var index = 2; index < properties.length; index++) {
      var property = properties[index];
      hasCorrectProperty = checkProperty(property, false);
      if (!(hasCorrectProperty)) wrongProperties.add(property.toUpperCase());
    }

    if (wrongProperties.length() > 0) {
      wrongPropertiesArray = wrongProperties.toString().split(" ");
      if (wrongPropertiesArray.length == 1) {
        allCorrect = checkProperty(wrongPropertiesArray[0], true);
      } else {
        allCorrect = false;
        System.out.println(
            "The properties "
                + Arrays.toString(wrongPropertiesArray)
                + " are wrong."
                + "\n"
                + "Available properties: "
                + Arrays.toString(Properties.values()));
        askRequest();
      }
    } else
      for (var idx = 2; idx < properties.length && allCorrect; idx++)
        allCorrect = hasNoExclusiveProperties(properties, idx, properties[idx].toUpperCase());

    return allCorrect;
  }

  public static boolean hasNoExclusiveProperties(
      String[] properties, int idx, String propertyString) {
    var allCorrect = true;

    for (var i = idx + 1; i < properties.length && allCorrect; i++) {
      var property =
          Properties.valueOf(
              propertyString.charAt(0) == '-'
                  ? propertyString.substring(1).toUpperCase()
                  : propertyString.toUpperCase());

      var nextProperty =
          Properties.valueOf(
              properties[i].charAt(0) == '-'
                  ? properties[i].substring(1).toUpperCase()
                  : properties[i].toUpperCase());

      var nextPropertyString = properties[i].toUpperCase();

      final var areExcludedProperties =
          (propertyString.charAt(0) == '-') == (nextPropertyString.charAt(0) == '-');

      switch (property) {
        case BUZZ:
        case PALINDROMIC:
        case GAPFUL:
        case JUMPING:
          if (property == nextProperty) allCorrect = propertyString.equals(nextPropertyString);
          break;
        case EVEN:
        case ODD:
          if (property == nextProperty) {
            allCorrect = propertyString.equals(nextPropertyString);
          } else if ((nextProperty == Properties.EVEN || nextProperty == Properties.ODD)
              && areExcludedProperties) allCorrect = false;
          break;
        case DUCK:
        case SPY:
          if (property == nextProperty) {
            allCorrect = propertyString.equals(nextPropertyString);
          } else if ((nextProperty == Properties.DUCK || nextProperty == Properties.SPY)
              && areExcludedProperties) allCorrect = false;
          break;
        case SUNNY:
        case SQUARE:
          if (property == nextProperty) {
            allCorrect = propertyString.equals(nextPropertyString);
          } else if ((nextProperty == Properties.SUNNY || nextProperty == Properties.SQUARE)
              && areExcludedProperties) allCorrect = false;
          break;
        case HAPPY:
        case SAD:
          if (property == nextProperty) {
            allCorrect = propertyString.equals(nextPropertyString);
          } else if ((nextProperty == Properties.HAPPY || nextProperty == Properties.SAD)
              && areExcludedProperties) allCorrect = false;
          break;
        default:
      }

      if (!(allCorrect)) {
        // Step 10
        System.out.println(
            "The request contains mutually exclusive properties: ["
                + propertyString
                + ", "
                + nextPropertyString
                + "]"
                + "\n"
                + "There are no numbers with these properties.");
        askRequest();
      }
    }

    return allCorrect;
  }

  public static Properties[] getProperties(String[] properties) {
    var sj = new StringJoiner(" ");

    for (var i = 2; i < properties.length; i++)
      if (properties[i].charAt(0) != '-') sj.add(properties[i].toUpperCase());

    return getPropertiesArray(sj);
  }

  public static Properties[] getExcludedProperties(String[] properties) {
    var sj = new StringJoiner(" ");

    for (var i = 2; i < properties.length; i++)
      if (properties[i].charAt(0) == '-') sj.add(properties[i].substring(1).toUpperCase());

    return getPropertiesArray(sj);
  }

  public static Properties[] getPropertiesArray(StringJoiner sj) {
    Properties[] propertiesArray;
    if (sj.length() > 0) {
      propertiesArray = new Properties[sj.toString().split(" ").length];
      for (var i = 0; i < propertiesArray.length; i++)
        propertiesArray[i] = Properties.valueOf(sj.toString().split(" ")[i]);
    } else propertiesArray = new Properties[0];

    return propertiesArray;
  }

  public static void printProperties(long number) {
    System.out.println(
        "Properties of "
            + number
            + "\n"
            + "even: "
            + AmazingNumbers.isEven(number)
            + "\n"
            + "odd: "
            + AmazingNumbers.isOdd(number)
            + "\n"
            + "buzz: "
            + AmazingNumbers.isBuzz(number)
            + "\n"
            + "duck: "
            + AmazingNumbers.isDuck(number)
            + "\n"
            + "palindromic: "
            + AmazingNumbers.isPalindromic(number)
            + "\n"
            + "gapful: "
            + AmazingNumbers.isGapful(number)
            + "\n"
            + "spy: "
            + AmazingNumbers.isSpy(number)
            + "\n"
            + "square: "
            + AmazingNumbers.isSquare(number)
            + "\n"
            + "sunny: "
            + AmazingNumbers.isSunny(number)
            + "\n"
            + "jumping: "
            + AmazingNumbers.isJumping(number)
            + "\n"
            + "happy: "
            + AmazingNumbers.isHappy(number)
            + "\n"
            + "sad: "
            + AmazingNumbers.isSad(number));
  }

  public static void printResumedProperties(long number) {
    System.out.print(number + " is ");
    if (AmazingNumbers.isEven(number)) System.out.print("even, ");
    if (AmazingNumbers.isOdd(number)) System.out.print("odd, ");
    if (AmazingNumbers.isBuzz(number)) System.out.print("buzz, ");
    if (AmazingNumbers.isDuck(number)) System.out.print("duck, ");
    if (AmazingNumbers.isPalindromic(number)) System.out.print("palindromic, ");
    if (AmazingNumbers.isGapful(number)) System.out.print("gapful, ");
    if (AmazingNumbers.isSpy(number)) System.out.print("spy, ");
    if (AmazingNumbers.isSquare(number)) System.out.print("square, ");
    if (AmazingNumbers.isSunny(number)) System.out.print("sunny, ");
    if (AmazingNumbers.isJumping(number)) System.out.print("jumping, ");
    if (AmazingNumbers.isHappy(number)) System.out.print("happy");
    if (AmazingNumbers.isSad(number)) System.out.print("sad");
    System.out.println();
  }

  public static void printResumedProperties(
      long param1, long param2, Properties[] properties, Properties[] excludedProperties) {
    var counter = 0;
    for (long i = param1; counter < param2; i++) {
      var isCountable = true;
      if (properties.length > 0)
        for (var property : properties) {
          switch (property) {
            case EVEN:
              isCountable = AmazingNumbers.isEven(i);
              break;
            case ODD:
              isCountable = AmazingNumbers.isOdd(i);
              break;
            case BUZZ:
              isCountable = AmazingNumbers.isBuzz(i);
              break;
            case DUCK:
              isCountable = AmazingNumbers.isDuck(i);
              break;
            case PALINDROMIC:
              isCountable = AmazingNumbers.isPalindromic(i);
              break;
            case GAPFUL:
              isCountable = AmazingNumbers.isGapful(i);
              break;
            case SPY:
              isCountable = AmazingNumbers.isSpy(i);
              break;
            case SQUARE:
              isCountable = AmazingNumbers.isSquare(i);
              break;
            case SUNNY:
              isCountable = AmazingNumbers.isSunny(i);
              break;
            case JUMPING:
              isCountable = AmazingNumbers.isJumping(i);
              break;
            case HAPPY:
              isCountable = AmazingNumbers.isHappy(i);
              break;
            case SAD:
              isCountable = AmazingNumbers.isSad(i);
              break;
            default:
          }
          if (!(isCountable)) break;
        }

      if (isCountable && excludedProperties.length > 0) {
        for (var property : excludedProperties) {
          switch (property) {
            case EVEN:
              isCountable = !(AmazingNumbers.isEven(i));
              break;
            case ODD:
              isCountable = !(AmazingNumbers.isOdd(i));
              break;
            case BUZZ:
              isCountable = !(AmazingNumbers.isBuzz(i));
              break;
            case DUCK:
              isCountable = !(AmazingNumbers.isDuck(i));
              break;
            case PALINDROMIC:
              isCountable = !(AmazingNumbers.isPalindromic(i));
              break;
            case GAPFUL:
              isCountable = !(AmazingNumbers.isGapful(i));
              break;
            case SPY:
              isCountable = !(AmazingNumbers.isSpy(i));
              break;
            case SQUARE:
              isCountable = !(AmazingNumbers.isSquare(i));
              break;
            case SUNNY:
              isCountable = !(AmazingNumbers.isSunny(i));
              break;
            case JUMPING:
              isCountable = !(AmazingNumbers.isJumping(i));
              break;
            case HAPPY:
              isCountable = !(AmazingNumbers.isHappy(i));
              break;
            case SAD:
              isCountable = !(AmazingNumbers.isSad(i));
              break;
            default:
          }
          if (!(isCountable)) break;
        }
      }

      if (isCountable) {
        printResumedProperties(i);
        ++counter;
      }
    }
  }

  public static void terminateProgram() {
    System.out.println("Goodbye!");
  }
}
