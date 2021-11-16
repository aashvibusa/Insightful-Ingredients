package com.example.insightfulingredientsfinalapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async


class  MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button2)
        button.setOnClickListener{
            val intent = Intent(this, Activity2::class.java)
            startActivity(intent)
            //val intent =   Intent(this, ListActivity::class.java)
            //startActivity(intent)
        }
    }

    }
