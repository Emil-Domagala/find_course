package emil.find_course.services.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import emil.find_course.domains.dto.CourseDto;
import emil.find_course.domains.entities.course.Course;
import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.enums.CourseCategory;
import emil.find_course.domains.enums.CourseStatus;
import emil.find_course.domains.enums.Level;
import emil.find_course.domains.enums.Role;
import emil.find_course.domains.pagination.PaginationRequest;
import emil.find_course.domains.pagination.PagingResult;
import emil.find_course.domains.requestDto.RequestCourseBody;
import emil.find_course.mapping.CourseMapping;
import emil.find_course.repositories.CourseRepository;
import emil.find_course.services.CourseService;
import emil.find_course.utils.PaginationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapping courseMapping;

    // **************************
    // ----------Public----------
    // **************************

    @Override
    public Course getPublishedCourse(UUID id) {
        return courseRepository.findByIdAndStatus(id, CourseStatus.PUBLISHED);
    }

    @Override
    public PagingResult<CourseDto> searchCourses(String keyword, CourseCategory category, PaginationRequest request) {
        final Pageable pageable = PaginationUtils.getPageable(request);
        System.out.println(category);
        final Page<Course> courses = courseRepository.searchCourses(keyword, CourseStatus.PUBLISHED, category,
                pageable);
        final List<CourseDto> coursesDto = courses.stream().map(courseMapping::toDto).toList();

        return new PagingResult<CourseDto>(
                coursesDto,
                courses.getTotalPages(),
                courses.getTotalElements(),
                courses.getSize(),
                courses.getNumber(),
                courses.isEmpty());
    }

    // **************************
    // ---------Teacher----------
    // **************************
    @Override
    public Course createCourse(User teacher) {
        if (!teacher.getRoles().contains(Role.TEACHER)) {
            throw new IllegalStateException("Only teachers can create courses.");
        }

        Course course = Course.builder().teacher(teacher).title("Untitled Course")
                .description("Course Description").category(CourseCategory.PROGRAMMING)
                .imageUrl(
                        "https://flowservedystrybucja.pl/wp-content/themes/u-design/assets/images/placeholders/post-placeholder.jpg")
                .price(0)
                .level(Level.BEGINNER).status(CourseStatus.DRAFT).build();

        return courseRepository.save(course);
    }

    @Override
    public PagingResult<CourseDto> searchTeacherCourses(String keyword, CourseCategory category,
            PaginationRequest request, User teacher) {
        final Pageable pageable = PaginationUtils.getPageable(request);
        System.out.println(teacher.getId());
        final Page<Course> courses = courseRepository.searchTeacherCourses(keyword, category,
                teacher.getId(), pageable);
        final List<CourseDto> coursesDto = courses.stream().map(courseMapping::toDto).toList();

        return new PagingResult<CourseDto>(
                coursesDto,
                courses.getTotalPages(),
                courses.getTotalElements(),
                courses.getSize(),
                courses.getNumber(),
                courses.isEmpty());
    }

    // @Override
    // public Course updateCourse(RequestCourseBody requestCourseBody, User teacher)
    // {
    // if (!teacher.getRoles().contains(Role.TEACHER)) {
    // throw new IllegalStateException("Only teachers can create courses.");
    // }

    // }

    @Transactional
    @Override
    public UUID deleteCourse(UUID id, UUID teacherId) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new IllegalArgumentException("You are not the teacher of this course");
        }
        courseRepository.delete(course);

        return id;
    }

    // **************************
    // ---------Student----------
    // **************************

    @Override
    public PagingResult<CourseDto> getUserEnrolledCourses(User student, PaginationRequest request) {
        final Pageable pageable = PaginationUtils.getPageable(request);

        final Page<Course> courses = courseRepository.findAllByStudents(student, pageable);
        final List<CourseDto> coursesDto = courses.stream().map(courseMapping::toDto).toList();

        return new PagingResult<CourseDto>(
                coursesDto,
                courses.getTotalPages(),
                courses.getTotalElements(),
                courses.getSize(),
                courses.getNumber(),
                courses.isEmpty());

    }

}
