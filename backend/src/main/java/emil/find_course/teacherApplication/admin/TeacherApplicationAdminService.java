package emil.find_course.teacherApplication.admin;

import java.util.List;
import java.util.Map;

import emil.find_course.common.pagination.PaginationRequest;
import emil.find_course.common.pagination.PagingResult;
import emil.find_course.teacherApplication.admin.dto.request.TeacherApplicationUpdateRequest;
import emil.find_course.teacherApplication.dto.TeacherApplicationDto;
import emil.find_course.teacherApplication.enums.TeacherApplicationStatus;

public interface TeacherApplicationAdminService {

    Map<String, Integer> getCountedNewTeacherApplications();

    PagingResult<TeacherApplicationDto> searchTeacherApplicationDto(TeacherApplicationStatus requestStatus,
            Boolean seenByAdmin,
            PaginationRequest request);

    void patchTeacherRequests(List<TeacherApplicationUpdateRequest> updates);
}
