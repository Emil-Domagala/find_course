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

    public static final String DEFAULT_SORT_FIELD = "id";
    public static final Sort.Direction DEFAULT_DIRECTION = Sort.Direction.ASC;
    public static final Integer DEFAULT_PAGE = 0;
    public static final Integer DEFAULT_SIZE = 12;

    public PaginationRequest(Integer page, Integer size, String sortField, Sort.Direction direction) {
        this.page = (page == null || page < 0) ? DEFAULT_PAGE : page;
        this.size = (size == null || size <= 0) ? DEFAULT_SIZE : Math.min(size, 100);
        this.sortField = (sortField == null || sortField.isEmpty()) ? DEFAULT_SORT_FIELD : sortField;
        this.direction = (direction == null) ? DEFAULT_DIRECTION : direction;
    }

}
