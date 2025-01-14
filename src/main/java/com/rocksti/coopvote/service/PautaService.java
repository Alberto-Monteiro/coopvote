package com.rocksti.coopvote.service;

import com.rocksti.coopvote.dto.PautaDto;
import com.rocksti.coopvote.entity.Pauta;
import com.rocksti.coopvote.enums.TipoVoto;
import com.rocksti.coopvote.exception.ConflictRequestException;
import com.rocksti.coopvote.exception.NotFoundException;
import com.rocksti.coopvote.repository.PautaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class PautaService {

    private static final String MSG_PAUTA_NAO_ENCONTRADA = "Pauta não encontrada";

    private final PautaRepository pautaRepository;

    /**
     * Cadastra uma nova pauta no sistema.
     *
     * @param pautaDto Dados para criação da pauta.
     * @return A pauta criada.
     */
    public Pauta cadastrarPauta(PautaDto pautaDto) {
        log.info("Iniciando cadastro da pauta: {}", pautaDto.getDescricao());
        Pauta novaPauta = criarNovaPauta(pautaDto);
        Pauta pautaSalva = pautaRepository.save(novaPauta);
        log.info("Pauta cadastrada com sucesso: {}", pautaSalva.getId());
        return pautaSalva;
    }

    /**
     * Abre a sessão de votação para uma pauta específica.
     *
     * @param pautaId     ID da pauta.
     * @param tempoSessao Tempo de duração da sessão em minutos.
     * @return A pauta com sessão aberta.
     */
    public Pauta abrirSessao(Long pautaId, Long tempoSessao) {
        log.info("Abrindo sessão de votação para a pauta ID: {}", pautaId);
        Pauta pauta = buscarPautaPorId(pautaId);

        validarSessaoNaoAberta(pauta);

        pauta.setInicioSessao(LocalDateTime.now());
        pauta.setTempoSessaoMinutos(tempoSessao != null ? tempoSessao : 1L);

        Pauta pautaAtualizada = pautaRepository.save(pauta);
        log.info("Sessão de votação aberta para a pauta ID: {}", pautaId);
        return pautaAtualizada;
    }

    /**
     * Retorna uma pauta pelo ID.
     *
     * @param pautaId ID da pauta.
     * @return A pauta encontrada.
     * @throws NotFoundException Se a pauta não for encontrada.
     */
    public Pauta buscarPautaPorId(Long pautaId) {
        return pautaRepository.findById(pautaId)
                .orElseThrow(() -> {
                    log.warn("Pauta não encontrada: ID {}", pautaId);
                    return new NotFoundException(MSG_PAUTA_NAO_ENCONTRADA);
                });
    }

    /**
     * Lista todas as pautas cadastradas.
     *
     * @return Lista de pautas.
     */
    public List<Pauta> listarPautas() {
        log.info("Listando todas as pautas cadastradas.");
        return pautaRepository.findAll();
    }

    /**
     * Conta os votos de uma pauta específica.
     *
     * @param pautaId ID da pauta.
     * @return Resultado da votação.
     */
    public String contarVotos(Long pautaId) {
        log.info("Contando votos para a pauta ID: {}", pautaId);
        Pauta pauta = buscarPautaPorId(pautaId);

        validarPautaComVotos(pauta);

        long votosSim = contarVotosPorTipo(pauta, TipoVoto.SIM);
        long votosNao = contarVotosPorTipo(pauta, TipoVoto.NAO);

        String resultado = formatarResultadoVotacao(pauta, votosSim, votosNao);
        log.info("Resultado da votação: {}", resultado);
        return resultado;
    }

    /**
     * Verifica se a sessão de uma pauta está expirada.
     *
     * @param pauta Pauta a ser verificada.
     * @return True se a sessão está expirada, false caso contrário.
     */
    public boolean isSessaoExpirada(Pauta pauta) {
        if (pauta.getInicioSessao() == null) {
            return false;
        }

        LocalDateTime fimSessao = pauta.getInicioSessao().plusMinutes(pauta.getTempoSessaoMinutos());
        return LocalDateTime.now().isAfter(fimSessao);
    }

    // Métodos auxiliares privados

    private Pauta criarNovaPauta(PautaDto pautaDto) {
        return Pauta.builder()
                .descricao(pautaDto.getDescricao())
                .build();
    }

    private void validarSessaoNaoAberta(Pauta pauta) {
        if (pauta.getInicioSessao() != null) {
            log.warn("Sessão já aberta para a pauta ID: {}", pauta.getId());
            throw new ConflictRequestException("A sessão já foi aberta para esta pauta");
        }
    }

    private void validarPautaComVotos(Pauta pauta) {
        if (pauta.getVotos().isEmpty()) {
            log.warn("Nenhum voto registrado para a pauta ID: {}", pauta.getId());
            throw new NotFoundException("Nenhum voto registrado para esta pauta");
        }
    }

    private long contarVotosPorTipo(Pauta pauta, TipoVoto tipoVoto) {
        return pauta.getVotos().stream()
                .filter(voto -> voto.getTipoVoto() == tipoVoto)
                .count();
    }

    private String formatarResultadoVotacao(Pauta pauta, long votosSim, long votosNao) {
        return String.format("Resultado da votação para a pauta '%s': SIM = %d, NÃO = %d",
                pauta.getDescricao(), votosSim, votosNao);
    }
}
