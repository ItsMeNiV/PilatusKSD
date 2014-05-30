package CH.PilatusKSD;


import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mailer {

	private String username;
	private String password;
	
	
	public Mailer(String songname, String artistname, String emailAdress, String emailPassword){		
 
		username = emailAdress;
		password = emailPassword;
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
 
		try {
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("yannik.hodel@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse("yannik.hodel@gmail.com"));
			message.setSubject("PilatusKSD - Doppelter Song entdeckt!");
			message.setText("Ein doppelter Song wurde entdeckt!"
					+ "\n\n Songname: " + songname + "\n Artistname: " + artistname);
 
			Transport.send(message);
 
			System.out.println("Mail sent!");
 
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
	
}