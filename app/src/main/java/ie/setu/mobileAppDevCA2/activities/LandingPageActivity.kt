package ie.setu.mobileAppDevCA2.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.lambda.AWSLambdaClient
import com.amazonaws.services.lambda.model.InvokeRequest
import com.amazonaws.services.lambda.model.InvokeResult
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ie.setu.mobileAppDevCA2.R
import ie.setu.mobileAppDevCA2.main.MainApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber.i
import java.nio.ByteBuffer
import java.security.MessageDigest

class LandingPageActivity : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var internalJsonButton: Button
    private lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        usernameInput = findViewById(R.id.usernameInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        internalJsonButton = findViewById(R.id.internalJsonButton)
        app = application as MainApp

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Snackbar.make(it, "Please enter username and password", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(it, "Logging in...", Snackbar.LENGTH_SHORT).show()
                callLambda(username, password)
            }
        }

        internalJsonButton.setOnClickListener {
            app.devices.load()
            Snackbar.make(it, "Loaded internal JSON devices", Snackbar.LENGTH_SHORT).show()
            val intent = Intent(this, DeviceListView::class.java)
            startActivity(intent)
        }
    }
    fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }


    private fun callLambda(username: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val credentials = BasicAWSCredentials(
                    getString(R.string.AWS_ACCESS_KEY_ID),
                    getString(R.string.AWS_SECRET_ACCESS_KEY)
                )

                val lambdaClient = AWSLambdaClient(credentials).apply {
                    setRegion(com.amazonaws.regions.Region.getRegion(
                        Regions.fromName(getString(R.string.AWS_REGION))
                    ))
                }

                val hashedPassword = sha256(password)
                i("Hashed password: $hashedPassword")


                val payload: String = """
                    {
                        "UserName": "$username",
                        "Password": "$hashedPassword"
                    }
                """.trimIndent()

                val request = InvokeRequest()
                    .withFunctionName(getString(R.string.LAMBDA_FUNCTION_NAME))
                    .withPayload(ByteBuffer.wrap(payload.toByteArray(Charsets.UTF_8)))

                val response: InvokeResult = lambdaClient.invoke(request)
                val responseString = String(response.payload.array())

                withContext(Dispatchers.Main) {
                    // Parse the Lambda body
                    val lambdaResponse = Gson().fromJson(responseString, object : TypeToken<Map<String, Any>>() {}.type) as Map<String, Any>
                    val statusCode = (lambdaResponse["statusCode"] as Double).toInt() // Lambda sometimes returns Double
                    val bodyString = lambdaResponse["body"] as String

                    if (statusCode == 200) {
                        // Login success, parse the user data
                        i("Response: $bodyString")
                        Snackbar.make(loginButton, "Login successful!", Snackbar.LENGTH_SHORT).show()
                        val bodyMap = Gson().fromJson(bodyString, object : TypeToken<Map<String, Any>>() {}.type) as Map<String, Any>
                        val intent = Intent(this@LandingPageActivity, DeviceListView::class.java)
                        val devicesJson = Gson().toJson(bodyMap["data"]) // the 'data' array from Lambda
                        app.devices.loadFromJson(devicesJson)

                        startActivity(intent)
                    } else {
                        // Login failed
                        Snackbar.make(loginButton, "Login failed: $bodyString", Snackbar.LENGTH_LONG).show()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Snackbar.make(loginButton, "Error: ${e.message}", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
}
