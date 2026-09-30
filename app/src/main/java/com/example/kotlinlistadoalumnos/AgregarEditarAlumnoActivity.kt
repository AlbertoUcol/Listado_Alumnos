package com.example.kotlinlistadoalumnos

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinlistadoalumnos.databinding.ActivityAgregarEditarAlumnoBinding

class AgregarEditarAlumnoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarEditarAlumnoBinding
    private lateinit var dbHelper: DBHelperAlumno
    private var alumnoEditar: Alumno? = null

    companion object {
        const val EXTRA_ALUMNO = "extra_alumno"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarEditarAlumnoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DBHelperAlumno(this)

        alumnoEditar = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_ALUMNO, Alumno::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(EXTRA_ALUMNO) as? Alumno
        }

        if (alumnoEditar != null) {
            binding.toolbarForm.title = "Editar Alumno"
            binding.btnGuardar.text = "Actualizar Alumno"
            cargarDatosAlumno(alumnoEditar!!)
        } else {
            binding.toolbarForm.title = "Agregar Nuevo Alumno"
            binding.btnGuardar.text = "Guardar Alumno"
        }

        binding.btnGuardar.setOnClickListener {
            guardarAlumno()
        }

        binding.btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun cargarDatosAlumno(alumno: Alumno) {
        binding.etNombre.setText(alumno.nombre)
        binding.etCuenta.setText(alumno.cuenta)
        binding.etCorreo.setText(alumno.correo)
        binding.etImagen.setText(alumno.imagen)
    }

    private fun guardarAlumno() {
        val nombre = binding.etNombre.text.toString().trim()
        val cuenta = binding.etCuenta.text.toString().trim()
        val correo = binding.etCorreo.text.toString().trim()
        val imagen = binding.etImagen.text.toString().trim()

        if (nombre.isEmpty()) {
            binding.etNombre.error = "Ingrese el nombre"
            binding.etNombre.requestFocus()
            return
        }

        if (cuenta.isEmpty()) {
            binding.etCuenta.error = "Ingrese el número de cuenta"
            binding.etCuenta.requestFocus()
            return
        }

        if (correo.isEmpty()) {
            binding.etCorreo.error = "Ingrese el correo electrónico"
            binding.etCorreo.requestFocus()
            return
        }

        val imagenFinal = if (imagen.isEmpty()) {
            "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200"
        } else {
            imagen
        }

        if (alumnoEditar != null) {
            val alumnoActualizado = Alumno(
                id = alumnoEditar!!.id,
                nombre = nombre,
                cuenta = cuenta,
                correo = correo,
                imagen = imagenFinal
            )
            val result = dbHelper.actualizarAlumno(alumnoActualizado)
            if (result > 0) {
                Toast.makeText(this, "Alumno actualizado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al actualizar alumno", Toast.LENGTH_SHORT).show()
            }
        } else {
            val nuevoAlumno = Alumno(
                nombre = nombre,
                cuenta = cuenta,
                correo = correo,
                imagen = imagenFinal
            )
            val result = dbHelper.insertarAlumno(nuevoAlumno)
            if (result != -1L) {
                Toast.makeText(this, "Alumno registrado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al registrar alumno", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
