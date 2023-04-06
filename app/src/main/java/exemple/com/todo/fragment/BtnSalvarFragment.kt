package exemple.com.todo.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import exemple.com.todo.MainActivity
import exemple.com.todo.R
import exemple.com.todo.databinding.FragmentBtnSalvarBinding
import java.util.HashMap

class BtnSalvarFragment(val title: Int, val color: Int, val referencia: String, val lista: MutableMap<String, String>, val new: Boolean, val returnActivity: Int) : Fragment() {

    var contextoPai: MeuFragmentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MeuFragmentListener) {
            contextoPai = context
        } else {
            throw RuntimeException("$context deve implementar a interface MeuFragmentListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_btn_salvar, container, false)
        val btnSalvar = view.findViewById<Button>(R.id.btn_salvar)
        btnSalvar.setText(title)
        btnSalvar.setBackgroundColor(color)

        btnSalvar.setOnClickListener {
            val sendOk: Boolean = contextoPai!!.onAlgumEvento()
            if(sendOk){
                when (new){
                    true -> { criarRegistro() }
                    false ->{ alterarRegistro() }
                }
            } else {
                Toast.makeText(context,R.string.imformecampos,Toast.LENGTH_SHORT).show()
            }

        }

        return view
    }

    private fun alterarRegistro() {
        val dbReal = FirebaseDatabase.getInstance().getReference(referencia)
        dbReal.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists()) return
                dbReal.setValue(lista)
                Toast.makeText(context, R.string.msg_save, Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, R.string.erro_registro, Toast.LENGTH_SHORT).show()
            }

        })
        activityReturna()
    }

    private fun criarRegistro() {
        val dbReal = FirebaseDatabase.getInstance().getReference(referencia)
        val baseGoogle = dbReal.push()
        baseGoogle.setValue(lista)
        Toast.makeText(context, R.string.msg_save, Toast.LENGTH_SHORT).show()
        activityReturna()
    }

    private fun activityReturna(){
        when(returnActivity){
            1 -> {
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    interface MeuFragmentListener {
        fun onAlgumEvento(): Boolean
    }
}