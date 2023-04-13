package exemple.com.todo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import exemple.com.todo.databinding.ActivityAlterarSenhaBinding
import exemple.com.todo.fragment.PasswordFragment
import exemple.com.todo.util.Analytics
import kotlin.random.Random

class AlterarSenhaActivity : AppCompatActivity(), PasswordFragment.OnDataUpdateListenerPass {
    lateinit var binding: ActivityAlterarSenhaBinding
    var password = ""
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlterarSenhaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

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

                var id: Int = Random.nextInt(0, 100000)

                Analytics.sendAnalytics(firebaseAnalytics,id.toString(),"Alteração de Senha")
            }
           val intent = Intent(this, PerfilActivity::class.java)
           startActivity(intent)
        }
    }

    override fun onDataUpdatedPass(data: String) {
        password = data
    }
}