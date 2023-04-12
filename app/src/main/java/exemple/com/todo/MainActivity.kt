package exemple.com.todo

import android.content.ContentProviderClient
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import exemple.com.todo.databinding.ActivityMainBinding
import android.content.SharedPreferences
import android.util.SparseBooleanArray
import android.view.View
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import exemple.com.todo.fragment.ListViewFragment
import exemple.com.todo.fragment.WeatherFragment


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    val nameFileShared = "lista_de_tarefas"
    lateinit var mGoogleClient: GoogleSignInClient
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val ref = FirebaseDatabase.getInstance().getReference("/users/$uid/tasks")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, WeatherFragment(this)).commit()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_list, ListViewFragment(ref, 0,this)).commit()


        supportActionBar!!.hide()

        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
        if(firebaseUser == null){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.id_client_web))
            .requestEmail()
            .build()

        mGoogleClient = GoogleSignIn.getClient(this, gso)

        sharedPreferences = getSharedPreferences(nameFileShared, Context.MODE_PRIVATE)

        binding.imgLogout.setOnClickListener {
            firebaseAuth.signOut()
            mGoogleClient.signOut()

            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

        binding.imgPerfil.setOnClickListener {
            val intent = Intent(this,PerfilActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnNovaTarefa.setOnClickListener {
            val intent = Intent(this, TarefaActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
    



}