package com.example.quiz_2_iii.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("productos")
public class Producto {
    @Id
    private Integer id;
    
    private String nombre;
    private Double precio;
    private Integer stock;
}
