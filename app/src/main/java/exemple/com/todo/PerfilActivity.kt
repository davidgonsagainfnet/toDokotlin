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
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import exemple.com.todo.databinding.ActivityPerfilBinding
import exemple.com.todo.util.BitmapConvert

class PerfilActivity : AppCompatActivity() {
    lateinit var binding: ActivityPerfilBinding
    companion object{
        private const val REQUEST_IMAGE_GALLERY = 1
        private const val REQUEST_IMAGE_CAPTURE = 2
    }
    private var fileName = ""
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

                        BitmapConvert.saveBitmap(imageBitmap,this,fileName)
                    }
                    REQUEST_IMAGE_CAPTURE -> {
                        val imageCaptured =  data?.extras?.get("data") as Bitmap
                        BitmapConvert.saveBitmap(imageCaptured,this,fileName)
                    }
                }
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
}