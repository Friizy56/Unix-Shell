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
                System.out.print(input.substring(5));
            }

            else if(input.startsWith("type ")){ 
                String command = input.substring(5); //after type is written after type
                if(command.matches("type|echo|exit")){
                    System.out.printf("%s is a shell builtin",command);
                }
                else{
                    String path = System.getenv("PATH");
                    boolean found = false;

                    for(String dir : path.split(":")){

                        File file = new File(dir , command);

                        if(file.isFile() && file.canExecute()){
                            System.out.printf("%s is %s", command, file.getAbsolutePath());
                            found = true;
                            break;
                        }
                    }
                    if(!found) {
                        System.out.printf("%s: not found", command);
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
                System.out.printf("%s: not found", command[0]);
            }

            }
            System.out.println();
        }
    }
}
