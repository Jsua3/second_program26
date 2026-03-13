package com.example.quiz_2_iii.repositories;

import com.example.quiz_2_iii.models.Producto;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ProductoRepository extends ReactiveCrudRepository<Producto, Integer> {
}
