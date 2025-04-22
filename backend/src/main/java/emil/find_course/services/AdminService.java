package emil.find_course.services;

import java.util.List;
import java.util.Map;

import emil.find_course.domains.dto.BecomeTeacherDto;
import emil.find_course.domains.pagination.PaginationRequest;
import emil.find_course.domains.pagination.PagingResult;
import emil.find_course.domains.requestDto.BecomeTeacherUpdateRequest;

public interface AdminService {

    Map<String, Integer> getNotifications();

    PagingResult<BecomeTeacherDto> searchBecomeTeacherDto(Boolean seenByAdmin, PaginationRequest request);

    void patchTeacherRequests(List<BecomeTeacherUpdateRequest> updates);
}
