package exemple.com.todo.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import exemple.com.todo.R
import exemple.com.todo.databinding.FragmentEmailBinding

class EmailFragment(val ctx: Context) : Fragment() {

    lateinit var binding: FragmentEmailBinding
    private var dataUpdateListener: OnDataUpdateListener? = null

    interface OnDataUpdateListener {
        fun onDataUpdated(data: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEmailBinding.inflate(layoutInflater)

        listener()

        return binding.root
    }

    private fun listener() {
        binding.editEmail.onFocusChangeListener = View.OnFocusChangeListener{view, hasFocus ->
            if(!hasFocus){
                if(isEmailValid(binding.editEmail.text.toString())){
                    dataUpdateListener!!.onDataUpdated(binding.editEmail.text.toString())
                } else {
                    dataUpdateListener!!.onDataUpdated("")
                    binding.editEmail.setError(resources.getString(R.string.emailinvalido))
                    Toast.makeText(ctx,R.string.emailinvalido,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnDataUpdateListener) {
            dataUpdateListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        dataUpdateListener = null
    }

    fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")

        return emailRegex.matches(email)
    }


}