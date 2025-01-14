package com.rocksti.coopvote.dto;

import com.rocksti.coopvote.enums.TipoVoto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VotoDto {
    private Long pautaId;
    private String associadoId;
    private TipoVoto tipoVoto;
}
