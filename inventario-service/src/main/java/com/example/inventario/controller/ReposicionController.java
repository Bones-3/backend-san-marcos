package com.example.inventario.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.inventario.dto.OrdenReposicionRequest;
import com.example.inventario.dto.OrdenReposicionResponse;
import com.example.inventario.service.ReposicionService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reposiciones")
@RequiredArgsConstructor
public class ReposicionController {
    private final ReposicionService reposicionService;

    @GetMapping
    public List<OrdenReposicionResponse> listar() {
        return reposicionService.listar();
    }

    @PostMapping
    public ResponseEntity<OrdenReposicionResponse> crear(@RequestBody OrdenReposicionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reposicionService.crear(request));
    }

    @PatchMapping("/{id}/recibir")
    public OrdenReposicionResponse recibir(@PathVariable Long id) {
        return reposicionService.recibir(id);
    }

    @PatchMapping("/{id}/cancelar")
    public OrdenReposicionResponse cancelar(@PathVariable Long id) {
        return reposicionService.cancelar(id);
    }
}
