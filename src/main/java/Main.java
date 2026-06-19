import java.io.File;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) throws Exception {
        // TODO: Uncomment the code below to pass the first stage
        
        Scanner sc = new Scanner(System.in);
        
        while(true){
            System.out.print("$ ");
            String input= sc.nextLine();
            if (input.equals("exit")){
                break;
            }
            else if (input.startsWith("echo ")){
                System.out.println(input.substring(5));
            }

            else if(input.equals("pwd")){
                System.out.println(System.getProperty("user.dir"));
            }

            else if(input.startsWith("cd ")){
                String directory = input.substring(3);

                File dir = new File(directory);

                if(dir.exists() && dir.isDirectory()){
                    System.setProperty("user.dir", dir.getAbsolutePath());
                }
                else{
                    System.out.println("cd: " + directory + ": No such file or directory");
                }
            }

            else if(input.startsWith("type ")){ 
                String command = input.substring(5); // after type is written

                if(command.matches("type|echo|exit|pwd|cd")){
                    System.out.println(command + " is a shell builtin");
                }

                else{
                    String path = System.getenv("PATH");
                    boolean found = false;

                    for(String dir : path.split(":")){

                        File file = new File(dir , command);

                        if(file.isFile() && file.canExecute()){
                            System.out.println(command + " is " + file.getAbsolutePath());
                            found = true;
                            break;
                        }
                    }
                    if(!found) {
                        System.out.println( command +": not found");
                    }
                    
                }
            }
            else{
            // System.out.printf("%s: command not found",input);


            String[] command = input.split(" ");
            String path = System.getenv("PATH");
            boolean found = false;

            for(String dir : path.split(":")){

                File file = new File(dir , command[0]);

                if(file.isFile() && file.canExecute()){
                    ProcessBuilder pb = new ProcessBuilder(command);
                    pb.inheritIO();

                    Process process = pb.start();
                    process.waitFor();

                    found = true;
                    break;
                }
            }

            if(!found) {
                System.out.println(command[0] + ": command not found");
            }

            }
            // System.out.println();
        }
    }
}
