package exemple.com.todo.fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
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
import exemple.com.todo.util.NotificationReceiver
import java.util.*

class BtnSalvarFragment(val title: Int, val color: Int, val referencia: String, val lista: MutableMap<String, String>, val new: Boolean, val returnActivity: Int, val ctx: Context) : Fragment() {

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
                NotificationTask(lista["titulo"]!!,lista["descricao"]!!,lista["data"]!!,lista["hora"]!!)
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun NotificationTask (title: String, task: String, data: String, hora: String){
        val intent = Intent(ctx, NotificationReceiver::class.java)

        intent.putExtra("titlemsg",title)
        intent.putExtra("msg",task)

        val pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()

        val data_dia = data.substring(0, 2).toInt()
        val data_mes = data.substring(3, 5).toInt() - 1
        val data_ano = data.substring(6, 10).toInt()
        val hora_hora = hora.substring(0, 2).toInt()
        val hora_minuto = hora.substring(3, 5).toInt()

        calendar.set(
            data_ano,
            data_mes,
            data_dia,
            hora_hora,
            hora_minuto,
            0
        )

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }

    interface MeuFragmentListener {
        fun onAlgumEvento(): Boolean
    }
}