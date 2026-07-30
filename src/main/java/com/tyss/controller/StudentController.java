package com.tyss.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tyss.dto.StudentDTO;
import com.tyss.entity.Course;
import com.tyss.entity.Student;
import com.tyss.entity.User;
import com.tyss.repo.CourseRepo;
import com.tyss.repo.StudentRepo;
import com.tyss.repo.UserRepo;
import com.tyss.service.StudentService;
import com.tyss.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StudentController {
	
	@Autowired
	private StudentService studentService;

	private final UserRepo userRepo;

	private final StudentRepo studentRepo;

	private final CourseRepo courseRepo;

	private final UserService userService;

	@GetMapping("/add-student")
	public String addStudentPage(Model model, Principal principal) {

		Integer uid = userService.getUid(principal);

		List<Course> courses = courseRepo.findByUserUid(uid);

		model.addAttribute("student", new StudentDTO());
		model.addAttribute("courses", courses);

		return "add-student";
	}

	@PostMapping("/add-student")
	public String saveStudent(@ModelAttribute("student") StudentDTO dto, Principal principal) {
		

		Optional<User> opt = userRepo.findByEmail(principal.getName());

		if (opt.isEmpty()) {
			return "redirect:/login?msg=Please Login Again!!";
		}

		List<Course> selectedCourses = courseRepo.findAllById(dto.getCourseIds());

		Student student = new Student();
		student.setName(dto.getName());
		student.setEmail(dto.getEmail());
		student.setCourses(selectedCourses);
		student.setUser(opt.get());

		studentService.saveStudent(student);

		return "redirect:/dashboard?msg=Student added successfully!!";
	}

	@GetMapping("/view-students")
	public String viewStudentPage(Principal principal, Model model) {

		Integer uid = userService.getUid(principal);

		List<Student> students = studentRepo.findByUserUid(uid);

		model.addAttribute("students", students);

		return "view-students";
	}
	
	
	//Edit-Student
	@GetMapping("/edit-student")
	public String editStudentPage(@RequestParam Integer sid, Model model,Principal principal) {
		
		Integer uid = userService.getUid(principal);

		//   this added for extra security so Two User's could not update each other students
		Optional<Student> optStudent =
		        studentRepo.findByIdAndUserUid(sid, uid);

		if (optStudent.isEmpty()) {
		    return "redirect:/dashboard?msg=Unauthorized Access!";
		}

		Student studentEntity = optStudent.get();
       //
		
	    StudentDTO dto = new StudentDTO();

	    BeanUtils.copyProperties(studentEntity, dto);

	    List<Integer> courseIds = studentEntity.getCourses()
	            .stream()
	            .map(course -> course.getId())
	            .toList();

	    dto.setCourseIds(courseIds);
	    List<Course> courses = courseRepo.findByUserUid(uid);

	    model.addAttribute("student", dto);
	    model.addAttribute("courses",courses );

	    return "edit-student";
	}
	
	
	
	@PostMapping("/edit-student")
	public String editStudent(StudentDTO dto,Principal principal) {
        //
		Integer uid = userService.getUid(principal);

	    Optional<Student> optStudent =
	            studentRepo.findByIdAndUserUid(dto.getId(), uid);

	    if (optStudent.isEmpty()) {
	        return "redirect:/dashboard?msg=Unauthorized Access!";
	    }

	    Student studentEntity = optStudent.get();
	    //
	    studentEntity.setName(dto.getName());
	    studentEntity.setEmail(dto.getEmail());

	    List<Course> selectedCourses =
	            courseRepo.findAllById(dto.getCourseIds());

	    studentEntity.setCourses(selectedCourses);

//	    studentRepo.save(studentEntity);
	    studentService.saveStudent(studentEntity);

	    return "redirect:/dashboard?msg=Student updated successfully!!";
	}
	
	
	@GetMapping("/delete-student")
	public String deleteStudent(@RequestParam Integer sid,Principal principal) {
		    //
		    Integer uid = userService.getUid(principal);

		    Optional<Student> optStudent =
		            studentRepo.findByIdAndUserUid(sid, uid);

		    if (optStudent.isEmpty()) {
		        return "redirect:/dashboard?msg=Unauthorized Access!";
		    }

		    Student student = optStudent.get();
		    //
	    List<Course> courses = student.getCourses();

	    for (Course course : courses) {

	        List<Student> students = course.getStudents();

	        students.remove(student);

	        course.setStudents(students);

	        courseRepo.save(course);
	    }

	    studentRepo.deleteById(sid);

	    return "redirect:/dashboard?msg=Student deleted successfully!!";
	}
}
