package emil.find_course.course.chapter.entity;

import java.util.UUID;

import emil.find_course.course.chapter.enums.ChapterType;
import emil.find_course.course.section.entity.Section;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = { "section" })
@EqualsAndHashCode(callSuper = false, of = "id")
@Table(name = "chapters")
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private int position;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ChapterType type = null;

    @Column(nullable = false)
    private String title;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = true)
    private String videoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @PrePersist
    @PreUpdate
    private void onSave() {
        if (videoUrl != null && !videoUrl.isBlank()) {
            type = ChapterType.VIDEO;
        } else {
            type = ChapterType.TEXT;
        }
    }

}
