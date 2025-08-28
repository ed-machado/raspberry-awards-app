package com.goldenraspberry.application.exception;

import com.goldenraspberry.application.dto.ProblemDetailDto;
import com.goldenraspberry.common.exception.BusinessException;
import com.goldenraspberry.common.exception.TechnicalException;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/** Manipulador global de excecoes Mapeia excecoes de dominio para respostas HTTP */
@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Trata excecoes de negocio
   *
   * @param ex Excecao de negocio
   * @param request Req web
   * @return Resposta HTTP com erro 400
   */
  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ProblemDetailDto> handleBusinessException(
      BusinessException ex, WebRequest request) {

    logger.warn("Erro de negocio: {}", ex.getMessage(), ex);

    ProblemDetailDto problemDetail =
        new ProblemDetailDto(
            "about:blank",
            "Erro de Negócio",
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            extractPath(request));

    return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
  }

  /**
   * Trata excecoes tecnicas
   *
   * @param ex Excecao tecnica
   * @param request Req web
   * @return Resposta HTTP com erro 500
   */
  @ExceptionHandler(TechnicalException.class)
  public ResponseEntity<ProblemDetailDto> handleTechnicalException(
      TechnicalException ex, WebRequest request) {

    logger.error("Erro tecnico: {}", ex.getMessage(), ex);

    ProblemDetailDto problemDetail =
        new ProblemDetailDto(
            "about:blank",
            "Erro Técnico",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Erro interno do servidor",
            extractPath(request));

    return new ResponseEntity<>(problemDetail, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Trata excecoes genericas nao mapeadas
   *
   * @param ex Excecao generica
   * @param request Req web
   * @return Resposta HTTP com erro 500
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetailDto> handleGenericException(Exception ex, WebRequest request) {

    logger.error("Erro nao mapeado: {}", ex.getMessage(), ex);

    ProblemDetailDto problemDetail =
        new ProblemDetailDto(
            "about:blank",
            "Erro Interno",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Erro interno do servidor",
            extractPath(request));

    return new ResponseEntity<>(problemDetail, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Trata erros de validação de Bean Validation
   *
   * @param ex Exceção de validação
   * @param request Requisição web
   * @return Resposta HTTP com erro 400
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetailDto> handleValidationException(
      MethodArgumentNotValidException ex, WebRequest request) {

    logger.warn("Erro de validação: {}", ex.getMessage());

    String validationErrors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));

    ProblemDetailDto problemDetail =
        new ProblemDetailDto(
            "about:blank",
            "Erro de Validação",
            HttpStatus.BAD_REQUEST.value(),
            "Dados inválidos: " + validationErrors,
            extractPath(request));

    return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
  }

  /**
   * Trata violações de constraint de validação
   *
   * @param ex Exceção de violação de constraint
   * @param request Requisição web
   * @return Resposta HTTP com erro 400
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ProblemDetailDto> handleConstraintViolationException(
      ConstraintViolationException ex, WebRequest request) {

    logger.warn("Violação de constraint: {}", ex.getMessage());

    String constraintErrors =
        ex.getConstraintViolations().stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .collect(Collectors.joining(", "));

    ProblemDetailDto problemDetail =
        new ProblemDetailDto(
            "about:blank",
            "Erro de Validação",
            HttpStatus.BAD_REQUEST.value(),
            "Violação de restrições: " + constraintErrors,
            extractPath(request));

    return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
  }

  /**
   * Extrai o caminho da requisição
   *
   * @param request Requisição web
   * @return Caminho limpo da requisição
   */
  private String extractPath(WebRequest request) {
    return request.getDescription(false).replace("uri=", "");
  }
}
