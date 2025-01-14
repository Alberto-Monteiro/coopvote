package com.rocksti.coopvote.controller;

import com.rocksti.coopvote.dto.VotoDto;
import com.rocksti.coopvote.entity.Voto;
import com.rocksti.coopvote.service.VotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/api/v1/votos")
@RequiredArgsConstructor
public class VotoController {

    private final VotoService votoService;

    /**
     * Registra um voto em uma pauta específica.
     *
     * @param votoDto Dados do voto a ser registrado.
     * @return ResponseEntity com o voto registrado.
     */
    @PostMapping
    public ResponseEntity<Voto> registrarVoto(@RequestBody VotoDto votoDto) {
        log.info("Recebendo requisição para registrar voto: Pauta ID {}, Associado ID {}",
                votoDto.getPautaId(), votoDto.getAssociadoId());

        Voto votoRegistrado = votoService.registrarVoto(votoDto);

        log.info("Voto registrado com sucesso: ID {}", votoRegistrado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(votoRegistrado);
    }
}
