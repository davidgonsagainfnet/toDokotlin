package exemple.com.todo.fragment

import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import exemple.com.todo.MainActivity
import exemple.com.todo.app.RetrofitClient
import exemple.com.todo.util.BitmapConvert
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherViewModel(private val ctx: MainActivity): AndroidViewModel(ctx.application) {
    val imgTimer = MutableLiveData<Bitmap>()
    val temperatura = MutableLiveData<Number>()
    val local1 = MutableLiveData<String>()
    val local2 = MutableLiveData<String>()

    fun getWeather(latitude: String, longitude: String){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiKey = "91fbe21a8c3c4d3d8ef174846232703"
                val apiTempo = RetrofitClient(ctx).syncCampService().getWeather("pt",apiKey,"$latitude,$longitude")
                println(apiTempo)
                val bitmap = BitmapConvert.urlToBitmap(apiTempo.current.condition.icon.replace("//","https://"), ctx)
                imgTimer.postValue(bitmap)
                temperatura.postValue(apiTempo.current.temp_c)
                local1.postValue(apiTempo.location.region)
                local2.postValue(apiTempo.location.name)
            } catch (e: Exception){
                println(e.message)
                Toast.makeText(ctx, e.message, Toast.LENGTH_SHORT).show()

            }
        }
    }

}