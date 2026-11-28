package mx.edu.uacm.is.slt.as.IDSISTEMA;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolizaRepository extends JpaRepository<Poliza, String> {
}
