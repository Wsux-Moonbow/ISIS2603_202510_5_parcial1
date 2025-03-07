package uniandes.dse.examen1.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import uniandes.dse.examen1.entities.CourseEntity;
import uniandes.dse.examen1.exceptions.RepeatedCourseException;
import uniandes.dse.examen1.repositories.CourseRepository;

@Slf4j
@Service
public class CourseService {

    @Autowired
    CourseRepository courseRepository;

    @Transactional
    public CourseEntity createCourse(CourseEntity newCourse) throws RepeatedCourseException {
        Optional<CourseEntity> course = courseRepository.findByCourseCode(newCourse.getCourseCode());
        if (course.isPresent()) {
            throw new RepeatedCourseException("El curso con el codigo " + newCourse.getCourseCode() + " ya existe");
        }
        return courseRepository.save(newCourse);
    }
}
