package emil.find_course.teacherApplication.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import emil.find_course.common.pagination.PaginationRequest;
import emil.find_course.common.pagination.PaginationUtils;
import emil.find_course.common.pagination.PagingResult;
import emil.find_course.teacherApplication.admin.dto.request.TeacherApplicationUpdateRequest;
import emil.find_course.teacherApplication.dto.TeacherApplicationDto;
import emil.find_course.teacherApplication.entity.TeacherApplication;
import emil.find_course.teacherApplication.enums.TeacherApplicationStatus;
import emil.find_course.teacherApplication.mapper.TeacherApplicationMapper;
import emil.find_course.teacherApplication.repository.TeacherApplicationRepository;
import emil.find_course.user.entity.User;
import emil.find_course.user.enums.Role;
import emil.find_course.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeacherApplicationAdminServiceImpl implements TeacherApplicationAdminService {

    private final TeacherApplicationRepository teacherApplicationRepository;
    private final TeacherApplicationMapper teacherApplicationMapper;
    private final UserRepository userRepository;

    @Override
    public Map<String, Integer> getCountedNewTeacherApplications() {
        Integer newRequests = teacherApplicationRepository.countAllBySeenByAdmin(false);

        return Map.of("newRequests", newRequests);
    }

    @Override
    public PagingResult<TeacherApplicationDto> searchTeacherApplicationDto(TeacherApplicationStatus status,
            Boolean seenByAdmin,
            PaginationRequest request) {
        final Pageable pageable = PaginationUtils.getPageable(request);

        Page<TeacherApplication> teacherApplication = teacherApplicationRepository.searchTeacherApplication(
                status, seenByAdmin, pageable);

        final List<TeacherApplicationDto> teacherApplicationDto = teacherApplication.stream()
                .map(teacherApplicationMapper::toDto)
                .toList();

        return new PagingResult<TeacherApplicationDto>(
                teacherApplicationDto,
                teacherApplication.getTotalPages(),
                teacherApplication.getTotalElements(),
                teacherApplication.getSize(),
                teacherApplication.getNumber(),
                teacherApplication.isEmpty());
    }

    @Override
    public void patchTeacherRequests(List<TeacherApplicationUpdateRequest> updates) {
        Map<UUID, TeacherApplicationUpdateRequest> updateMap = updates.stream()
                .collect(Collectors.toMap(TeacherApplicationUpdateRequest::getId, Function.identity()));

        List<TeacherApplication> listOfEntities = teacherApplicationRepository.findAllById(updateMap.keySet());

        List<User> listOfAcceptedUsers = new ArrayList<>();

        for (TeacherApplication entity : listOfEntities) {
            if (entity.getStatus().equals(TeacherApplicationStatus.ACCEPTED)) {
                continue;
            }

            TeacherApplicationUpdateRequest update = updateMap.get(entity.getId());
            if (update.getStatus() != null) {
                entity.setStatus(update.getStatus());

                if (update.getStatus().equals(TeacherApplicationStatus.ACCEPTED)) {
                    User changedUser = entity.getUser();
                    changedUser.getRoles().add(Role.TEACHER);
                    listOfAcceptedUsers.add(changedUser);
                }
            }
            entity.setSeenByAdmin(true);
        }
        userRepository.saveAll(listOfAcceptedUsers);
        teacherApplicationRepository.saveAll(listOfEntities);
    }

}