package ani.genshincoders.anime

import ani.genshincoders.anime.source.HAnimeSources
import ani.genshincoders.anime.source.WatchSources

class HWatchFragment:AnimeWatchFragment() {
    override val watchSources: WatchSources = HAnimeSources
}