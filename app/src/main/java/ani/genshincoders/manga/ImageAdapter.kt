package ani.genshincoders.manga

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import ani.genshincoders.databinding.ItemImageBinding
import ani.genshincoders.px
import ani.genshincoders.settings.CurrentReaderSettings
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import java.io.File

class ImageAdapter(
private val chapter: MangaChapter,
private val settings: CurrentReaderSettings,
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val images = chapter.images?: arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun getItemCount(): Int = images.size

    inner class ImageViewHolder(val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ImageViewHolder){
            val binding = holder.binding

            val imageView:SubsamplingScaleImageView = if(settings.layout != CurrentReaderSettings.Layouts.PAGED){
                if(settings.padding){
                    binding.root.updatePadding(bottom = 16f.px)
                }
                binding.imgProgImageNoGestures
            }else binding.imgProgImageGestures

            binding.imgProgProgress.visibility= View.VISIBLE


            Glide.with(imageView).download(GlideUrl(images[position]){chapter.headers?: mutableMapOf()}).override(Target.SIZE_ORIGINAL)
            .apply{
                val target = object : CustomViewTarget<SubsamplingScaleImageView, File>(imageView) {
                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        binding.imgProgProgress.visibility= View.GONE
                    }
                    override fun onResourceCleared(placeholder: Drawable?) {}

                    override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                        imageView.visibility = View.VISIBLE
                        if(settings.layout != CurrentReaderSettings.Layouts.PAGED) binding.root.updateLayoutParams {
                            height = ViewGroup.LayoutParams.WRAP_CONTENT
                        }
                        view.setImage(ImageSource.uri(Uri.fromFile(resource)))
                        binding.imgProgProgress.visibility= View.GONE
                    }
                }
                val transformation = chapter.transformation
                if(transformation!=null)
                    transform(File("").javaClass, transformation).into(target)
                else
                    into(target)

            }
        }
    }
}
