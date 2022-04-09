package ani.genshincoders.manga.source

import ani.genshincoders.media.MediaDetailsViewModel
import ani.genshincoders.media.Source
import ani.genshincoders.media.SourceAdapter
import ani.genshincoders.media.SourceSearchDialogFragment
import kotlinx.coroutines.CoroutineScope

class MangaSourceAdapter(sources: ArrayList<Source>,val model: MediaDetailsViewModel,val i:Int,val id:Int,fragment: SourceSearchDialogFragment,scope: CoroutineScope) : SourceAdapter(sources,fragment,scope) {
    override fun onItemClick(source: Source) {
        model.overrideMangaChapters(i, source, id)
    }
}