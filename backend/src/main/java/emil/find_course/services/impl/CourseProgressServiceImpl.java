package emil.find_course.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import emil.find_course.common.exception.UnauthorizedException;
import emil.find_course.domains.dto.courseProgress.CourseProgressDto;
import emil.find_course.domains.dto.courseProgress.CourseProgressProjection;
import emil.find_course.domains.entities.course.Chapter;
import emil.find_course.domains.entities.course.Course;
import emil.find_course.domains.entities.course.Section;
import emil.find_course.domains.entities.courseProgress.ChapterProgress;
import emil.find_course.domains.entities.courseProgress.CourseProgress;
import emil.find_course.domains.entities.courseProgress.SectionProgress;
import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.requestDto.UpdateProgressRequest;
import emil.find_course.mapping.CourseProgressMapping;
import emil.find_course.repositories.ChapterProgressRepository;
import emil.find_course.repositories.CourseProgressRepository;
import emil.find_course.repositories.CourseRepository;
import emil.find_course.services.CourseProgressService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseProgressServiceImpl implements CourseProgressService {
    private final CourseProgressRepository courseProgressRepository;
    private final CourseRepository courseRepository;
    private final ChapterProgressRepository chapterProgressRepository;
    private final CourseProgressMapping courseProgressMapping;

    @Override
    @Transactional
    public ChapterProgress updateChapterProgress(UUID courseId, User user, UpdateProgressRequest request) {

        ChapterProgress chapterProgress = chapterProgressRepository
                .findByUserCourseAndOriginalChapter(
                        user.getId(),
                        courseId,
                        request.getChapterProgressId())
                .orElseThrow(() -> new EntityNotFoundException("Course progress not found"));

        chapterProgress.setCompleted(request.isCompleted());
        return chapterProgressRepository.save(chapterProgress);
    }

    @Override
    @Transactional
    public CourseProgressDto getCourseProgress(UUID courseId, User user) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));

        Optional<LocalDateTime> courseProgressUpdatedAt = courseProgressRepository
                .findUpdatedAtByCourseIdAndUserId(courseId, user.getId());

        if (!courseRepository.isEnrolled(courseId, user)) {
            throw new UnauthorizedException("User is not enrolled in this course");
        }

        CourseProgressDto courseProgressDto;
        if (courseProgressUpdatedAt.isEmpty()) {
            // This means that the CourseUpdata was not created yet. So i needt to create
            // new Entity.
            System.out.println("Course progress not found");
            courseProgressDto = courseProgressMapping.toDto(createCourseProject(course, user));
        } else if (course.getUpdatedAt().isAfter(courseProgressUpdatedAt.get())) {
            // Course progress might be unsync
            System.out.println("Course progress might be unsync");
            courseProgressDto = courseProgressMapping.toDto(updateCourseProgress(course, user));
        } else {
            // Course progress is sync
            System.out.println("Course progress found");
            CourseProgressProjection courseProgressProjection = courseProgressRepository
                    .findProjectedByCourseIdAndUserId(courseId, user.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Course progress not found"));
            courseProgressDto = courseProgressMapping.toDto(courseProgressProjection);
        }
        return courseProgressDto;
    }

    // UPDATE COURSES PROGRESS BLOCK

    @Transactional
    private CourseProgress updateCourseProgress(Course course, User user) {

        CourseProgress courseProgress = courseProgressRepository.findByCourseIdAndUserId(course.getId(), user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Course progress not found"));

        Map<UUID, SectionProgress> sectionProgressMapByOriginalSectionId = courseProgress.getSections().stream()
                .collect(Collectors.toMap(sp -> sp.getOriginalSection().getId(), Function.identity()));

        List<SectionProgress> sectionsProgressFinall = new ArrayList<>();
        for (Section section : course.getSections()) {
            SectionProgress sectionProgress = sectionProgressMapByOriginalSectionId.get(section.getId());
            if (sectionProgress == null) {
                // There is no Section Progress that means its new Section!
                sectionProgress = new SectionProgress();
                createSectionProgress(courseProgress, section, sectionProgress);
                sectionsProgressFinall.add(sectionProgress);
            } else {
                // Section Progress found, Needs to update position and check Chapters
                updateSectionProgress(section, sectionProgress);
                sectionsProgressFinall.add(sectionProgress);
            }
        }

        courseProgress.getSections().clear();
        courseProgress.getSections().addAll(sectionsProgressFinall);

        CourseProgress savedCourseProgress = courseProgressRepository.save(courseProgress);

        return savedCourseProgress;
    }

    // Section exists
    private void updateSectionProgress(Section section, SectionProgress sectionProgressToProceed) {
        sectionProgressToProceed.setPosition(section.getPosition());
        Map<UUID, ChapterProgress> chapterProgressMapByOriginalChapterId = sectionProgressToProceed.getChapters()
                .stream()
                .collect(Collectors.toMap(cp -> cp.getOriginalChapter().getId(), Function.identity()));

        List<ChapterProgress> chaptersProgressFinall = new ArrayList<>();

        for (Chapter chapter : section.getChapters()) {
            ChapterProgress chapterProgress = chapterProgressMapByOriginalChapterId.get(chapter.getId());
            if (chapterProgress == null) {
                // There is no Chapter Progress that means its new Chapter)
                chapterProgress = new ChapterProgress();
                createChapterProgress(sectionProgressToProceed, chapter, chapterProgress);
                chaptersProgressFinall.add(chapterProgress);
            } else {
                // Chapter Progress found, Needs to update position and check Chapters
                updateChapterProgress(chapter, chapterProgress);
                chaptersProgressFinall.add(chapterProgress);
            }
        }
        sectionProgressToProceed.getChapters().clear();
        sectionProgressToProceed.getChapters().addAll(chaptersProgressFinall);
    }

    // Chapter exists
    private void updateChapterProgress(Chapter chapter, ChapterProgress chapterProgressToProceed) {
        chapterProgressToProceed.setPosition(chapter.getPosition());
    }

    // CREATE NEW COURSES PROGRESS BLOCK

    @Transactional
    private CourseProgress createCourseProject(Course course, User user) {

        CourseProgress courseProgress = CourseProgress.builder().course(course).user(user).build();

        List<SectionProgress> sectionsProgressFinall = new ArrayList<>();
        for (Section section : course.getSections()) {
            SectionProgress sectionProgressToProceed = new SectionProgress();
            ;
            createSectionProgress(courseProgress, section, sectionProgressToProceed);
            sectionsProgressFinall.add(sectionProgressToProceed);
        }

        courseProgress.setSections(sectionsProgressFinall);

        CourseProgress savedCourseProgress = courseProgressRepository.save(courseProgress);

        return savedCourseProgress;
    }

    private void createSectionProgress(CourseProgress courseProgress, Section section,
            SectionProgress sectionProgressToProceed) {
        sectionProgressToProceed.setCourseProgress(courseProgress);
        sectionProgressToProceed.setOriginalSection(section);
        sectionProgressToProceed.setPosition(section.getPosition());

        List<ChapterProgress> chaptersProgressFinall = new ArrayList<>();
        for (Chapter chapter : section.getChapters()) {
            ChapterProgress chapterProgressToProceed = new ChapterProgress();
            createChapterProgress(sectionProgressToProceed, chapter, chapterProgressToProceed);
            chaptersProgressFinall.add(chapterProgressToProceed);
        }
        sectionProgressToProceed.setChapters(chaptersProgressFinall);

    }

    private void createChapterProgress(SectionProgress sectionProgress, Chapter chapter,
            ChapterProgress chapterProgressToProceed) {
        chapterProgressToProceed.setSectionProgress(sectionProgress);
        chapterProgressToProceed.setOriginalChapter(chapter);
        chapterProgressToProceed.setPosition(chapter.getPosition());
    }

}
