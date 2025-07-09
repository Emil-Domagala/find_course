package emil.find_course.common.responseWrapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseWrappper<T, U> {
    T data;
    U meta;
}
