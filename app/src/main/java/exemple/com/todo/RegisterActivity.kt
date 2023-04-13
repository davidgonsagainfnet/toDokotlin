package exemple.com.todo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import exemple.com.todo.databinding.ActivityLoginBinding
import exemple.com.todo.databinding.ActivityRegisterBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import exemple.com.todo.fragment.EmailFragment
import exemple.com.todo.fragment.PasswordFragment

class RegisterActivity : AppCompatActivity(), EmailFragment.OnDataUpdateListener, PasswordFragment.OnDataUpdateListenerPass {

    lateinit var binding: ActivityRegisterBinding
    val Req_Code: Int = 123
    lateinit var mGoogleClient: GoogleSignInClient
    private  lateinit var firebaseAuth: FirebaseAuth
    //lateinit var createAccountInputArray:  Array<EditText>
    var email = ""
    var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()

        supportFragmentManager.beginTransaction().replace(R.id.fragment_email, EmailFragment(this)).commit()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_password,PasswordFragment(this)).commit()

        FirebaseApp.initializeApp(this)

        //createAccountInputArray = arrayOf(binding.editEmail,binding.editSenha,binding.editSenhaConfirma)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.id_client_web))
            .requestEmail()
            .build()

        mGoogleClient = GoogleSignIn.getClient(this, gso)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnEntrar.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnGoogle.setOnClickListener {
            signInGoogle()
        }

        binding.btnRegistrar.setOnClickListener {
            signIn()
        }

    }

    override fun onDataUpdated(data: String) {
        email = data
    }

    override fun onDataUpdatedPass(data: String) {
        password = data
    }

    private  fun verificaSenha(): Boolean{
        var identica = true
        if(password.trim() != binding.editSenhaConfirma.text.toString().trim()){
            identica = false
            Toast.makeText(this,R.string.senhasNoIdentica,Toast.LENGTH_SHORT).show()
        }

        if(email.isEmpty() || password.isEmpty() || binding.editSenhaConfirma.text.toString().isEmpty()){
            identica = false
            Toast.makeText(this,R.string.imformecampos,Toast.LENGTH_SHORT).show()
        }

        return identica
    }

    private fun signIn() {
        if(!verificaSenha()){
            return
        }

        val userEmail = email.trim()
        val userPassword = password.trim()

        firebaseAuth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener {task ->
            if(task.isSuccessful){
                sendEmailVerification()
                Toast.makeText(this,R.string.userCreate,Toast.LENGTH_SHORT).show()
                finish()
            } else {
                val exception = task.exception
                if(exception is FirebaseAuthException && exception.errorCode == "ERROR_EMAIL_ALREADY_IN_USE"){
                    Toast.makeText(this,R.string.userCreate,Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this,R.string.errorcreatuser,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun sendEmailVerification() {
        val firebaseUser : FirebaseUser? = firebaseAuth.currentUser
        firebaseUser?.let {
            it.sendEmailVerification().addOnCompleteListener {task ->
                if(task.isSuccessful){
                    Toast.makeText(this,R.string.emailSend,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun signInGoogle() {
        var signIntent: Intent = mGoogleClient.signInIntent
        startActivityForResult(signIntent, Req_Code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == Req_Code){
            var result = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(result)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>){
        try {
            var account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            Toast.makeText(this, R.string.loginsucesso, Toast.LENGTH_SHORT).show()
            AtuaLizaUsuario(account)
        }catch (e: ApiException){
            println(e)
            Toast.makeText(this, R.string.loginsemsucesso, Toast.LENGTH_SHORT).show()
        }
    }

    private fun AtuaLizaUsuario(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener{task ->
            if(task.isSuccessful){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

}