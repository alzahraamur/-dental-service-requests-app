package hmd.ec.a22143_project;

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class AddRequestFragment : Fragment(R.layout.fragment_add_request) {

    private lateinit var serviceInput: EditText
    private lateinit var noteInput: EditText
    private lateinit var buttonSubmit: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        serviceInput = view.findViewById(R.id.editTextService)
        noteInput = view.findViewById(R.id.editTextNote)
        buttonSubmit = view.findViewById(R.id.buttonSubmit)

        buttonSubmit.setOnClickListener {
            val service = serviceInput.text.toString().trim()
            val note = noteInput.text.toString().trim()

            if (service.isNotEmpty() && note.isNotEmpty()) {
                AddRequestTask().execute(service, note)
            } else {
                Toast.makeText(requireContext(), "Fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class AddRequestTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String): String {
            return try {
                val url = URL("http://eatsys2025.atwebpages.com/add_request.php")
                val conn = url.openConnection() as HttpURLConnection

                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

                val postData = "service_name=${URLEncoder.encode(params[0], "UTF-8")}" +
                        "&note=${URLEncoder.encode(params[1], "UTF-8")}"

                val outputStream = BufferedWriter(OutputStreamWriter(conn.outputStream, "UTF-8"))
                outputStream.write(postData)
                outputStream.flush()
                outputStream.close()

                val response = conn.inputStream.bufferedReader().use { it.readText() }
                response
            } catch (e: Exception) {
                e.printStackTrace()
                "{\"success\":false,\"message\":\"${e.message}\"}"
            }
        }

        override fun onPostExecute(result: String?) {
            Toast.makeText(requireContext(), result ?: "Error", Toast.LENGTH_LONG).show()
        }
    }
}
