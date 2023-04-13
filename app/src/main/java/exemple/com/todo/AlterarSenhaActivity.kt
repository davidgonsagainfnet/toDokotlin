package exemple.com.todo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import exemple.com.todo.databinding.ActivityAlterarSenhaBinding
import exemple.com.todo.fragment.PasswordFragment

class AlterarSenhaActivity : AppCompatActivity(), PasswordFragment.OnDataUpdateListenerPass {
    lateinit var binding: ActivityAlterarSenhaBinding
    var password = ""
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlterarSenhaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()

        supportFragmentManager.beginTransaction().replace(R.id.fragment_password,
            PasswordFragment(this)
        ).commit()

        listener()
    }

    private fun listener() {
        binding.btnAlterPass.setOnClickListener {
            alterarSenha()
        }
    }

    private fun alterarSenha() {
        if(password != binding.editSenhaConfirma.text.toString()){
            Toast.makeText(this, R.string.senhasNoIdentica, Toast.LENGTH_SHORT).show()
            return
        }

        if(password.trim().isEmpty() || binding.editSenhaConfirma.text.toString().trim().isEmpty()){
            Toast.makeText(this,R.string.imformecampos,Toast.LENGTH_SHORT).show()
            return
        }

        val user = firebaseAuth.currentUser

        user!!.updatePassword(password.trim()).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(this,R.string.senhaalterada,Toast.LENGTH_SHORT).show()
            }
           val intent = Intent(this, PerfilActivity::class.java)
           startActivity(intent)
        }
    }

    override fun onDataUpdatedPass(data: String) {
        password = data
    }
}