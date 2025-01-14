package com.rocksti.coopvote.integration;

import com.rocksti.coopvote.dto.PautaDto;
import com.rocksti.coopvote.dto.VotoDto;
import com.rocksti.coopvote.entity.Pauta;
import com.rocksti.coopvote.entity.Voto;
import com.rocksti.coopvote.enums.TipoVoto;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class VotoControllerIT {

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @Order(1)
    class PautaControllerIT {
        private static final String BASE_URL = "http://localhost:%d/api/v1";

        @LocalServerPort
        private int port;

        @Autowired
        private TestRestTemplate restTemplate;

        private String baseUrlPautas() {
            return String.format(BASE_URL + "/pautas", port);
        }

        private String baseUrlVotos() {
            return String.format(BASE_URL + "/votos", port);
        }


        @Test
        @Order(1)
        void testCadastrarPauta() {
            PautaDto pautaDto = PautaDto.builder()
                    .descricao("Pauta de teste")
                    .build();

            ResponseEntity<Pauta> response = restTemplate.postForEntity(baseUrlPautas(), pautaDto, Pauta.class);

            assertThat(response.getStatusCode())
                    .as("Verifica se o status da resposta é 201 CREATED")
                    .isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody())
                    .as("Verifica se a descrição da pauta está correta")
                    .extracting(Pauta::getDescricao)
                    .isEqualTo(pautaDto.getDescricao());
        }

        @Test
        @Order(2)
        void testAbrirSessao() {
            PautaDto pautaDto = PautaDto.builder()
                    .descricao("Pauta de teste")
                    .build();

            ResponseEntity<Pauta> response = restTemplate.postForEntity(baseUrlPautas(), pautaDto, Pauta.class);
            Pauta pauta = response.getBody();

            assert pauta != null;
            ResponseEntity<Pauta> responseSessao = restTemplate.postForEntity(baseUrlPautas() + "/{pautaId}/abrir-sessao", null, Pauta.class, pauta.getId());

            assertThat(responseSessao.getStatusCode())
                    .as("Verifica se o status da resposta é 200 OK")
                    .isEqualTo(HttpStatus.OK);
            assertThat(responseSessao.getBody())
                    .as("Verifica se a sessão foi aberta corretamente")
                    .extracting(Pauta::getInicioSessao)
                    .isNotNull();
        }

        @Test
        @Order(3)
        void testAbrirSessaoNaoEncontrada() {
            ResponseEntity<Pauta> responseSessao = restTemplate.postForEntity(baseUrlPautas() + "/{pautaId}/abrir-sessao", null, Pauta.class, 9999);

            assertThat(responseSessao.getStatusCode())
                    .as("Verifica se o status da resposta é 404 NOT FOUND")
                    .isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @Order(4)
        void testListarPautas() {
            ResponseEntity<Pauta[]> response = restTemplate.getForEntity(baseUrlPautas(), Pauta[].class);

            assertThat(response.getStatusCode())
                    .as("Verifica se o status da resposta é 200 OK")
                    .isEqualTo(HttpStatus.OK);
            assertThat(response.getBody())
                    .as("Verifica se a lista de pautas não está vazia")
                    .isNotEmpty();
        }

        @Test
        @Order(5)
        void testBuscarPautaPorId() {
            PautaDto pautaDto = PautaDto.builder()
                    .descricao("Pauta de teste")
                    .build();

            ResponseEntity<Pauta> response = restTemplate.postForEntity(baseUrlPautas(), pautaDto, Pauta.class);
            Pauta pauta = response.getBody();

            assert pauta != null;
            ResponseEntity<Pauta> responsePauta = restTemplate.getForEntity(baseUrlPautas() + "/{pautaId}", Pauta.class, pauta.getId());

            assertThat(responsePauta.getStatusCode())
                    .as("Verifica se o status da resposta é 200 OK")
                    .isEqualTo(HttpStatus.OK);
            assertThat(responsePauta.getBody())
                    .as("Verifica se a pauta retornada é a mesma cadastrada")
                    .isEqualTo(pauta);
        }

        @Test
        @Order(6)
        void testBuscarPautaPorIdNaoEncontrada() {
            ResponseEntity<Pauta> responsePauta = restTemplate.getForEntity(baseUrlPautas() + "/{pautaId}", Pauta.class, 9999);

            assertThat(responsePauta.getStatusCode())
                    .as("Verifica se o status da resposta é 404 NOT FOUND")
                    .isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @Order(7)
        void testVotar() {
            PautaDto pautaDto = PautaDto.builder()
                    .descricao("Pauta de teste")
                    .build();

            ResponseEntity<Pauta> response = restTemplate.postForEntity(baseUrlPautas(), pautaDto, Pauta.class);
            Pauta pauta = response.getBody();

            assert pauta != null;
            restTemplate.postForEntity(baseUrlPautas() + "/{pautaId}/abrir-sessao", null, Pauta.class, pauta.getId());

            VotoDto votoDto = VotoDto.builder()
                    .pautaId(pauta.getId())
                    .associadoId("1")
                    .tipoVoto(TipoVoto.SIM)
                    .build();

            ResponseEntity<Voto> responseVoto = restTemplate.postForEntity(baseUrlVotos(), votoDto, Voto.class);

            assertThat(responseVoto.getStatusCode())
                    .as("Verifica se o status da resposta é 201 CREATED")
                    .isEqualTo(HttpStatus.CREATED);
            assertThat(responseVoto.getBody())
                    .as("Verifica se o voto foi registrado corretamente")
                    .extracting(Voto::getAssociadoId)
                    .isEqualTo(votoDto.getAssociadoId());
        }

        @Test
        @Order(8)
        void testVotarAssociadoJaVotou() {
            PautaDto pautaDto = PautaDto.builder()
                    .descricao("Pauta de teste")
                    .build();

            ResponseEntity<Pauta> response = restTemplate.postForEntity(baseUrlPautas(), pautaDto, Pauta.class);
            Pauta pauta = response.getBody();

            assert pauta != null;
            restTemplate.postForEntity(baseUrlPautas() + "/{pautaId}/abrir-sessao", null, Pauta.class, pauta.getId());

            VotoDto votoDto = VotoDto.builder()
                    .pautaId(pauta.getId())
                    .associadoId("1")
                    .tipoVoto(TipoVoto.SIM)
                    .build();

            restTemplate.postForEntity(baseUrlVotos(), votoDto, Voto.class);

            ResponseEntity<Voto> responseVoto = restTemplate.postForEntity(baseUrlVotos(), votoDto, Voto.class);

            assertThat(responseVoto.getStatusCode())
                    .as("Verifica se o status da resposta é 409 CONFLICT")
                    .isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        @Order(9)
        void testContarVotosSemVotos() {
            PautaDto pautaDto = PautaDto.builder()
                    .descricao("Pauta de teste")
                    .build();

            ResponseEntity<Pauta> response = restTemplate.postForEntity(baseUrlPautas(), pautaDto, Pauta.class);
            Pauta pauta = response.getBody();

            assert pauta != null;
            ResponseEntity<String> responseContagem = restTemplate.getForEntity(baseUrlPautas() + "/resultado/{pautaId}", String.class, pauta.getId());

            assertThat(responseContagem.getStatusCode())
                    .as("Verifica se o status da resposta é 404 NOT FOUND")
                    .isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @Order(10)
        void testContarVotosComVotos() {
            PautaDto pautaDto = PautaDto.builder()
                    .descricao("Pauta de teste")
                    .build();

            ResponseEntity<Pauta> response = restTemplate.postForEntity(baseUrlPautas(), pautaDto, Pauta.class);
            Pauta pauta = response.getBody();

            assert pauta != null;
            restTemplate.postForEntity(baseUrlPautas() + "/{pautaId}/abrir-sessao", null, Pauta.class, pauta.getId());

            restTemplate.postForEntity(baseUrlVotos(), VotoDto.builder().pautaId(pauta.getId()).associadoId("1").tipoVoto(TipoVoto.SIM).build(), Voto.class);
            restTemplate.postForEntity(baseUrlVotos(), VotoDto.builder().pautaId(pauta.getId()).associadoId("2").tipoVoto(TipoVoto.NAO).build(), Voto.class);

            ResponseEntity<String> responseContagem = restTemplate.getForEntity(baseUrlPautas() + "/resultado/{pautaId}", String.class, pauta.getId());

            assertThat(responseContagem.getStatusCode())
                    .as("Verifica se o status da resposta é 200 OK")
                    .isEqualTo(HttpStatus.OK);
            assertThat(responseContagem.getBody())
                    .as("Verifica se a contagem de votos está correta")
                    .contains("SIM = 1, NÃO = 1");
        }

        @Test
        @Order(11)
        void testContarVotosComVotosSemSessao() {
            PautaDto pautaDto = PautaDto.builder()
                    .descricao("Pauta de teste")
                    .build();

            ResponseEntity<Pauta> response = restTemplate.postForEntity(baseUrlPautas(), pautaDto, Pauta.class);
            Pauta pauta = response.getBody();

            assert pauta != null;
            ResponseEntity<String> responseContagem = restTemplate.getForEntity(baseUrlPautas() + "/resultado/{pautaId}", String.class, pauta.getId());

            assertThat(responseContagem.getStatusCode())
                    .as("Verifica se o status da resposta é 404 NOT FOUND")
                    .isEqualTo(HttpStatus.NOT_FOUND);
        }
    }
}
