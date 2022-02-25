package me.sid.smartcropper_testing.pdf

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.LinearInterpolator
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.rajat.pdfviewer.PdfRendererCore
import kotlinx.android.synthetic.main.list_item_pdf_page.view.*
import kotlinx.android.synthetic.main.pdf_rendererview.view.*
import me.sid.smartcropper_testing.R
import me.sid.smartcropper_testing.views.activities.BaseActivity
import me.sid.smartcropper_testing.views.activities.EditActivity
import me.sid.smartcropper_testing.views.activities.TranslationActivity
import java.io.File
import java.net.URLEncoder

class PdfRendererView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var status : Boolean?= true
    private lateinit var pageNumCount: TextView
    private lateinit var backWordTv: ImageView
    private lateinit var forwordTv: ImageView

    private lateinit var pageNoCustom: TextView

    private var translate: Boolean = false

    private lateinit var btn_tranlsate_pdf_page: Button

    private var foundPosition: Int = 0

    private lateinit var recyclerView: RecyclerView
    private lateinit var pdfRendererCore: PdfRendererCore
    private lateinit var pdfViewAdapter: PdfViewAdapter
    private var quality = PdfQuality.NORMAL
    private var engine = PdfEngine.INTERNAL
    private var showDivider = true
    private var divider: Drawable? = null
    private var runnable = Runnable {}
    private var pdfRendererCoreInitialised = false

    var statusListener: StatusCallBack? = null
    val totalPageCount: Int
        get() {
            return pdfRendererCore.getPageCount()
        }

    interface StatusCallBack {
        fun onDownloadStart() {}
        fun onDownloadProgress(progress: Int, downloadedBytes: Long, totalBytes: Long?) {}
        fun onDownloadSuccess() {}
        fun onError(error: Throwable) {}
        fun onPageChanged(currentPage: Int, totalPage: Int) {}
    }

    fun initWithUrl(
        url: String,
        pdfQuality: PdfQuality = this.quality,
        engine: PdfEngine = this.engine
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP || engine == PdfEngine.GOOGLE) {
            initUnderKitkat(url)
            statusListener?.onDownloadStart()
            return
        }

        PdfDownloader(url, object : PdfDownloader.StatusListener {
            override fun getContext(): Context = context
            override fun onDownloadStart() {
                statusListener?.onDownloadStart()
            }

            override fun onDownloadProgress(currentBytes: Long, totalBytes: Long) {
                var progress = (currentBytes.toFloat() / totalBytes.toFloat() * 100F).toInt()
                if (progress >= 100)
                    progress = 100
                statusListener?.onDownloadProgress(progress, currentBytes, totalBytes)
            }

            override fun onDownloadSuccess(absolutePath: String) {
                initWithPath(absolutePath, pdfQuality)
                statusListener?.onDownloadSuccess()
            }

            override fun onError(error: Throwable) {
                error.printStackTrace()
                statusListener?.onError(error)
            }
        })
    }

    fun initWithPath(path: String, pdfQuality: PdfQuality = this.quality) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            throw UnsupportedOperationException("should be over API 21")
        initWithFile(false, File(path), pdfQuality)
    }

    fun initWithFile(translate: Boolean, file: File, pdfQuality: PdfQuality = this.quality) {
        this.translate = translate
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            throw UnsupportedOperationException("should be over API 21")
        init(file, pdfQuality)
    }

    private fun init(file: File, pdfQuality: PdfQuality) {
        pdfRendererCore = PdfRendererCore(context, file, pdfQuality)
        pdfRendererCoreInitialised = true
        pdfViewAdapter = PdfViewAdapter(pdfRendererCore)
        val v = LayoutInflater.from(context).inflate(R.layout.pdf_rendererview, this, false)
        addView(v)

        pageNoCustom = findViewById(R.id.pageNocustom)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.apply {
            adapter = pdfViewAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            itemAnimator = DefaultItemAnimator()
            if (showDivider) {
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                    divider?.let { setDrawable(it) }
                }.let { addItemDecoration(it) }
            }
            addOnScrollListener(scrollListener)


        }

        backWordTv = findViewById(R.id.backWordTv)
        backWordTv.setOnClickListener() {

            if (foundPosition > 0) {
                status = true
                foundPosition--
                pageNoCustom.setText("${(foundPosition + 1)} of $totalPageCount")
                recyclerView.layoutManager!!.scrollToPosition(foundPosition)
            }


        }

        forwordTv = findViewById(R.id.forwordTv)
        forwordTv.setOnClickListener() {

            if (foundPosition < totalPageCount-1) {
                status = true
                foundPosition++
                pageNoCustom.setText("${(foundPosition + 1)} of $totalPageCount")
                recyclerView.layoutManager!!.scrollToPosition(foundPosition)
            }

        }

        if (translate) {


            btn_tranlsate_pdf_page = findViewById(R.id.btn_tranlsate_pdf_page)
            btn_tranlsate_pdf_page.visibility = VISIBLE
            btn_tranlsate_pdf_page.setOnClickListener() {

                if(status!!){
                    status = false
                    pdfViewAdapter.getCurrentBitmap(foundPosition);
                }



            }
        }


        runnable = Runnable {
            /*pageNo.visibility = View.GONE*/
        }

    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            (recyclerView.layoutManager as LinearLayoutManager).run {

                status = true
                foundPosition = findFirstCompletelyVisibleItemPosition()

                pageNo.run {
                    if (foundPosition != NO_POSITION)
                        text = "${(foundPosition + 1)} of $totalPageCount"
                    pageNoCustom.setText("${(foundPosition + 1)} of $totalPageCount")
                    pageNo.visibility = View.VISIBLE
                }

                if (foundPosition == 0)
                    pageNo.postDelayed({
                        pageNo.visibility = GONE
                    }, 3000)

                if (foundPosition != NO_POSITION) {
                    statusListener?.onPageChanged(foundPosition, totalPageCount)
                    pageNoCustom.setText("${(foundPosition + 1)} of $totalPageCount")
                    return@run
                }
                foundPosition = findFirstVisibleItemPosition()
                if (foundPosition != NO_POSITION) {
                    statusListener?.onPageChanged(foundPosition, totalPageCount)
                    pageNoCustom.setText("${(foundPosition + 1)} of $totalPageCount")
                    return@run
                }
            }

        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                pageNo.postDelayed(runnable, 3000)
            } else {
                pageNo.removeCallbacks(runnable)
            }
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initUnderKitkat(url: String) {
        val v = LayoutInflater.from(context).inflate(R.layout.pdf_rendererview, this, false)
        addView(v)


        backWordTv = findViewById(R.id.backWordTv)
        forwordTv = findViewById(R.id.forwordTv)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.visibility = View.GONE
        webView.visibility = View.VISIBLE
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = PdfWebViewClient(statusListener)
        webView.loadUrl(
            "https://drive.google.com/viewer/viewer?hl=en&embedded=true&url=${
                URLEncoder.encode(
                    url,
                    "UTF-8"
                )
            }"
        )
    }

    internal class PdfWebViewClient(private val statusListener: StatusCallBack?) : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            statusListener?.onDownloadSuccess()
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            statusListener?.onError(Throwable("Web resource error"))
        }

        override fun onReceivedError(
            view: WebView?,
            errorCode: Int,
            description: String?,
            failingUrl: String?
        ) {
            super.onReceivedError(view, errorCode, description, failingUrl)
            statusListener?.onError(Throwable("Web resource error"))
        }
    }

    init {
        getAttrs(attrs, defStyleAttr)
    }

    private fun getAttrs(attrs: AttributeSet?, defStyle: Int) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.PdfRendererView, defStyle, 0)
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        val ratio =
            typedArray.getInt(R.styleable.PdfRendererView_pdfView_quality, PdfQuality.NORMAL.ratio)
        quality = PdfQuality.values().first { it.ratio == ratio }
        val engineValue =
            typedArray.getInt(R.styleable.PdfRendererView_pdfView_engine, PdfEngine.INTERNAL.value)
        engine = PdfEngine.values().first { it.value == engineValue }
        showDivider = typedArray.getBoolean(R.styleable.PdfRendererView_pdfView_showDivider, true)
        divider = typedArray.getDrawable(R.styleable.PdfRendererView_pdfView_divider)

        typedArray.recycle()
    }

    fun closePdfRender() {
        if (pdfRendererCoreInitialised)
            pdfRendererCore.closePdfRender()
    }

    fun jumpTopage(page: Int) {

    }
}