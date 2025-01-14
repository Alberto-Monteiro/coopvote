package com.rocksti.coopvote.service;

import com.rocksti.coopvote.dto.VotoDto;
import com.rocksti.coopvote.entity.Pauta;
import com.rocksti.coopvote.entity.Voto;
import com.rocksti.coopvote.enums.TipoVoto;
import com.rocksti.coopvote.exception.SessaoExpiradaException;
import com.rocksti.coopvote.repository.PautaRepository;
import com.rocksti.coopvote.repository.VotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VotoServiceTest {
    @Mock
    private VotoRepository votoRepository;

    @Mock
    private PautaRepository pautaRepository;

    @Mock
    private PautaService pautaService;

    @InjectMocks
    private VotoService votoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegistrarVoto() {
        VotoDto votoDto = VotoDto.builder()
                .pautaId(1L)
                .associadoId("1")
                .tipoVoto(TipoVoto.SIM)
                .build();

        Pauta pauta = Pauta.builder().id(1L).build();

        when(pautaService.buscarPautaPorId(votoDto.getPautaId())).thenReturn(pauta);
        when(pautaService.isSessaoExpirada(pauta)).thenReturn(false);
        when(votoRepository.existsByPautaAndAssociadoId(pauta, votoDto.getAssociadoId())).thenReturn(false);

        Voto expectedVoto = Voto.builder()
                .pauta(pauta)
                .associadoId(votoDto.getAssociadoId())
                .tipoVoto(votoDto.getTipoVoto())
                .build();

        when(votoRepository.save(any(Voto.class))).thenReturn(expectedVoto);

        Voto actualVoto = votoService.registrarVoto(votoDto);

        assertEquals(expectedVoto, actualVoto);
        verify(votoRepository).save(any(Voto.class));
    }

    @Test
    void testRegistrarVotoExpirada() {
        VotoDto votoDto = VotoDto.builder()
                .pautaId(1L)
                .associadoId("1")
                .tipoVoto(TipoVoto.SIM)
                .build();

        Pauta pauta = Pauta.builder().id(1L).build();

        when(pautaService.buscarPautaPorId(votoDto.getPautaId())).thenReturn(pauta);
        when(pautaService.isSessaoExpirada(pauta)).thenReturn(true);

        SessaoExpiradaException exception = assertThrows(SessaoExpiradaException.class, () -> votoService.registrarVoto(votoDto));

        assertEquals("A sessão para esta pauta já expirou", exception.getMessage());
    }
}
