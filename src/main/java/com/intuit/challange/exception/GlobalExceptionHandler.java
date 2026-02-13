package com.intuit.challange.exception;

import com.intuit.challange.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* ===============================
       VALIDACIONES DTO
       =============================== */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errores.put(error.getField(), error.getDefaultMessage())
                );

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Error de validación en los datos enviados",
                request.getRequestURI(),
                LocalDateTime.now(),
                errores
        );

        return ResponseEntity.badRequest().body(response);
    }

    /* ===============================
       ERROR ENUM / PARAM
       =============================== */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {

        String mensaje;

        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {

            mensaje = "El valor '" + ex.getValue() +
                    "' no es válido para el parámetro '" + ex.getName() +
                    "'. Valores permitidos: " +
                    String.join(", ",
                            Arrays.toString(ex.getRequiredType()
                                    .getEnumConstants())
                    );
        } else {
            mensaje = "Parámetro inválido: " + ex.getName();
        }

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                mensaje,
                request.getRequestURI(),
                LocalDateTime.now(),
                null
        );

        return ResponseEntity.badRequest().body(response);
    }

    /* ===============================
       UNIQUE / CONSTRAINT DB
       =============================== */
    @ExceptionHandler( DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDatabaseErrors(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {

        String mensaje = "Error de integridad en base de datos";

        if (ex.getMostSpecificCause().getMessage().contains("cuit")) {
            mensaje = "Ya existe un cliente con ese CUIT";
        }

        if (ex.getMostSpecificCause().getMessage().contains("email")) {
            mensaje = "Ya existe un cliente con ese email";
        }

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                mensaje,
                request.getRequestURI(),
                LocalDateTime.now(),
                null
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /* ===============================
       GENERIC ERROR
       =============================== */
    @ExceptionHandler(Exception.class)
    public ResponseEntity< ApiErrorResponse > handleGeneralException(
            Exception ex,
            HttpServletRequest request
    ) {

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Ocurrió un error inesperado. Contacte al administrador",
                request.getRequestURI(),
                LocalDateTime.now(),
                null
        );

        log.error("Error no controlado en: {} - Mensaje: {}", request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler( ArgumentoDuplicadoException.class)
    public ResponseEntity<ApiErrorResponse> handleArgumentoDuplicadoException(
            ArgumentoDuplicadoException ex,
            HttpServletRequest request
    ){
        ApiErrorResponse response = new ApiErrorResponse(
                400,
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now(),
                null
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ClienteNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            ClienteNotFoundException ex,
            HttpServletRequest request
    ) {

        ApiErrorResponse response = new ApiErrorResponse(
                404,
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now(),
                null
        );

        return ResponseEntity.status(404).body(response);
    }


}
