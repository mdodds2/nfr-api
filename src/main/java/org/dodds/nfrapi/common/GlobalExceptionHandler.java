package org.dodds.nfrapi.common;

import lombok.extern.slf4j.Slf4j;
import org.dodds.nfrapi.authentication.InvalidResetTokenException;
import org.dodds.nfrapi.category.CategoryNotFoundException;
import org.dodds.nfrapi.category.DuplicateCategoryException;
import org.dodds.nfrapi.category.DuplicateSubCategoryException;
import org.dodds.nfrapi.category.SubCategoryNotFoundException;
import org.dodds.nfrapi.report.DuplicateReportException;
import org.dodds.nfrapi.report.ReportNotFoundException;
import org.dodds.nfrapi.requirement.DuplicateRequirementException;
import org.dodds.nfrapi.requirement.RequirementNotFoundException;
import org.dodds.nfrapi.users.DuplicateEmailException;
import org.dodds.nfrapi.users.DuplicateUserException;
import org.dodds.nfrapi.users.UserNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDto> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorDto> handleCategoryNotFound(CategoryNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(SubCategoryNotFoundException.class)
    public ResponseEntity<ErrorDto> handleSubCategoryNotFoundException(SubCategoryNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<ErrorDto> handleReportNotFound(ReportNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(RequirementNotFoundException.class)
    public ResponseEntity<ErrorDto> handleRequirementNotFound(RequirementNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDto> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateCategoryException.class)
    public ResponseEntity<ErrorDto> handleDuplicateCategoryException(DuplicateCategoryException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateSubCategoryException.class)
    public ResponseEntity<ErrorDto> handleDuplicateSubCategoryName(DuplicateSubCategoryException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateReportException.class)
    public ResponseEntity<ErrorDto> handleDuplicateReportException(DuplicateReportException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateRequirementException.class)
    public ResponseEntity<ErrorDto> handleDuplicateRequirementException(DuplicateRequirementException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ErrorDto> handleDuplicateUser(DuplicateUserException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorDto> handleDuplicateEmail(DuplicateEmailException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDto> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDto> handleDatabaseError(DataIntegrityViolationException  ex) {
        log.warn("Data integrity violation", ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDto("This conflicts with existing data, for example a duplicate or a record still in use."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleBadArguments(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDto(message));
    }

    @ExceptionHandler(InvalidResetTokenException.class)
    public ResponseEntity<ErrorDto> handlePasswordResetException(InvalidResetTokenException  ex) {
        log.warn("Invalid password reset token");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDto("Something went wrong.  Password Reset has failed"));
    }

}
