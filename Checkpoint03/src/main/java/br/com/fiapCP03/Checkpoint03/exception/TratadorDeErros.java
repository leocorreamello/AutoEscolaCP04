package br.com.fiapCP03.Checkpoint03.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class TratadorDeErros {

    public record ErroResponse(String mensagem) {
    }

    public record ErroCampoResponse(String campo, String mensagem) {
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErroCampoResponse>> tratarErroValidacao(MethodArgumentNotValidException ex) {
        List<ErroCampoResponse> erros = ex.getFieldErrors().stream()
                .map(erro -> new ErroCampoResponse(erro.getField(), erro.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest().body(erros);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> tratarRegraNegocio(RegraNegocioException ex) {
        return ResponseEntity.badRequest().body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErroResponse> tratarResponseStatus(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(new ErroResponse(ex.getReason()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> tratarCorpoInvalido(HttpMessageNotReadableException ex) {
        if (ex.getMostSpecificCause() instanceof InvalidFormatException invalido
                && invalido.getTargetType() != null
                && invalido.getTargetType().isEnum()) {
            String aceitos = Arrays.toString(invalido.getTargetType().getEnumConstants());
            return ResponseEntity.badRequest().body(new ErroResponse(
                    "Valor invalido '" + invalido.getValue() + "'. Valores aceitos: " + aceitos));
        }
        return ResponseEntity.badRequest().body(new ErroResponse("Corpo da requisicao invalido ou mal formatado"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> tratarViolacaoIntegridade(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErroResponse("Registro duplicado ou violacao de integridade dos dados"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErroResponse> tratarFalhaAutenticacao(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErroResponse("Login ou senha invalidos"));
    }
}
