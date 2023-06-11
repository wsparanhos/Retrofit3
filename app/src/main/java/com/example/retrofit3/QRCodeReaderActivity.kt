package com.example.retrofit3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class QRCodeReaderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_reader)

        startQRCodeScanner()
    }

    private fun startQRCodeScanner() {
        IntentIntegrator(this).initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val result: IntentResult? =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {
            if (result.contents == null) {
                Log.d("QRCodeReaderActivity", "Cancelled")
                finish()
            } else {
                Log.d("QRCodeReaderActivity", "Scanned: " + result.contents)
                // Aqui você pode exibir o conteúdo do QR Code como desejar
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
