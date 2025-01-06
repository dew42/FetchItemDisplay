package com.example.fetchitemdisplay.api

import android.util.Log
import com.example.fetchitemdisplay.models.Item
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class FetchAPI {

    private val loggingTag = "FetchAPI"

    private val listDataURL = "https://fetch-hiring.s3.amazonaws.com/hiring.json"

    private suspend fun getJsonFromUrl(urlString: String) : String {
        val apiResults = withContext(Dispatchers.IO) {
            try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    Log.d(loggingTag, "Error: ${connection.responseCode}")
                }
            }
            catch (e: Exception) {
                Log.d(loggingTag, "Error: ${e.message}")
            }
        }

        return apiResults.toString()
    }

    suspend fun getItemListFromAPI() :ArrayList<Item> {
        val apiResults = getJsonFromUrl(listDataURL)
        val gson = Gson()
        return gson.fromJson(apiResults, Array<Item>::class.java).toCollection(ArrayList())
    }
}