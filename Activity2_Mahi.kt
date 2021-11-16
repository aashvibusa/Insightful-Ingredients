package com.example.insightfulingredientsfinalapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import kotlinx.android.synthetic.main.activity_2.*

import android.view.View
import android.widget.*
import com.fasterxml.jackson.core.JsonFactory
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection
import java.net.URL
import com.google.api.services.sheets.v4.Sheets

import com.google.api.client.json.jackson2.JacksonFactory

import com.google.api.client.extensions.android.http.AndroidHttp

import com.google.api.client.http.HttpTransport




private const val CAMERA_REQUEST_CODE = 101

var google_api_key = "AIzaSyAYKjhfGyH4x5W5v61dlE_4VxCOuhkBlzc"
var spreadsheet_id = "1cDQZuRyDa13grv7FvhgyhIeRrPrcFf2JhqmFBBXjoPc"

var label = arrayOf<String>()
val printIngredients = arrayOfNulls<String>(40)
val printDescriptions = arrayOfNulls<String>(40)

class Activity2 : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var codeScanner: CodeScanner

    var barcodeNum = "49000050103"
    var resultView: TextView? = null

    override fun onNothingSelected(parent: AdapterView<*>?) {
        barcodeNum = "49000050103"
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            barcodeNum = parent.getItemAtPosition(position).toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_2)

        setupPermissions()
        codeScanner()

        var buttonView: Button = findViewById(R.id.button)
        buttonView.setOnClickListener {
            //GlobalScope.async {
                //getProducts()
            //}
            val intent =   Intent(this, ListActivity::class.java)
            startActivity(intent)
        }

        resultView = findViewById(R.id.resultView)
    }

    private fun codeScanner() {
        codeScanner = CodeScanner(this, scanner_view)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                runOnUiThread {
                    barcodeNum = it.text
                    tv_textView.text = barcodeNum
                }
            }

            errorCallback = ErrorCallback {
                runOnUiThread {
                    Log.e("Main", "Camera initialization error: ${it.message}")
                }
            }
        }

        scanner_view.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }


    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You need the camera permission to be able to use this app", Toast.LENGTH_SHORT).show()
                } else {
                    // successful
                }
            }
        }
    }

    private fun setText(text: TextView?, value: String) {
        runOnUiThread { text!!.text = value }
    }

    private suspend fun getProducts() {
        try {
            val result = GlobalScope.async {
                callBarcodeAPI()
                callSheetsAPI()
            }.await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callBarcodeAPI(){
        GlobalScope.launch(Dispatchers.IO) {
            val url = URL("https://edamam-food-and-grocery-database.p.rapidapi.com/parser?upc=" + barcodeNum)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.setRequestProperty("Accept", "application/json") // The format of response we want to get from the server
            // set host name
            httpURLConnection.setRequestProperty("x-rapidapi-host", "edamam-food-and-grocery-database.p.rapidapi.com")
            // set the rapid-api key
            httpURLConnection.setRequestProperty("x-rapidapi-key", "653e30becfmsh771e972cf94156dp1d68f9jsnc217777e6b1a")
            httpURLConnection.requestMethod = "GET"
            httpURLConnection.doInput = true
            httpURLConnection.doOutput = false
            // Check if the connection is successful
            val responseCode = httpURLConnection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = httpURLConnection.inputStream.bufferedReader()
                    .use { it.readText() }  // defaults to UTF-8
                withContext(Dispatchers.Main) {

                    // Convert raw JSON to pretty JSON using GSON library
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(JsonParser.parseString(response))

                    // convert the string to JSON object for better reading
                    val resultJson = JSONObject(prettyJson)

                    try {

                        // Initialize product text
                        var name = ""

                        // Update text with various fields from response
                        name += resultJson.getJSONArray("hints").getJSONObject(0).getJSONObject("food").getString("label")
                        name += "\n"
                        name += "\n"
                        var ingredients = resultJson.getJSONArray("hints").getJSONObject(0).getJSONObject("food").getString("foodContentsLabel")
                        // var ingredients = "DRIED POTATOES; VEGETABLE OIL (CORN; COTTONSEED; HIGH OLEIC SOYBEAN; AND/OR SUNFLOWER OIL); DEGERMINATED YELLOW CORN FLOUR; CORNSTARCH; RICE FLOUR; MALTODEXTRIN; MONO- AND DIGLYCERIDES. CONTAINS 2% OR LESS OF SALT; WHEAT STARCH."
                        var test = ""
                        // Diya look from here down
                        ingredients = ingredients.replace("\\s*\\([^\\)]*\\)\\s*".toRegex(), "")
                        var strs = ingredients.split(";").toTypedArray()
                        for (i in strs.indices) {
                            strs[i] = strs[i].trim().uppercase()
                        }
                        label = strs
                        //Log.e(test, responseCode.toString())
                        //setText(resultView,label[2])




                    } catch (e: Exception) {
                        e.printStackTrace()
                        resultView!!.text = "Oops!! something went wrong, please try again"
                    }

                }
            } else {
                setText(resultView, "Sorry, the product you scanned doesn't match anything in the database")
                Log.e("HTTPURLCONNECTION_ERROR", responseCode.toString())
            }
        }
    }


    private fun callSheetsAPI() {
        GlobalScope.launch(Dispatchers.IO) {
            val url = URL("https://sheets.googleapis.com/v4/spreadsheets/1cDQZuRyDa13grv7FvhgyhIeRrPrcFf2JhqmFBBXjoPc/values/Sheet1?key=AIzaSyAYKjhfGyH4x5W5v61dlE_4VxCOuhkBlzc")
            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.setRequestProperty("Accept", "application/json") // The format of response we want to get from the server
            httpURLConnection.requestMethod = "GET"
            httpURLConnection.doInput = true
            httpURLConnection.doOutput = false
            // Check if the connection is successful
            val responseCode = httpURLConnection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = httpURLConnection.inputStream.bufferedReader()
                    .use { it.readText() }  // defaults to UTF-8
                withContext(Dispatchers.Main) {

                    // Convert raw JSON to pretty JSON using GSON library
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(JsonParser.parseString(response))

                    // convert the string to JSON object for better reading
                    val resultJson = JSONObject(prettyJson)

                    val gIngredients = Array<String>(40) { "it = $it"}
                    val gDescriptions = Array<String>(40) { "it = $it"}
                    var tempArray = Array<String>(40) { "it = $it"}

                    //callBarcodeAPI()
                    try {
                        var string = ""

                        for (i in 1..30) {
                            var temp = resultJson.getJSONArray("values").getString(i)
                            temp = temp.replace("\\[|\\]".toRegex(), "")
                            val matchCommaNotInQuotes = Regex("""\,(?=([^"]*"[^"]*")*[^"]*$)""")
                            tempArray = temp.split(matchCommaNotInQuotes).toTypedArray()
                            gIngredients[i-1] = tempArray[0].trim().uppercase().replace("\"", "")
                            gDescriptions[i-1] = tempArray[1].trim().uppercase().replace("\"", "")
                            string += gDescriptions[i-1] + ", "
                        }

                        var count = 0
                        for (i in label.indices) {
                            for (j in gIngredients.indices) {
                                if (label[i].equals(gIngredients[j])) {
                                    printIngredients[count] = gIngredients[i]
                                    printDescriptions[count] = gDescriptions[i]
                                    count += 1
                                }
                            }
                        }

                        var tally = 0
                        for (i in printIngredients.indices) {
                            if (printIngredients[i] != null) {
                                tally += 1
                            }
                        }

                        val printIngredientsFinal = Array<String>(tally) { "it = $it"}
                        val printDescriptionsFinal = Array<String>(tally) { "it = $it"}

                        var list = ""
                        for (i in printIngredients.indices) {
                            if (printIngredients[i] != null) {
                                printIngredientsFinal[i] = printIngredients[i].toString()
                                printDescriptionsFinal[i] = printDescriptions[i].toString()
                                list += printIngredientsFinal[i] + ", "
                            }
                        }
                        if (list == "") {
                            //setText(resultView, "No ingredients were found in our database")
                        } else {
                            //setText(resultView,list)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        resultView!!.text = "Oops!! something went wrong, please try again"
                    }

                }
            } else {
                setText(resultView, "Sorry, the product you scanned doesn't match anything in the database")
                Log.e("HTTPURLCONNECTION_ERROR", responseCode.toString())
            }
        }
    }

}

private operator fun <T> Array<T>.set(i: T, value: T) {

}