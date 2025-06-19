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
import emil.find_course.course.chapter.mapper.ChapterMapper;
import emil.find_course.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Chapter Controller", description = "Endpoints for Chapter")
@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterMapper chapterMapper;
    private final ChapterService chapterService;

    @Operation(summary = "Get full Chapter information", description = "Get Chapter by id if user is enrolled in course")
    @GetMapping("/courses/{courseId}/chapters/{chapterId}")
    public ResponseEntity<ChapterProtectedDto> getChapter(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @PathVariable UUID chapterId) {
        final User user = userDetails.getUser();
        final ChapterProtectedDto chapter = chapterMapper
                .toProtectedDto(chapterService.getChapterIfStudentEnrolled(courseId, chapterId, user));
        return ResponseEntity.ok(chapter);
    }

}
