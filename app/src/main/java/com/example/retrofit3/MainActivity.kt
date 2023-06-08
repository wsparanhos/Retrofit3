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
            //val src: String = "data:image/jpeg;base64,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"
            println("Passei aqui 0")
            mApiServicePost = ApiClient.client.create(ApiServicePost::class.java)
            //-22.9015117,-47.0717623

            val ra = findViewById<EditText>(R.id.raEditTextNumber).text.toString()
            val lat = findViewById<TextView>(R.id.latTextView).text.toString()
            val long = findViewById<TextView>(R.id.textViewLongitude).text.toString()
            val img = findViewById<ImageView>(R.id.imgVM)
            val bitmap = (img.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val imageBytes = baos.toByteArray()
            val base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT)
            //dadosI = DadosI(ra="1000",lat="-22.9015117", lon="-47.0717623", img = src)
            dadosI = DadosI(ra,lat, long, base64Image)

            var gson = Gson().newBuilder().disableHtmlEscaping().create()
            var str = gson.toJson(dadosI)

            val call = mApiServicePost!!.sendDados(str)
            call!!.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.d("Resposta", "Resp : "+ response.body().toString())
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("Erro", "Got Error : " + t.localizedMessage)
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
    }
}