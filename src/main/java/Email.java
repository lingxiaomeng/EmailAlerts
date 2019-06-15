import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

class Email {



    static void sendEmail(String content,String EmailAddress,String EmailPassword) throws MessagingException {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.host", "smtp.exmail.qq.com");
        properties.put("mail.debug", "true");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465");
        final String em = EmailAddress;
        final String ep = EmailPassword;
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(em, ep);
            }
        };
        Session session = Session.getDefaultInstance(properties, auth);
        session.setDebug(true);
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(EmailAddress));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(EmailAddress));
        message.setContent(content, "text/html;charset=utf-8");
        message.setSubject("成绩更新");
        message.setSentDate(new Date());
        Transport.send(message);

    }
}
