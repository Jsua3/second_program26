package com.example.quiz_2_iii.repositories;

import com.example.quiz_2_iii.models.Pedido;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PedidoRepository extends ReactiveCrudRepository<Pedido, Integer> {
}
