package br.com.fiapCP04.Checkpoint04.service.validacao.cancelamento;

import br.com.fiapCP04.Checkpoint04.domain.Instrucao;
import br.com.fiapCP04.Checkpoint04.exception.RegraNegocioException;
import org.springframework.stereotype.Component;

@Component
public class ValidadorInstrucaoNaoCancelada implements ValidadorCancelamento {

    @Override
    public void validar(Instrucao instrucao) {
        if (instrucao.isCancelada()) {
            throw new RegraNegocioException("A instrucao ja foi cancelada");
        }
    }
}
