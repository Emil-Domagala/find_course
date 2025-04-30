package emil.find_course.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import emil.find_course.domains.entities.course.Chapter;
import emil.find_course.domains.entities.course.Section;
import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.requestDto.course.ChapterRequest;
import emil.find_course.repositories.ChapterRepository;
import emil.find_course.services.ChapterService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChapterServiceImpl implements ChapterService {

    private final ChapterRepository chapterRepository;

    @Override
    public Chapter getChapterIfStudentEnrolled(UUID chapterId, User user) {
        Chapter chapter = chapterRepository.findChapterByIdIfUserEnrolled(chapterId, user)
                .orElseThrow(() -> new EntityNotFoundException("Chapter not found or user is not enrolled"));
        return chapter;
    }

    @Override
    public void syncChapter(Section section, List<ChapterRequest> chapterRequests) {
        List<Chapter> oldChapters = section.getChapters();
        Map<UUID, Chapter> oldChaptersMap = oldChapters.stream()
                .collect(Collectors.toMap(Chapter::getId, Function.identity()));

        List<Chapter> finalChapters = new ArrayList<>();

        for (ChapterRequest chapterRequest : chapterRequests) {
            Chapter chapterToProcess;

            if (chapterRequest.getId() != null) {
                chapterToProcess = oldChaptersMap.get(chapterRequest.getId());
                if (chapterToProcess == null) {
                    throw new IllegalArgumentException("Chapter not found");
                }
                // MultipartFile video = videos.get("video_" +
                // chapterRequest.getId().toString());
                updateChapter(chapterRequest, chapterToProcess, section);
                finalChapters.add(chapterToProcess);

            } else {
                chapterToProcess = new Chapter();
                manageNewChapter(chapterRequest, chapterToProcess, section);
                finalChapters.add(chapterToProcess);
            }
        }
        for (int i = 0; i < finalChapters.size(); i++) {
            finalChapters.get(i).setPosition(i);
        }
        section.getChapters().clear();
        section.getChapters().addAll(finalChapters);
    }

    private void manageNewChapter(ChapterRequest chapterRequest, Chapter chapterToProcess, Section section) {
        chapterToProcess.setTitle(chapterRequest.getTitle() == null ? "Default Title" : chapterRequest.getTitle());
        chapterToProcess.setContent(
                chapterRequest.getContent() == null ? "Default Content" : chapterRequest.getContent());
        chapterToProcess.setSection(section);
    }

    private void updateChapter(ChapterRequest chapterRequest, Chapter chapterToProcess, Section section) {
        if (chapterRequest.getTitle() != null) {
            chapterToProcess.setTitle(chapterRequest.getTitle());
        }
        if (chapterRequest.getContent() != null) {
            chapterToProcess.setContent(chapterRequest.getContent());
        }

    }

}
