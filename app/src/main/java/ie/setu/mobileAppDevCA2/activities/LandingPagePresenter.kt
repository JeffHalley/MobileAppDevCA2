package ie.setu.mobileAppDevCA2.activities

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.lambda.AWSLambdaClient
import com.amazonaws.services.lambda.model.InvokeRequest
import com.amazonaws.services.lambda.model.InvokeResult
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

class LandingPagePresenter(
    private val view: LandingPageActivity,
    private val app: MainApp
) {

    fun handleLogin(username: String, password: String) {
        if (username.isEmpty() || password.isEmpty()) {
            view.showSnackbar("Please enter username and password")
            return
        }

        view.showSnackbar("Logging in...")
        callLambda(username, password)
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun callLambda(username: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val credentials = BasicAWSCredentials(
                    view.getString(R.string.AWS_ACCESS_KEY_ID),
                    view.getString(R.string.AWS_SECRET_ACCESS_KEY)
                )

                val lambdaClient = AWSLambdaClient(credentials).apply {
                    setRegion(
                        com.amazonaws.regions.Region.getRegion(
                            Regions.fromName(view.getString(R.string.AWS_REGION))
                        )
                    )
                }

                val hashedPassword = sha256(password)
                i("Hashed password: $hashedPassword")

                val payload = """
                    {
                        "UserName": "$username",
                        "Password": "$hashedPassword"
                    }
                """.trimIndent()

                val request = InvokeRequest()
                    .withFunctionName(view.getString(R.string.LAMBDA_FUNCTION_NAME))
                    .withPayload(ByteBuffer.wrap(payload.toByteArray(Charsets.UTF_8)))

                val response: InvokeResult = lambdaClient.invoke(request)
                val responseString = String(response.payload.array())

                withContext(Dispatchers.Main) {
                    val lambdaResponse = Gson().fromJson(
                        responseString,
                        object : TypeToken<Map<String, Any>>() {}.type
                    ) as Map<String, Any>

                    val statusCode = (lambdaResponse["statusCode"] as Double).toInt()
                    val bodyString = lambdaResponse["body"] as String

                    if (statusCode == 200) {
                        view.showSnackbar("Login successful!")

                        val bodyMap = Gson().fromJson(
                            bodyString,
                            object : TypeToken<Map<String, Any>>() {}.type
                        ) as Map<String, Any>

                        val devicesJson = Gson().toJson(bodyMap["data"])
                        app.devices.loadFromJson(devicesJson)

                        view.navigateToDeviceList()
                    } else {
                        view.showSnackbar("Login failed: $bodyString")
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view.showSnackbar("Error: ${e.message}")
                }
            }
        }
    }
}
