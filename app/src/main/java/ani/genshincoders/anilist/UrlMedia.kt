package ani.genshincoders.anilist

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ani.genshincoders.loadIsMAL
import ani.genshincoders.loadMedia
import ani.genshincoders.startMainActivity
import ani.genshincoders.toastString

class UrlMedia: AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data: Uri? = intent?.data
        if (data?.host!="anilist.co") loadIsMAL = true
        try{
            if (data?.pathSegments?.get(1)!=null) loadMedia = data.pathSegments?.get(1)?.toIntOrNull()
        }catch (e:Exception){
            toastString(e.toString())
        }
        startMainActivity(this)
    }
}