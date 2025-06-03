package emil.find_course.course.section.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import emil.find_course.course.section.entity.Section;

@Repository
public interface SectionRepository extends JpaRepository<Section, UUID> {

}
