package com.example.retrofit3

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.location.LocationManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.retrofit3.databinding.ActivityMainBinding
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var mApiService: ApiService? = null
    lateinit var EstruturaList: ArrayList<EstruturaApi>

    private var mApiServicePost: ApiServicePost? = null

    var dadosI : DadosI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLerQrCode.setOnClickListener {
            val intent = Intent(this, QRCodeReaderActivity::class.java)
            startActivity(intent)
        }

        binding.btnGet.setOnClickListener {

            //val ra = R.id.raEditTextNumber

            val raEditText = findViewById<EditText>(R.id.raEditTextNumber)
            val ra: String = raEditText.text.toString()

            mApiService = ApiClient.client.create(ApiService::class.java)
            // agora chamamos o call para enfilerar a chamada e esperar a resposta
            val call = mApiService!!.fetchDados(ra)
            call!!.enqueue(object : Callback<ArrayList<EstruturaApi>?> {

                override fun onResponse(
                    call: Call<ArrayList<EstruturaApi>?>,
                    response: Response<ArrayList<EstruturaApi>?>
                ) {
                   Log.d("OK", " dados : "+ response.body()!!)
                    EstruturaList = response.body()!!
                    var msg: String = " id :"
                    msg = msg + EstruturaList[2].id!! + " ra"
                    msg = msg + EstruturaList[2].ra!! + "\n"
                    msg = msg + "data : " + EstruturaList[2].data!! + "\n"
                    msg = msg + EstruturaList[2].lat!! + "\n"
                    msg = msg + EstruturaList[2].log!! + "\n"
                    binding.resultado.text = msg
                    Log.d("Img", EstruturaList[2].img!!)
                    val imageBytes = Base64.decode(EstruturaList[2].img!!, Base64.DEFAULT)
                    var bitmap: Bitmap? = null
                    bitmap = BitmapFactory.decodeByteArray(
                        imageBytes,
                        0,
                        imageBytes.size)
                    binding.imgVM.setImageBitmap(bitmap)

                }

                override fun onFailure(call: Call<ArrayList<EstruturaApi>?>, t : Throwable){
                    Log.e("Erro", "Got Error : " + t.localizedMessage)
                }
            })
        }

        binding.btnPost.setOnClickListener {
            println("passei 1")
            val ra = findViewById<EditText>(R.id.raEditTextNumber).text.toString()
            println("passei 2")
            val lat = findViewById<TextView>(R.id.latTextView).text.toString()
            println("passei 3")
            val long = findViewById<TextView>(
            R.id.textViewLongitude).text.toString()
            println("passei 4")
            val img = findViewById<ImageView>(R.id.imgVM)
            println("passei 5")
            val bitmap = (img.drawable as BitmapDrawable).bitmap
            println("passei 6")
            val baos = ByteArrayOutputStream()
            println("passei 7")
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val imageBytes = baos.toByteArray()
            val base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT)
            dadosI = DadosI(ra, lat, long, base64Image)

            val gson = Gson().newBuilder().disableHtmlEscaping().create()
            val str = gson.toJson(dadosI)

            mApiServicePost = ApiClient.client.create(ApiServicePost::class.java)
            val call = mApiServicePost!!.sendDados(str)
            call!!.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.d("Resposta", "Resp: " + response.body().toString())
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("Erro", "Got Error: " + t.localizedMessage)
                }
            })
        }


        binding.btnTakePhotoM.setOnClickListener{
            binding.resultado.text = "Tirar a foto!"

            //val intent = Intent(this,
            //    PhotoActivity::class.java)
            //startActivity(intent)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.imgVM.setImageBitmap(imageBitmap)

            // Verificar permissão para acessar a localização
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Obter o serviço de localização
                val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

                // Verificar se o GPS está habilitado
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    // Obter a última localização conhecida
                    val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

                    // Verificar se a localização é válida
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude

                        binding.textViewLatitude.text = latitude.toString()
                        binding.textViewLongitude.text = longitude.toString()

                        // Utilize as coordenadas do GPS conforme necessário
                        Log.d("GPS", "Latitude: $latitude, Longitude: $longitude")
                    } else {
                        // Caso a localização seja nula, pode ser necessário solicitar uma atualização da localização
                        // ou exibir uma mensagem ao usuário informando que a localização não está disponível no momento.
                        Log.d("GPS", "Localização não disponível")
                    }
                } else {
                    // Caso o GPS não esteja habilitado, você pode solicitar ao usuário para habilitá-lo
                    // ou exibir uma mensagem informando que o GPS está desabilitado.
                    Log.d("GPS", "GPS desabilitado")
                }
            } else {
                // Caso a permissão de acesso à localização não tenha sido concedida pelo usuário, você pode
                // solicitar a permissão novamente ou exibir uma mensagem informando que a permissão é necessária.
                Log.d("GPS", "Permissão de localização não concedida")
            }
        }
    }
    companion object {
        private const val REQUEST_PHOTO_ACTIVITY = 1
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_QR_CODE = 1
    }
}