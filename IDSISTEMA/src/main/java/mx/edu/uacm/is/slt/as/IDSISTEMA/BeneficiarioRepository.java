package mx.edu.uacm.is.slt.as.IDSISTEMA.repositories;

import mx.edu.uacm.is.slt.as.IDSISTEMA.Beneficiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BeneficiarioRepository extends JpaRepository<Beneficiario, Long> {

    List<Beneficiario> findByClavePoliza(String clavePoliza);

    Optional<Beneficiario> findByCurp(String curp);

    boolean existsByCurp(String curp);
}