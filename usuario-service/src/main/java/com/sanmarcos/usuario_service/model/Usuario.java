package com.sanmarcos.usuario_service.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_usuario")
    private Long id;

    @Column(name = "correo_usuario", unique = true)
    private String correoUsuario;

    @Column(name = "nombres")
    private String nombres;

    @Column(name = "apellidos")
    private String apellidos;

    @Column(name = "telefono_usuario")
    private String telefonoUsuario;

    @Column(name = "estado_usuario")
    private Boolean estadoUsuario;

    @Column(name = "run_usuario", unique = true, length = 12)
    private String run;

    @ToString.Exclude
    @Column(name = "clave_hash", length = 100)
    private String claveHash;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false, length = 20)
    private TipoUsuario tipoUsuario = TipoUsuario.CLIENTE;

    @Column(name = "region", length = 100)
    private String region;

    @Column(name = "comuna", length = 100)
    private String comuna;

    @Column(name = "direccion", length = 300)
    private String direccion;
}
