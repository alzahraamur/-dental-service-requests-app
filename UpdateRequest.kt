package hmd.ec.a22143_project

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class UpdateRequestActivity : AppCompatActivity() {

    private lateinit var editTextId: EditText
    private lateinit var editTextService: EditText
    private lateinit var editTextNote: EditText
    private lateinit var buttonUpdate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        editTextId = findViewById(R.id.editTextId)
        editTextService = findViewById(R.id.editTextService)
        editTextNote = findViewById(R.id.editTextNote)
        buttonUpdate = findViewById(R.id.buttonUpdate)


        val id = intent.getIntExtra("id", -1).toString()
        val serviceName = intent.getStringExtra("service_name") ?: ""
        val note = intent.getStringExtra("note") ?: ""

        editTextId.setText(id)
        editTextService.setText(serviceName)
        editTextNote.setText(note)
        buttonUpdate.setOnClickListener {
            val requestId = editTextId.text.toString().trim()
            val service = editTextService.text.toString().trim()
            val noteText = editTextNote.text.toString().trim()


            Log.d("UPDATE_DEBUG", "ID=$requestId, Service=$service, Note=$noteText")

            if (requestId.isNotEmpty() && service.isNotEmpty() && noteText.isNotEmpty()) {
                UpdateRequestTask().execute(requestId, service, noteText)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class UpdateRequestTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String): String {
            val url = URL("http://eatsys2025.atwebpages.com/update_request.php")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

            val data = "id=${params[0]}&service_name=${params[1]}&note=${params[2]}"
            val out = BufferedWriter(OutputStreamWriter(conn.outputStream))
            out.write(data)
            out.flush()
            out.close()

            return conn.inputStream.bufferedReader().readText()
        }

        override fun onPostExecute(result: String?) {
            Toast.makeText(this@UpdateRequestActivity, result ?: "Update failed", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
