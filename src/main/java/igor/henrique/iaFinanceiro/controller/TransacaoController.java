package igor.henrique.iaFinanceiro.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    private final TransacaoService service;

    public TransacaoController(TransacaoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Transacao> salvar(@RequestBody Transacao transacao) {
        return ResponseEntity.ok(service.salvar(transacao));
    }

    @GetMapping
    public ResponseEntity<List<Transacao>> listar() {
        return ResponseEntity.ok(service.listar());
    }
}