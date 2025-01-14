package com.rocksti.coopvote.controller;

import com.rocksti.coopvote.dto.PautaDto;
import com.rocksti.coopvote.entity.Pauta;
import com.rocksti.coopvote.service.PautaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/v1/pautas")
@RequiredArgsConstructor
public class PautaController {

    private final PautaService pautaService;

    /**
     * Cadastra uma nova pauta.
     *
     * @param pautaDto Dados da pauta a ser cadastrada.
     * @return ResponseEntity com a pauta cadastrada e o URI de sua localização.
     * @throws URISyntaxException Se houver erro ao criar o URI.
     */
    @PostMapping
    public ResponseEntity<Pauta> cadastrarPauta(@RequestBody PautaDto pautaDto) throws URISyntaxException {
        log.info("Recebendo requisição para cadastrar nova pauta: {}", pautaDto.getDescricao());
        Pauta pautaCadastrada = pautaService.cadastrarPauta(pautaDto);
        log.info("Pauta cadastrada com sucesso: ID {}", pautaCadastrada.getId());
        return ResponseEntity
                .created(new URI("/api/v1/pautas/" + pautaCadastrada.getId()))
                .body(pautaCadastrada);
    }

    /**
     * Abre a sessão de votação para uma pauta específica.
     *
     * @param pautaId            ID da pauta.
     * @param tempoSessaoMinutos Tempo de duração da sessão (opcional).
     * @return ResponseEntity com a pauta atualizada.
     */
    @PostMapping("/{pautaId}/abrir-sessao")
    public ResponseEntity<Pauta> abrirSessao(@PathVariable Long pautaId,
                                             @RequestParam(required = false) Long tempoSessaoMinutos) {
        log.info("Recebendo requisição para abrir sessão na pauta ID: {}", pautaId);
        Pauta pautaAtualizada = pautaService.abrirSessao(pautaId, tempoSessaoMinutos);
        log.info("Sessão de votação aberta para a pauta ID: {}", pautaAtualizada.getId());
        return ResponseEntity.ok(pautaAtualizada);
    }

    /**
     * Lista todas as pautas cadastradas.
     *
     * @return ResponseEntity com a lista de pautas.
     */
    @GetMapping
    public ResponseEntity<List<Pauta>> listarPautas() {
        log.info("Recebendo requisição para listar todas as pautas.");
        List<Pauta> pautas = pautaService.listarPautas();
        log.info("Total de pautas encontradas: {}", pautas.size());
        return ResponseEntity.ok(pautas);
    }

    /**
     * Busca uma pauta pelo ID.
     *
     * @param pautaId ID da pauta a ser buscada.
     * @return ResponseEntity com a pauta encontrada.
     */
    @GetMapping("/{pautaId}")
    public ResponseEntity<Pauta> buscarPautaPorId(@PathVariable Long pautaId) {
        log.info("Recebendo requisição para buscar pauta ID: {}", pautaId);
        Pauta pauta = pautaService.buscarPautaPorId(pautaId);
        log.info("Pauta encontrada: ID {}", pauta.getId());
        return ResponseEntity.ok(pauta);
    }

    /**
     * Retorna o resultado da votação de uma pauta.
     *
     * @param pautaId ID da pauta.
     * @return ResponseEntity com o resultado da votação.
     */
    @GetMapping("/resultado/{pautaId}")
    public ResponseEntity<String> contarVotos(@PathVariable Long pautaId) {
        log.info("Recebendo requisição para contar votos da pauta ID: {}", pautaId);
        String resultado = pautaService.contarVotos(pautaId);
        log.info("Resultado da votação para a pauta ID {}: {}", pautaId, resultado);
        return ResponseEntity.ok(resultado);
    }
}
