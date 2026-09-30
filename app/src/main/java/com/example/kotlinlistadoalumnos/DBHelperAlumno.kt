package com.example.kotlinlistadoalumnos

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelperAlumno(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Alumnos.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "Alumnos"
        const val COLUMN_ID = "id"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_CUENTA = "cuenta"
        const val COLUMN_CORREO = "correo"
        const val COLUMN_IMAGEN = "imagen"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = ("CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NOMBRE + " TEXT, "
                + COLUMN_CUENTA + " TEXT, "
                + COLUMN_CORREO + " TEXT, "
                + COLUMN_IMAGEN + " TEXT)")
        db?.execSQL(createTableQuery)

        insertarDatosIniciales(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    private fun insertarDatosIniciales(db: SQLiteDatabase?) {
        val alumnosIniciales = listOf(
            Alumno(
                nombre = "Juan Pérez",
                cuenta = "20210001",
                correo = "juan.perez@alumnos.com",
                imagen = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200"
            ),
            Alumno(
                nombre = "María García",
                cuenta = "20210002",
                correo = "maria.garcia@alumnos.com",
                imagen = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200"
            ),
            Alumno(
                nombre = "Carlos López",
                cuenta = "20210003",
                correo = "carlos.lopez@alumnos.com",
                imagen = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=200"
            )
        )

        for (alumno in alumnosIniciales) {
            val values = ContentValues().apply {
                put(COLUMN_NOMBRE, alumno.nombre)
                put(COLUMN_CUENTA, alumno.cuenta)
                put(COLUMN_CORREO, alumno.correo)
                put(COLUMN_IMAGEN, alumno.imagen)
            }
            db?.insert(TABLE_NAME, null, values)
        }
    }

    fun insertarAlumno(alumno: Alumno): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, alumno.nombre)
            put(COLUMN_CUENTA, alumno.cuenta)
            put(COLUMN_CORREO, alumno.correo)
            put(COLUMN_IMAGEN, alumno.imagen)
        }
        val result = db.insert(TABLE_NAME, null, values)
        db.close()
        return result
    }

    fun obtenerAlumnos(): ArrayList<Alumno> {
        val listaAlumnos = ArrayList<Alumno>()
        val selectQuery = "SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_ID DESC"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val idIndex = cursor.getColumnIndex(COLUMN_ID)
                val nombreIndex = cursor.getColumnIndex(COLUMN_NOMBRE)
                val cuentaIndex = cursor.getColumnIndex(COLUMN_CUENTA)
                val correoIndex = cursor.getColumnIndex(COLUMN_CORREO)
                val imagenIndex = cursor.getColumnIndex(COLUMN_IMAGEN)

                val id = if (idIndex != -1) cursor.getInt(idIndex) else 0
                val nombre = if (nombreIndex != -1) cursor.getString(nombreIndex) ?: "" else ""
                val cuenta = if (cuentaIndex != -1) cursor.getString(cuentaIndex) ?: "" else ""
                val correo = if (correoIndex != -1) cursor.getString(correoIndex) ?: "" else ""
                val imagen = if (imagenIndex != -1) cursor.getString(imagenIndex) ?: "" else ""

                listaAlumnos.add(Alumno(id, nombre, cuenta, correo, imagen))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return listaAlumnos
    }

    fun actualizarAlumno(alumno: Alumno): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, alumno.nombre)
            put(COLUMN_CUENTA, alumno.cuenta)
            put(COLUMN_CORREO, alumno.correo)
            put(COLUMN_IMAGEN, alumno.imagen)
        }
        val success = db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(alumno.id.toString()))
        db.close()
        return success
    }

    fun eliminarAlumno(id: Int): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return success
    }
}
