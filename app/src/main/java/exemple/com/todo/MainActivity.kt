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


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var gson = Gson()
    val nameChaveShared = "localLista"
    val nameFileShared = "lista_de_tarefas"
    lateinit var mGoogleClient: GoogleSignInClient
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val ref = FirebaseDatabase.getInstance().getReference("/users/$uid/tasks")
    val listItems = ArrayList<String>()


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
            finish()
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
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
        binding.lvLista.adapter = adapter

        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listItems.clear()
                var cont = 0
                for(child in snapshot.children){
                    cont++
                    listItems.add(child.child("titulo").value.toString())
                }
                if(cont > 0){
                    binding.tvNolista.visibility = View.GONE
                } else {
                    binding.tvNolista.visibility = View.VISIBLE
                }
                adapter.notifyDataSetChanged()

                binding.lvLista.setOnItemLongClickListener { parent, view, position, id ->
                    val itemId =  snapshot.children.toList()[position].key

                    if(itemId != null){
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle("Deletar tarefa")
                            .setMessage("Deseja deletar a tarefa?")
                            .setPositiveButton("Sim"){ dialog, which ->
                                ref.child(itemId).removeValue()
                                Toast.makeText(this@MainActivity, "Tarefa deletada com sucesso", Toast.LENGTH_SHORT).show()
                            }
                            .setNegativeButton("Não"){ dialog, which ->
                                dialog.dismiss()
                            }
                            .show()
                    }

                    true
                }
                binding.lvLista.setOnItemClickListener { parent, view, position, id ->
                    val itemId =  snapshot.children.toList()[position].key

                    val activity = Intent(this@MainActivity, TarefaActivity::class.java)
                    activity.putExtra("id", itemId)
                    startActivity(activity)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Erro ao carregar tarefas", Toast.LENGTH_SHORT).show()
            }

        })

        //adapter.notifyDataSetChanged()

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