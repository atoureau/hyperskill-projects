package correcter;

import java.io.*;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.StringJoiner;

public class Main {
  public static void main(String[] args) {
    final var input = new Scanner(System.in);
    File inputFile;
    File outputFile;
    byte[] bytes;

    System.out.println("Write a mode: ");
    final var mode = Message.Modes.valueOf(input.nextLine().toUpperCase());

    switch (mode) {
      case DECODE:
        inputFile = new File("received.txt");
        outputFile = new File("decoded.txt");
        bytes = Message.readFile(inputFile, mode);
        Message.writeFile(outputFile, mode, Message.decode(bytes));
        break;
      case ENCODE:
        inputFile = new File("send.txt");
        outputFile = new File("encoded.txt");
        bytes = Message.readFile(inputFile, mode);
        Message.writeFile(outputFile, mode, new byte[][] {Message.encode(bytes)});
        break;
      case SEND:
        inputFile = new File("encoded.txt");
        outputFile = new File("received.txt");
        bytes = Message.readFile(inputFile, mode);
        Message.writeFile(outputFile, mode, new byte[][] {Message.send(bytes)});
        break;
      default:
    }
  }
}

class Message {
  public enum Modes {
    DECODE,
    ENCODE,
    SEND
  }

  private static final Random GENERATOR = new Random();

  private Message() {}

  public static byte[] readFile(File file, Modes mode) {
    byte[] bytes;

    try (final var input = new FileInputStream(file)) {
      bytes = input.readAllBytes();
    } catch (IOException exception) {
      System.out.println("Exception when reading the file: " + exception.getMessage());
      bytes = new byte[0];
    }

    switch (mode) {
      case DECODE:
        System.out.printf(
            "received.txt:%n" + "%s%n" + "%s", getHexadecimalView(bytes), getBinaryView(bytes));
        break;
      case ENCODE:
        System.out.printf(
            "send.txt:%n" + "text view: %s%n" + "%s%n" + "%s",
            new String(bytes), getHexadecimalView(bytes), getBinaryView(bytes));
        break;
      case SEND:
        System.out.printf(
            "encoded.txt:%n" + "%s%n" + "%s", getHexadecimalView(bytes), getBinaryView(bytes));
        break;
      default:
    }

    return bytes;
  }

  public static void writeFile(File file, Modes mode, byte[][] bytes) {
    try (final var output = new FileOutputStream(file)) {
      output.write(bytes[0]);
    } catch (IOException exception) {
      System.out.println("Exception when writing the file: " + exception.getMessage());
    }

    switch (mode) {
      case DECODE:
        final var correct = new StringJoiner(" ").add("correct:");
        for (var row : bytes[1]) correct.add(getBinary(row));

        final var decode = new StringJoiner(" ").add("decode:");
        for (var row : bytes[0]) decode.add(getBinary(row));

        System.out.printf(
            "%n%n" + "decoded.txt:%n" + "%s%n" + "%s%n" + "%s%n" + "text view: %s",
            correct, decode, getHexadecimalView(bytes[0]), new String(bytes[0]));
        break;
      case ENCODE:
        final var expands = new StringJoiner(" ").add("expand:");
        final var parities = getBinaryView(bytes[0]).replace("bin view:", "parity:");

        for (var index = 0; index < bytes[0].length; index++) {
          var expand = new StringBuilder(getBinary(bytes[0][index]));

          for (var i = 0; i < expand.length(); i++)
            if (Integer.bitCount(i + 1) == 1) expand.setCharAt(i, '.');

          expands.add(expand);
        }

        System.out.printf(
            "%n%n" + "encoded.txt:%n" + "%s%n" + "%s%n" + "%s",
            expands, parities, getHexadecimalView(bytes[0]));
        break;
      case SEND:
        System.out.printf(
            "%n%n" + "received.txt:%n" + "%s%n" + "%s",
            getBinaryView(bytes[0]), getHexadecimalView(bytes[0]));
        break;
      default:
    }
  }

  public static byte[] encode(byte[] bytes) {
    final var binaries = new StringBuilder();
    for (var bits : bytes) binaries.append(getBinary(bits));
    return encodeBytes(binaries.toString());
  }

  public static byte[] send(byte[] bytes) {
    for (var index = 0; index < bytes.length; index++)
      bytes[index] = Message.changeBit(getBinary(bytes[index]));

    return bytes;
  }

