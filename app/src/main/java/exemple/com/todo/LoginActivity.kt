package exemple.com.todo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import exemple.com.todo.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import exemple.com.todo.fragment.EmailFragment
import exemple.com.todo.fragment.ListViewFragment

class LoginActivity : AppCompatActivity(), EmailFragment.OnDataUpdateListener {

    private lateinit var binding: ActivityLoginBinding
    val Req_Code: Int = 123
    lateinit var mGoogleClient: GoogleSignInClient
    private  lateinit var firebaseAuth: FirebaseAuth
    var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()

        supportFragmentManager.beginTransaction().replace(R.id.fragment_email, EmailFragment(this)).commit()



        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.id_client_web))
            .requestEmail()
            .build()

        mGoogleClient = GoogleSignIn.getClient(this, gso)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnRegistrar.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnGoogle.setOnClickListener {
            signInGoogle()
        }

        binding.btnEntrar.setOnClickListener {
            login()
        }

        binding.tvLembraSenha.setOnClickListener {
            val intent = Intent(this,RecuperarSenhaActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onDataUpdated(data: String) {
        email = data
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

    private fun notEmpty():Boolean = email.trim().isNotEmpty() && binding.editSenha.text.toString().trim().isNotEmpty()

    private fun login(){
        if(notEmpty()){
            val userEmail = email.trim()
            val userPassword = binding.editSenha.text.toString().trim()

            firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
                    if(firebaseUser != null && firebaseUser.isEmailVerified){
                        val intent = Intent(this, MainActivity::class.java)
                        Toast.makeText(this, R.string.loginsucesso,Toast.LENGTH_SHORT).show()
                        startActivity(intent)
                        finish()
                    } else if(firebaseUser != null && !firebaseUser.isEmailVerified){
                        firebaseAuth.signOut()
                        Toast.makeText(this,R.string.emailNovalid, Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this,R.string.loginsemsucesso,Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this,R.string.loginsemsucesso,Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this,R.string.imformecampos,Toast.LENGTH_SHORT).show()
        }
    }

}