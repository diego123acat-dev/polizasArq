package mx.edu.uacm.is.slt.as.IDSISTEMA;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "mx.edu.uacm.is.slt.as.IDSISTEMA",
        "mx.uacm.polizas.client"
})
public class IdsistemaApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdsistemaApplication.class, args);
    }
}
