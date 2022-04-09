package ani.saikou.manga

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ani.saikou.*
import ani.saikou.anilist.Anilist
import ani.saikou.databinding.ActivityMangaReaderBinding
import ani.saikou.manga.source.HMangaSources
import ani.saikou.manga.source.MangaSources
import ani.saikou.media.Media
import ani.saikou.media.MediaDetailsViewModel
import ani.saikou.settings.ReaderSettings
import ani.saikou.settings.UserInterfaceSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MangaReaderActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMangaReaderBinding
    private val model: MediaDetailsViewModel by viewModels()
    private val scope = lifecycleScope

    private lateinit var media:Media
    private lateinit var chapter: MangaChapter
    private lateinit var chapters:MutableMap<String,MangaChapter>
    private lateinit var chaptersArr: List<String>
    private lateinit var chaptersTitleArr: ArrayList<String>
    private var currentChapterIndex = 0

    private var isContVisible = false
    private var showProgressDialog = true
    private var progressDialog : AlertDialog.Builder?=null
    private var maxChapterPage = 0L
    private var currentChapterPage = 0L

    private var settings = ReaderSettings()
    private var uiSettings = UserInterfaceSettings()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMangaReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideSystemBars()

        settings = loadData("reader_settings")?: ReaderSettings().apply { saveData("reader_settings",this) }
        uiSettings = loadData("ui_settings")?: UserInterfaceSettings().apply { saveData("ui_settings",this) }

        val overshoot = AnimationUtils.loadInterpolator(this,R.anim.over_shoot)
        val controllerDuration = (uiSettings.animationSpeed*200).toLong()
        fun handleController(shouldShow:Boolean?=null){

            shouldShow?.apply { isContVisible = !this }

            if(isContVisible){
                toastString("hide : $shouldShow")
                ObjectAnimator.ofFloat(binding.mangaReaderCont, "alpha", 1f, 0f).setDuration(controllerDuration).start()
                ObjectAnimator.ofFloat(binding.mangaReaderBottomCont, "translationY", 0f, 128f).apply { interpolator = overshoot;duration = controllerDuration;start() }
                ObjectAnimator.ofFloat(binding.mangaReaderTopCont, "translationY", 0f, -128f).apply { interpolator = overshoot;duration = controllerDuration;start() }

                binding.mangaReaderCont.postDelayed({
                    isContVisible = false
                    binding.mangaReaderCont.visibility = View.GONE
                },controllerDuration)
            }else{
                toastString("show : $shouldShow")
                isContVisible = true
                binding.mangaReaderCont.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(binding.mangaReaderCont,"alpha",0f,1f).setDuration(controllerDuration).start()
                ObjectAnimator.ofFloat(binding.mangaReaderTopCont,"translationY",128f,0f).apply { interpolator=overshoot;duration=controllerDuration;start() }
                ObjectAnimator.ofFloat(binding.mangaReaderBottomCont,"translationY",-128f,0f).apply { interpolator=overshoot;duration=controllerDuration;start() }
            }
        }

        binding.mangaReaderRecycler.tapListener = {
            toastString("clicked")
            handleController()
        }

        binding.mangaReaderRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(v: RecyclerView, dx: Int, dy: Int) {
                handleController(false)
//                if(!v.canScrollVertically(-1)) {
//                    handleController(true)
//                }
//                if(!v.canScrollVertically(1)) {
//                    handleController(true)
//                }
                super.onScrolled(v, dx, dy)
            }
        })

        media = if(model.getMedia().value==null)
            try{
                (intent.getSerializableExtra("media") as? Media)?:return
            }catch (e:Exception){
                toastString(e.toString())
                return
            }
        else model.getMedia().value?:return
        model.setMedia(media)
        chapters = media.manga?.chapters?:return
        chapter = chapters[media.manga!!.selectedChapter]?:return

        model.readMangaReadSources = if (media.isAdult) HMangaSources else MangaSources
        binding.mangaReaderSource.text = model.readMangaReadSources!!.names[media.selected!!.source]

        binding.mangaReaderTitle.text = media.userPreferredName

        chaptersArr = chapters.keys.toList()
        currentChapterIndex = chaptersArr.indexOf(media.manga!!.selectedChapter)

        chaptersTitleArr = arrayListOf()
        chapters.forEach {
            val chapter = it.value
            chaptersTitleArr.add("${if(!chapter.title.isNullOrEmpty() && chapter.title!="null") "" else "Chapter "}${chapter.number}${if(!chapter.title.isNullOrEmpty() && chapter.title!="null") " : "+chapter.title else ""}")
        }

        showProgressDialog = if(settings.askIndividual) loadData<Boolean>("${media.id}_progressDialog") != true else false
        progressDialog = if(showProgressDialog && Anilist.userid!=null && if(media.isAdult) settings.updateForH else true) AlertDialog.Builder(this, R.style.DialogTheme).setTitle("Update progress on anilist?").apply {
            setMultiChoiceItems(arrayOf("Don't ask again for ${media.userPreferredName}"), booleanArrayOf(false)) { _, _, isChecked ->
                if (isChecked) {
                    saveData("${media.id}_progressDialog", isChecked)
                    progressDialog = null
                }
                showProgressDialog = isChecked
            }
            setOnCancelListener { hideSystemBars() }
        } else null

        //Chapter Change
        fun change(index:Int){
            saveData("${media.id}_${chaptersArr[currentChapterIndex]}", currentChapterPage, this)
            maxChapterPage = 0
            media.manga!!.selectedChapter = chaptersArr[index]
            model.setMedia(media)
            scope.launch(Dispatchers.IO) { model.loadMangaChapterImages(chapters[chaptersArr[index]]!!,media.selected!!) }
        }

        //ChapterSelector
        binding.mangaReaderChapterSelect.adapter = NoPaddingArrayAdapter(this, R.layout.item_dropdown,chaptersTitleArr)
        binding.mangaReaderChapterSelect.setSelection(currentChapterIndex)
        binding.mangaReaderChapterSelect.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if(position!=currentChapterIndex) change(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>) { }
        }

        //Next Chapter
        binding.mangaReaderNextChapter.setOnClickListener {
            if(chaptersArr.size > currentChapterIndex + 1) progress { change(currentChapterIndex + 1) }
            else toastString("Next Chapter Not Found")
        }
        //Prev Chapter
        binding.mangaReaderPreviousChapter.setOnClickListener {
            if(currentChapterIndex>0) change(currentChapterIndex - 1)
            else toastString("This is the 1st Chapter!")
        }

        val chapterObserverRunnable = Runnable {
            model.getMangaChapter().observe(this) {
                hideSystemBars()
                if (it != null) {
                    chapter = it
                    media.selected = model.loadSelected(media)
                    currentChapterIndex = chaptersArr.indexOf(it.number)
                    binding.mangaReaderChapterSelect.setSelection(currentChapterIndex)
                    currentChapterPage = loadData("${media.id}_${it.number}", this) ?: 0
                    val chapImages = chapter.images
                    if (chapImages != null) {
                        maxChapterPage = chapImages.size.toLong()
                        saveData("${media.id}_${it.number}", maxChapterPage)

                        binding.mangaReaderRecycler.adapter = ImageAdapter(chapter,settings.default)
                        binding.mangaReaderRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    }
                }
            }
        }
        chapterObserverRunnable.run()

        scope.launch(Dispatchers.IO) { model.loadMangaChapterImages(chapter, media.selected!!) }
    }

    private fun progress(runnable: Runnable){
        if ( maxChapterPage-currentChapterPage <= settings.readPercentage && Anilist.userid != null) {
            if (showProgressDialog && progressDialog!=null) {
                progressDialog?.setCancelable(false)
                    ?.setPositiveButton("Yes") { dialog, _ ->
                        saveData("${media.id}_save_progress",true)
                        updateAnilistProgress(media.id, media.manga!!.selectedChapter!!)
                        dialog.dismiss()
                        runnable.run()
                    }
                    ?.setNegativeButton("No") { dialog, _ ->
                        saveData("${media.id}_save_progress",false)
                        dialog.dismiss()
                        runnable.run()
                    }
                progressDialog?.show()
            }
            else {
                if(loadData<Boolean>("${media.id}_save_progress")!=false && if(media.isAdult) settings.updateForH else true)
                    updateAnilistProgress(media.id, media.manga!!.selectedChapter!!)
                runnable.run()
            }
        } else {
            runnable.run()
        }
    }

    override fun onBackPressed() {
        progress { super.onBackPressed() }
    }
}