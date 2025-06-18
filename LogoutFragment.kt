package hmd.ec.a22143_project;

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import hmd.ec.a22143_project.Constants
import hmd.ec.a22143_project.LoginActivity
import hmd.ec.a22143_project.R
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class LogoutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_logout, container, false)

        val currentPassword = view.findViewById<EditText>(R.id.editCurrentPassword)
        val newPassword = view.findViewById<EditText>(R.id.editNewPassword)
        val confirmPassword = view.findViewById<EditText>(R.id.editConfirmPassword)
        val buttonChange = view.findViewById<Button>(R.id.buttonChangePassword)
        val buttonLogout = view.findViewById<Button>(R.id.buttonLogout)

        buttonChange.setOnClickListener {
            val current = currentPassword.text.toString().trim()
            val newPass = newPassword.text.toString().trim()
            val confirmPass = confirmPassword.text.toString().trim()

            if (current.isNotEmpty() && newPass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (newPass == confirmPass) {
                    ChangePasswordTask(current, newPass).execute()
                } else {
                    Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        buttonLogout.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }

    inner class ChangePasswordTask(private val current: String, private val newPass: String) : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String {
            val url = URL(Constants.CHANGE_PASSWORD)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

            val data = "current_password=$current&new_password=$newPass"
            val out = BufferedWriter(OutputStreamWriter(conn.outputStream))
            out.write(data)
            out.flush()
            out.close()

            return conn.inputStream.bufferedReader().readText()
        }

        override fun onPostExecute(result: String?) {
            Toast.makeText(requireContext(), result ?: "Error changing password", Toast.LENGTH_SHORT).show()
        }
    }
}
