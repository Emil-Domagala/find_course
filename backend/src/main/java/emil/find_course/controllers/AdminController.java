package emil.find_course.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import emil.find_course.domains.dto.BecomeTeacherDto;
import emil.find_course.domains.dto.CourseDto;
import emil.find_course.domains.enums.BecomeTeacherStatus;
import emil.find_course.domains.pagination.PaginationRequest;
import emil.find_course.domains.pagination.PagingResult;
import emil.find_course.domains.requestDto.BecomeTeacherUpdateRequest;
import emil.find_course.mapping.BecomeTeacherMapping;
import emil.find_course.services.AdminService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final BecomeTeacherMapping becomeTeacherMapping;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("teacher-requests")
    public ResponseEntity<PagingResult<BecomeTeacherDto>> getTeacherRequests(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) Sort.Direction direction,
            @RequestParam(required = false) BecomeTeacherStatus status,
            @RequestParam(required = false) Boolean seenByAdmin) {

if(size>100){size=100;}

        if (seenByAdmin == null) {
            seenByAdmin = false;
        }
        if (sortField == null) {
            sortField = "createdAt";
        }

        final PaginationRequest request = new PaginationRequest(page, size, sortField, direction);

        final PagingResult<BecomeTeacherDto> becomeTeacher = adminService.searchBecomeTeacherDto(
                status, seenByAdmin, request);

        return ResponseEntity.ok(becomeTeacher);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("notifications/teacher-requests")
    public ResponseEntity<Map<String, Integer>> getNotifications() {
        System.out.println("getNotifications");
        Map<String, Integer> result = adminService.getNotifications();
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("teacher-requests")
    public ResponseEntity<Void> patchTeacherRequests(@RequestBody List<BecomeTeacherUpdateRequest> requests) {

        adminService.patchTeacherRequests(requests);

        return ResponseEntity.noContent().build();
    }

}
