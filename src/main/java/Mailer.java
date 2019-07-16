import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Authenticator;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class Mailer {

    private static final Logger LOGGER = Logger.getLogger(Mailer.class.getName());
    private static final LogManager logManager = LogManager.getLogManager();

    private String login;
    private String password;
    private String smtpHost;
    private String imapHost;
    private int startSslPort;
    private int sslTlsPort;
    private int imapPort;


    Mailer(String propertiesFilePath) throws IOException {

        readLoggerParams();

        Properties prop = new Properties();
        FileInputStream inputStream = new FileInputStream(propertiesFilePath);
        prop.load(inputStream);

        this.login = prop.getProperty("Login");
        this.password = prop.getProperty("Password");
        this.smtpHost = prop.getProperty("SmtpHost");
        this.imapHost = prop.getProperty("ImapHost");
        this.startSslPort = Integer.parseInt(prop.getProperty("StartSslPort"));
        this.sslTlsPort = Integer.parseInt(prop.getProperty("SslTlsPort"));
        this.imapPort = Integer.parseInt(prop.getProperty("ImapPort"));
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
                "Host:\t\t\t" + this.smtpHost + "\n" +
                "StartSslPort:\t" + this.startSslPort + "\n" +
                "SslTlsPort:\t\t" + this.sslTlsPort + "\n";
    }

    public String getSender(){
        return this.login;
    }

    public void sendEmailViaSMTPwithTLS(Email mail){
        Properties prop = new Properties();

        prop.put("mail.smtp.host", this.smtpHost);
        LOGGER.info("Host : " + this.smtpHost);
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

        prop.put("mail.smtp.host", this.smtpHost);
        LOGGER.info( "Host : " + this.smtpHost);
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

    public void getInbox(){
        Properties prop = new Properties();

        prop.setProperty("mail.imap.ssl.enable", "true");
        prop.setProperty("mail.store.protocol", "imaps");
        prop.setProperty("mail.imap.port", String.valueOf(this.imapPort));

        Authenticator auth = getAuthenticator();

        Session session = Session.getInstance(prop, auth);

        try{
            Store store = session.getStore();
            store.connect(this.imapHost, this.login, this.password);

            Folder inbox = store.getFolder("INBOX");
            printMessages(inbox);

        } catch (Exception e){
            LOGGER.warning(e.toString());
        }
    }

    private void printMessages(Folder folder){
        try {
            folder.open(Folder.READ_ONLY);

            System.out.println("Number of mails : " + folder.getMessageCount());
            LOGGER.info("Number of mails : " + folder.getMessageCount());

            int numberOfMessagesForView = folder.getMessageCount() + 1; // "+1" because they start indexing from 1
            if (folder.getMessageCount() > 11) {
                numberOfMessagesForView = 11;
                LOGGER.info("Number of messages to view : " + numberOfMessagesForView);
            }
            for (int i = 1; i < numberOfMessagesForView; i++) {
                Message message = folder.getMessage(i);
                prettyPrintMailList(message);
            }
        } catch (Exception e){
            LOGGER.warning(e.toString());
        }
    }

    private void prettyPrintMailList(Message message) throws MessagingException{
        String m = String.format("%5s", String.valueOf(message.getMessageNumber())) + "\t" +
                String.format("%15s", message.getReceivedDate().toString()) + "\t" +
                String.format("%15s", message.getFrom()) + "\t" +
                String.format("%20s", message.getSubject());
        System.out.println(m);
        LOGGER.fine(message.toString()); // just logging fact of getting message
    }
}
