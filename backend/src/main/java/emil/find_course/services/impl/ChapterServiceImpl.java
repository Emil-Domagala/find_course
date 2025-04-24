package emil.find_course.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import emil.find_course.domains.entities.course.Chapter;
import emil.find_course.domains.entities.course.Section;
import emil.find_course.domains.requestDto.course.ChapterRequest;
import emil.find_course.services.ChapterService;

@Service
public class ChapterServiceImpl implements ChapterService {

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
        section.getChapters().clear();
        section.getChapters().addAll(finalChapters);
    }

    private void manageNewChapter(ChapterRequest chapterRequest, Chapter chapterToProcess, Section section) {
        chapterToProcess.setPosition(chapterRequest.getPosition() == null ? 0 : chapterRequest.getPosition());
        chapterToProcess.setTitle(chapterRequest.getTitle() == null ? "Default Title" : chapterRequest.getTitle());
        chapterToProcess.setContent(
                chapterRequest.getContent() == null ? "Default Content" : chapterRequest.getContent());
        chapterToProcess.setSection(section);
    }

    private void updateChapter(ChapterRequest chapterRequest, Chapter chapterToProcess, Section section) {
        if (chapterRequest.getPosition() != null) {
            chapterToProcess.setPosition(chapterRequest.getPosition());
        }
        if (chapterRequest.getTitle() != null) {
            chapterToProcess.setTitle(chapterRequest.getTitle());
        }
        if (chapterRequest.getContent() != null) {
            chapterToProcess.setContent(chapterRequest.getContent());
        }

    }

}
