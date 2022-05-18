package com.example.volleyandcurrencyapp

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.volleyandcurrencyapp.adapters.CurrencyAdapter
import com.example.volleyandcurrencyapp.databinding.ActivityMainBinding
import com.example.volleyandcurrencyapp.databinding.BottomDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private var list: List<com.example.volleyandcurrencyapp.models.Currency>? = null
    private var adapter: CurrencyAdapter? = null
    private val uri = "https://cbu.uz/oz/arkhiv-kursov-valyut/json/"
    private var requestQueue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // disable night mode settings
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        loadData()
        itemClick()
    }

    private fun itemClick() {

        adapter?.setOnItemClick(object : CurrencyAdapter.OnItemClick {
            @SuppressLint("ResourceAsColor")
            override fun onClick(currency: com.example.volleyandcurrencyapp.models.Currency) {
                val dialog = BottomSheetDialog(this@MainActivity, R.style.SheetDialog)
                val dialogBinding = BottomDialogBinding.inflate(layoutInflater, null, false)
                dialogBinding.code.text = currency.Ccy
                dialogBinding.nameUz.text = currency.CcyNm_UZ
                dialogBinding.digitalCode.text = currency.Code
                dialogBinding.rate.text = currency.Rate
                dialogBinding.diff.text = currency.Diff
                dialogBinding.date.text = currency.Date
                if (currency.Diff.startsWith("+", true)) {
                    dialogBinding.diff.setTextColor(R.color.diff_p)
                } else if (currency.Diff.startsWith("-", true)) {
                    dialogBinding.diff.setTextColor(R.color.diff_m)
                }

                val smsBody =
                    dialogBinding.code1.text.toString() + ":  " + dialogBinding.code.text.toString() + "\n\n" +
                            dialogBinding.nameUz1.text.toString() + ":  " + dialogBinding.nameUz.text.toString() + "\n\n" +
                            dialogBinding.digitalCode1.text.toString() + ":  " + dialogBinding.digitalCode.text.toString() + "\n\n" +
                            dialogBinding.rate1.text.toString() + ":  " + dialogBinding.rate.text.toString() + "\n\n" +
                            dialogBinding.diff1.text.toString() + ":  " + dialogBinding.diff.text.toString() + "\n\n" +
                            dialogBinding.date1.text.toString() + ":  " + dialogBinding.date.text.toString()

                dialogBinding.message.setOnClickListener {

                    val sendIntent = Intent(Intent.ACTION_VIEW)
                    sendIntent.putExtra("sms_body", smsBody)
                    sendIntent.type = "vnd.android-dir/mms-sms"
                    startActivity(sendIntent)
                }

                dialogBinding.share.setOnClickListener {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, smsBody)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                }

                dialogBinding.copy.setOnClickListener {
                    val myClipboard = ContextCompat.getSystemService(
                        this@MainActivity,
                        ClipboardManager::class.java
                    ) as ClipboardManager
                    val clip: ClipData = ClipData.newPlainText("simple text", smsBody)
                    myClipboard.setPrimaryClip(clip)
                    Toast.makeText(this@MainActivity, "Copied", Toast.LENGTH_SHORT).show()
                }

                dialog.setContentView(dialogBinding.root)
                dialog.show()
            }

        })
    }

    private fun loadData() {
        requestQueue = Volley.newRequestQueue(this)
        val networkHelper = NetworkHelper(this)
        adapter = CurrencyAdapter()

        if (networkHelper.isNetworkConnected()) {
            val jsonArrayRequest = JsonArrayRequest(
                Request.Method.GET, uri, null,
                { response ->
                    val type = object : TypeToken<List<Currency>>() {}.type
                    list = Gson().fromJson(response.toString(), type)
                    list?.let { adapter?.setAdapter(it) }
                    binding.rv.adapter = adapter
                    Log.d("AAAA", "onCreate: Success!")
                }) {
                Toast.makeText(this, "Internetni tekshiring", Toast.LENGTH_LONG).show()
            }
            requestQueue?.add(jsonArrayRequest)
        }
    }
}