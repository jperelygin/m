import java.util.Scanner;

public class MailMaker {

    public Email make(){
        Email mail = new Email();
        Scanner scan = new Scanner(System.in);

        System.out.println("Enter recipient:");
        mail.setTo(scan.nextLine());
        System.out.println("Enter title:");
        mail.setTitle(scan.nextLine());
        System.out.println("Enter message (enter END to finish entering)");
        StringBuilder sb = new StringBuilder();
        String append;
        do {
           append = scan.nextLine();
           sb.append(append + "\n");
        } while (!append.equals("END"));
        mail.setBody(sb.toString());
        System.out.println("\nOK!");
        return mail;
    }

}
