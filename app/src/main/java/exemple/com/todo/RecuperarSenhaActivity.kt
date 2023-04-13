package exemple.com.todo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import exemple.com.todo.databinding.ActivityRecuperarSenhaBinding
import exemple.com.todo.fragment.EmailFragment

class RecuperarSenhaActivity : AppCompatActivity(), EmailFragment.OnDataUpdateListener {
    private lateinit var binding: ActivityRecuperarSenhaBinding
    var email = ""
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecuperarSenhaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().replace(R.id.fragment_email, EmailFragment(this)).commit()

        supportActionBar!!.hide()

        listener()

    }

    override fun onDataUpdated(data: String) {
        email = data
    }

    private fun listener() {
        binding.btnVoltar.setOnClickListener {
            login()
        }

        binding.btnRecuperar.setOnClickListener {
            this?.currentFocus?.clearFocus()
            if(email.trim().isEmpty()){
                return@setOnClickListener
            }

            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener{task ->
                if(task.isSuccessful){
                    Toast.makeText(this,R.string.emailSend,Toast.LENGTH_SHORT).show()
                    login()
                }
            }

        }
    }

    fun login(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}