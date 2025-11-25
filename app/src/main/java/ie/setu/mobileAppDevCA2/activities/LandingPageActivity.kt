package ie.setu.mobileAppDevCA2.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import ie.setu.mobileAppDevCA2.R

class LandingPageActivity : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        usernameInput = findViewById(R.id.usernameInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Snackbar.make(it, "Please enter username and password", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(it, "Logging in", Snackbar.LENGTH_LONG).show()

                // For now, go to device list (dummy)
                val intent = Intent(this, DeviceListView::class.java)
                startActivity(intent)
            }
        }
    }
}
