package igor.henrique.iaFinanceiro.controller;

import igor.henrique.iaFinanceiro.ai.ExtratorComInstruct;
import igor.henrique.iaFinanceiro.dtos.transacao.InterpretacaoTransacao;
import igor.henrique.iaFinanceiro.service.DispatcherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/consulta")
public class ConsultaController {

    private final DispatcherService dispatcherService;

    public ConsultaController(DispatcherService dispatcherService) {
        this.dispatcherService = dispatcherService;
    }

    @GetMapping
    public ResponseEntity<String> consultar(@RequestParam String texto) {
        InterpretacaoTransacao interpretacao = ExtratorComInstruct.dadosTransacao(texto);
        String resposta = dispatcherService.processarConsulta(interpretacao);
        return ResponseEntity.ok(resposta);
    }
}
