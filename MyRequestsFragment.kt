package hmd.ec.a22143_project;

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class MyRequestsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RequestAdapter
    private var requestList: ArrayList<Request> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_requests, container, false)



        recyclerView = view.findViewById(R.id.recyclerViewRequests)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val buttonAdd: Button = view.findViewById(R.id.buttonAddRequest)
        buttonAdd.setOnClickListener {
            findNavController().navigate(R.id.nav_add_request)
        }

        loadRequests()

        return view
    }

    override fun onResume() {
        super.onResume()
        loadRequests()
    }

    private fun loadRequests() {
        FetchRequestsTask().execute()
    }

    inner class FetchRequestsTask : AsyncTask<Void, Void, List<Request>>() {
        override fun doInBackground(vararg params: Void?): List<Request> {
            val url = URL("http://eatsys2025.atwebpages.com/get_requests.php")
            val conn = url.openConnection() as HttpURLConnection
            val result = conn.inputStream.bufferedReader().readText()

            val tempList = ArrayList<Request>()
            val dataArray = JSONArray(result)
            for (i in 0 until dataArray.length()) {
                val item = dataArray.getJSONObject(i)
                val id = item.getInt("id")
                val serviceName = item.getString("service_name")
                val note = item.getString("note")
                val status = item.getString("status")
                tempList.add(Request(id, serviceName, note, status))
            }
            return tempList
        }

        override fun onPostExecute(result: List<Request>?) {
            if (result != null) {
                requestList.clear()
                requestList.addAll(result)
                adapter = RequestAdapter(
                    requestList,
                    onRequestDeleted = { loadRequests() }
                )
                recyclerView.adapter = adapter
            }
        }
    }
}
