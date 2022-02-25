package me.sid.smartcropper_testing.pdf

import android.R.attr
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.rajat.pdfviewer.PdfRendererCore
import kotlinx.android.synthetic.main.list_item_pdf_page.view.*
import me.sid.smartcropper_testing.R
import org.jetbrains.anko.imageBitmap
import android.graphics.drawable.BitmapDrawable
import kotlinx.android.synthetic.main.notification_show.view.*
import me.sid.smartcropper_testing.views.activities.BaseActivity
import me.sid.smartcropper_testing.views.activities.EditActivity
import me.sid.smartcropper_testing.views.activities.TranslationActivity
import java.lang.Exception
import android.R.attr.rotation
import android.util.Log
import android.widget.Toast
import me.sid.smartcropper_testing.utils.OCRUtils
import me.sid.smartcropper_testing.utils.TextTranslation
import java.io.File


internal class PdfViewAdapter(private val renderer: PdfRendererCore) :
    RecyclerView.Adapter<PdfViewAdapter.PdfPageViewHolder>() {


    lateinit var v: View;
    lateinit var imageView: ImageView;
    var text: String?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfPageViewHolder {
        v =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_pdf_page, parent, false)
        return PdfPageViewHolder(v)
    }

    override fun getItemCount(): Int {
        return renderer.getPageCount()
    }

    override fun onBindViewHolder(holder: PdfPageViewHolder, position: Int) {
        holder.bind()
    }



    fun getCurrentBitmap(pos: Int) {

        imageView = v.findViewById(R.id.pageView)
        with(imageView) {
            pageView.setImageBitmap(null)
            renderer.renderPage(pos) { bitmap: Bitmap?, pageNo: Int ->
                if (pageNo != pos)
                    return@renderPage
                bitmap?.let {
                    text = OCRUtils.getTextFromBitmap(v.context, bitmap)


                    if(!text!!.isEmpty()){

                        val intent = Intent(v.context, TranslationActivity::class.java)
                        intent.putExtra("text", text)
                        v.context.startActivity(intent)

                    }
                }
            }
        }

    }

    inner class PdfPageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {
            with(itemView) {
                pageView.setImageBitmap(null)
                renderer.renderPage(adapterPosition) { bitmap: Bitmap?, pageNo: Int ->
                    if (pageNo != adapterPosition)
                        return@renderPage
                    bitmap?.let {
                        pageView.layoutParams = pageView.layoutParams.apply {
                            height =
                                (pageView.width.toFloat() / ((bitmap.width.toFloat() / bitmap.height.toFloat()))).toInt()
                        }
                        pageView.setImageBitmap(bitmap)
                        pageView.animation = AlphaAnimation(0F, 1F).apply {
                            interpolator = LinearInterpolator()
                            duration = 300
                        }
                    }
                }
        }
        }
    }


}