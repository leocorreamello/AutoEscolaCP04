package br.com.fiapCP03.Checkpoint03.support;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Relogio controlado pelos testes, para validar regras que dependem da data/hora atual.
 */
public class RelogioMutavel extends Clock {

    private final ZoneId zona;
    private Instant instante;

    public RelogioMutavel(ZoneId zona, LocalDateTime agora) {
        this.zona = zona;
        definir(agora);
    }

    public void definir(LocalDateTime agora) {
        this.instante = agora.atZone(zona).toInstant();
    }

    @Override
    public ZoneId getZone() {
        return zona;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return Clock.fixed(instante, zone);
    }

    @Override
    public Instant instant() {
        return instante;
    }
}
