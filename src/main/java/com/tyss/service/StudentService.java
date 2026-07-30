package com.tyss.service;

import com.tyss.entity.Student;
import com.tyss.repo.StudentRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    @Autowired
    private StudentRepo studentRepository;

    @Autowired
    private MailService mailService;

    public void saveStudent(Student student) {
    	
    	boolean isNewStudent = (student.getId() == null);

        studentRepository.save(student);
        
        
        if(isNewStudent) {
        	mailService.sendStudentAddedMail(
        			 student.getEmail(),
                     student.getName()
        			);
        }


    }
}