  public static byte[][] decode(byte[] bytes) {
    final var correct = Arrays.copyOf(bytes, bytes.length);

    for (var index = 0; index < bytes.length; index++)
      correct[index] = correctByte(getBinary(bytes[index]));

    final var decode = decodeBytes(correct);

    return new byte[][] {decode, correct};
  }

  private static byte[] encodeBytes(String binaries) {
    final var output = new ByteArrayOutputStream();
    var binaryIndex = 0;

    for (var index = 0; index < binaries.length() / 8 * 2; index++) {
      var bits = new StringBuilder("........");

      for (var bit = 0; bit < bits.length(); bit++)
        if (Integer.bitCount(bit + 1) != 1) bits.setCharAt(bit, binaries.charAt(binaryIndex++));

      output.write(Integer.parseInt(getParity(bits), 2));
    }

    return output.toByteArray();
  }

  private static String getParity(StringBuilder binary) {
    for (var index = 0; index < binary.length(); index++)
      if (Integer.bitCount(index + 1) == 1) {
        var ones = getOnes(index, binary.toString(), Modes.ENCODE);
        binary.setCharAt(index, ones % 2 == 0 ? '0' : '1');
      }
    return binary.toString();
  }

  private static byte changeBit(String binary) {
    final var bin = new StringBuilder();
    int bits;

    do {
      bin.append(binary);
      var index = Message.GENERATOR.nextInt(bin.length() - 1);
      var bit = bin.charAt(index) == '0' ? '1' : '0';
      bin.setCharAt(index, bit);
      bits = Integer.parseInt(bin.toString(), 2);
      bin.delete(0, bin.length());
    } while (bits >= Byte.MIN_VALUE && bits <= Byte.MAX_VALUE);

    return (byte) bits;
  }

  private static byte correctByte(String binary) {
    final var output = new ByteArrayOutputStream();
    final var bits = new StringBuilder(binary);
    var wrongBit = 0;

    for (var index = 0; index < bits.length(); index++)
      if (Integer.bitCount(index + 1) == 1) {
        var ones = getOnes(index, binary, Modes.DECODE);
        var isEven = ones % 2 == 0;
        var isZero = bits.charAt(index) == '0';
        if ((isEven && !(isZero)) || (!(isEven) && isZero)) wrongBit += index + 1;
      }

    bits.setCharAt(wrongBit - 1, bits.charAt(wrongBit - 1) == '0' ? '1' : '0');
    output.write(Integer.parseInt(bits.toString(), 2));
    return output.toByteArray()[0];
  }

  private static byte[] decodeBytes(byte[] bytes) {
    final var output = new ByteArrayOutputStream();
    final var binaries = new StringBuilder();

    for (var bits : bytes) {
      var binary = getBinary(bits);

      for (var index = 0; index < binary.length(); index++)
        if (Integer.bitCount(index + 1) != 1) binaries.append(binary.charAt(index));

      if (binaries.length() == 8) {
        output.write(Integer.parseInt(binaries.toString(), 2));
        binaries.delete(0, binaries.length());
      }
    }

    return output.toByteArray();
  }

  private static int getOnes(int parityBit, String binary, Modes mode) {
    final var parityIndex = parityBit + 1;
    var ones = 0;

    for (var parity = parityBit; parity < binary.length(); parity += parityIndex * 2)
      for (var bit = parity; bit < parity + parityIndex && bit < binary.length(); bit++) {
        if ((mode == Modes.ENCODE && binary.charAt(bit) == '1')
            || (mode == Modes.DECODE && bit != parityBit && binary.charAt(bit) == '1')) ++ones;
      }

    return ones;
  }

  private static String getBinary(byte bits) {
    var binary = String.format("%8s", Integer.toBinaryString(bits));

    if (binary.length() > 8) {
      binary = binary.substring(binary.length() - 8);
    } else binary = binary.replace(' ', '0');

    return binary;
  }

  private static String getBinaryView(byte[] bytes) {
    final var binaryView = new StringJoiner(" ").add("bin view:");
    for (var bits : bytes) binaryView.add(getBinary(bits));
    return binaryView.toString();
  }

  private static String getHexadecimalView(byte[] bytes) {
    final var hexadecimalView = new StringJoiner(" ").add("hex view:");
    for (var bits : bytes) hexadecimalView.add(String.format("%02x", bits).toUpperCase());
    return hexadecimalView.toString();
  }
}
