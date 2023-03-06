package com.example.gosumoparent

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    //Global variable for EscrowId
    var escrowIdDig = 15
    var myJson = JSONObject()
    var User1 = "0xce3Db11E9f521F1b3dA0B5B6c07EE8f7a5A0F94D"
    var User2 = "0xc53aba332e45e7B49C86D4D35747037288b23D57"


    override fun onCreate(savedInstanceState: Bundle?) {
        //Get Id when the application starts
        GetEscrowId()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val blankFragment = BlankFragment.newInstance("param1", "param2")
        transaction.add(R.id.fragment_container, blankFragment)
        transaction.commit()

        //Button declarations
        myJson.put("escrowId", ReturnUpdatedId())
        val button1 = findViewById<Button>(R.id.Create)
        val button2 = findViewById<Button>(R.id.Open)
        val button3 = findViewById<Button>(R.id.Get)
        val spinner = findViewById<Spinner>(R.id.spinner)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Do something with the selected item
                val selectedItem = parent.getItemAtPosition(position)
                if(selectedItem == "User1"){
                    myJson.put("walletAddress", User1)
                    Toast.makeText(applicationContext, "Selected: $selectedItem with wallet address $User1", Toast.LENGTH_SHORT).show()
                }else if(selectedItem == "User2"){
                    myJson.put("walletAddress", User2)
                    Toast.makeText(applicationContext, "Selected: $selectedItem with wallet address $User1", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Toast.makeText(applicationContext, "Nothing selected", Toast.LENGTH_SHORT).show()
            }

        }

        //Create Escrow Id
        button1.setOnClickListener {
            Popup("Hold on let me create a Escrow Id for you")
            button1.isEnabled = false
            Handler(Looper.getMainLooper()).postDelayed({
                button1.isEnabled = true
            }, 4000)
            CreateEscrowId()
        }

        //Open game
        button2.setOnClickListener {
            val id = findViewById<TextView>(R.id.EscrowId)
            val userInput = id.text.toString()
            myJson.put("escrowId", userInput)
            blankFragment.launchGame(myJson.toString())
        }

        // Get Escrow Id
        button3.setOnClickListener{
            GetEscrowId()// Funtion to get Escrow Id
        }
    }

    //Getter setter for Escrow Id
    fun StoreUpdatedId(value: Int){
        escrowIdDig = value
    }
    fun ReturnUpdatedId():Int{
        return escrowIdDig
    }

    //Launch game with Escrow Id

    //Custom Toast
    fun Popup(value: String){
        runOnUiThread{
            Toast.makeText(this, value, Toast.LENGTH_SHORT).show()
        }
    }

    //Updating Escrow Id by calling Custom API
    fun UpdateEscrowId(){
        val dynamicId = ReturnUpdatedId()+1 //dynamic variable

        val json = """
                {
                    "digit":$dynamicId
                }"""

        val url = "https://escrowid-example.onrender.com/digit"
        val requestBody = RequestBody.create(MediaType.parse("application/json"), json)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Popup("Failed to Update Id")
            }
            override fun onResponse(call: Call, response: Response)
            {
                val id = findViewById<TextView>(R.id.EscrowId)
                val json = JSONObject( response.body()?.string())
                val digit = json.getInt("digit")
                Popup("Successfully Updated Escrow Id "+digit)
                StoreUpdatedId(digit)
                id.text = digit.toString()
            }
        })
    }

    //Getting Escrow Id from Custom API
    fun GetEscrowId(){
        val url = "https://escrowid-example.onrender.com/digit"
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
              print("Failed Created Id" + e)
            }
            override fun onResponse(call: Call, response: Response) {

                val json = JSONObject( response.body()?.string())
                val digit = json.getInt("digit")
                StoreUpdatedId(digit)
                val id = findViewById<TextView>(R.id.EscrowId)
                id.text = digit.toString()
                print("Successfully Created Id" + digit.toString())
            }
        })
    }

    //Creating Escrow Id by passing User Data
    fun CreateEscrowId(){
        val json = """
            {
                "user1":"0xce3Db11E9f521F1b3dA0B5B6c07EE8f7a5A0F94D",
                "user2":"0xc53aba332e45e7B49C86D4D35747037288b23D57", 
                "password":"5b7d0c83146141dac52cd845554130b4db4291a0ca9ef846fb73bc79072736eb@test.com",
                "keystore":"{\"encSeed\":{\"encStr\":\"oTeqhGKAbGUmwS91WTk1G049n78oANk6ohmDGOkHEhkECRB6o3EDwftYgv2zkot3QkgNVltvnJ7VbDD5p9+eUAmHOH5jiy4tIruicDbBSwy4TKh0C6zZRWl92SFaLzZYE7Um4QzXoQTO8PABuLetZDQqywDwC2jMsUojddcYgxQ54M8vxSQ1mA==\",\"nonce\":\"5RWt9n3Hlg9cjHh4PoiY4MRciOEpj4tQ\"},\"encHdRootPriv\":{\"encStr\":\"aH2F+Z0ADpa0OYK9+GhHblR9ipzGntbD03+fni/c122XfzYbaAzTsNQIGBKkbFQ4PnbqfYGq5+iVO58vKqKneEdQt129qgrWq9jkWmqHsDtz/eeFshG1SmhKRze4RUYwuvZ5+WDpuhEl7SJsytEJR2OgOb5zo3I8idPTCWl1Uw==\",\"nonce\":\"y7fb/OLEBnl3SwPJ+HY4ysBncsb076sq\"},\"addresses\":[\"ce3db11e9f521f1b3da0b5b6c07ee8f7a5a0f94d\"],\"encPrivKeys\":{\"ce3db11e9f521f1b3da0b5b6c07ee8f7a5a0f94d\":{\"key\":\"JzlVp7P381qKDrsBuzXSVsbCz4/vzgMGJqGlWPRwwgCgIDqlyMIto1O6g7YS3dyL\",\"nonce\":\"c88NINY/p3Iv9R/3joa0hXEDJmX4XLA+\"}},\"hdPathString\":\"m/44'/60'/0'/0\",\"salt\":\"t0aNdyv2UfBrsZ4PVc7EtGHcnkM7ztKehFWd3j1SrUs=\",\"hdIndex\":1,\"version\":10}"
            }"""
        val url = "http://3.236.113.97/escrow/createescrow/"
        val requestBody = RequestBody.create(MediaType.parse("application/json"), json)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Popup("Failed to createEscrow Id")
                print("Failed Create Id")
            }
            override fun onResponse(call: Call, response: Response)
            {
                Popup("Successfully created Escrow Id")
                print("Successfully Created Id")
                val id = findViewById<TextView>(R.id.EscrowId)
                id.text = "Generating"
                UpdateEscrowId()
            }
        })
    }
}



