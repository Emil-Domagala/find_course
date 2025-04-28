package emil.find_course.domains.entities.courseProgress;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import emil.find_course.domains.entities.course.Section;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
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
@Table(name = "section_progress", uniqueConstraints = {
        @UniqueConstraint(name = "uk_course_prog_section_orig", columnNames = { "course_progress",
                "original_section_id" }) })
public class SectionProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_progress", nullable = false)
    private CourseProgress courseProgress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_section", nullable = false)
    private Section originalSection;

    @Column(nullable = false)
    private int position;

    @OneToMany(mappedBy = "sectionProgress", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("position ASC")
    @Builder.Default
    private List<ChapterProgress> chapters = new ArrayList<>();

}
