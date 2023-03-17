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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var gson = Gson()
    val nameChaveShared = "localLista"
    val nameFileShared = "lista_de_tarefas"
    lateinit var mGoogleClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()

        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
        if(firebaseUser == null){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.id_client_web))
            .requestEmail()
            .build()

        mGoogleClient = GoogleSignIn.getClient(this, gso)

        sharedPreferences = getSharedPreferences(nameFileShared, Context.MODE_PRIVATE)

        val Lista = getData()
        if(Lista.isNotEmpty()){
            binding.tvNolista.visibility = View.GONE
        }else{
            binding.btnRemove.visibility = View.GONE
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, Lista)
        binding.lvLista.adapter = adapter
        adapter.notifyDataSetChanged()

        binding.imgLogout.setOnClickListener {
            firebaseAuth.signOut()
            mGoogleClient.signOut()

            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnAdd.setOnClickListener {
            val item = binding.edtItem.text.toString()
            if(validInfo(item, Lista)) {
                Lista.add(item)
                binding.tvNolista.visibility = View.GONE
                binding.lvLista.adapter = adapter
                adapter.notifyDataSetChanged()
                saveLocal(Lista)
                binding.edtItem.text.clear()
                binding.btnRemove.visibility = View.VISIBLE
            }
        }

        binding.btnRemove.setOnClickListener {
            val position: SparseBooleanArray = binding.lvLista.checkedItemPositions
            val count = binding.lvLista.count
            var item = count - 1
            while (item >= 0){
                if(position.get(item)){
                    adapter.remove(Lista.get(item))
                }
                item--
            }
            saveLocal(Lista)
            position.clear()
            if(Lista.isEmpty()){
                binding.btnRemove.visibility = View.GONE
                binding.tvNolista.visibility = View.VISIBLE
            }
            adapter.notifyDataSetChanged()
        }

        binding.btnClear.setOnClickListener {
            Lista.clear()
            saveLocal(Lista)
            adapter.notifyDataSetChanged()
            binding.btnRemove.visibility = View.GONE
            binding.tvNolista.visibility = View.VISIBLE
        }

        binding.imgPerfil.setOnClickListener {
            val intent = Intent(this,PerfilActivity::class.java)
            startActivity(intent)
            finish()
        }

//        val intent = Intent(this,LoginActivity::class.java)
//        startActivity(intent)
    }
    
    private fun validInfo(item: String, lista: ArrayList<String>): Boolean{
        if(item.isEmpty()){
            Toast.makeText(this, R.string.notextlist, Toast.LENGTH_SHORT).show()
            return false
        }

        lista.forEach {
            if(item == it){
                Toast.makeText(this, R.string.taskList, Toast.LENGTH_SHORT).show()
                return false
            }
        }

        return  true
    }

    private fun getData(): ArrayList<String> {
        val arrayJson = sharedPreferences.getString(nameChaveShared, null);
        return if(arrayJson.isNullOrEmpty()){
            arrayListOf();
        }else{
            gson.fromJson(arrayJson, object: TypeToken<ArrayList<String>>(){}.type)
        }
    }

    private fun saveLocal(array: ArrayList<String>){
        val arrayJson = gson.toJson(array);
        val editor = sharedPreferences.edit();
        editor.putString(nameChaveShared, arrayJson);
        editor.apply();
    }
}