package raisetech.Re.Student.Management.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.Re.Student.Management.controller.StudentConverter;
import raisetech.Re.Student.Management.data.Student;
import raisetech.Re.Student.Management.data.StudentCourse;
import raisetech.Re.Student.Management.domain.StudentDetail;
import raisetech.Re.Student.Management.repository.StudentRepository;


