package br.com.fiapCP04.Checkpoint04.service.validacao.agendamento;

import br.com.fiapCP04.Checkpoint04.dto.InstrucaoAgendamentoRequest;

public interface ValidadorAgendamento {

    void validar(InstrucaoAgendamentoRequest dados);
}
