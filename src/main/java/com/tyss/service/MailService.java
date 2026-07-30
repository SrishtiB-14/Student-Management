package com.tyss.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendStudentAddedMail(String toEmail, String studentName) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("Student Registration Successful");

        message.setText(
            "Hello " + studentName + ",\n\n"
          + "You have been successfully added to our Student Management System.\n"
          + "Welcome aboard!\n\n"
          + "Regards,\nAdmin Team"
        );

        mailSender.send(message);
    }
}