package br.edu.ifma.dai.processadorsparql;

import org.mentawai.mail.EmailException;
import org.mentawai.mail.SimpleEmail;

public class SendMail {

    public static void main(String[] args) {
	try {
	    sendMail("thiagonasper@gmail.com", "teste", "teste msg");
	} catch (EmailException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public static void sendMail(String destinatario, String subject, String message) throws EmailException {
	SimpleEmail email = new SimpleEmail();
	SimpleEmail.setSSLConnection(true);
	email.setSmtpPort(465);
	email.setHostName("smtp.gmail.com");
	email.setAuthentication("thiagonasper", "nasper!()$");
	email.setFrom("thiagonasper@gmail.com");
	// email.setHostName("smtp.tre-ma.gov.br");
	// email.setAuthentication("thiago.pereira", "340863");
	// email.setFrom("thiago.pereira@tre-ma.gov.br"); // aqui necessita ser
	// o email que voce fara a autenticacao
	email.addTo(destinatario); // pode ser qualquer um email
	email.setSubject(subject);
	email.setMsg(message);
	email.setDebug(true);
	email.send();
    }
}