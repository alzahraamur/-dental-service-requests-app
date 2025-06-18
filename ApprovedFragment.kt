package hmd.ec.a22143_project;

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class ApprovedFragment : Fragment() {

    private var approvedList: ArrayList<Request> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RequestAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_approved, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewApproved)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadRequests()

        return view
    }

    private fun loadRequests() {
        FetchApprovedRequestsTask().execute()
    }

    private fun approveRequest(id: Int) {
        ApproveRequestTask(id).execute()
    }

    inner class FetchApprovedRequestsTask : AsyncTask<Void, Void, List<Request>>() {
        override fun doInBackground(vararg params: Void?): List<Request> {
            val url = URL("http://eatsys2025.atwebpages.com/get_requests.php")
            val conn = url.openConnection() as HttpURLConnection
            val result = conn.inputStream.bufferedReader().readText()

            val tempList = ArrayList<Request>()
            val data = JSONArray(result)

            for (i in 0 until data.length()) {
                val item = data.getJSONObject(i)
                val id = item.getInt("id")
                val serviceName = item.getString("service_name")
                val note = item.getString("note")
                val status = item.getString("status")
                if (status.equals("Approved", ignoreCase = true)) {
                    tempList.add(Request(id, serviceName, note, status))
                }
            }
            return tempList
        }

        override fun onPostExecute(result: List<Request>?) {
            if (result != null) {
                approvedList.clear()
                approvedList.addAll(result)

                if (!::adapter.isInitialized) {
                    adapter = RequestAdapter(
                        approvedList,
                        onRequestDeleted = { loadRequests() },

                    )
                    recyclerView.adapter = adapter
                } else {
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    inner class ApproveRequestTask(private val requestId: Int) : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String {
            val url = URL("http://eatsys2025.atwebpages.com/approve_request.php")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

            val data = "id=$requestId"
            val out = BufferedWriter(OutputStreamWriter(conn.outputStream))
            out.write(data)
            out.flush()
            out.close()

            return conn.inputStream.bufferedReader().readText()
        }

        override fun onPostExecute(result: String?) {
            Toast.makeText(requireContext(), result ?: "Approval failed", Toast.LENGTH_SHORT).show()
            loadRequests()
        }
    }
}
