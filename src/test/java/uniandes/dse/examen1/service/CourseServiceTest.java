package uniandes.dse.examen1.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import jakarta.transaction.Transactional;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uniandes.dse.examen1.entities.CourseEntity;
import uniandes.dse.examen1.exceptions.RepeatedCourseException;
import uniandes.dse.examen1.services.CourseService;

@DataJpaTest
@Transactional
@Import(CourseService.class)
public class CourseServiceTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    @BeforeEach
    void setUp() {

    }

    @Test
    void testCreateRecordMissingCourse() {
        CourseEntity newCourse = factory.manufacturePojo(CourseEntity.class);
    newCourse.setCourseCode("CURSO404");
    try {
        CourseEntity createdCourse = courseService.createCourse(newCourse);
        assertEquals("CURSO404", createdCourse.getCourseCode(), "El código del curso no coincide");
    } catch (RepeatedCourseException e) {
        fail("No debería lanzarse una excepción en un caso válido");
    }
    }

    @Test
    void testCreateRepeatedCourse() {
        CourseEntity newCourse = factory.manufacturePojo(CourseEntity.class);
        newCourse.setCourseCode("CURSO123");
        try {
            courseService.createCourse(newCourse);
            courseService.createCourse(newCourse);
            fail("Se esperaba una RepeatedCourseException");
        } catch (RepeatedCourseException e) {
            assertEquals("El curso con el codigo CURSO123 ya existe", e.getMessage());
        }
    }
}
