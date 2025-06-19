package emil.find_course.course.courseTeacher;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import emil.find_course.common.exception.ForbiddenException;
import emil.find_course.common.pagination.PaginationRequest;
import emil.find_course.common.pagination.PaginationUtils;
import emil.find_course.common.pagination.PagingResult;
import emil.find_course.common.service.FileStorageService;
import emil.find_course.course.CourseService;
import emil.find_course.course.dto.CourseDto;
import emil.find_course.course.dto.request.CourseRequest;
import emil.find_course.course.entity.Course;
import emil.find_course.course.enums.CourseCategory;
import emil.find_course.course.enums.CourseStatus;
import emil.find_course.course.enums.Level;
import emil.find_course.course.mapper.CourseMapper;
import emil.find_course.course.repository.CourseRepository;
import emil.find_course.course.section.SectionService;
import emil.find_course.user.entity.User;
import emil.find_course.user.enums.Role;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseTeacherServiceImpl implements CourseTeacherService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final SectionService sectionService;
    private final FileStorageService fileStorageService;
    private final CourseService courseService;

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
    @Transactional
    public void updateCourse(UUID courseId, CourseRequest courseRequest, MultipartFile image, User user) {
        Course course = courseService.getById(courseId);
        if (!course.getTeacher().getId().equals(user.getId())) {
            throw new ForbiddenException("You are not the teacher of this course");
        }

        if (courseRequest.getTitle() != null) {
            course.setTitle(courseRequest.getTitle());
        }
        if (courseRequest.getDescription() != null) {
            course.setDescription(courseRequest.getDescription());
        }
        if (courseRequest.getCategory() != null) {
            course.setCategory(courseRequest.getCategory());
        }
        if (courseRequest.getPrice() != null) {
            course.setPrice(courseRequest.getPrice());
        }
        if (courseRequest.getLevel() != null) {
            course.setLevel(courseRequest.getLevel());
        }
        if (courseRequest.getStatus() != null) {
            if (courseRequest.getStatus() == CourseStatus.DRAFT) {
                if (courseRepository.countStudentsByCourse(course) == 0) {
                    course.setStatus(courseRequest.getStatus());
                }
            } else {
                course.setStatus(courseRequest.getStatus());
            }
        }

        String oldImageUrl = course.getImageUrl();
        if (image != null) {
            InputStream resizedImage = fileStorageService.resizeImage(image, 800, 16, 9, 512_000);
            String imgUrl = fileStorageService.saveProcessedImage(resizedImage, "Course", image.getOriginalFilename());
            course.setImageUrl(imgUrl);
            if (oldImageUrl != null) {
                fileStorageService.deleteImage(oldImageUrl);
            }
        }

        if (courseRequest.getSections() != null) {
            sectionService.syncSections(course, courseRequest.getSections());
        } else {
            course.getSections().clear();
        }

        courseRepository.save(course);

    }

    @Override
    public PagingResult<CourseDto> searchTeacherCourses(String keyword, CourseCategory category,
            PaginationRequest request, User teacher) {
        final Pageable pageable = PaginationUtils.getPageable(request);
        final Page<Course> courses = courseRepository.searchTeacherCourses(keyword, category,
                teacher.getId(), pageable);
        final List<CourseDto> coursesDto = courses.stream().map(courseMapper::toDto).toList();

        return new PagingResult<CourseDto>(
                coursesDto,
                courses.getTotalPages(),
                courses.getTotalElements(),
                courses.getSize(),
                courses.getNumber(),
                courses.isEmpty());
    }

    @Transactional
    @Override
    public UUID deleteCourse(UUID id, UUID teacherId) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));
        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new IllegalArgumentException("You are not the teacher of this course");
        }
        if (courseRepository.countStudentsByCourse(course) > 0) {
            throw new IllegalArgumentException("You cannot delete this course because it has students enrolled");
        }
        course.getTeacher().getTeachingCourses().remove(course);
        course.getStudents().clear();
        courseRepository.delete(course);

        return id;
    }
}
