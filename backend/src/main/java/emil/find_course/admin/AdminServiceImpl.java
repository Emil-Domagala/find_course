package emil.find_course.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import emil.find_course.admin.dto.BecomeTeacherUpdateRequest;
import emil.find_course.common.pagination.PaginationRequest;
import emil.find_course.common.pagination.PaginationUtils;
import emil.find_course.common.pagination.PagingResult;
import emil.find_course.user.becomeTeacher.dto.BecomeTeacherDto;
import emil.find_course.user.becomeTeacher.entity.BecomeTeacher;
import emil.find_course.user.becomeTeacher.enums.BecomeTeacherStatus;
import emil.find_course.user.becomeTeacher.mapper.BecomeTeacherMapper;
import emil.find_course.user.becomeTeacher.repository.BecomeTeacherRepository;
import emil.find_course.user.entity.User;
import emil.find_course.user.enums.Role;
import emil.find_course.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final BecomeTeacherRepository becomeTeacherRepository;
    private final BecomeTeacherMapper becomeTeacherMapper;
    private final UserRepository userRepository;

    @Override
    public Map<String, Integer> getNotifications() {
        Integer newRequests = becomeTeacherRepository.countAllBySeenByAdmin(false);

        return Map.of("newRequests", newRequests);
    }

    @Override
    public PagingResult<BecomeTeacherDto> searchBecomeTeacherDto(BecomeTeacherStatus status, Boolean seenByAdmin,
            PaginationRequest request) {
        final Pageable pageable = PaginationUtils.getPageable(request);

        Page<BecomeTeacher> becomeTeacher = becomeTeacherRepository.searchBecomeTeacherRequest(
                status, seenByAdmin, pageable);

        final List<BecomeTeacherDto> becomeTeacherDto = becomeTeacher.stream().map(becomeTeacherMapper::toDto)
                .toList();

        return new PagingResult<BecomeTeacherDto>(
                becomeTeacherDto,
                becomeTeacher.getTotalPages(),
                becomeTeacher.getTotalElements(),
                becomeTeacher.getSize(),
                becomeTeacher.getNumber(),
                becomeTeacher.isEmpty());
    }

    @Override
    public void patchTeacherRequests(List<BecomeTeacherUpdateRequest> updates) {
        Map<UUID, BecomeTeacherUpdateRequest> updateMap = updates.stream()
                .collect(Collectors.toMap(BecomeTeacherUpdateRequest::getId, Function.identity()));
        List<BecomeTeacher> listOfEntities = becomeTeacherRepository.findAllById(updateMap.keySet());

        List<User> listOfAcceptedUsers = new ArrayList<>();

        for (BecomeTeacher entity : listOfEntities) {
            BecomeTeacherUpdateRequest update = updateMap.get(entity.getId());
            if (update.getStatus() != null) {
                entity.setStatus(update.getStatus());
            }
            if (update.getStatus().equals(BecomeTeacherStatus.ACCEPTED)) {
                User changedUser = entity.getUser();
                changedUser.getRoles().add(Role.TEACHER);
                listOfAcceptedUsers.add(changedUser);
            }
            entity.setSeenByAdmin(true);
        }
        userRepository.saveAll(listOfAcceptedUsers);
        becomeTeacherRepository.saveAll(listOfEntities);
    }

}