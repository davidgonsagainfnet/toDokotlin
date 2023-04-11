package exemple.com.todo

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.PermissionRequest
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import exemple.com.todo.databinding.ActivityPerfilBinding
import exemple.com.todo.fragment.BtnSalvarFragment
import exemple.com.todo.util.BitmapConvert
import java.io.ByteArrayOutputStream

class PerfilActivity : AppCompatActivity(), BtnSalvarFragment.MeuFragmentListener {
    lateinit var binding: ActivityPerfilBinding
    companion object{
        private const val REQUEST_IMAGE_GALLERY = 1
        private const val REQUEST_IMAGE_CAPTURE = 2
    }
    private var fileName = ""
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var new: Boolean = true
    val listTask = mutableMapOf<String, String>()
    val ref = FirebaseDatabase.getInstance().getReference("/users/$uid/profile")
    var taskId: String = ""
    lateinit var imgemWeb: Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var mGoogleSignClient: GoogleSignInClient;
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.id_client_web))
            .requestEmail()
            .build();
        mGoogleSignClient = GoogleSignIn.getClient(this, gso);
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance();

        val user = firebaseAuth.currentUser
        if(user != null) {
            var displayName = user.displayName
            val email = user.email
            var photoUrl = user.photoUrl

            binding.edtEmail.setText(email)
        }

        binding.imgHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnPhoto.setOnClickListener {
            validatePermissions(1)
        }

        binding.btnGaleria.setOnClickListener {
            validatePermissions(2)
        }

        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.children.toList().isNotEmpty()){
                    taskId =  snapshot.children.toList()[0].key!!
                    binding.edtNome.setText(snapshot.children.toList()[0].child("nome").value.toString())
                    binding.edtSobreNome.setText(snapshot.children.toList()[0].child("sobrenome").value.toString())
                }

                var referencia = ""
                if (taskId === ""){
                    referencia = "/users/$uid/profile"
                    new = true
                } else {
                    referencia = "/users/$uid/profile/$taskId"
                    new = false
                }

                val meuFragment = BtnSalvarFragment(R.string.salvar, R.color.marron, referencia,listTask,new,0)

                meuFragment.contextoPai = this@PerfilActivity

                supportFragmentManager.beginTransaction().add(R.id.fragment_btn_salvar, meuFragment).commit()
            }

            override fun onCancelled(error: DatabaseError) {}

        })

        saveImageWebToLocal()
    }

    override fun onAlgumEvento(): Boolean {
        listTask.clear()
        listTask["email"] = binding.edtEmail.text.toString()
        listTask["nome"] = binding.edtNome.text.toString()
        listTask["sobrenome"] = binding.edtSobreNome.text.toString()

        return binding.edtEmail.text.toString() !== "" && binding.edtNome.text.toString() !== "" && binding.edtSobreNome.text.toString() !== ""
    }

    override fun onResume() {
        super.onResume()
        loadImgProfile()
    }

    fun validatePermissions(escolha: Int) {
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
            .withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    escolhaAlteracaoFoto(escolha)
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    Toast.makeText(this@PerfilActivity,resources.getString(R.string.erro_escolher_foto),Toast.LENGTH_SHORT).show()
                }
            }).check()
    }

    fun launchCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    fun escolhaOpcao(escolha: Int){
        when(escolha){
            1 -> launchCamera()
            2 -> galeria()
        }
    }

    fun escolhaAlteracaoFoto(escolha: Int){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.profilefoto)
        builder.setMessage(R.string.escolhaopcoes)
        builder.setPositiveButton(R.string.atualizar_perfil) { dialog, which ->
            escolhaOpcao(escolha)
            fileName = "profile.png"
        }

        builder.setNegativeButton(R.string.atualizar_capa) { dialog, which ->
            escolhaOpcao(escolha)
            fileName = "profile_fundo.png"
        }
        val alert = builder.create()
        alert.show()
    }

    fun galeria(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)
            if(resultCode == RESULT_OK){
                when(requestCode){

                    REQUEST_IMAGE_GALLERY -> {
                        val selectedImage: Uri? = data?.data
                        val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
                        imgemWeb = imageBitmap

                        BitmapConvert.saveBitmap(imageBitmap,this,fileName)
                    }
                    REQUEST_IMAGE_CAPTURE -> {
                        val imageCaptured =  data?.extras?.get("data") as Bitmap
                        imgemWeb = imageCaptured
                        BitmapConvert.saveBitmap(imageCaptured,this,fileName)
                    }
                }

                saveImageWeb(imgemWeb)
            }
        } catch (e: Exception){
            Toast.makeText(this@PerfilActivity,e.message.toString(),Toast.LENGTH_SHORT).show()
        }
    }

    fun loadImgProfile(){
        val bitmapImg = BitmapFactory.decodeFile("${this.filesDir}/profile.png")
        if(bitmapImg != null){
            binding.imgPerfil.setImageBitmap(bitmapImg)
        } else {
            binding.imgPerfil.setImageResource(R.mipmap.user_default)
        }
        val bitmapImgFundo = BitmapFactory.decodeFile("${this.filesDir}/profile_fundo.png")
        if(bitmapImgFundo != null){
            binding.imgPerfilFundo.setImageBitmap(bitmapImgFundo)
        } else {
            binding.imgPerfilFundo.setColorFilter(R.color.purple_500)
        }
    }

    fun saveImageWeb(img: Bitmap){
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        val ende = if(fileName === "profile.png") "userImg" else "capaImg"
        val imageRef = storageRef.child("$ende/$uid")
        val byteVar = ByteArrayOutputStream()
        img.compress(Bitmap.CompressFormat.JPEG,100,byteVar)

        val data = byteVar.toByteArray()
        val uploadFoto = imageRef.putBytes(data)

        uploadFoto.addOnFailureListener{
            Toast.makeText(this, getString(R.string.falhasaveimg), Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            Toast.makeText(this, getString(R.string.imagesavesucess), Toast.LENGTH_SHORT).show()
        }

    }

    fun saveImageWebToLocal(){
        val ref = FirebaseStorage.getInstance().getReference("/userImg/$uid")
        val refFundo = FirebaseStorage.getInstance().getReference("/capaImg/$uid")

        ref.getBytes(1024 * 1024).addOnSuccessListener { bytes ->
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            BitmapConvert.saveBitmap(bitmap, this, "profile.png")
            binding.imgPerfil.setImageBitmap(bitmap)
        }.addOnFailureListener {
            Toast.makeText(this, getString(R.string.falhaloadimgperfil), Toast.LENGTH_SHORT).show()
        }

        refFundo.getBytes(1024 * 1024).addOnSuccessListener { bytes ->
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            BitmapConvert.saveBitmap(bitmap, this, "profile_fundo.png")
            binding.imgPerfilFundo.setImageBitmap(bitmap)
        }.addOnFailureListener {
            Toast.makeText(this, getString(R.string.falhaloadimgperfilfundo), Toast.LENGTH_SHORT).show()
        }
    }
}