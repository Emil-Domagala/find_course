package emil.find_course.controllers;

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

import emil.find_course.domains.dto.ApiErrorResponse;
import emil.find_course.exceptions.FieldValidationException;
import emil.find_course.security.jwt.JwtAuthException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@RestController
@ControllerAdvice
@Slf4j
public class ErrorController {

        // All exceptions
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiErrorResponse> handResponse(Exception ex) {
                log.error("Caught exception", ex);
                ApiErrorResponse error = ApiErrorResponse.builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .message("An unexpected error occured").build();
                return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);

        }

        // Auth

        @ExceptionHandler(JwtAuthException.class)
        public ResponseEntity<Object> handleJwtAuthException(JwtAuthException ex) {
                ApiErrorResponse errorResponse = new ApiErrorResponse();
                errorResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                errorResponse.setMessage(ex.getMessage());

                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
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
                                .message("Access Denied").build();
                return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);

        }

        // Entity

        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<ApiErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
                ApiErrorResponse error = ApiErrorResponse.builder().status(HttpStatus.NOT_FOUND.value())
                                .message(ex.getMessage()).build();
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(IllegalStateException.class)
        public ResponseEntity<ApiErrorResponse> handleIllegalStateException(IllegalStateException ex) {
                ApiErrorResponse error = ApiErrorResponse.builder().status(HttpStatus.BAD_REQUEST.value())
                                .message(ex.getMessage()).build();
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
                ApiErrorResponse error = ApiErrorResponse.builder().status(HttpStatus.BAD_REQUEST.value())
                                .message(ex.getMessage()).build();
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

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

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(
                        HttpMessageNotReadableException ex) {
                ApiErrorResponse error = ApiErrorResponse.builder().status(HttpStatus.BAD_REQUEST.value())
                                .message(ex.getMessage()).build();
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

}
