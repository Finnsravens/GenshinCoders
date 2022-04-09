package ani.genshincoders.manga

import java.io.Serializable

data class Manga (
    var totalChapters: Int? = null,
    var selectedChapter: String?=null,
    var chapters: MutableMap<String, MangaChapter>? = null,
    var slug:String?=null,
):Serializable