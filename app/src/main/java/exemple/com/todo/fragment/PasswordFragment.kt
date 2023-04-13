package exemple.com.todo.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import exemple.com.todo.R
import exemple.com.todo.databinding.FragmentPasswordBinding

class PasswordFragment(val ctx: Context) : Fragment() {

    lateinit var binding: FragmentPasswordBinding
    private var dataUpdateListener: OnDataUpdateListenerPass? = null
    var nivelPass = 0

    interface OnDataUpdateListenerPass {
        fun onDataUpdatedPass(data: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPasswordBinding.inflate(layoutInflater)

        listener()

        return binding.root
    }

    private fun listener() {
        binding.editPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val nivel = checkPasswordStrength(s.toString())
                binding.ratingBar.rating = nivel.toFloat()
                nivelPass = nivel.toInt()
            }
        })

        binding.editPassword.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {

                if(binding.editPassword.text.toString().trim().isEmpty()){
                    binding.editPassword.setError(resources.getString(R.string.senhas6))
                    return@OnFocusChangeListener
                }

                if (nivelPass <= 2) {
                    binding.editPassword.setError(resources.getString(R.string.senhafraca))
                    dataUpdateListener!!.onDataUpdatedPass("")
                    Toast.makeText(ctx, R.string.emailinvalido, Toast.LENGTH_SHORT).show()
                    return@OnFocusChangeListener
                }
                dataUpdateListener!!.onDataUpdatedPass(binding.editPassword.text.toString())
            }

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnDataUpdateListenerPass) {
            dataUpdateListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        dataUpdateListener = null
    }

    fun checkPasswordStrength(password: String): Number {
        var score = 0
        if (password.length >= 8) {
            score++
        }
        if (password.matches(Regex(".*[a-z].*")) && password.matches(Regex(".*[A-Z].*"))) {
            score++
        }
        if (password.matches(Regex(".*\\d.*"))) {
            score++
        }
        if (password.matches(Regex(".*[!@#\$%^&*()_+\\-=[\\\\]{};:'\"|,.<>\\/?].*"))) {
            score++
        }
        if (!password.matches(Regex(".*(.)\\1{2,}.*"))) {
            score++
        }
        when (score) {
            0 -> return 0.0
            1 -> return 1.0
            2 -> return 2.0
            3 -> return 3.0
            else -> return 4.0
        }
    }

}