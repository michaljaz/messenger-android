package com.github.michaljaz.messenger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import io.socket.emitter.Emitter

private const val RC_SIGN_IN = 7

class LoginFragment : Fragment() {
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

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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

    private fun showLog(message: String){
        Log.d("lul", message.toString())
    }
}
