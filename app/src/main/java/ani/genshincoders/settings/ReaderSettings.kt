package ani.genshincoders.settings

data class ReaderSettings (
    var askIndividual:Boolean=true,
    var updateForH:Boolean=false,
    var readPercentage:Int=1,

    var default: CurrentReaderSettings = CurrentReaderSettings()
)