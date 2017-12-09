package usbpc.aoc.inputgetter

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

class AdventOfCode(private val session: String) {
    private val httpClient = OkHttpClient()
    fun getInput(year: Int, day: Int) : String {
        val cachedFile = File("resources/$year/$day.txt")
        if (!cachedFile.exists()) {
            val request = Request.Builder()
                    .url("https://adventofcode.com/$year/day/$day/input")
                    .addHeader("Cookie", "session=$session")
                    .build()
            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                throw IllegalStateException("Something went wrong ${response.message()}")
            }
            val responseText = response.body()?.string() ?: throw IllegalStateException("The response Body was empty!")
            cachedFile.parentFile.mkdirs()
            cachedFile.createNewFile()
            cachedFile.writeText(responseText)
            return responseText.removeSuffix("\n")
        }
        return cachedFile.readText().removeSuffix("\n")
    }
}