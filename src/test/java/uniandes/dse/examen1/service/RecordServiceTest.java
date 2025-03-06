package uniandes.dse.examen1.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import jakarta.transaction.Transactional;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uniandes.dse.examen1.entities.CourseEntity;
import uniandes.dse.examen1.entities.StudentEntity;
import uniandes.dse.examen1.entities.RecordEntity;
import uniandes.dse.examen1.exceptions.RepeatedCourseException;
import uniandes.dse.examen1.exceptions.RepeatedStudentException;
import uniandes.dse.examen1.exceptions.InvalidRecordException;
import uniandes.dse.examen1.repositories.CourseRepository;
import uniandes.dse.examen1.repositories.StudentRepository;
import uniandes.dse.examen1.services.CourseService;
import uniandes.dse.examen1.services.StudentService;
import uniandes.dse.examen1.services.RecordService;

@DataJpaTest
@Transactional
@Import({ RecordService.class, CourseService.class, StudentService.class })
public class RecordServiceTest {

    @Autowired
    private RecordService recordService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CourseRepository courseRepository;

    private PodamFactory factory = new PodamFactoryImpl();

    private String login;
    private String courseCode;

    @BeforeEach
    void setUp() throws RepeatedCourseException, RepeatedStudentException {
        CourseEntity newCourse = factory.manufacturePojo(CourseEntity.class);
        newCourse = courseService.createCourse(newCourse);
        courseCode = newCourse.getCourseCode();

        StudentEntity newStudent = factory.manufacturePojo(StudentEntity.class);
        newStudent = studentService.createStudent(newStudent);
        login = newStudent.getLogin();
    }

    /**
     * Tests the normal creation of a record for a student in a course
     */
    @Test
    void testCreateRecord() {
        try {
            String semester = "2024-1";
            Double grade = 4.0;
            RecordEntity record = recordService.createRecord(login, courseCode, grade, semester);
    
            assertEquals(login, record.getStudent().getLogin(), "El login del estudiante no coincide");
            assertEquals(courseCode, record.getCourse().getCourseCode(), "El codigo del curso no coincide");
            assertEquals(grade, record.getFinalGrade(), "La nota final no coincide");
            assertEquals(semester, record.getSemester(), "El semestre no coincide");
        } catch (InvalidRecordException e) {
            fail("No deberia lanzarse una excepcion en un caso valido");
        }
    }
    }

    /**
     * Tests the creation of a record when the login of the student is wrong
     */
    @Test
    void testCreateRecordMissingStudent() {
        try {
            recordService.createRecord("noExiste", courseCode, 3.5, "2024-1");
            fail("Se esperaba una InvalidRecordException");
        } catch (InvalidRecordException e) {
            assertEquals("El estudiante con el login noExiste no existe", e.getMessage());
        }
    }

    /**
     * Tests the creation of a record when the course code is wrong
     */
    @Test
    void testCreateInscripcionMissingCourse() {
        try {
            recordService.createRecord(login, "CURSO404", 3.5, "2024-1");
            fail("Se esperaba una InvalidRecordException");
        } catch (InvalidRecordException e) {
            assertEquals("El curso con el codigo CURSO404 no existe", e.getMessage());
        }
    }

    /**
     * Tests the creation of a record when the grade is not valid
     */
    @Test
    void testCreateInscripcionWrongGrade() {
        try {
            recordService.createRecord(login, courseCode, 1.0, "2024-1");
            fail("Se esperaba una InvalidRecordException por nota inválida");
        } catch (InvalidRecordException e) {
            
        }
    }

    /**
     * Tests the creation of a record when the student already has a passing grade
     * for the course
     */
    @Test
    void testCreateInscripcionRepetida1() {
        try {
            recordService.createRecord(login, courseCode, 4.5, "2024-1");
            recordService.createRecord(login, courseCode, 3.0, "2024-2");
            fail("Se esperaba una InvalidRecordException porque el estudiante ya aprobó");
        } catch (InvalidRecordException e) {
            
        }
    }
    

    /**
     * Tests the creation of a record when the student already has a record for the
     * course, but he has not passed the course yet.
     */
    @Test
    void testCreateInscripcionRepetida2() {
        try {
            recordService.createRecord(login, courseCode, 2.0, "2024-1");
            recordService.createRecord(login, courseCode, 3.0, "2024-2");
        } catch (InvalidRecordException e) {
            fail("No debería lanzarse una excepción, el estudiante no ha aprobado aún");
        }
    }
}
