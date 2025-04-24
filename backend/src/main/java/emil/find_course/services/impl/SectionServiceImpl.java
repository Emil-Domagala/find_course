package emil.find_course.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import emil.find_course.domains.entities.course.Course;
import emil.find_course.domains.entities.course.Section;
import emil.find_course.domains.requestDto.course.SectionRequest;
import emil.find_course.services.ChapterService;
import emil.find_course.services.SectionService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SectionServiceImpl implements SectionService {

    private final ChapterService chapterService;

    @Override
    public void syncSections(Course course, List<SectionRequest> sectionRequests, Map<String, MultipartFile> videos) {
        List<Section> oldSections = course.getSections();
        Map<UUID, Section> oldSectionsMap = oldSections.stream()
                .collect(Collectors.toMap(Section::getId, Function.identity()));
        List<Section> finalSections = new ArrayList<>();
        for (SectionRequest sectionRequest : sectionRequests) {
            Section sectionToProcess;

            if (sectionRequest.getId() != null) {
                sectionToProcess = oldSectionsMap.get(sectionRequest.getId());
                if (sectionToProcess == null) {
                    throw new IllegalArgumentException("Section not found");
                }

                updateSection(sectionRequest, sectionToProcess, course);
                finalSections.add(sectionToProcess);
            } else {
                sectionToProcess = new Section();

                manageNewSection(sectionRequest, sectionToProcess, course);
                finalSections.add(sectionToProcess);

            }
            chapterService.syncChapter(sectionToProcess, sectionRequest.getChapters(),videos);
        }
        course.getSections().clear();
        course.getSections().addAll(finalSections);
    }

    private void manageNewSection(SectionRequest sectionRequest, Section sectionToProcess, Course course) {
        sectionToProcess.setPosition(sectionRequest.getPosition() == null ? 0 : sectionRequest.getPosition());
        sectionToProcess.setTitle(sectionRequest.getTitle() == null ? "Default Title" : sectionRequest.getTitle());
        sectionToProcess.setDescription(
                sectionRequest.getDescription() == null ? "Default Description" : sectionRequest.getDescription());
        sectionToProcess.setCourse(course);
        sectionToProcess.setChapters(new ArrayList<>());

    }

    private void updateSection(SectionRequest sectionRequest, Section sectionToProcess, Course course) {

        if (sectionRequest.getPosition() != null) {
            sectionToProcess.setPosition(sectionRequest.getPosition());
        }
        if (sectionRequest.getTitle() != null) {
            sectionToProcess.setTitle(sectionRequest.getTitle());
        }
        if (sectionRequest.getDescription() != null) {
            sectionToProcess.setDescription(sectionRequest.getDescription());
        }

    }

}
