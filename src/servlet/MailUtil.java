import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class MailUtil {

    public static void sendOrderConfirmation(
            String customerEmail,
            String customerName,
            String foodName,
            int quantity,
            String address) throws Exception {

        // Your email account
        String senderEmail = System.getenv("MAIL_USERNAME");
        String senderPassword = System.getenv("MAIL_PASSWORD");

        if (senderEmail == null || senderPassword == null) {
            throw new Exception("Mail environment variables are missing.");
        }

        // Gmail SMTP settings
        Properties props = new Properties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Create mail session
        Session mailSession = Session.getInstance(
                props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                senderEmail,
                                senderPassword
                        );
                    }
                }
        );

        // Create email
        Message message = new MimeMessage(mailSession);

        message.setFrom(new InternetAddress(senderEmail));

        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(customerEmail)
        );

        message.setSubject(
                "FoodieHub - Order Placed Successfully"
        );

        String emailBody =
                "Hello " + customerName + ",\n\n"
                + "Your FoodieHub order has been placed successfully!\n\n"
                + "Order Details\n"
                + "-----------------------------\n"
                + "Food: " + foodName + "\n"
                + "Quantity: " + quantity + "\n\n"
                + "Delivery Address\n"
                + "-----------------------------\n"
                + address + "\n\n"
                + "Thank you for ordering with FoodieHub!\n\n"
                + "FoodieHub Team";

        message.setText(emailBody);

        // Send email    
        }
}