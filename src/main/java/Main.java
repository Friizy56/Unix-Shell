
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    static List<String> parseCommand(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        boolean inSingleQuotes = false;
        boolean inDoubleQuotes = false;
        boolean escaped = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (escaped) {
                current.append(c);
                escaped = false;
            } else if (c == '\\' && inDoubleQuotes) {
                if (i + 1 < input.length()) {
                    char next = input.charAt(i + 1);
                    if (next == '"' || next == '\\') {
                        current.append(next);
                        i++;
                        continue;
                    }
                }

                current.append('\\');

            } else if (c == '\\' && !inSingleQuotes && !inDoubleQuotes) {
                escaped = true;
            } else if (c == '\'' && !inDoubleQuotes) {
                inSingleQuotes = !inSingleQuotes;
            } else if (c == '"' && !inSingleQuotes) {
                inDoubleQuotes = !inDoubleQuotes;
            } else if (c == ' ' && !inSingleQuotes && !inDoubleQuotes) {
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

            List<String> tokens = parseCommand(input);

            // String redirectFile = null;
            String stdoutRedirect = null;
            String stderrRedirect = null;

            for (int i = 0; i < tokens.size(); i++) {
                if (tokens.get(i).equals(">") || tokens.get(i).equals("1>")) {
                    stdoutRedirect = tokens.get(i + 1);
                    tokens = new ArrayList<>(tokens.subList(0, i));
                    break;
                }

                if (tokens.get(i).equals("2>")) {
                    stderrRedirect = tokens.get(i + 1);
                    tokens = new ArrayList<>(tokens.subList(0, i));
                    break;
                }
            }

            if (input.equals("exit")) {
                break;
            } else if (!tokens.isEmpty() && tokens.get(0).equals("echo")) {
                StringBuilder output = new StringBuilder();
                for (int i = 1; i < tokens.size(); i++) {
                    if (i > 1) {
                        output.append(" ");
                    }
                    output.append(tokens.get(i));
                }
                output.append("\n");

                if (stderrRedirect != null) {
                    new java.io.PrintWriter(new File(stderrRedirect)).close();
                }

                if (stdoutRedirect != null) {
                    try (java.io.PrintWriter writer
                            = new java.io.PrintWriter(new File(stdoutRedirect))) {

                        writer.print(output.toString());
                    }
                } else {
                    System.out.print(output.toString());
                }
            } else if (input.equals("pwd")) {
                System.out.println(System.getProperty("user.dir"));
            } else if (input.startsWith("cd ")) {
                String directory = input.substring(3);

                File targetDir;
                if (directory.equals("~")) {

                    String home = System.getenv("HOME"); // for Linux

                    if (home == null) { // for Windows
                        home = System.getProperty("user.home");
                    }

                    targetDir = new File(home);
                } else {
                    File currentDir = new File(System.getProperty("user.dir"));

                    if (directory.startsWith("/")) {
                        targetDir = new File(directory);
                    } else {
                        targetDir = new File(currentDir, directory);
                    }
                }

                if (targetDir.exists() && targetDir.isDirectory()) {
                    System.setProperty("user.dir", targetDir.getCanonicalPath());
                } else {
                    if (stderrRedirect != null) {
                        try (java.io.PrintWriter writer
                                = new java.io.PrintWriter(new File(stderrRedirect))) {

                            writer.println("cd: " + directory + ": No such file or directory");
                        }
                    } else {
                        System.err.println("cd: " + directory + ": No such file or directory");
                    }
                }
            } else if (input.startsWith("type ")) {
                String command = tokens.get(1);

                if (command.matches("type|echo|exit|pwd|cd")) {
                    System.out.println(command + " is a shell builtin");
                } else {
                    String path = System.getenv("PATH");
                    boolean found = false;

                    for (String dir : path.split(File.pathSeparator)) {

                        File file = new File(dir, command);

                        if (file.isFile() && file.canExecute()) {
                            System.out.println(command + " is " + file.getAbsolutePath());
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        if (stderrRedirect != null) {
                            try (java.io.PrintWriter writer
                                    = new java.io.PrintWriter(new File(stderrRedirect))) {

                                writer.println(command + ": not found");
                            }
                        } else {
                            System.err.println(command + ": not found");
                        }
                    }

                }
            } else {

                List<String> command = tokens;
                String path = System.getenv("PATH");
                boolean found = false;

                for (String dir : path.split(File.pathSeparator)) {

                    File file = new File(dir, command.get(0));

                    if (file.isFile() && file.canExecute()) {
                        ProcessBuilder pb = new ProcessBuilder(command);

                        pb.directory(new File(System.getProperty("user.dir")));

                        if (stdoutRedirect != null) {
                            pb.redirectOutput(new File(stdoutRedirect));
                        }

                        if (stderrRedirect != null) {
                            pb.redirectError(new File(stderrRedirect));
                        }

                        if (stdoutRedirect == null && stderrRedirect == null) {
                            pb.inheritIO();
                        }
                        Process process = pb.start();
                        process.waitFor();
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    if (stderrRedirect != null) {
                        try (java.io.PrintWriter writer
                                = new java.io.PrintWriter(new File(stderrRedirect))) {

                            writer.println(command.get(0) + ": command not found");
                        }
                    } else {
                        System.err.println(command.get(0) + ": command not found");
                    }
                }
            }
        }
    }
}
