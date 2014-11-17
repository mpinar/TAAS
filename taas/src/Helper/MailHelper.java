package Helper;

import java.security.SecureRandom;
import java.util.Random;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import Model.Instructor;


public class MailHelper {


	private static final Random RANDOM = new SecureRandom();

	/** Length of password. @see #generateRandomPassword() */
	public static final int PASSWORD_LENGTH = 12;


	/**
	 * Generate a random String suitable for use as a temporary password.
	 *
	 * @return String suitable for use as a temporary password
	 * 
	 */
	public String generateRandomPassword()
	{
		// Pick from some letters that won't be easily mistaken for each
		// other. So, for example, omit o O and 0, 1 l and L.
		String letters = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789+@";

		String pw = "";
		for (int i=0; i<PASSWORD_LENGTH; i++)
		{
			int index = (int)(RANDOM.nextDouble()*letters.length());
			pw += letters.substring(index, index+1);
		}
		return pw;
	}

	/**
	 * Sends password information to an Instructor.
	 * @param i
	 */
	public void sendEmail(Instructor i, String rp){
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("taassigner@gmail.com","TAASSAAT");
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("taassigner@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(i.getMail()));
			message.setSubject("TA Assigner Password");
			message.setText("Dear "+i.getName()+" "+i.getSurname()+
					"\n\nYour password for TA Assigner application is: " + rp +"\nWe advise you to change your password with in the application.\n\nHave a nice day.");

			Transport.send(message);


		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}





}
