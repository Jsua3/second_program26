package com.example.quiz_2_iii.controllers;

import com.example.quiz_2_iii.models.Pedido;
import com.example.quiz_2_iii.repositories.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoRepository pedidoRepository;

    @GetMapping
    public Flux<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Pedido>> findById(@PathVariable Integer id) {
        return pedidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Pedido>> create(@RequestBody Pedido pedido) {
        return pedidoRepository.save(pedido)
                .map(saved -> ResponseEntity.created(URI.create("/api/pedidos/" + saved.getId())).body(saved));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Pedido>> update(@PathVariable Integer id, @RequestBody Pedido pedido) {
        return pedidoRepository.findById(id)
                .flatMap(existing -> {
                    existing.setDescripcion(pedido.getDescripcion());
                    existing.setEstado(pedido.getEstado());
                    existing.setTotal(pedido.getTotal());
                    return pedidoRepository.save(existing);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Integer id) {
        return pedidoRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.just(ResponseEntity.notFound().build());
                    }
                    return pedidoRepository.deleteById(id)
                            .then(Mono.just(ResponseEntity.noContent().<Void>build()));
                });
    }
}
