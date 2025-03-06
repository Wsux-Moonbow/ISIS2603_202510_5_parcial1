package uniandes.dse.examen1.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import uniandes.dse.examen1.entities.CourseEntity;
import uniandes.dse.examen1.entities.StudentEntity;
import uniandes.dse.examen1.entities.RecordEntity;
import uniandes.dse.examen1.exceptions.InvalidRecordException;
import uniandes.dse.examen1.repositories.CourseRepository;
import uniandes.dse.examen1.repositories.StudentRepository;
import uniandes.dse.examen1.repositories.RecordRepository;

@Slf4j
@Service
public class RecordService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private RecordRepository recordRepository;

    @Transactional
    public RecordEntity createRecord(String loginStudent, String courseCode, Double grade, String semester)
            throws InvalidRecordException {
        Optional<StudentEntity> student = studentRepository.findByLogin(loginStudent);
        if (!student.isPresent()) {
            throw new InvalidRecordException("El estudiante con el login " + loginStudent + " no existe");
        }
        Optional<CourseEntity> course = courseRepository.findByCourseCode(courseCode);
        if (!course.isPresent()) {
            throw new InvalidRecordException("El curso con el codigo " + courseCode + " no existe");
        }
        RecordEntity recordEntity = new RecordEntity();
        recordEntity.setStudent(student.get());
        recordEntity.setCourse(course.get());
        recordEntity.setFinalGrade(grade);
        recordEntity.setSemester(semester);
        return recordRepository.save(recordEntity);
    }
}
