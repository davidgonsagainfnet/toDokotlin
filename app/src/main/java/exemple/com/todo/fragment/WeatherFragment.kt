package exemple.com.todo.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import exemple.com.todo.MainActivity
import exemple.com.todo.R
import exemple.com.todo.databinding.FragmentWeatherBinding
import exemple.com.todo.util.BitmapConvert
import kotlinx.coroutines.launch

class WeatherFragment(val ctx: MainActivity) : Fragment() {

    private lateinit var viewModel: WeatherViewModel
    private lateinit var binding: FragmentWeatherBinding

    companion object{
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWeatherBinding.inflate(layoutInflater)

        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getLocation(binding.root)
        }else{
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }

        viewModel = WeatherViewModel(ctx)


        observer()

        //return inflater.inflate(R.layout.fragment_weather, container, false)
        return binding.root
    }

    private fun observer() {
        viewModel.imgTimer.observe(ctx){
            binding.imgLogo.setImageBitmap(it)
        }
        viewModel.temperatura.observe(ctx){
            binding.txtGraus.text = "$it ºC"
        }
        viewModel.local1.observe(ctx){
            binding.txtLocal.text = it
        }
        viewModel.local2.observe(ctx){
            binding.txtLocal2.text = it
        }
    }

    private fun getLocation(view: View){
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if(ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, object:
            LocationListener {
            override fun onLocationChanged(location: Location) {
                viewModel.getWeather(location.latitude.toString(), location.longitude.toString())
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        })
    }
}