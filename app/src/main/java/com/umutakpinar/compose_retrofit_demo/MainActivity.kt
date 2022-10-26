package com.umutakpinar.compose_retrofit_demo

import android.content.res.Resources.Theme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umutakpinar.compose_retrofit_demo.model.CryptoModel
import com.umutakpinar.compose_retrofit_demo.service.CryptoAPI
import com.umutakpinar.compose_retrofit_demo.ui.theme.ComposeretrofitdemoTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//https://raw.githubusercontent.com/atilsamancioglu/K21-JSONDataSet/master/crypto.json
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeretrofitdemoTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(){

    val cryptoModels = remember { mutableStateListOf<CryptoModel>()}

    val BASE_URL = "https://raw.githubusercontent.com/"
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CryptoAPI::class.java)

    val call = retrofit.getData()

    call.enqueue(object : Callback<List<CryptoModel>>{
        override fun onResponse(
            call: Call<List<CryptoModel>>,
            response: Response<List<CryptoModel>>
        ) {
            if(response.isSuccessful){
                response.body()?.let { it ->
                    cryptoModels.addAll(it)
//                    cryptoModels.forEach{
//                        println("${it.currency} - ${it.price}")
//                    }
                }
            }
        }

        override fun onFailure(call: Call<List<CryptoModel>>, t: Throwable) {
            t.printStackTrace()
        }

    })

    Scaffold(topBar = {AppBar()}) { paddingValues ->
        CryptoList(cryptos = cryptoModels, paddingValues)
    }

}

@Composable
fun CryptoList(cryptos : List<CryptoModel>, paddingValues: PaddingValues){
    LazyColumn(Modifier.padding(paddingValues)){
        items(cryptos){ it ->
            CryptoRow(crypto = it)
        }
    }
}

@Composable
fun CryptoRow(crypto : CryptoModel){
    Column(modifier = Modifier
        .fillMaxWidth(1f)
        .background(color = MaterialTheme.colors.surface)
    ) {
        Text(text = crypto.currency,
            fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.h3,
            fontWeight = FontWeight.Bold
            )
        Text(text = crypto.price,
            fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Light)
    }
}

@Composable
fun AppBar(){
    TopAppBar(contentPadding = PaddingValues(10.dp)) {
        Text(text = "Retrofit & Compose Demo App",
            fontSize = 26.sp,
            fontFamily = FontFamily.Monospace)
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeretrofitdemoTheme {
        CryptoList(cryptos = listOf(
            CryptoModel("RCO","593498$"),
            CryptoModel("SVO","5998$"),
            CryptoModel("TSO","59$") )
        , PaddingValues(10.dp))
    }
}

