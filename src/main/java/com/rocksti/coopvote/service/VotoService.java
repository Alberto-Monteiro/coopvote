package com.rocksti.coopvote.service;

import com.rocksti.coopvote.dto.VotoDto;
import com.rocksti.coopvote.entity.Pauta;
import com.rocksti.coopvote.entity.Voto;
import com.rocksti.coopvote.exception.ConflictRequestException;
import com.rocksti.coopvote.exception.SessaoExpiradaException;
import com.rocksti.coopvote.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class VotoService {

    private final VotoRepository votoRepository;
    private final PautaService pautaService;

    /**
     * Registra um voto para uma pauta específica.
     *
     * @param votoDto Dados do voto a ser registrado.
     * @return O voto registrado.
     * @throws SessaoExpiradaException  Se a sessão da pauta expirou.
     * @throws ConflictRequestException Se o associado já votou na pauta.
     */
    public Voto registrarVoto(VotoDto votoDto) {
        log.info("Registrando voto para a pauta ID: {}", votoDto.getPautaId());
        Pauta pauta = pautaService.buscarPautaPorId(votoDto.getPautaId());

        validarSessaoAtiva(pauta);
        validarAssociadoNaoVotou(pauta, votoDto.getAssociadoId());

        Voto voto = criarVoto(votoDto, pauta);
        Voto votoSalvo = votoRepository.save(voto);
        log.info("Voto registrado com sucesso: ID do voto: {}", votoSalvo.getId());
        return votoSalvo;
    }

    // Métodos auxiliares privados

    private void validarSessaoAtiva(Pauta pauta) {
        if (pautaService.isSessaoExpirada(pauta)) {
            log.warn("Sessão expirada para a pauta ID: {}", pauta.getId());
            throw new SessaoExpiradaException("A sessão para esta pauta já expirou");
        }
    }

    private void validarAssociadoNaoVotou(Pauta pauta, String associadoId) {
        boolean associadoJaVotou = votoRepository.existsByPautaAndAssociadoId(pauta, associadoId);
        if (associadoJaVotou) {
            log.warn("Associado ID: {} já votou na pauta ID: {}", associadoId, pauta.getId());
            throw new ConflictRequestException("Associado já votou nesta pauta");
        }
    }

    private Voto criarVoto(VotoDto votoDto, Pauta pauta) {
        return Voto.builder()
                .pauta(pauta)
                .associadoId(votoDto.getAssociadoId())
                .tipoVoto(votoDto.getTipoVoto())
                .build();
    }
}
