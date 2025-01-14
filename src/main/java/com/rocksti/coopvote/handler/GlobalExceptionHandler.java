package com.rocksti.coopvote.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rocksti.coopvote.exception.ConflictRequestException;
import com.rocksti.coopvote.exception.NotFoundException;
import com.rocksti.coopvote.exception.SessaoExpiradaException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.StringJoiner;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({Exception.class})
    protected ResponseEntity<Object> handleConflict(Exception ex, HttpServletRequest request, WebRequest webRequest) {
        HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;
        log.error(internalServerError.getReasonPhrase(), ex);
        return getHandleExceptionInternal(request, webRequest, internalServerError, ex, null);
    }

    @ExceptionHandler({NotFoundException.class})
    protected ResponseEntity<Object> handleNotFoundException(RuntimeException ex, HttpServletRequest request, WebRequest webRequest) {
        return getHandleExceptionInternal(request, webRequest, HttpStatus.NOT_FOUND, ex, ex.getMessage());
    }

    @ExceptionHandler({ConflictRequestException.class})
    protected ResponseEntity<Object> handleConflictException(RuntimeException ex, HttpServletRequest request, WebRequest webRequest) {
        return getHandleExceptionInternal(request, webRequest, HttpStatus.CONFLICT, ex, ex.getMessage());
    }

    @ExceptionHandler({SessaoExpiradaException.class})
    protected ResponseEntity<Object> handleSessaoExpiradaException(RuntimeException ex, HttpServletRequest request, WebRequest webRequest) {
        return getHandleExceptionInternal(request, webRequest, HttpStatus.GONE, ex, ex.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        StringJoiner joiner = new StringJoiner(" ; ", "[ ", " ]");
        ex.getAllErrors().forEach(error -> joiner.add(error.getDefaultMessage()));
        return getHandleExceptionInternal(
                ((ServletWebRequest) request).getNativeRequest(HttpServletRequest.class),
                request,
                (HttpStatus) status,
                ex,
                joiner.toString());
    }

    private ResponseEntity<Object> getHandleExceptionInternal(HttpServletRequest request, WebRequest webRequest,
                                                              HttpStatus httpStatus, Exception ex, String message) {
        return handleExceptionInternal(
                ex,
                buildProblem(request, httpStatus, ex, message),
                new HttpHeaders(),
                httpStatus,
                webRequest);
    }

    private ResponseProblem buildProblem(HttpServletRequest request, HttpStatus httpStatus, Exception ex, String message) {
        return new ResponseProblem()
                .setType(null)
                .setStatus(httpStatus)
                .setTitle(httpStatus.getReasonPhrase())
                .setDetail(message)
                .setTimestamp(LocalDateTime.now().toString())
                .setCode(httpStatus.value())
                .setException(ex.getClass().getSimpleName())
                .setPath(request.getRequestURI())
                .setMethod(request.getMethod());
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class ResponseProblem {
        //Padrão RFC 7807
        private URI type;
        private String title;
        private HttpStatus status;
        private String detail;

        //Personalizado
        private String timestamp;
        private int code;
        private String exception;
        private String path;
        private String method;
    }
}
