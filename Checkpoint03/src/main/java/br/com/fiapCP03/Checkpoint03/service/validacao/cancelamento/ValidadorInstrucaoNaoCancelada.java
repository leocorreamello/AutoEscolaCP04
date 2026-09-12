package br.com.fiapCP03.Checkpoint03.service.validacao.cancelamento;

import br.com.fiapCP03.Checkpoint03.domain.Instrucao;
import br.com.fiapCP03.Checkpoint03.exception.RegraNegocioException;
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
