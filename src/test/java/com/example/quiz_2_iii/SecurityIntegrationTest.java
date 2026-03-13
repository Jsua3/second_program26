package com.example.quiz_2_iii;

import com.example.quiz_2_iii.dtos.LoginRequest;
import com.example.quiz_2_iii.dtos.RegisterRequest;
import com.example.quiz_2_iii.models.Producto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class SecurityIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateProductoAsAdmin() throws Exception {
        // 1. Registro de ADMIN
        RegisterRequest adminRegister = RegisterRequest.builder()
                .username("admin_test")
                .password("admin123")
                .role("ADMIN")
                .build();

        webTestClient.post().uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(adminRegister)
                .exchange()
                .expectStatus().isOk();

        // 2. Login de ADMIN para obtener token
        LoginRequest adminLogin = LoginRequest.builder()
                .username("admin_test")
                .password("admin123")
                .build();

        String response = webTestClient.post().uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(adminLogin)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        String adminToken = objectMapper.readTree(response).get("token").asText();

        // 3. Intento de crear producto con ADMIN
        Producto producto = new Producto();
        producto.setNombre("Laptop");
        producto.setPrecio(1500.0);
        producto.setStock(10);

        webTestClient.post().uri("/api/productos")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(producto)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void testCreateProductoAsUserFails() throws Exception {
        // 1. Registro de USER
        RegisterRequest userRegister = RegisterRequest.builder()
                .username("user_test")
                .password("user123")
                .role("USER")
                .build();

        webTestClient.post().uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userRegister)
                .exchange()
                .expectStatus().isOk();

        // 2. Login de USER
        LoginRequest userLogin = LoginRequest.builder()
                .username("user_test")
                .password("user123")
                .build();

        String response = webTestClient.post().uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userLogin)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        String userToken = objectMapper.readTree(response).get("token").asText();

        // 3. Intento de crear producto con USER (Debe fallar con 403)
        Producto producto = new Producto();
        producto.setNombre("Secret Item");

        webTestClient.post().uri("/api/productos")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(producto)
                .exchange()
                .expectStatus().isForbidden();
    }
}
