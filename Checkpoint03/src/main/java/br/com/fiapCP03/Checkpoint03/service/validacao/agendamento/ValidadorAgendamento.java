package br.com.fiapCP03.Checkpoint03.service.validacao.agendamento;

import br.com.fiapCP03.Checkpoint03.dto.InstrucaoAgendamentoRequest;

public interface ValidadorAgendamento {

    void validar(InstrucaoAgendamentoRequest dados);
}
