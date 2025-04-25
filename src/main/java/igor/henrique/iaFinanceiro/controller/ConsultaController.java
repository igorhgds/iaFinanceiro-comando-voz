package igor.henrique.iaFinanceiro.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/consulta")
public class ConsultaController {

    private final TransacaoQueryService queryService;

    public ConsultaController(TransacaoQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    public ResponseEntity<String> consultar(@RequestParam String texto) {
        String resposta = queryService.interpretarConsulta(texto);
        return ResponseEntity.ok(resposta);
    }
}
