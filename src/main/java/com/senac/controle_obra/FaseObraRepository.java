package com.senac.controle_obra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FaseObraRepository extends JpaRepository<FaseObra, Long> {
    // Este método é crucial: ele garante a filtragem por usuário no banco de dados
    List<FaseObra> findByUsuario(String usuario);
}