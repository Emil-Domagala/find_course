package emil.find_course.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import emil.find_course.domains.entities.course.Chapter;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, UUID> {

}
