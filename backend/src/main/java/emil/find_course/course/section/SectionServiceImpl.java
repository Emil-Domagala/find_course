package emil.find_course.course.section;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import emil.find_course.course.chapter.ChapterService;
import emil.find_course.course.entity.Course;
import emil.find_course.course.section.dto.request.SectionRequest;
import emil.find_course.course.section.entity.Section;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SectionServiceImpl implements SectionService {

    private final ChapterService chapterService;

    @Override
    public void syncSections(Course course, List<SectionRequest> sectionRequests) {
        List<Section> oldSections = course.getSections();
        Map<UUID, Section> oldSectionsMap = oldSections.stream()
                .collect(Collectors.toMap(Section::getId, Function.identity()));
        List<Section> finalSections = new ArrayList<>();
        for (SectionRequest sectionRequest : sectionRequests) {
            Section sectionToProcess;

            if (sectionRequest.getId() != null) {
                sectionToProcess = oldSectionsMap.get(sectionRequest.getId());
                if (sectionToProcess == null) {
                    throw new EntityNotFoundException("Section with id " + sectionRequest.getId() + " not found");
                }

                updateSection(sectionRequest, sectionToProcess, course);
                finalSections.add(sectionToProcess);
            } else {
                sectionToProcess = new Section();

                manageNewSection(sectionRequest, sectionToProcess, course);
                finalSections.add(sectionToProcess);

            }
            if (sectionRequest.getChapters() != null) {
                chapterService.syncChapter(sectionToProcess, sectionRequest.getChapters());
            } else {
                sectionToProcess.getChapters().clear();
            }
        }

        for (int i = 0; i < finalSections.size(); i++) {
            finalSections.get(i).setPosition(i);
        }

        course.getSections().clear();
        course.getSections().addAll(finalSections);
    }

    private void manageNewSection(SectionRequest sectionRequest, Section sectionToProcess, Course course) {
        sectionToProcess.setTitle(sectionRequest.getTitle() == null ? "Default Title" : sectionRequest.getTitle());
        sectionToProcess.setDescription(
                sectionRequest.getDescription() == null ? "Default Description" : sectionRequest.getDescription());
        sectionToProcess.setCourse(course);
        sectionToProcess.setChapters(new ArrayList<>());
    }

    private void updateSection(SectionRequest sectionRequest, Section sectionToProcess, Course course) {

        if (sectionRequest.getTitle() != null) {
            sectionToProcess.setTitle(sectionRequest.getTitle());
        }
        if (sectionRequest.getDescription() != null) {
            sectionToProcess.setDescription(sectionRequest.getDescription());
        }

    }

}
