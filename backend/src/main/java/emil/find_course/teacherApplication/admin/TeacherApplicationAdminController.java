package emil.find_course.teacherApplication.admin;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import emil.find_course.common.pagination.PaginationRequest;
import emil.find_course.common.pagination.PagingResult;
import emil.find_course.teacherApplication.admin.dto.request.TeacherApplicationUpdateRequest;
import emil.find_course.teacherApplication.dto.TeacherApplicationDto;
import emil.find_course.teacherApplication.enums.TeacherApplicationStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/api/v1/admin/teacher-application") // TODO: Change teacher-request to teacher-application on frontend
                                                     // // B4 deploying
@RequiredArgsConstructor
public class TeacherApplicationAdminController {

    private final TeacherApplicationAdminService adminService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<PagingResult<TeacherApplicationDto>> getTeacherApplications(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) Sort.Direction direction,
            @RequestParam(required = false) TeacherApplicationStatus status,
            @RequestParam(required = false) Boolean seenByAdmin) {

        if (sortField == null || !TeacherApplicationDto.ALLOWED_SORT_FIELDS.contains(sortField)) {
            sortField = "createdAt";
        }

        final PaginationRequest request = new PaginationRequest(page, size, sortField, direction);

        final PagingResult<TeacherApplicationDto> becomeTeacher = adminService.searchTeacherApplicationDto(
                status, seenByAdmin, request);

        return ResponseEntity.ok(becomeTeacher);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/notifications") // TODO: Fix path on frontend
    public ResponseEntity<Map<String, Integer>> getCountedNewTeacherApplications() {
        Map<String, Integer> result = adminService.getCountedNewTeacherApplications();
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping
    public ResponseEntity<Void> patchTeacherApplications(
            @NotNull @Size(min = 1, message = "List must not be empty") @RequestBody List<@NotNull @Valid TeacherApplicationUpdateRequest> requests) {

        adminService.patchTeacherRequests(requests);

        return ResponseEntity.noContent().build();
    }

}
