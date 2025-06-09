package emil.find_course.IntegrationTests.teacherApplication.admin;

import java.util.UUID;

import emil.find_course.teacherApplication.admin.dto.request.TeacherApplicationUpdateRequest;
import emil.find_course.teacherApplication.enums.TeacherApplicationStatus;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class TeacherApplicationUpdateRequestFactory {
    public static TeacherApplicationUpdateRequest createTeacherApplicationUpdateRequest(UUID id) {
        return TeacherApplicationUpdateRequest.builder().id(id).build();
    }

    public static TeacherApplicationUpdateRequest createTeacherApplicationUpdateRequest(UUID id, Boolean seenByAdmin) {
        return TeacherApplicationUpdateRequest.builder().id(id).seenByAdmin(seenByAdmin).build();
    }
    
    public static TeacherApplicationUpdateRequest createTeacherApplicationUpdateRequest(UUID id, Boolean seenByAdmin,TeacherApplicationStatus status) {
        return TeacherApplicationUpdateRequest.builder().id(id).seenByAdmin(seenByAdmin).status(status).build();
    }
}
