package hmd.ec.a22143_project

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class RequestAdapter(
    private val requests: List<Request>,
    private val onRequestDeleted: () -> Unit
) : RecyclerView.Adapter<RequestAdapter.RequestViewHolder>() {

    inner class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textService: TextView = itemView.findViewById(R.id.textServiceName)
        val textNote: TextView = itemView.findViewById(R.id.textNote)
        val textStatus: TextView = itemView.findViewById(R.id.textStatus)
        val buttonEdit: Button = itemView.findViewById(R.id.buttonEdit)
        val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_request, parent, false)
        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val request = requests[position]
        holder.textService.text = request.serviceName
        holder.textNote.text = request.note
        holder.textStatus.text = request.status

        if (request.status.equals("Approved", ignoreCase = true)) {
            holder.textStatus.setTextColor(Color.parseColor("#388E3C"))
        } else {
            holder.textStatus.setTextColor(Color.parseColor("#F44336"))
        }

        holder.buttonEdit.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, UpdateRequestActivity::class.java).apply {
                putExtra("id", request.id)
                putExtra("service_name", request.serviceName)
                putExtra("note", request.note)
            }
            context.startActivity(intent)
        }

        holder.buttonDelete.setOnClickListener {
            DeleteRequestTask(holder.itemView.context, onRequestDeleted).execute(request.id.toString())
        }


    }

    override fun getItemCount(): Int = requests.size

    inner class DeleteRequestTask(
        private val context: Context,
        private val onRequestDeleted: () -> Unit
    ) : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String): String {
            val url = URL("http://eatsys2025.atwebpages.com/delete_request.php")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

            val data = "id=${params[0]}"
            val out = BufferedWriter(OutputStreamWriter(conn.outputStream))
            out.write(data)
            out.flush()
            out.close()

            return conn.inputStream.bufferedReader().readText()
        }

        override fun onPostExecute(result: String?) {
            Toast.makeText(context, result ?: "Error deleting", Toast.LENGTH_LONG).show()
            onRequestDeleted()
        }
    }
}
