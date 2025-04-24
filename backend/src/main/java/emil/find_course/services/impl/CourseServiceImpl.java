package emil.find_course.services.impl;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import emil.find_course.domains.dto.CourseDto;
import emil.find_course.domains.entities.course.Course;
import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.enums.CourseCategory;
import emil.find_course.domains.enums.CourseStatus;
import emil.find_course.domains.enums.Level;
import emil.find_course.domains.enums.Role;
import emil.find_course.domains.pagination.PaginationRequest;
import emil.find_course.domains.pagination.PagingResult;
import emil.find_course.domains.requestDto.course.CourseRequest;
import emil.find_course.exceptions.UnauthorizedException;
import emil.find_course.mapping.CourseMapping;
import emil.find_course.repositories.CourseRepository;
import emil.find_course.services.CourseService;
import emil.find_course.services.FileStorageService;
import emil.find_course.services.SectionService;
import emil.find_course.utils.PaginationUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapping courseMapping;
    private final SectionService sectionService;
    private final FileStorageService fileStorageService;

    // **************************
    // ----------Public----------
    // **************************

    @Override
    public Course getById(UUID id) {
        return courseRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Course not found"));
    }

    @Override
    public Course getPublishedCourse(UUID id) {
        return courseRepository.findByIdAndStatus(id, CourseStatus.PUBLISHED);
    }

    @Override
    public PagingResult<CourseDto> searchCourses(String keyword, CourseCategory category, PaginationRequest request) {
        final Pageable pageable = PaginationUtils.getPageable(request);

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
    @Transactional
    public void updateCourse(UUID courseId, CourseRequest courseRequest, MultipartFile image, User user,
            Map<String, MultipartFile> videos) {
        Course course = getById(courseId);
        if (course.getTeacher().getId() != user.getId()) {
            throw new UnauthorizedException("You are not the teacher of this course");
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
            course.setStatus(courseRequest.getStatus());
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
            sectionService.syncSections(course, courseRequest.getSections(),videos);

        }

        course.getSections().toString();
        // courseRepository.save(course);

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
