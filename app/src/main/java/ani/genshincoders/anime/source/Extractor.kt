package ani.genshincoders.anime.source

import ani.genshincoders.anime.Episode

abstract class Extractor {
    abstract fun getStreamLinks(name:String, url: String): Episode.StreamLinks
}