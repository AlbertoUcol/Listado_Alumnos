package com.example.kotlinlistadoalumnos

import java.io.Serializable

data class Alumno(
    var id: Int = 0,
    var nombre: String,
    var cuenta: String,
    var correo: String,
    var imagen: String
) : Serializable
