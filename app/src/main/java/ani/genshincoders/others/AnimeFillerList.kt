package ani.genshincoders.others

import ani.genshincoders.anime.Episode
import ani.genshincoders.logger
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.jsoup.Jsoup

object AnimeFillerList {
    fun getFillers(malId:Int):MutableMap<String,Episode>?{
        try{
            val map = mutableMapOf<String,Episode>()
            val json = Jsoup.connect("https://raw.githubusercontent.com/saikou-app/mal-id-filler-list/main/fillers/$malId.json").ignoreHttpErrors(true).ignoreContentType(true).get().body().text()
            if(json!="404: Not Found") Json.decodeFromString<JsonObject>(json)["episodes"]!!.jsonArray.forEach {
                val num = it.jsonObject["number"].toString().trim('"')
                map[num] = Episode(num,it.jsonObject["title"].toString().trim('"'), filler = it.jsonObject["filler-bool"].toString() == "true")
            }
            return map
        }catch (e:Exception){
            logger(e)
        }
        return null
    }
}