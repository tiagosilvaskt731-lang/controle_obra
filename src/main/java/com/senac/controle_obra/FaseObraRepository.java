package com.senac.controle_obra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FaseObraRepository extends JpaRepository<FaseObra, Long> {
    // NOVO: Método mágico que filtra no MySQL apenas pelo nome do usuário logado
    List<FaseObra> findByUsuario(String usuario);
}