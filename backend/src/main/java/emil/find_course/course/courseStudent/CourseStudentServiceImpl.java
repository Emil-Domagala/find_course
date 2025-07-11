package emil.find_course.course.courseStudent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import emil.find_course.common.pagination.PaginationRequest;
import emil.find_course.common.pagination.PaginationUtils;
import emil.find_course.common.pagination.PagingResult;
import emil.find_course.course.chapter.repository.ChapterRepository;
import emil.find_course.course.chapter.repository.projection.CourseIdChapterIdProjection;
import emil.find_course.course.dto.CourseDto;
import emil.find_course.course.dto.CourseDtoWithFirstChapter;
import emil.find_course.course.entity.Course;
import emil.find_course.course.mapper.CourseMapper;
import emil.find_course.course.repository.CourseRepository;
import emil.find_course.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseStudentServiceImpl implements CourseStudentService {
        private final ChapterRepository chapterRepository;
        private final CourseRepository courseRepository;
        private final CourseMapper courseMapper;

        @Override
        public PagingResult<CourseDtoWithFirstChapter> getUserEnrolledCourses(User student, PaginationRequest request) {
                final Pageable pageable = PaginationUtils.getPageable(request);
                // TODO: Test that students can access course even if course is DRAFT and later
                // soft deletion;
                final Page<Course> coursesPage = courseRepository.findAllByStudents(student, pageable);
                List<Course> courseList = coursesPage.getContent();

                if (courseList.isEmpty()) {
                        return new PagingResult<>(
                                        Collections.emptyList(),
                                        coursesPage.getTotalPages(),
                                        coursesPage.getTotalElements(),
                                        coursesPage.getSize(),
                                        coursesPage.getNumber(),
                                        true);
                }

                List<UUID> courseIds = courseList.stream()
                                .map(Course::getId)
                                .collect(Collectors.toList());

                List<CourseIdChapterIdProjection> chapterIdResults = chapterRepository
                                .findFirstChapterIdsForCourses(courseIds);

                Map<UUID, UUID> firstChapterMap = chapterIdResults.stream()
                                .collect(Collectors.toMap(
                                                CourseIdChapterIdProjection::getCourseId,
                                                CourseIdChapterIdProjection::getChapterId,
                                                (existing, replacement) -> existing));

                List<CourseDtoWithFirstChapter> dtosWithChapter = courseList.stream().map(course -> {
                        CourseDto baseDto = courseMapper.toDto(course);
                        CourseDtoWithFirstChapter dtoWithChapter = new CourseDtoWithFirstChapter();
                        BeanUtils.copyProperties(baseDto, dtoWithChapter);
                        dtoWithChapter.setFirstChapter(firstChapterMap.get(course.getId()));
                        return dtoWithChapter;
                }).collect(Collectors.toList());

                return new PagingResult<>(
                                dtosWithChapter,
                                coursesPage.getTotalPages(),
                                coursesPage.getTotalElements(),
                                coursesPage.getSize(),
                                coursesPage.getNumber(),
                                coursesPage.isEmpty());
        }

        @Override
        public void grantAccessToCourse(User student, Set<Course> courses) {
                log.info("granting access to courses {} to user {}",
                                courses.stream().map(item -> item.getId()).toList(),
                                student.getEmail());
                List<Course> cToSave = new ArrayList<>();

                for (Course c : courses) {
                        c.getStudents().add(student);
                        cToSave.add(c);
                }
                courseRepository.saveAll(cToSave);

        }

}
