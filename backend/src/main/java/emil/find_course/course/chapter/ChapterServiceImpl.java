package emil.find_course.course.chapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import emil.find_course.course.chapter.dto.request.ChapterRequest;
import emil.find_course.course.chapter.entity.Chapter;
import emil.find_course.course.chapter.repository.ChapterRepository;
import emil.find_course.course.section.entity.Section;
import emil.find_course.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChapterServiceImpl implements ChapterService {

    private final ChapterRepository chapterRepository;

    @Override
    public Chapter getChapterIfStudentEnrolled(UUID courseId, UUID chapterId, User user) {
        Chapter chapter = chapterRepository.findChapterByIdIfUserEnrolled(
                courseId, chapterId, user)
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
        // TODO: For now Video Url are fine but in the future create presigned.
        if (chapterRequest.getVideoUrl() != null) {
            chapterToProcess.setVideoUrl(chapterRequest.getVideoUrl());
        }
    }

    private void updateChapter(ChapterRequest chapterRequest, Chapter chapterToProcess, Section section) {
        if (chapterRequest.getTitle() != null) {
            chapterToProcess.setTitle(chapterRequest.getTitle());
        }
        if (chapterRequest.getContent() != null) {
            chapterToProcess.setContent(chapterRequest.getContent());
        }
        if (chapterRequest.getVideoUrl() != null) {
            chapterToProcess.setVideoUrl(chapterRequest.getVideoUrl());
        }

    }

}
