package clientUI;

import java.util.Scanner;

//  Read input from the console

public class ConsoleInputReader {
    private static final Scanner scanner = new Scanner(System.in);

    public static String readInput() {
        return scanner.nextLine().trim();
    }
}