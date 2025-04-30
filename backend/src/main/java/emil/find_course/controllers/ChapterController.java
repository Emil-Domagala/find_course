package emil.find_course.controllers;

import java.security.Principal;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emil.find_course.domains.dto.detailsProt.ChapterProtectedDto;
import emil.find_course.domains.entities.user.User;
import emil.find_course.mapping.ChapterMapping;
import emil.find_course.services.ChapterService;
import emil.find_course.services.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ChapterController {

    private final UserService userService;
    private final ChapterMapping chapterMapping;
    private final ChapterService chapterService;

    @GetMapping("/user/courses/{courseId}/chapters/{chapterId}")
    public ResponseEntity<ChapterProtectedDto> getChapter(Principal principal, @PathVariable UUID chapterId) {
        final User user = userService.findByEmail(principal.getName());
        final ChapterProtectedDto chapter = chapterMapping
                .toProtectedDto(chapterService.getChapterIfStudentEnrolled(chapterId, user));
        return ResponseEntity.ok(chapter);
    }

}
