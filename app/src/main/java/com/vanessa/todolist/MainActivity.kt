package com.vanessa.todolist

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputProducte = findViewById<EditText>(R.id.inputProducte)
        //val btnAfegir = findViewById<Button>(R.id.btnAfegir)
        val btnAfegir = findViewById<FloatingActionButton>(R.id.btnAfegir)
        val llistaProductes = findViewById<LinearLayout>(R.id.llistaProductes)
        val btnEnter = findViewById<Button>(R.id.btnEnter)
        val carpetesProductes = mutableListOf<String>()
        var etNouMissatge = findViewById<EditText>(R.id.etNouMissatge)
        // Inicialitzem la connexió amb la BD
        var baseDeDades: DatabaseReference = FirebaseDatabase.getInstance().reference

        btnEnter.setOnClickListener {
            val text = inputProducte.text.toString()

            if (text.isNotEmpty()) {
                val fila = LinearLayout(this)
                // Layout horitzontal
                fila.orientation = LinearLayout.HORIZONTAL

                // CheckBox amb el text del producte.
                val checkBox = CheckBox(this)

                // Llegeix el String situat a l'input.
                // val nouText = inputProducte.text.toString()

                inputProducte.text.clear()

                checkBox.text = text

                // Afegim els components a la fila.
                fila.addView(checkBox)

                // Afegim la fila a la llista
                llistaProductes.addView(fila)

                // Botó afegir a completacions.
                checkBox.setOnClickListener {
                    // Utilitzem una corutina per generar un delay.
                    lifecycleScope.launch {
                        delay(1000)
                        llistaProductes.removeView(fila)
                    }
                }
                //  Donem el valor del node "missatge"
                // baseDeDades.child("missatge").setValue(nouText)
            }

            /*
            baseDeDades.child("missatge").get().addOnSuccessListener {
                val text = it.value?.toString() ?: "(sense text)"
                etNouMissatge.setText(text)
            }.addOnFailureListener {
                etNouMissatge.setText("Error de lectura: ${it.message}")
            }
            }
        */

            btnAfegir.setOnClickListener {
                val popup = PopupMenu(this, btnAfegir)
                popup.menu.add("Afegir carpeta")
                popup.menu.add("Revisar llista complerta")


            }
        }
    }
}


