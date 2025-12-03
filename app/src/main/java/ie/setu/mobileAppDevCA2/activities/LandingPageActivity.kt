package ie.setu.mobileAppDevCA2.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import ie.setu.mobileAppDevCA2.R
import ie.setu.mobileAppDevCA2.main.MainApp

class LandingPageActivity : AppCompatActivity(){

    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var internalJsonButton: Button
    private lateinit var app: MainApp
    private lateinit var presenter: LandingPagePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        usernameInput = findViewById(R.id.usernameInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        internalJsonButton = findViewById(R.id.internalJsonButton)
        app = application as MainApp

        presenter = LandingPagePresenter(this, app)

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            presenter.handleLogin(username, password)
        }

        internalJsonButton.setOnClickListener {
            app.devices.load()
            Snackbar.make(it, "Loaded internal JSON devices", Snackbar.LENGTH_SHORT).show()
            startActivity(Intent(this, DeviceListView::class.java))
        }
    }

    fun showSnackbar(message: String) {
        Snackbar.make(loginButton, message, Snackbar.LENGTH_SHORT).show()
    }

    fun navigateToDeviceList() {
        startActivity(Intent(this, DeviceListView::class.java))
    }
}
