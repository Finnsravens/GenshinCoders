package ani.genshincoders.anilist

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ani.genshincoders.logger
import ani.genshincoders.startMainActivity
import ani.genshincoders.toastString
import java.lang.Exception

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data: Uri? = intent?.data
        logger(data.toString())
        try{
        Anilist.token = Regex("""(?<=access_token=).+(?=&token_type)""").find(data.toString())!!.value
        val filename = "anilistToken"
        this.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(Anilist.token!!.toByteArray())
        }
        }catch (e:Exception){ toastString(e.toString()) }
        startMainActivity(this)
    }
}
