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
                if(input.substring(5).matches("type|echo|exit")){
                    System.out.printf("%s is a shell builtin",input.substring(5));
                }
                else{
                    System.out.printf("%s: command not found",input.substring(5));
                }
            }
            else{
            System.out.printf("%s: command not found",input);
            }
            System.out.println();
        }
    }
}
