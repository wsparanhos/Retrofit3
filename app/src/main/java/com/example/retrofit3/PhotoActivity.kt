package com.example.retrofit3

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.textview.MaterialTextView

class PhotoActivity : AppCompatActivity() {

    private lateinit var imgVF: ImageView
    private lateinit var raEditTextNumber: EditText
    private lateinit var latTextView: MaterialTextView
    private lateinit var longTextView: MaterialTextView
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)

        imgVF = findViewById(R.id.imgVF)
        raEditTextNumber = findViewById(R.id.raEditTextNumber)
        latTextView = findViewById(R.id.latTextView)
        longTextView = findViewById(R.id.longTextView)
        val btnTakePhotoF: Button = findViewById(R.id.btnTakePhotoF)

        btnTakePhotoF.setOnClickListener {
            println("PHOTO-Passei 0")
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                capturePhoto()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    PERMISSION_REQUEST_CAMERA
                )
            }
        }

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_LOCATION
            )
        } else {
            startLocationUpdates()
        }
    }

    private fun capturePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        println("PHOTO-Passei 1")
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("PHOTO-Passei 2")
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.getString("data") as? Bitmap
            println("PHOTO-Passei 1")
            if (imageBitmap != null) {
                imgVF.setImageBitmap(imageBitmap)
            } else {
                // Tratar caso o valor retornado seja nulo
            }
        }
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BETWEEN_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                    locationListener
                )
            } else {
                // Exibir mensagem ou solicitar que o usuário habilite a localização
            }
        }
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val latitude = location.latitude
            val longitude = location.longitude

            latTextView.text = latitude.toString()
            longTextView.text = longitude.toString()
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

        override fun onProviderEnabled(provider: String) {
            // Provedor de localização ativado
        }

        override fun onProviderDisabled(provider: String) {
            // Provedor de localização desativado
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            }
        } else if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                capturePhoto()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(locationListener)
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val PERMISSION_REQUEST_LOCATION = 2
        private const val PERMISSION_REQUEST_CAMERA = 3
        private const val MIN_TIME_BETWEEN_UPDATES: Long = 1000 // 1 segundo
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 1.0f // 1 metro
    }
}
