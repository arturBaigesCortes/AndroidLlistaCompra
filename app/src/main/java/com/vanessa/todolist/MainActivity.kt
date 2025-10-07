package com.vanessa.todolist

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
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


        // Funció que genera un retard en el programa sense congelar la UI.
        suspend fun espera(temps: Long) {
            delay(temps)
        }

        btnAfegir.setOnClickListener {
            val text = inputProducte.text.toString()
            if (text.isNotEmpty()) {
                // Layout horitzontal
                val fila = LinearLayout(this)
                fila.orientation = LinearLayout.HORIZONTAL

                // CheckBox amb el text del producte
                val checkBox = CheckBox(this)
                checkBox.text = text

                // Afegim els components a la fila
                fila.addView(checkBox)

                // Afegim la fila a la llista
                llistaProductes.addView(fila)

                inputProducte.text.clear()

                // Botó afegir a completacions.
                checkBox.setOnClickListener {
                    // Utilitzem una corutina per generar un delay
                    lifecycleScope.launch {
                        espera(1000)
                        llistaProductes.removeView(fila)
                    }
                }
            }
            }
    }
}


