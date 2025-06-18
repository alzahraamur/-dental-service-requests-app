package hmd.ec.a22143_project;


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView



data class Service(val name: String)

class ServicesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBox: EditText
    private lateinit var adapter: ServiceAdapter
    private lateinit var serviceList: ArrayList<Service>
    private lateinit var filteredList: ArrayList<Service>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_services, container, false)

        searchBox = view.findViewById(R.id.editTextSearch)
        recyclerView = view.findViewById(R.id.recyclerViewServices)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        serviceList = arrayListOf(
            Service("Dental Checkup"),
            Service("Tooth Cleaning"),
            Service("Whitening"),
            Service("Braces Consultation")
        )

        filteredList = ArrayList(serviceList)
        adapter = ServiceAdapter(filteredList)
        recyclerView.adapter = adapter

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase()
                filteredList.clear()
                filteredList.addAll(serviceList.filter {
                    it.name.lowercase().contains(query)
                })
                adapter.notifyDataSetChanged()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }
}
