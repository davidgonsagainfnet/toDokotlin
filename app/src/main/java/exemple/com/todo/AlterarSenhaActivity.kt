package exemple.com.todo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import exemple.com.todo.databinding.ActivityAlterarSenhaBinding
import exemple.com.todo.fragment.PasswordFragment

class AlterarSenhaActivity : AppCompatActivity(), PasswordFragment.OnDataUpdateListenerPass {
    lateinit var binding: ActivityAlterarSenhaBinding
    var password = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlterarSenhaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()

        supportFragmentManager.beginTransaction().replace(R.id.fragment_password,
            PasswordFragment(this)
        ).commit()
    }

    override fun onDataUpdatedPass(data: String) {
        password = data
    }
}