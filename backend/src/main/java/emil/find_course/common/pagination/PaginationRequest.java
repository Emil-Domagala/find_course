package emil.find_course.common.pagination;

import org.springframework.data.domain.Sort;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class PaginationRequest {

    private Integer page;

    private Integer size;

    private String sortField;

    private Sort.Direction direction;

    public PaginationRequest(Integer page, Integer size, String sortField, Sort.Direction direction) {
        this.page = (page == null || page < 0) ? 0 : page;
        this.size = (size == null || size <= 0) ? 12 : size;
        this.sortField = (sortField == null || sortField.isEmpty()) ? "id" : sortField;
        this.direction = (direction == null) ? Sort.Direction.ASC : direction;
    }

}
