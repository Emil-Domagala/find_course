package emil.find_course.admin;

import java.util.List;
import java.util.Map;

import emil.find_course.admin.dto.BecomeTeacherUpdateRequest;
import emil.find_course.common.pagination.PaginationRequest;
import emil.find_course.common.pagination.PagingResult;
import emil.find_course.user.becomeTeacher.dto.BecomeTeacherDto;
import emil.find_course.user.becomeTeacher.enums.BecomeTeacherStatus;

public interface AdminService {

    Map<String, Integer> getNotifications();

    PagingResult<BecomeTeacherDto> searchBecomeTeacherDto(BecomeTeacherStatus requestStatus,Boolean seenByAdmin, PaginationRequest request);

    void patchTeacherRequests(List<BecomeTeacherUpdateRequest> updates);
}
