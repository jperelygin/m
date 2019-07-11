package com.company;


public class Main {

    public static void main(String[] args) {
        //test_makeEmail();
        //test_Mailer_readProps();
        test_send_email();
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
            Mailer mailer = new Mailer("/Users/ivanperelygin/Desktop/JavaPractice/m/mailer.properties");
            System.out.println(mailer.toString());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void test_send_email(){
        Email mail = new Email();
        mail.setTo("jperelygin@gmail.com");
        mail.setFrom("saint@horsefucker.org");
        mail.setTitle("Test title");
        mail.setBody("Big body\nBIGGEST BODY!\n(not so big...)\n");
        System.out.println(mail.toString());
        try {
            Mailer mailer = new Mailer("/Users/ivanperelygin/Desktop/JavaPractice/m/mailer_yandex.properties");
            System.out.println(mailer.toString());
            mailer.sendEmailSSL(mail);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
