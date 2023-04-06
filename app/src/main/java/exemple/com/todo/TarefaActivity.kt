package exemple.com.todo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import exemple.com.todo.databinding.ActivityTarefaBinding
import exemple.com.todo.fragment.BtnSalvarFragment
import java.util.*
import kotlin.collections.*

class TarefaActivity : AppCompatActivity(), BtnSalvarFragment.MeuFragmentListener {
    lateinit var binding: ActivityTarefaBinding
    var day: Int = 0
    var month: Int = 0
    var year: Int = 0
    var hour: Int = 0
    var minute: Int = 0
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var taskId: String = ""
    var new: Boolean = true
    val listTask = mutableMapOf<String, String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTarefaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()

        val current_date_time = Calendar.getInstance()
        day = current_date_time.get(Calendar.DAY_OF_MONTH)
        month = current_date_time.get(Calendar.MONTH)
        year = current_date_time.get(Calendar.YEAR)
        hour = current_date_time.get(Calendar.HOUR_OF_DAY)
        minute = current_date_time.get(Calendar.MINUTE)

        listener()

        this.taskId = intent.getStringExtra("id") ?: ""

        var referencia = ""
        if (taskId === ""){
            referencia = "/users/$uid/tasks"
            new = true
        } else {
            referencia = "/users/$uid/tasks/$taskId"
            loadTarefa()
            new = false
        }

        val meuFragment = BtnSalvarFragment(R.string.salvar, R.color.marron, referencia,listTask,new,1)

        meuFragment.contextoPai = this

        supportFragmentManager.beginTransaction().add(R.id.fragment_btn_salvar, meuFragment).commit()

    }

    private fun loadTarefa() {
        val db_ref = FirebaseDatabase.getInstance().getReference("/users/$uid/tasks/$taskId")
        db_ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists()) return

                binding.etTitle.setText(snapshot.child("titulo").value.toString())
                binding.edDescricao.setText(snapshot.child("descricao").value.toString())
                binding.etDate.setText(snapshot.child("data").value.toString())
                binding.etHora.setText(snapshot.child("hora").value.toString())
            }


            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@TarefaActivity, "Erro ao carregar tarefa", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onAlgumEvento(): Boolean {
        listTask.clear()
        listTask["titulo"] = binding.etTitle.text.toString()
        listTask["descricao"] = binding.edDescricao.text.toString()
        listTask["data"] = binding.etDate.text.toString()
        listTask["hora"] = binding.etHora.text.toString()
        return binding.etTitle.text.toString() !== "" &&
               binding.edDescricao.text.toString() !== "" &&
               binding.etDate.text.toString() !== "" &&
               binding.etHora.text.toString() != ""
    }

    private fun listener() {
        binding.btnDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, {_, yearOfYear, monthOfYear, dayOfMonth ->
                binding.etDate.setText(String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, yearOfYear))
            }, year, month, day)
            datePickerDialog.show()
        }

        binding.btnHora.setOnClickListener {
            val timePickerDialog = TimePickerDialog(this, {_, hourOfDay, minuteOfHour ->
                binding.etHora.setText(String.format("%02d:%02d", hourOfDay, minuteOfHour))
            }, hour, minute, true)
            timePickerDialog.show()
        }

    }
}