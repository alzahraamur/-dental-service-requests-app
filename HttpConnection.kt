package hmd.ec.a22143_project

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class HttpConnection {
    suspend fun connect(url: URL, postData: String): String {
        return withContext(Dispatchers.IO) {
            val http = url.openConnection() as HttpURLConnection
            try {
                http.requestMethod = "POST"
                http.doOutput = true
                http.useCaches = false
                http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

                DataOutputStream(http.outputStream).use { it.writeBytes(postData) }

                val responseBuilder = StringBuilder()
                BufferedReader(InputStreamReader(http.inputStream)).use { br ->
                    var line: String?
                    while (br.readLine().also { line = it } != null) {
                        responseBuilder.append(line)
                    }
                }
                responseBuilder.toString()
            } finally {
                http.disconnect()
            }
        }
    }
}