package com.company;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class Mailer {

    private String login;
    private String password;
    private String host;
    private int startSslPort;
    private int sslTlsPort;

    Mailer(String propertiesFilePath) throws IOException {
        Properties prop = new Properties();
        FileInputStream inputStream = new FileInputStream(propertiesFilePath);
        prop.load(inputStream);
        this.login = prop.getProperty("Login");
        this.password = prop.getProperty("Password");
        this.host = prop.getProperty("Host");
        this.startSslPort = Integer.parseInt(prop.getProperty("StartSslPort"));
        this.sslTlsPort = Integer.parseInt(prop.getProperty("SslTlsPort"));
    }

    @Override
    public String toString(){
        return "Login:\t\t\t" + this.login + "\n" +
                "Password:\t\t" + this.password + "\n" +
                "Host:\t\t\t" + this.host + "\n" +
                "StartSslPort:\t" + this.startSslPort + "\n" +
                "SslTlsPort:\t\t" + this.sslTlsPort + "\n";
    }

    public void sendEmailViaSMTPwithTLS(Email mail){
        System.out.println("-- preparing tls propeties");
        Properties prop = new Properties();
        String password = this.password;
        String host = this.host;
        prop.put("mail.smtp.host", this.host);
        prop.put("mail.smtp.port", String.valueOf(this.startSslPort));
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(host, password);
            }
        };

        Session session = Session.getInstance(prop, auth);

        try{
            System.out.println("-- Sending email");
            sendEmail(session, mail);
        } catch (Exception e){
            e.printStackTrace();
        }

    }


    public void sendEmailSSL(Email mail){
        System.out.println("-- preparing ssl propeties");
        Properties prop = new Properties();
        String password = this.password;
        String host = this.host;
        prop.put("mail.smtp.host", this.host);
        prop.put("mail.smtp.socketFactory.port", String.valueOf(this.sslTlsPort));
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.port", String.valueOf(this.sslTlsPort));

        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(host, password);
            }
        };

        Session session = Session.getInstance(prop, auth);

        try{
            System.out.println("-- sending email");
            sendEmail(session, mail);
            System.out.println("-- email sent");
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    private void sendEmail(Session session, Email mail) throws MessagingException {
        MimeMessage msg = new MimeMessage(session);
        msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
        msg.addHeader("format", "flowed");
        msg.addHeader("Content-Transfer-Encoding", "8bit");

        msg.setFrom(new InternetAddress(mail.getFrom()));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail.getTo()));

        msg.setSubject(mail.getTitle());
        msg.setText(mail.getBody());
        msg.setSentDate(new Date());

        Transport.send(msg);
        System.out.println("-- email sent");
    }
}
