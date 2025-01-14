package com.rocksti.coopvote.service;

import com.rocksti.coopvote.dto.PautaDto;
import com.rocksti.coopvote.entity.Pauta;
import com.rocksti.coopvote.entity.Voto;
import com.rocksti.coopvote.enums.TipoVoto;
import com.rocksti.coopvote.exception.ConflictRequestException;
import com.rocksti.coopvote.exception.NotFoundException;
import com.rocksti.coopvote.repository.PautaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PautaServiceTest {

    @Mock
    private PautaRepository pautaRepository;

    @InjectMocks
    private PautaService pautaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCadastrarPauta() {

        PautaDto pautaDto = PautaDto.builder()
                .descricao("Descrição da pauta")
                .build();

        Pauta expectedPauta = Pauta.builder()
                .id(1L)
                .descricao("Descrição da pauta")
                .build();

        when(pautaRepository.save(any(Pauta.class))).thenReturn(expectedPauta);

        Pauta resultado = pautaService.cadastrarPauta(pautaDto);

        assertNotNull(resultado, "O resultado não deve ser nulo");
        assertEquals(expectedPauta.getDescricao(), resultado.getDescricao(), "A descrição deve ser igual à esperada");
        assertEquals(expectedPauta.getId(), resultado.getId(), "O ID deve ser igual ao esperado");

        ArgumentCaptor<Pauta> captor = ArgumentCaptor.forClass(Pauta.class);
        verify(pautaRepository, times(1)).save(captor.capture());
        Pauta pautaSalva = captor.getValue();

        assertNotNull(pautaSalva, "A pauta salva não deve ser nula");
        assertEquals(pautaDto.getDescricao(), pautaSalva.getDescricao(), "A descrição da pauta salva deve ser igual ao DTO");
    }

    @Test
    void testAbrirSessaoPautaJaAberta() {
        Long pautaId = 1L;
        Long tempoSessao = 1L;

        Pauta pauta = Pauta.builder()
                .id(pautaId)
                .inicioSessao(java.time.LocalDateTime.now())
                .build();

        when(pautaRepository.findById(pautaId)).thenReturn(java.util.Optional.of(pauta));

        ConflictRequestException exception = assertThrows(ConflictRequestException.class, () -> pautaService.abrirSessao(pautaId, tempoSessao));

        assertEquals("A sessão já foi aberta para esta pauta", exception.getMessage(), "A mensagem de erro deve ser 'A sessão já foi aberta para esta pauta'");
    }

    @Test
    void testAbrirSessao() {
        Long pautaId = 1L;
        Long tempoSessao = 1L;

        Pauta pauta = Pauta.builder()
                .id(pautaId)
                .descricao("Descrição da pauta")
                .build();

        when(pautaRepository.findById(pautaId)).thenReturn(java.util.Optional.of(pauta));
        when(pautaRepository.save(any(Pauta.class))).thenReturn(pauta);

        Pauta resultado = pautaService.abrirSessao(pautaId, tempoSessao);

        assertNotNull(resultado, "O resultado não deve ser nulo");
        assertEquals(pautaId, resultado.getId(), "O ID deve ser igual ao esperado");
        assertEquals(tempoSessao, resultado.getTempoSessaoMinutos(), "O tempo de sessão deve ser igual ao esperado");

        ArgumentCaptor<Pauta> captor = ArgumentCaptor.forClass(Pauta.class);
        verify(pautaRepository, times(1)).save(captor.capture());
        Pauta pautaSalva = captor.getValue();

        assertNotNull(pautaSalva, "A pauta salva não deve ser nula");
        assertEquals(tempoSessao, pautaSalva.getTempoSessaoMinutos(), "O tempo de sessão da pauta salva deve ser igual ao esperado");
    }

    @Test
    void testAbrirSessaoPautaNaoEncontrada() {
        Long pautaId = 1L;
        Long tempoSessao = 1L;

        when(pautaRepository.findById(pautaId)).thenReturn(java.util.Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> pautaService.abrirSessao(pautaId, tempoSessao));

        assertEquals("Pauta não encontrada", exception.getMessage(), "A mensagem de erro deve ser 'Pauta não encontrada'");
    }

    @Test
    void testBuscarPautas() {
        Pauta pauta1 = Pauta.builder()
                .id(1L)
                .descricao("Descrição da pauta 1")
                .build();

        Pauta pauta2 = Pauta.builder()
                .id(2L)
                .descricao("Descrição da pauta 2")
                .build();

        when(pautaRepository.findAll()).thenReturn(List.of(pauta1, pauta2));

        var pautas = pautaService.listarPautas();

        assertNotNull(pautas, "A lista de pautas não deve ser nula");
        assertEquals(2, pautas.size(), "A lista de pautas deve conter 2 elementos");

        Pauta pauta1Resultado = pautas.getFirst();
        assertEquals(pauta1.getId(), pauta1Resultado.getId(), "O ID da pauta 1 deve ser igual ao esperado");
        assertEquals(pauta1.getDescricao(), pauta1Resultado.getDescricao(), "A descrição da pauta 1 deve ser igual à esperada");

        Pauta pauta2Resultado = pautas.get(1);
        assertEquals(pauta2.getId(), pauta2Resultado.getId(), "O ID da pauta 2 deve ser igual ao esperado");
        assertEquals(pauta2.getDescricao(), pauta2Resultado.getDescricao(), "A descrição da pauta 2 deve ser igual à esperada");
    }

    @Test
    void testBuscarPautaPorId() {
        Long pautaId = 1L;

        Pauta pauta = Pauta.builder()
                .id(pautaId)
                .descricao("Descrição da pauta")
                .build();

        when(pautaRepository.findById(pautaId)).thenReturn(java.util.Optional.of(pauta));

        Pauta resultado = pautaService.buscarPautaPorId(pautaId);

        assertNotNull(resultado, "O resultado não deve ser nulo");
        assertEquals(pautaId, resultado.getId(), "O ID deve ser igual ao esperado");
        assertEquals(pauta.getDescricao(), resultado.getDescricao(), "A descrição deve ser igual à esperada");
    }

    @Test
    void testBuscarPautaPorIdPautaNaoEncontrada() {
        Long pautaId = 1L;

        when(pautaRepository.findById(pautaId)).thenReturn(java.util.Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> pautaService.buscarPautaPorId(pautaId));

        assertEquals("Pauta não encontrada", exception.getMessage(), "A mensagem de erro deve ser 'Pauta não encontrada'");
    }

    @Test
    void testContarVotosComVotos() {
        Long pautaId = 1L;

        Pauta pauta = Pauta.builder()
                .id(pautaId)
                .votos(new ArrayList<>())
                .descricao("Descrição da pauta")
                .build();

        pauta.getVotos().add(Voto.builder().tipoVoto(TipoVoto.SIM).build());
        pauta.getVotos().add(Voto.builder().tipoVoto(TipoVoto.SIM).build());
        pauta.getVotos().add(Voto.builder().tipoVoto(TipoVoto.NAO).build());

        when(pautaRepository.findById(pautaId)).thenReturn(java.util.Optional.of(pauta));

        String resultado = pautaService.contarVotos(pautaId);

        assertNotNull(resultado, "O resultado não deve ser nulo");
        assertEquals("Resultado da votação para a pauta 'Descrição da pauta': SIM = 2, NÃO = 1", resultado, "O resultado deve ser igual ao esperado");
    }

    @Test
    void testContarVotosSemVotos() {
        Long pautaId = 1L;

        Pauta pauta = Pauta.builder()
                .id(pautaId)
                .votos(new ArrayList<>())
                .descricao("Descrição da pauta")
                .build();

        when(pautaRepository.findById(pautaId)).thenReturn(java.util.Optional.of(pauta));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> pautaService.contarVotos(pautaId));

        assertEquals("Nenhum voto registrado para esta pauta", exception.getMessage(), "A mensagem de erro deve ser 'Nenhum voto registrado para esta pauta'");
    }

    @Test
    void testContarVotosPautaNaoEncontrada() {
        Long pautaId = 1L;

        when(pautaRepository.findById(pautaId)).thenReturn(java.util.Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> pautaService.contarVotos(pautaId));

        assertEquals("Pauta não encontrada", exception.getMessage(), "A mensagem de erro deve ser 'Pauta não encontrada'");
    }

    @Test
    void testIsSessaoExpirada() {
        Pauta pauta = Pauta.builder()
                .inicioSessao(null)
                .build();

        boolean resultado = pautaService.isSessaoExpirada(pauta);

        assertFalse(resultado, "O resultado deve ser falso");
    }

    @Test
    void testIsSessaoExpiradaComInicioSessao() {
        Pauta pauta = Pauta.builder()
                .inicioSessao(java.time.LocalDateTime.now().minusMinutes(2))
                .tempoSessaoMinutos(1L)
                .build();

        boolean resultado = pautaService.isSessaoExpirada(pauta);

        assertTrue(resultado, "O resultado deve ser verdadeiro");
    }
}
