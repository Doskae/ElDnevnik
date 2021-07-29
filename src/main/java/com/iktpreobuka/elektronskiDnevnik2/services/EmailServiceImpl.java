package com.iktpreobuka.elektronskiDnevnik2.services;

import java.io.File;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonView;
import com.iktpreobuka.elektronskiDnevnik2.models.EmailObject;
import com.iktpreobuka.elektronskiDnevnik2.security.Views;

@Service
public class EmailServiceImpl implements EmailService {
	
	@Autowired
	public JavaMailSender emailSender;

	@JsonView(Views.AdminView.class)
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void sendSimpleMessage(EmailObject object) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(object.getTo());
			message.setSubject(object.getSubject());
			message.setText(object.getText());
			emailSender.send(message);
		} catch (MailException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
	}

	@Override
	public void sendMessageWithAttachment(EmailObject object, String pathToAttachment) throws Exception {
		try {
			MimeMessage mail = emailSender.createMimeMessage();
			MimeMessageHelper helper = new
			MimeMessageHelper(mail, true);
			helper.setTo(object.getTo());
			helper.setSubject(object.getSubject());
			helper.setText(object.getText(), false);
			FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
			helper.addAttachment(file.getFilename(), file);
			emailSender.send(mail);
		} catch (MailException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (MessagingException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
	}

}
