package com.example.insightfulingredientsfinalapp

import com.example.insightfulingredientsfinalapp.MainActivity

import android.R
import android.content.Intent
import android.graphics.Color

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import android.widget.TextView

class ListActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.insightfulingredientsfinalapp.R.layout.list_activity)

        var listView = findViewById<ListView>(com.example.insightfulingredientsfinalapp.R.id.listView)

        var ing = arrayOf<String>("Phosphoric Acid: Phosphoric acid is a colorless, odorless crystalline liquid. It gives soft drinks a tangy flavor and prevents the growth of mold and bacteria, which can multiply easily in a sugary solution.",
            "Aspartame: Aspartme is an artifical sweetener 200 times sweeter than sucrose. It is commonly used as a sugar substitute in food and drinks.",
            "Potassium Benzoate: Potassium benzoate is a white, odorless powder used as a preservative, as it prevents the growth of bacteria, yeast, and particularly mold.",
            "Acesulfame Potassium: Acesulfame Potassium is a white powder that is more intense than sucrose, and is used as a noncaloric sweetener in foods and beverages.",
            "Sodium Benzoate: Sodium benzoate prohibits the growth of potentially harmful bacteria, mold, and other microbes in food, thus deterring spoilage. It's particularly effective in acidic foods. Therefore, it's commonly used in soda.",
            "Yellow 5: Yellow 5 is an artificial food color. Its purpose to is make foods, particularly highly processed foods and drinks, like candy and soda appear more fresh, flavorful, and appetizing.",
            "Caramel Color: Caramel color is added to many soft drinks and some foods to turn them brown, but does not resemble real caramel.")

        val adapter: ArrayAdapter<String?> = object : ArrayAdapter<String?>(
            this, R.layout.simple_list_item_1, ing
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<View>(R.id.text1) as TextView

                /*YOUR CHOICE OF COLOR*/textView.setTextColor(Color.WHITE)
                return view
            }
        }
        listView.adapter = adapter
    }





}