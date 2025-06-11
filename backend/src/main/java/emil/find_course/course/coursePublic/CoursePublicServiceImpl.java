package emil.find_course.course.coursePublic;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import emil.find_course.common.pagination.PaginationRequest;
import emil.find_course.common.pagination.PaginationUtils;
import emil.find_course.common.pagination.PagingResult;
import emil.find_course.course.dto.CourseDto;
import emil.find_course.course.entity.Course;
import emil.find_course.course.enums.CourseCategory;
import emil.find_course.course.enums.CourseStatus;
import emil.find_course.course.mapper.CourseMapping;
import emil.find_course.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoursePublicServiceImpl implements CoursePublicService {

    private final CourseRepository courseRepository;
    private final CourseMapping courseMapping;

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
}
