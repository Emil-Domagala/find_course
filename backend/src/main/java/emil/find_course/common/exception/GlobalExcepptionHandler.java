package emil.find_course.common.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import emil.find_course.common.exception.dto.ApiErrorResponse;
import emil.find_course.payment.stripe.exception.CustomStripeException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestController
@ControllerAdvice
@Slf4j
public class GlobalExcepptionHandler {

        // All exceptions
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiErrorResponse> handResponse(Exception ex) {
                log.error("Caught exception", ex);
                ApiErrorResponse error = ApiErrorResponse.builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .message("An unexpected error occured:" + ex.getMessage()).build();
                return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);

        }

        // Auth

        @ExceptionHandler(UnauthorizedException.class)
        public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex) {

                ApiErrorResponse error = ApiErrorResponse.builder()
                                .status(HttpStatus.UNAUTHORIZED.value()).message(ex.getMessage()).build();

                return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(JwtAuthException.class)
        public ResponseEntity<Object> handleJwtAuthException(JwtAuthException ex) {
                ApiErrorResponse error = ApiErrorResponse.builder()
                                .status(HttpStatus.FORBIDDEN.value()).message(ex.getMessage()).build();

                return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
        }

        @ExceptionHandler(JwtInvalidTokenException.class)
        public ResponseEntity<Object> handleJwtInvalidTokenException(JwtInvalidTokenException ex) {
                ApiErrorResponse error = ApiErrorResponse.builder()
                                .status(498).message(ex.getMessage()).build();

                return ResponseEntity.status(498).body(error);
        }

        @ExceptionHandler(JwtTokenRequiredException.class)
        public ResponseEntity<Object> handleJwtTokenRequiredException(JwtTokenRequiredException ex) {
                ApiErrorResponse error = ApiErrorResponse.builder()
                                .status(499).message(ex.getMessage()).build();

                return ResponseEntity.status(499).body(error);
        }

        @ExceptionHandler(InvalidRefreshTokenException.class)
        public ResponseEntity<Object> handleInvalidRefreshTokenException(InvalidRefreshTokenException ex) {
                ApiErrorResponse error = ApiErrorResponse.builder()
                                .status(440).message(ex.getMessage()).build();

                return ResponseEntity.status(440).body(error);
        }

        @ExceptionHandler({ BadCredentialsException.class, UsernameNotFoundException.class })
        public ResponseEntity<ApiErrorResponse> handAuthException(RuntimeException ex) {
                ApiErrorResponse error = ApiErrorResponse.builder().status(HttpStatus.UNAUTHORIZED.value())
                                .message("Email or password is incorrect").build();
                return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);

        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ApiErrorResponse> handAccesDenied(AccessDeniedException ex) {
                ApiErrorResponse error = ApiErrorResponse.builder().status(HttpStatus.FORBIDDEN.value())
                                .message(ex.getMessage()).build();
                return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);

        }

        @ExceptionHandler(ForbiddenException.class)
        public ResponseEntity<ApiErrorResponse> handForbiddenException(Exception ex) {
                ApiErrorResponse error = ApiErrorResponse.builder().status(HttpStatus.FORBIDDEN.value())
                                .message(ex.getMessage()).build();
                return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);

        }

        @ExceptionHandler({ EmailFilterException.class })
        public ResponseEntity<ApiErrorResponse> handleEmailFilterException(Exception ex) {
                ApiErrorResponse error = ApiErrorResponse.builder().status(HttpStatus.FORBIDDEN.value())
                                .message(ex.getMessage()).build();
                return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);

        }

        // Entity

        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<ApiErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
                ApiErrorResponse error = ApiErrorResponse.builder().status(HttpStatus.NOT_FOUND.value())
                                .message(ex.getMessage()).build();
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler({ IllegalStateException.class, IllegalArgumentException.class })
        public ResponseEntity<ApiErrorResponse> handleIllegalStateException(Exception ex) {
                ApiErrorResponse error = ApiErrorResponse.builder().status(HttpStatus.BAD_REQUEST.value())
                                .message(ex.getMessage()).build();
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(
                        HttpMessageNotReadableException ex) {
                ApiErrorResponse error = ApiErrorResponse.builder().status(HttpStatus.BAD_REQUEST.value())
                                .message(ex.getMessage()).build();
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        // 404
        @ExceptionHandler(NoResourceFoundException.class)
        public ResponseEntity<ApiErrorResponse> handleNoResourceFoundException(
                        NoResourceFoundException ex) {
                ApiErrorResponse error = ApiErrorResponse.builder().status(HttpStatus.NOT_FOUND.value())
                                .message(ex.getMessage()).build();
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        // Payment
        @ExceptionHandler(CustomStripeException.class)
        public ResponseEntity<ApiErrorResponse> handleCustomStripeException(
                        CustomStripeException ex) {
                ApiErrorResponse error = ApiErrorResponse.builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .message(ex.getMessage()).build();
                return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Params Validation
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(
                        MethodArgumentNotValidException ex) {
                List<ApiErrorResponse.FieldError> errors = ex.getBindingResult().getFieldErrors().stream()
                                .map(fieldError -> new ApiErrorResponse.FieldError(fieldError.getField(),
                                                fieldError.getDefaultMessage()))
                                .toList();

                ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message("Validation failed")
                                .errors(errors)
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(FieldValidationException.class)
        public ResponseEntity<ApiErrorResponse> handleFieldValidationException(FieldValidationException ex) {
                List<ApiErrorResponse.FieldError> error = List
                                .of(new ApiErrorResponse.FieldError(ex.getFieldError(), ex.getMessage()));

                ApiErrorResponse errorResponse = ApiErrorResponse.builder().status(HttpStatus.BAD_REQUEST.value())
                                .message("Validation failed").errors(error).build();

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatchException(
                        MethodArgumentTypeMismatchException ex) {
                ApiErrorResponse error = ApiErrorResponse.builder().status(HttpStatus.BAD_REQUEST.value())
                                .message("Invalid type for parameter: " + ex.getName()).build();
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(
                        ConstraintViolationException ex) {
                ApiErrorResponse error = ApiErrorResponse.builder().status(HttpStatus.BAD_REQUEST.value())
                                .message("Validation failed: " + ex.getMessage()).build();
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(MissingServletRequestPartException.class)
        public ResponseEntity<ApiErrorResponse> handleMissingServletRequestPartException(
                        MissingServletRequestPartException ex) {
                ApiErrorResponse error = ApiErrorResponse.builder().status(HttpStatus.BAD_REQUEST.value())
                                .message("Missing request part: " + ex.getRequestPartName()).build();
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

}
