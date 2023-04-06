package exemple.com.todo.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Environment
import android.util.Log
import android.view.View
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


object BitmapConvert {

    suspend fun urlToBitmap(url:String, ctx: Context): Bitmap? {

        val loading :ImageLoader = ImageLoader(ctx)
        val request = ImageRequest.Builder(ctx).data(url).build()

        val result = (loading.execute(request) as SuccessResult).drawable

        return (result as BitmapDrawable).bitmap

    }

    fun saveBitmap(bitmap: Bitmap, ctx: Context, nameFile: String){

        val out = ctx.openFileOutput(nameFile, Context.MODE_PRIVATE)
        bitmap.compress(Bitmap.CompressFormat.PNG,100,out)
        out.close()

    }

}