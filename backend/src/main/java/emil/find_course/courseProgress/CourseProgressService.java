package emil.find_course.courseProgress;

import java.util.UUID;

import emil.find_course.courseProgress.dto.CourseProgressDto;
import emil.find_course.courseProgress.dto.request.UpdateProgressRequest;
import emil.find_course.courseProgress.entity.ChapterProgress;
import emil.find_course.user.entity.User;

public interface CourseProgressService {

    public CourseProgressDto getCourseProgress(UUID courseId, User user);

    public ChapterProgress updateChapterProgress(UUID courseId, User user, UpdateProgressRequest request);
}
