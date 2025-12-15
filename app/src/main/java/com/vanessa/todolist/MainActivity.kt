package com.vanessa.todolist

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var inputProducte: EditText
    private lateinit var btnAfegir: FloatingActionButton
    private lateinit var llistaProductes: LinearLayout
    private lateinit var baseDeDades: DatabaseReference

    private var carpetesProductes = mutableListOf<String>()
    private var carpetaActual: String = "General" // Carpeta por defecto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputProducte = findViewById(R.id.inputProducte)
        btnAfegir = findViewById(R.id.btnAfegir)
        llistaProductes = findViewById(R.id.llistaProductes)
        val btnEnter = findViewById<Button>(R.id.btnEnter) // Assuming you still have this button for adding items

        // Inicialitzem la connexió amb la BD
        baseDeDades = FirebaseDatabase.getInstance().reference

        // Añadimos la carpeta por defecto a la lista
        if (!carpetesProductes.contains(carpetaActual)) {
            carpetesProductes.add(carpetaActual)
        }

        btnAfegir.setOnClickListener {
            mostrarMenuCarpetas()
        }

        btnEnter.setOnClickListener {
            val text = inputProducte.text.toString().trim()
            if (text.isNotEmpty()) {
                afegirTascaALlista(text)
                inputProducte.text.clear()
            }
        }
    }

    private fun mostrarMenuCarpetas() {
        val popup = PopupMenu(this, btnAfegir)
        popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)

        // Limpiamos el menú antes de añadir las carpetas para no duplicarlas
        popup.menu.removeGroup(R.id.grupCarpetes)

        // Añadimos las carpetas dinámicamente
        carpetesProductes.forEachIndexed { index, nomCarpeta ->
            popup.menu.add(R.id.grupCarpetes, index, index, nomCarpeta).setIcon(R.drawable.ic_folder)
        }

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.crearCarpeta -> {
                    mostrarDialogoNuevaCarpeta()
                    true
                }
                R.id.veureLlista -> {
                    // Lógica para ver el listado completo (si es necesario)
                    Toast.makeText(this, "Mostrando todas las tareas", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> {
                    // Hemos pulsado una de las carpetas dinámicas
                    val nomCarpetaSeleccionada = menuItem.title.toString()
                    canviarCarpeta(nomCarpetaSeleccionada)
                    true
                }
            }
        }

        popup.show()
    }

    private fun canviarCarpeta(nomCarpeta: String) {
        carpetaActual = nomCarpeta
        Toast.makeText(this, "Carpeta actual: $nomCarpeta", Toast.LENGTH_SHORT).show()
        // Aquí iría la lógica para limpiar la lista actual y cargar las tareas de la nueva carpeta
        llistaProductes.removeAllViews()
        // carregarTasquesDeFirebase(nomCarpeta)
    }

    private fun afegirTascaALlista(text: String) {
        val fila = LinearLayout(this)
        fila.orientation = LinearLayout.HORIZONTAL

        val checkBox = CheckBox(this)
        checkBox.text = text
        fila.addView(checkBox)

        llistaProductes.addView(fila)

        checkBox.setOnClickListener {
            lifecycleScope.launch {
                delay(1000)
                llistaProductes.removeView(fila)
                // Aquí podrías añadir lógica para marcarla como completada en Firebase
            }
        }

        // Guardar la tarea en Firebase bajo la carpeta actual
        val tascaId = baseDeDades.child("Carpetas").child(carpetaActual).push().key
        if (tascaId != null) {
            baseDeDades.child("Carpetas").child(carpetaActual).child(tascaId).setValue(text)
        }
    }

    private fun afegirCarpeta(nombreCarpeta: String) {
        // 1. Añadir el nombre a la lista interna (para que aparezca en el menú)
        if (!carpetesProductes.contains(nombreCarpeta)) {
            carpetesProductes.add(nombreCarpeta)
            // 2. Opcional: Guardar la carpeta en Firebase.
            // baseDeDades.child("Carpetas").child(nombreCarpeta).setValue(true)
            Toast.makeText(this, "Carpeta '$nombreCarpeta' añadida.", Toast.LENGTH_SHORT).show()
            // 3. Establecer esta nueva carpeta como la carpeta actual
            canviarCarpeta(nombreCarpeta)
        } else {
            Toast.makeText(this, "La carpeta '$nombreCarpeta' ya existe.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarDialogoNuevaCarpeta() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Nueva Carpeta")

        val input = EditText(this)
        input.hint = "Introduce el nombre de la carpeta"
        builder.setView(input)

        builder.setPositiveButton("Añadir") { dialog, _ ->
            val nombreCarpeta = input.text.toString().trim()
            if (nombreCarpeta.isNotEmpty()) {
                afegirCarpeta(nombreCarpeta)
            } else {
                Toast.makeText(this, "El nombre de la carpeta no puede estar vacío.", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }
}
