package emil.find_course.utils;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import emil.find_course.domains.pagination.PaginationRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaginationUtils {

    public static Pageable getPageable(PaginationRequest request) {
        return PageRequest.of(request.getPage(), request.getSize(), request.getDirection(), request.getSortField());
    }
}