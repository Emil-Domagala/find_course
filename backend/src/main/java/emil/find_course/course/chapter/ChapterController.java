package emil.find_course.course.chapter;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emil.find_course.common.security.jwt.UserDetailsImpl;
import emil.find_course.course.chapter.dto.prot.ChapterProtectedDto;
import emil.find_course.course.chapter.mapper.ChapterMapping;
import emil.find_course.user.entity.User;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterMapping chapterMapping;
    private final ChapterService chapterService;

    @GetMapping("/user/courses/{courseId}/chapters/{chapterId}")
    public ResponseEntity<ChapterProtectedDto> getChapter(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID chapterId) {
        final User user = userDetails.getUser();
        final ChapterProtectedDto chapter = chapterMapping
                .toProtectedDto(chapterService.getChapterIfStudentEnrolled(chapterId, user));
        return ResponseEntity.ok(chapter);
    }

}
