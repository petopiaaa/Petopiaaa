package edu.sswu.petopia.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import edu.sswu.petopia.R
import edu.sswu.petopia.SearchActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import edu.sswu.petopia.data.WeatherApi
import edu.sswu.petopia.data.WeatherResponse
import android.content.Intent
import edu.sswu.petopia.BuildConfig


class HomeFragment : Fragment() {

    private val apiKey = BuildConfig.OPENWEATHER_API_KEY
    private val baseUrl = "https://api.openweathermap.org/data/2.5/"
    private lateinit var weatherTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        // 카테고리 아이콘 설정
        val koreanFoodIcon: ImageView = rootView.findViewById(R.id.icon_korean_food)
        val japaneseFoodIcon: ImageView = rootView.findViewById(R.id.icon_japanese_food)
        val westernFoodIcon: ImageView = rootView.findViewById(R.id.icon_western_food)
        val chineseFoodIcon: ImageView = rootView.findViewById(R.id.icon_chinese_food)
        val pubIcon: ImageView = rootView.findViewById(R.id.icon_pub)
        val cafeIcon: ImageView = rootView.findViewById(R.id.icon_cafe)

        // 날씨 정보 TextView
        weatherTextView = rootView.findViewById(R.id.weatherTextView)

        // 날씨 정보 가져오기
        fetchWeatherInfo()

        // 각 아이콘 클릭 리스너
        koreanFoodIcon.setOnClickListener { navigateToSearchActivity("한식") }
        japaneseFoodIcon.setOnClickListener { navigateToSearchActivity("일식") }
        westernFoodIcon.setOnClickListener { navigateToSearchActivity("양식") }
        chineseFoodIcon.setOnClickListener { navigateToSearchActivity("중식") }
        pubIcon.setOnClickListener { navigateToSearchActivity("펍") }
        cafeIcon.setOnClickListener { navigateToSearchActivity("카페") }

        return rootView
    }

    private fun navigateToSearchActivity(category: String) {
        val intent = Intent(requireContext(), SearchActivity::class.java)
        intent.putExtra("category", category)
        startActivity(intent)
    }

    private fun fetchWeatherInfo() {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(WeatherApi::class.java)
        val call = api.getCurrentWeather("Seoul", apiKey, "metric")

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    val weatherText = "현재 날씨: ${weatherResponse?.main?.temp}°C, ${weatherResponse?.weather?.get(0)?.description}"
                    weatherTextView.text = weatherText
                } else {
                    weatherTextView.text = "날씨 정보를 가져올 수 없습니다."
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("HomeFragment", "Failed to fetch weather info", t)
                weatherTextView.text = "날씨 정보를 가져올 수 없습니다."
            }
        })
    }
}
