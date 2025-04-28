package emil.find_course.domains.entities.courseProgress;

import java.util.UUID;

import emil.find_course.domains.entities.course.Chapter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "chapter_progress", uniqueConstraints = {
       @UniqueConstraint(name = "uk_section_prog_chapter_orig", columnNames = {"section_progress", "original_chapter"})
})
public class ChapterProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

     @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_progress", nullable = false)
    private SectionProgress sectionProgress;

     @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "original_chapter", nullable = false) 
    private Chapter originalChapter;

    @Column(nullable = false)
    private int position;

    @Column(nullable = false)
    @Builder.Default
    private boolean completed = false;

    // @Column(nullable = false)
    // private int lastPosition;

}
