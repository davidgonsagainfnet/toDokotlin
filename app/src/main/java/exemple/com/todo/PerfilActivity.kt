package exemple.com.todo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import exemple.com.todo.databinding.ActivityPerfilBinding

class PerfilActivity : AppCompatActivity() {
    lateinit var binding: ActivityPerfilBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var mGoogleSignClient: GoogleSignInClient;
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.id_client_web))
            .requestEmail()
            .build();
        mGoogleSignClient = GoogleSignIn.getClient(this, gso);
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance();

        val user = firebaseAuth.currentUser
        if(user != null) {
            var displayName = user.displayName
            val email = user.email
            var photoUrl = user.photoUrl

            binding.edtEmail.setText(email)
        }

        binding.imgHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}