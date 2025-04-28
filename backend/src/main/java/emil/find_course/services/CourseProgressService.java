package emil.find_course.services;

import java.util.UUID;

import emil.find_course.domains.dto.courseProgress.CourseProgressDto;
import emil.find_course.domains.entities.courseProgress.ChapterProgress;
import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.requestDto.UpdateProgressRequest;

public interface CourseProgressService {

    public CourseProgressDto getCourseProgress(UUID courseId, User user);

    public ChapterProgress updateChapterProgress(UUID courseId, User user, UpdateProgressRequest request);
}
