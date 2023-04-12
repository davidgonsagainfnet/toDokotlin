package exemple.com.todo.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import exemple.com.todo.TarefaActivity
import exemple.com.todo.databinding.FragmentListViewBinding

class ListViewFragment(val ref: DatabaseReference, val tela: Int, val ctx: Context) : Fragment() {
    private lateinit var binding: FragmentListViewBinding
    val listItems = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListViewBinding.inflate(layoutInflater)

        when (tela) {
            0 -> {telaPrincipal()}
        }

        return binding.root
    }

    private fun telaPrincipal(){
        val adapter = ArrayAdapter(ctx, android.R.layout.simple_list_item_1, listItems)
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
                        AlertDialog.Builder(ctx)
                            .setTitle("Deletar tarefa")
                            .setMessage("Deseja deletar a tarefa?")
                            .setPositiveButton("Sim"){ dialog, which ->
                                ref.child(itemId).removeValue()
                                Toast.makeText(ctx, "Tarefa deletada com sucesso", Toast.LENGTH_SHORT).show()
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

                    val activity = Intent(ctx, TarefaActivity::class.java)
                    activity.putExtra("id", itemId)
                    startActivity(activity)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(ctx, "Erro ao carregar tarefas", Toast.LENGTH_SHORT).show()
            }

        })
    }
}