package ru.jperelygin;


public class Main {

    public static void main(String[] args) {
        readArg(args[0]);
    }

    public static void test_makeEmail(){
        Email mail = new Email();
        System.out.println(mail.toString());
        mail.setTo("example@yandex.ru");
        mail.setFrom("me@mail.ru");
        mail.setTitle("Test title");
        mail.setBody("Big body\nBIGGEST BODY!\n(not so big...)\n");
        System.out.println(mail.toString());
    }

    public static void test_Mailer_readProps(){
        try {
            Mailer mailer = new Mailer("./mailer.properties");
            System.out.println(mailer.toString());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void test_send_email(){
        Email mail = new Email();
        mail.setTo("jperelygin@gmail.com");
        mail.setTitle("Test title");
        mail.setBody("Big body\nBIGGEST BODY!\n(not so big...)\n");
        System.out.println(mail.toString());
        send(mail);
    }

    private static void makeMail(){
        MailMaker mm = new MailMaker();
        Email newEmail = mm.make();
        send(newEmail);
    }

    private static void send(Email mail){
        try{
            Mailer mailer = new Mailer("./mailer.properties");
            mail.setFrom(mailer.getSender());
            mailer.sendEmailSSL(mail);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void readArg(String arg){
        switch (arg){
            case "-h":
                printHelp();
                break;
            case "-t":
                test_send_email();
                break;
            case "-s":
                makeMail();
                break;
            default:
                printHelp();
                break;
        }
    }

    private static void printHelp(){
        System.out.println("-- Help for \"m\"");
        System.out.println("-- use argument \"-t\" to send test mail");
        System.out.println("-- use argument\"-h\" to see this help message");
    }
}
