package emil.find_course.domains.dto;

import java.util.Set;
import java.util.UUID;

import emil.find_course.domains.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private UUID id;
    private String email;
    private String username;
    private String userLastname;
    private Set<Role> roles;
}