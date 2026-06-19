
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    static List<String> parseCommand(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        boolean inSingleQuotes = false;

        for (char c : input.toCharArray()) {

            if (c == '\'') {
                inSingleQuotes = !inSingleQuotes;
            } else if (c == ' ' && !inSingleQuotes) {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            tokens.add(current.toString());
        }
        return tokens;
    }

    public static void main(String[] args) throws Exception {
        // TODO: Uncomment the code below to pass the first stage

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("$ ");
            String input = sc.nextLine();
            if (input.equals("exit")) {
                break;
            } else if (input.startsWith("echo")) {
                List<String> tokens = parseCommand(input);

                for (int i = 1; i < tokens.size(); i++) {
                    if (i > 1) {
                        System.out.print(" ");
                    }
                    System.out.print(tokens.get(i));
                }

                System.out.println();
            } 
            else if (input.equals("pwd")) {
                System.out.println(System.getProperty("user.dir"));
            } 
            else if (input.startsWith("cd ")) {
                String directory = input.substring(3);

                File targetDir;
                if (directory.equals("~")) {
                    // targetDir = new File(System.getenv("HOME"));

                    String home = System.getenv("HOME"); // for Linux

                    if (home == null) { // for Windows
                        home = System.getProperty("user.home");
                    }

                    targetDir = new File(home);
                } 
                else {
                    File currentDir = new File(System.getProperty("user.dir"));

                    if (directory.startsWith("/")) {
                        targetDir = new File(directory);
                    } else {
                        targetDir = new File(currentDir, directory);
                    }
                }

                if (targetDir.exists() && targetDir.isDirectory()) {
                    System.setProperty("user.dir", targetDir.getCanonicalPath());
                } 
                else {
                    System.out.println("cd: " + directory + ": No such file or directory");
                }
            } 
            else if (input.startsWith("type ")) {
                String command = parseCommand(input).get(1);

                if (command.matches("type|echo|exit|pwd|cd")) {
                    System.out.println(command + " is a shell builtin");
                } 
                else {
                    String path = System.getenv("PATH");
                    boolean found = false;

                    for (String dir : path.split(":")) {

                        File file = new File(dir, command);

                        if (file.isFile() && file.canExecute()) {
                            System.out.println(command + " is " + file.getAbsolutePath());
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        System.out.println(command + ": not found");
                    }

                }
            } 
            else {

                String[] command = input.split(" ");
                String path = System.getenv("PATH");
                boolean found = false;

                for (String dir : path.split(":")) {

                    File file = new File(dir, command[0]);

                    if (file.isFile() && file.canExecute()) {
                        ProcessBuilder pb = new ProcessBuilder(command);
                        pb.inheritIO();

                        Process process = pb.start();
                        process.waitFor();

                        found = true;
                        break;
                    }
                }

                if (!found) {
                    System.out.println(command[0] + ": command not found");
                }
            }
        }
    }
}
