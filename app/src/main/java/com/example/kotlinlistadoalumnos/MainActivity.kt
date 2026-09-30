package com.example.kotlinlistadoalumnos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinlistadoalumnos.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DBHelperAlumno
    private lateinit var adapter: AlumnoAdapter
    private var listaAlumnos = ArrayList<Alumno>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DBHelperAlumno(this)

        setupRecyclerView()

        binding.fabAgregar.setOnClickListener {
            val intent = Intent(this, AgregarEditarAlumnoActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarAlumnos()
    }

    private fun setupRecyclerView() {
        adapter = AlumnoAdapter(
            listaAlumnos = listaAlumnos,
            onEditarClick = { alumno -> editarAlumno(alumno) },
            onEliminarClick = { alumno -> confirmarEliminarAlumno(alumno) }
        )
        binding.rvAlumnos.layoutManager = LinearLayoutManager(this)
        binding.rvAlumnos.adapter = adapter
    }

    private fun cargarAlumnos() {
        listaAlumnos = dbHelper.obtenerAlumnos()
        adapter.actualizarLista(listaAlumnos)

        if (listaAlumnos.isEmpty()) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvAlumnos.visibility = View.GONE
        } else {
            binding.tvEmpty.visibility = View.GONE
            binding.rvAlumnos.visibility = View.VISIBLE
        }
    }

    private fun editarAlumno(alumno: Alumno) {
        val intent = Intent(this, AgregarEditarAlumnoActivity::class.java).apply {
            putExtra(AgregarEditarAlumnoActivity.EXTRA_ALUMNO, alumno)
        }
        startActivity(intent)
    }

    private fun confirmarEliminarAlumno(alumno: Alumno) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Alumno")
            .setMessage("¿Estás seguro de que deseas eliminar a ${alumno.nombre}?")
            .setPositiveButton("Eliminar") { _, _ ->
                val result = dbHelper.eliminarAlumno(alumno.id)
                if (result > 0) {
                    Toast.makeText(this, "Alumno eliminado correctamente", Toast.LENGTH_SHORT).show()
                    cargarAlumnos()
                } else {
                    Toast.makeText(this, "Error al eliminar alumno", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
