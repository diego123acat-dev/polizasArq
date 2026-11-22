package mx.edu.uacm.is.slt.as.IDSISTEMA;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BeneficiarioRepository extends JpaRepository<Beneficiario, Long> {
    List<Beneficiario> findByClavePoliza(String clavePoliza);
    boolean existsByClavePolizaAndCurpIgnoreCase(String clavePoliza, String curp);
    boolean existsByCurpIgnoreCase(String curp);
    void deleteByCurpIgnoreCase(String curp);
}