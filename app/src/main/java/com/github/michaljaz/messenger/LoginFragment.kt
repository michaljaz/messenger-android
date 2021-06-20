package com.github.michaljaz.messenger

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.*


private const val RC_SIGN_IN = 7

class LoginFragment : Fragment() {
    private lateinit var callbackManager: CallbackManager
    private lateinit var m: MainActivity

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        m=(activity as MainActivity)

        //block drawer
        m.disableDrawer()

        //set toolbar title
        m.setToolbarTitle("Messenger")

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        callbackManager=CallbackManager.Factory.create()

        //redirect to register fragment
        view.findViewById<Button>(R.id.LogRegister).setOnClickListener {
            findNavController().navigate(R.id.action_register)
        }

        //manual sign in
        view.findViewById<Button>(R.id.Login).setOnClickListener {
            if(m.isOnline()){
                val email=view.findViewById<TextInputLayout>(R.id.Email).editText?.text.toString().trim { it <= ' ' }
                val password=view.findViewById<TextInputLayout>(R.id.Password).editText?.text.toString().trim { it <= ' ' }
                when {
                    TextUtils.isEmpty(email) -> {
                        Toast.makeText(context,"Please enter email!", Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(password) -> {
                        Toast.makeText(context,"Please enter password!", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        m.auth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener { task ->
                                if(task.isSuccessful){
                                    try {
                                        m.hideKeyboard(it)
                                        findNavController().navigate(R.id.login)
                                    } catch (e: Exception){}
                                }else{
                                    Toast.makeText(context,task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
            }else{
                Toast.makeText(context, "You are offline", Toast.LENGTH_SHORT).show()
            }
        }

        //Google button
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(m, gso);

        view.findViewById<Button>(R.id.google_login).setOnClickListener {
            if(m.isOnline()){
                val signInIntent = mGoogleSignInClient.signInIntent
                startActivityForResult(signInIntent,RC_SIGN_IN)
            }else{
                Toast.makeText(context, "You are offline", Toast.LENGTH_SHORT).show()
            }

        }

        // Facebook button
        view.findViewById<Button>(R.id.facebook_login).setOnClickListener {
            if(m.isOnline()){
                LoginManager.getInstance()
                    .logInWithReadPermissions(this, listOf("email", "public_profile"))
                LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
                    override fun onSuccess(loginResult: LoginResult?) {
                        Log.d("TAG", "Success Login")
                        val credential=FacebookAuthProvider.getCredential(loginResult?.accessToken?.token.toString())
                        m.auth.signInWithCredential(credential)
                            .addOnCompleteListener { task ->
                                if(task.isSuccessful){
                                    getUserProfile(loginResult?.accessToken, loginResult?.accessToken?.userId)
                                }else{
                                    Toast.makeText(context,task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                                }
                            }

                    }

                    override fun onCancel() {
                        Toast.makeText(context, "Login Cancelled", Toast.LENGTH_LONG).show()
                    }

                    override fun onError(exception: FacebookException) {
                        Toast.makeText(context, exception.message, Toast.LENGTH_LONG).show()
                    }
                })
            }else{
                Toast.makeText(context, "You are offline", Toast.LENGTH_SHORT).show()
            }

        }

        //Session check
        if(m.auth.currentUser != null){
            try {
                findNavController().navigate(R.id.login)
            } catch (e: Exception){}
        }else{
            mGoogleSignInClient.signOut()
            LoginManager.getInstance().logOut()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                m.auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            try {
                                findNavController().navigate(R.id.login)
                            } catch (e: Exception) {
                            }
                        } else {
                            Toast.makeText(context, "signInWithCredential:failure"+task.exception, Toast.LENGTH_SHORT).show()
                        }
                    }

            } catch (e: ApiException) {
                Toast.makeText(context, "signInResult:failed code=" + e.statusCode, Toast.LENGTH_SHORT).show()
            }
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

            // Facebook Profile Pic URL
            if (jsonObject.has("picture")) {
                val facebookPictureObject = jsonObject.getJSONObject("picture")
                if (facebookPictureObject.has("data")) {
                    val facebookDataObject = facebookPictureObject.getJSONObject("data")
                    if (facebookDataObject.has("url")) {
                        val facebookProfilePicURL = facebookDataObject.getString("url")

                        Log.d("Facebook Profile Pic URL: ", facebookProfilePicURL)
                        val profile=UserProfileChangeRequest.Builder().setPhotoUri(Uri.parse(facebookProfilePicURL)).build()
                        m.auth.currentUser!!.updateProfile(profile).addOnCompleteListener {
                            try {
                                findNavController().navigate(R.id.login)
                            } catch (e: Exception){}
                        }
                    }
                }
            } else {
                try {
                    findNavController().navigate(R.id.login)
                } catch (e: Exception){}
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
}
