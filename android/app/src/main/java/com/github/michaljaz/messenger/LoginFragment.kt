package com.github.michaljaz.messenger

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import io.socket.emitter.Emitter
import java.util.*


private const val RC_SIGN_IN = 7

class LoginFragment : Fragment() {
    lateinit var callbackManager: CallbackManager
    private var list: Emitter? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        this.list=(activity as MainActivity).getSocket()?.once("signin_ok") {
            showLog("sign in success")
            try {
                findNavController().navigate(R.id.login)
            } catch (e: Exception){}
        }

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.LogRegister).setOnClickListener {
            findNavController().navigate(R.id.action_register)
        }
        view.findViewById<Button>(R.id.Login).setOnClickListener {
            val username=view.findViewById<TextInputLayout>(R.id.Username).editText?.text.toString()
            val password=view.findViewById<TextInputLayout>(R.id.Password).editText?.text.toString()
            (activity as MainActivity).getSocket()?.emit("signin",username,password)
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient((activity as MainActivity), gso);

        val google=view.findViewById<SignInButton>(R.id.sign_in_button)
        google.setOnClickListener {
            showLog("LUL")
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent,RC_SIGN_IN)
        }
        google.setSize(SignInButton.SIZE_WIDE)
        callbackManager = CallbackManager.Factory.create()


        val loginButton = view.findViewById<LoginButton>(R.id.login_button)
        loginButton.setReadPermissions(listOf("public_profile", "email"))
        // If you are using in a fragment, call loginButton.setFragment(this)
        loginButton.fragment = this
        // Callback registration
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                Log.d("TAG", "Success Login")
                getUserProfile(loginResult?.accessToken, loginResult?.accessToken?.userId)
            }

            override fun onCancel() {
                Toast.makeText(context, "Login Cancelled", Toast.LENGTH_LONG).show()
            }

            override fun onError(exception: FacebookException) {
                Toast.makeText(context, exception.message, Toast.LENGTH_LONG).show()
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            showLog("GOOGLE SUCCESS")
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            showLog("GOOGLE FAIL")
            showLog("signInResult:failed code=" + e.statusCode)
        }
    }

    @SuppressLint("LongLogTag")
    fun getUserProfile(token: AccessToken?, userId: String?) {

        val parameters = Bundle()
        parameters.putString(
            "fields",
            "id, first_name, middle_name, last_name, name, picture, email"
        )
        GraphRequest(token,
            "/$userId/",
            parameters,
            HttpMethod.GET
        ) { response ->
            val jsonObject = response.jsonObject

            // Facebook Access Token
            // You can see Access Token only in Debug mode.
            // You can't see it in Logcat using Log.d, Facebook did that to avoid leaking user's access token.
            if (BuildConfig.DEBUG) {
                FacebookSdk.setIsDebugEnabled(true)
                FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
            }

            // Facebook Id
            if (jsonObject.has("id")) {
                val facebookId = jsonObject.getString("id")
                Log.d("Facebook Id: ", facebookId.toString())
            } else {
                Log.d("Facebook Id: ", "Not exists")
            }


            // Facebook First Name
            if (jsonObject.has("first_name")) {
                val facebookFirstName = jsonObject.getString("first_name")
                Log.d("Facebook First Name: ", facebookFirstName)
            } else {
                Log.d("Facebook First Name: ", "Not exists")
            }


            // Facebook Middle Name
            if (jsonObject.has("middle_name")) {
                val facebookMiddleName = jsonObject.getString("middle_name")
                Log.d("Facebook Middle Name: ", facebookMiddleName)
            } else {
                Log.d("Facebook Middle Name: ", "Not exists")
            }


            // Facebook Last Name
            if (jsonObject.has("last_name")) {
                val facebookLastName = jsonObject.getString("last_name")
                Log.d("Facebook Last Name: ", facebookLastName)
            } else {
                Log.d("Facebook Last Name: ", "Not exists")
            }


            // Facebook Name
            if (jsonObject.has("name")) {
                val facebookName = jsonObject.getString("name")
                Log.d("Facebook Name: ", facebookName)
            } else {
                Log.d("Facebook Name: ", "Not exists")
            }


            // Facebook Profile Pic URL
            if (jsonObject.has("picture")) {
                val facebookPictureObject = jsonObject.getJSONObject("picture")
                if (facebookPictureObject.has("data")) {
                    val facebookDataObject = facebookPictureObject.getJSONObject("data")
                    if (facebookDataObject.has("url")) {
                        val facebookProfilePicURL = facebookDataObject.getString("url")
                        Log.d("Facebook Profile Pic URL: ", facebookProfilePicURL)
                    }
                }
            } else {
                Log.d("Facebook Profile Pic URL: ", "Not exists")
            }

            // Facebook Email
            if (jsonObject.has("email")) {
                val facebookEmail = jsonObject.getString("email")
                Log.d("Facebook Email: ", facebookEmail)
            } else {
                Log.d("Facebook Email: ", "Not exists")
            }
        }.executeAsync()
    }

    private fun showLog(message: String){
        Log.d("lul", message.toString())
    }
}
