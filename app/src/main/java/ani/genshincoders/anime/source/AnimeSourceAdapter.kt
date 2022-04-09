package ani.genshincoders.anime.source

import ani.genshincoders.media.MediaDetailsViewModel
import ani.genshincoders.media.Source
import ani.genshincoders.media.SourceAdapter
import ani.genshincoders.media.SourceSearchDialogFragment
import kotlinx.coroutines.CoroutineScope

class AnimeSourceAdapter(sources: ArrayList<Source>,val model: MediaDetailsViewModel,val i:Int,val id:Int,fragment: SourceSearchDialogFragment,scope:CoroutineScope) : SourceAdapter(sources,fragment,scope) {
    override fun onItemClick(source: Source) {
        model.overrideEpisodes(i, source, id)
    }
}