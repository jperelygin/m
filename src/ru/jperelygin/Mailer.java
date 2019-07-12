package ru.jperelygin;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import java.util.logging.*;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class Mailer {

    private static final Logger LOGGER = Logger.getLogger(Mailer.class.getName());
    private static final LogManager logManager = LogManager.getLogManager();

    private String login;
    private String password;
    private String host;
    private int startSslPort;
    private int sslTlsPort;


    Mailer(String propertiesFilePath) throws IOException {

        readLoggerParams();

        Properties prop = new Properties();
        FileInputStream inputStream = new FileInputStream(propertiesFilePath);
        prop.load(inputStream);

        this.login = prop.getProperty("Login");
        this.password = prop.getProperty("Password");
        this.host = prop.getProperty("Host");
        this.startSslPort = Integer.parseInt(prop.getProperty("StartSslPort"));
        this.sslTlsPort = Integer.parseInt(prop.getProperty("SslTlsPort"));
    }

    private static void readLoggerParams(){
        try{
            logManager.readConfiguration(new FileInputStream("./logger.cfg"));
        }catch (Exception e){
            LOGGER.warning(e.toString());
        }
    }

    @Override
    public String toString(){
        return "Login:\t\t\t" + this.login + "\n" +
                "Password:\t\t" + "*".repeat(this.password.length()) + "\n" +
                "Host:\t\t\t" + this.host + "\n" +
                "StartSslPort:\t" + this.startSslPort + "\n" +
                "SslTlsPort:\t\t" + this.sslTlsPort + "\n";
    }

    public String getSender(){
        return this.login;
    }

    public void sendEmailViaSMTPwithTLS(Email mail){
        Properties prop = new Properties();

        prop.put("mail.smtp.host", this.host);
        LOGGER.info("Host : " + this.host);
        prop.put("mail.smtp.port", String.valueOf(this.startSslPort));
        LOGGER.info("Sslport : " + this.startSslPort);
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        Authenticator auth = getAuthenticator();

        logProperties(prop);

        Session session = Session.getInstance(prop, auth);

        try{
            System.out.println("-- Sending email");
            sendEmail(session, mail);
        } catch (Exception e){
            e.printStackTrace();
            LOGGER.info("Exception:" + e);
        }
    }

    public void sendEmailSSL(Email mail){
        Properties prop = new Properties();

        prop.put("mail.smtp.host", this.host);
        LOGGER.info( "Host : " + this.host);
        prop.put("mail.smtp.socketFactory.port", String.valueOf(this.sslTlsPort));
        LOGGER.info("Port : " + this.sslTlsPort);
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.port", String.valueOf(this.sslTlsPort));

        Authenticator auth = getAuthenticator();

        logProperties(prop);

        Session session = Session.getInstance(prop, auth);

        try{
            LOGGER.info("-- sending email");
            sendEmail(session, mail);
            LOGGER.info("-- email sent!");
        } catch (Exception e){
            LOGGER.warning("Exception: " + e.toString());
        }
    }

    private void logProperties(Properties prop){
        LOGGER.config( "Properties:"); //!
        prop.forEach((k,v) -> LOGGER.config(k + " : " + v));
    }

    private Authenticator getAuthenticator(){
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                PasswordAuthentication pwAuth = new PasswordAuthentication(login, password);
                LOGGER.config(pwAuth.toString());
                LOGGER.config(pwAuth.getUserName());
                LOGGER.config(pwAuth.getPassword());
                return pwAuth;
            }
        };
        return auth;
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
    }
}
