package com.example.quiz_2_iii.controllers;

import com.example.quiz_2_iii.models.Producto;
import com.example.quiz_2_iii.repositories.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoRepository productoRepository;

    @GetMapping
    public Flux<Producto> findAll() {
        return productoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Producto>> findById(@PathVariable Integer id) {
        return productoRepository.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Producto>> create(@RequestBody Producto producto) {
        return productoRepository.save(producto)
                .map(saved -> ResponseEntity.created(URI.create("/api/productos/" + saved.getId())).body(saved));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Producto>> update(@PathVariable Integer id, @RequestBody Producto producto) {
        return productoRepository.findById(id)
                .flatMap(existing -> {
                    existing.setNombre(producto.getNombre());
                    existing.setPrecio(producto.getPrecio());
                    existing.setStock(producto.getStock());
                    return productoRepository.save(existing);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Integer id) {
        return productoRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.just(ResponseEntity.notFound().build());
                    }
                    return productoRepository.deleteById(id)
                            .then(Mono.just(ResponseEntity.noContent().<Void>build()));
                });
    }
}
