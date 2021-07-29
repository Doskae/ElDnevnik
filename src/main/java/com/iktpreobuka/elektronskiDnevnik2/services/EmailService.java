package com.iktpreobuka.elektronskiDnevnik2.services;

import com.iktpreobuka.elektronskiDnevnik2.models.EmailObject;

public interface EmailService {
	
	void sendSimpleMessage (EmailObject object);
	
	void sendMessageWithAttachment (EmailObject object,	String pathToAttachment) throws Exception;
			

}
