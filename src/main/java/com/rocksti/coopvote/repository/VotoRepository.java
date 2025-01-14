package com.rocksti.coopvote.repository;

import com.rocksti.coopvote.entity.Pauta;
import com.rocksti.coopvote.entity.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {
    boolean existsByPautaAndAssociadoId(Pauta pauta, String associadoId);

    List<Voto> findByPauta(Pauta pauta);
}
