package me.sid.smartcropper.views.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.danefinlay.ttsutil.Speaker
import com.danefinlay.ttsutil.isReady
import com.google.android.material.textfield.TextInputEditText
import com.google.mlkit.nl.languageid.LanguageIdentification
import me.sid.smartcropper.R
import me.sid.smartcropper.utils.Constants.*
import me.sid.smartcropper.utils.Specker.ApplicationEx
import me.sid.smartcropper.utils.TinyDB
import org.jetbrains.anko.AlertDialogBuilder
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*


class TranslationActivity : BaseActivity(), OnInitListener {

    val myApplication: ApplicationEx
        get() = application as ApplicationEx

    val speaker: Speaker?
        get() = myApplication.speaker

    private var layoutTranslated: RelativeLayout?= null
    private var statusTranslated: Boolean?= false
    //Voice
    private var tts: TextToSpeech? = null
    private var translation_specker: TextView? = null
    private var translated_speck: ImageView? = null

    var sourceLangugaeCode: String? = null
    var targetLangugaeCode: String? = null

    var sourceText: String? = null
    var translateText: String? = null

    private var btn_tranlsate: Button? = null

    var switchButton: ImageView? = null
    var srcTextView: TextInputEditText? = null
    var targetTextView: TextInputEditText? = null

    var sourceLangSelector: Button? = null
    var targetLangSelector: Button? = null

    var translation_paste: ImageView? = null
    var translation_import: TextView? = null
    var translation_import2: TextView? = null
    var translated_share: ImageView? = null
    var translated_copy: ImageView? = null

    var close: ImageView? = null

    private var backimg: ImageView? = null
    private  var premiumImg:ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translation)

        hideKeyboard(this@TranslationActivity)

        val toolbarTitle = findViewById<TextView>(R.id.toolbarTitle)
        toolbarTitle.text = "PDF Translation"

        if (savedInstanceState == null && speaker == null) {
            // Start the speaker.
            myApplication.startSpeaker(this, null)
        }

        tinyDB = TinyDB(this@TranslationActivity)

        layoutTranslated = findViewById<RelativeLayout>(R.id.layoutTranslated)

        backimg = findViewById<ImageView>(R.id.backimg)
        backimg!!.setOnClickListener(View.OnClickListener {
            speaker!!.stopSpeech()
            onBackPressed()
        })

        if(!havePurchase()) {
            premiumImg = findViewById<ImageView>(R.id.premiumImg)
            premiumImg!!.visibility = View.VISIBLE
            premiumImg!!.setOnClickListener(View.OnClickListener {
                speaker!!.stopSpeech()
                startActivity(
                    Intent(
                        this@TranslationActivity,
                        PremiumActivity::class.java
                    )
                )
            })
        }

        switchButton = findViewById<ImageView>(R.id.buttonSwitchLang)
        srcTextView = findViewById<TextInputEditText>(R.id.sourceText)


        close = findViewById<ImageView>(R.id.close)
        close!!.setOnClickListener(View.OnClickListener {


            if(statusTranslated!!){
                hideKeyboard(this@TranslationActivity)
                statusTranslated  = false
                srcTextView!!.setText("")
                targetTextView!!.setText("")
                layoutTranslated!!.visibility = View.GONE

                speaker!!.stopSpeech()
            }else{
                hideKeyboard(this@TranslationActivity)
                srcTextView!!.setText("")
                speaker!!.stopSpeech()
            }


        })

        srcTextView!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(count>0){
                    close!!.visibility = View.VISIBLE

                }else{
                    close!!.visibility = View.INVISIBLE

                }
            }
            override fun afterTextChanged(s: Editable) {

            }
        })

        targetTextView = findViewById<TextInputEditText>(R.id.targetText)

        translation_import = findViewById<TextView>(R.id.translation_import)
        translation_import!!.setOnClickListener(View.OnClickListener {
            speaker!!.stopSpeech()
            hideKeyboard(this@TranslationActivity)
            val intent = Intent(this@TranslationActivity, TranslatePDFActivity::class.java)
            startActivity(intent)

        })

        translation_import2 = findViewById<TextView>(R.id.translation_import2)
        translation_import2!!.setOnClickListener(View.OnClickListener {
            speaker!!.stopSpeech()
            hideKeyboard(this@TranslationActivity)
            val intent = Intent(this@TranslationActivity, TranslatePDFActivity::class.java)
            startActivity(intent)

        })


        val intent = intent
        if (intent != null) {
            sourceText = getIntent().getStringExtra("text")
            translateText = getIntent().getStringExtra("translated")
            if(!sourceText.isNullOrEmpty() || !translateText.isNullOrEmpty()){

                translation_import!!.visibility = View.GONE

                srcTextView!!.setText(sourceText)
                targetTextView!!.setText(translateText)

                translation_import2!!.visibility = View.VISIBLE

            }

        }else{
            translation_import!!.visibility = View.VISIBLE
            translation_import2!!.visibility = View.GONE
        }


        sourceLangSelector = findViewById<Button>(R.id.sourceLangSelector)
        sourceLangSelector!!.setOnClickListener(View.OnClickListener {

            /*speaker!!.stopSpeech()

            val intent = Intent(this@TranslationActivity, LanguageActivity::class.java)
            intent.putExtra("source", "sourceLangugaeCode")

            if (!srcTextView!!.text.toString().isBlank()) {
                intent.putExtra("text", srcTextView!!.text.toString())
            }

            if (!targetTextView!!.text.toString().isBlank()) {
                intent.putExtra("translated", targetTextView!!.text.toString())
            }

            startActivity(intent)*/

        })

        targetLangSelector = findViewById<Button>(R.id.targetLangSelector)
        targetLangSelector!!.setOnClickListener(View.OnClickListener {


            speaker!!.stopSpeech()

            val intent = Intent(this@TranslationActivity, LanguageActivity::class.java)
            intent.putExtra("source", "targetLangugaeCode")

            if (!srcTextView!!.text.toString().isBlank()) {
                intent.putExtra("text", srcTextView!!.text.toString())
            }

            if (!targetTextView!!.text.toString().isBlank()) {
                intent.putExtra("translated", targetTextView!!.text.toString())
            }

            startActivity(intent)

        })

        initLanguage()

        switchButton!!.setOnClickListener(View.OnClickListener {

            Log.d("Language", sourceLangugaeCode.toString())
            Log.d("Language", targetLangugaeCode.toString())

            if(!sourceLangSelector!!.text.toString()!!.trim().contains("Auto Detect") && !targetLangSelector!!.text.toString()!!.trim().contains("Auto Detect")){

                var text: String? = null
                var translateText = srcTextView!!.text.toString() ?: ""
                var translatedText = targetTextView!!.text.toString() ?: ""

                if (!srcTextView!!.text.toString().isBlank() && !targetTextView!!.text.toString()
                        .isBlank()
                ) {

                    speaker!!.stopSpeech()

                    srcTextView!!.setText("")
                    targetTextView!!.setText("")

                    text = translateText
                    translateText = translatedText
                    translatedText = text

                    srcTextView!!.setText(translateText)
                    targetTextView!!.setText(translatedText)

                    var temp_language: String? = null
                    temp_language = sourceLangSelector!!.text.toString()
                    sourceLangSelector!!.setText(targetLangSelector!!.text.toString())
                    targetLangSelector!!.setText(temp_language)


                    var temp_code: String? = null
                    temp_code = sourceLangugaeCode
                    sourceLangugaeCode = targetLangugaeCode
                    targetLangugaeCode = temp_code

                }

            }else{
                showSnackbar(this@TranslationActivity, R.string.select_language)
            }



        })

        btn_tranlsate = findViewById(R.id.btn_tranlsate)
        btn_tranlsate!!.setOnClickListener(View.OnClickListener {

            if(havePurchase()){
                if(haveNetworkConnection()){

                    val content = srcTextView!!.text.toString() ?: ""
                    if (content.isBlank()) {
                        hideKeyboard(this@TranslationActivity)
                        showSnackbar(this@TranslationActivity, R.string.cannot_tranlsate_empty_text_msg)
                    } else {

                        hideKeyboard(this@TranslationActivity)
                        speaker!!.stopSpeech()
                        if(!statusTranslated!!){
                            statusTranslated = true
                            translateText(content!!, sourceLangugaeCode!!, targetLangugaeCode!!)
                            layoutTranslated!!.visibility = View.VISIBLE
                        }
                        identifyLanguage(content)

                    }

                }else{
                    hideKeyboard(this@TranslationActivity)
                    showSnackbar(this@TranslationActivity, R.string.connection_error)
                }
            }else{
                startActivity(Intent(this@TranslationActivity, PremiumActivity::class.java))
            }


        })



        translation_specker = findViewById(R.id.translation_specker)
        translation_specker!!.setOnClickListener(View.OnClickListener {

            if (speaker.isReady()) {
                val content = srcTextView!!.text.toString() ?: ""
                if (content.isBlank()) {
                    showSnackbar(this@TranslationActivity, R.string.cannot_speak_empty_text_msg)
                    hideKeyboard(this@TranslationActivity)
                }else{

                    tts = TextToSpeech(this) { status ->
                        if (status == TextToSpeech.SUCCESS) {

                            if(sourceLangugaeCode.equals("auto")){
                                speaker?.speak(content)
                            }else{
                                val res: Int = tts!!.setLanguage(Locale.forLanguageTag(sourceLangugaeCode))
                                if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                                    Log.e("TTS", "Language not Supporter")
                                    showSnackbar(this@TranslationActivity, R.string.language_not_suport)
                                } else {
                                    // Speak text.
                                    speaker?.speak(content)
                                }
                            }

                        } else {
                            showSnackbar(this@TranslationActivity, R.string.no_engine_available_message)
                            Log.e("TTS", "Init Failed")
                        }
                    }

                }




            } else {
                // Show the speaker not ready message.
                showSpeakerNotReadyMessage()
            }

        })


        translated_speck = findViewById(R.id.translated_speck)
        translated_speck!!.setOnClickListener(View.OnClickListener {

            val content = targetTextView!!.text.toString() ?: ""
            if (content.isBlank()) {
                showSnackbar(this@TranslationActivity, R.string.cannot_speak_empty_text_msg)
                hideKeyboard(this@TranslationActivity)
            }else{
                if (speaker.isReady()) {
                    tts = TextToSpeech(this) { status ->
                        if (status == TextToSpeech.SUCCESS) {
                            val res: Int = tts!!.setLanguage(Locale.forLanguageTag(targetLangugaeCode))
                            if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                                Log.e("TTS", "Language not Supporter")
                                showSnackbar(this@TranslationActivity, R.string.language_not_suport)
                            } else {
                                // Speak text.
                                speaker?.speak(content)
                            }
                        } else {
                            showSnackbar(this@TranslationActivity, R.string.no_engine_available_message)
                            Log.e("TTS", "Init Failed")
                        }
                    }

                } else {
                    // Show the speaker not ready message.
                    showSpeakerNotReadyMessage()
                }
            }



        })


        translated_share = findViewById<ImageView>(R.id.translated_share)
        translated_share!!.setOnClickListener(View.OnClickListener {
            speaker!!.stopSpeech()
            hideKeyboard(this@TranslationActivity)
            val content = targetTextView!!.text.toString() ?: ""
            if (!content.isBlank()) {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    content
                )
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            }else{
                showSnackbar(this@TranslationActivity, R.string.empty_text)
            }


        })
        translated_copy = findViewById<ImageView>(R.id.translated_copy)
        translated_copy!!.setOnClickListener(View.OnClickListener {
            speaker!!.stopSpeech()
            hideKeyboard(this@TranslationActivity)
            val content = targetTextView!!.text.toString() ?: ""
            if (!content.isBlank()) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("label",content)
                clipboard.setPrimaryClip(clip)


                showSnackbar(this@TranslationActivity, R.string.copied)

            }else{
                showSnackbar(this@TranslationActivity, R.string.empty_text)
            }


        })
    }


    override fun onBackPressed() {
        startActivity(
            Intent(
                this@TranslationActivity,
                MainActivity::class.java
            )
        )
    }

    private fun initLanguage() {

        if (!tinyDB?.getListString("sourceLangugaeCode").isNullOrEmpty()) {

            Log.d("sourceLangugaeCode", "true")

            sourceLangSelector!!.setText(
                tinyDB.getListString("sourceLangugaeCode").get(0).toString()
            )


            sourceLangugaeCode = tinyDB.getListString("sourceLangugaeCode").get(1).toString()



        } else {

            Log.d("sourceLangugaeCode", "false")

            sourceLangSelector!!.setText("Auto Detect")
            sourceLangugaeCode = "auto"
        }

        if (!tinyDB?.getListString("targetLangugaeCode").isNullOrEmpty()) {

            Log.d("targetLangugaeCode", "true")

            targetLangSelector!!.setText(
                tinyDB.getListString("targetLangugaeCode").get(0).toString()
            )

            targetLangugaeCode = tinyDB.getListString("targetLangugaeCode").get(1).toString()


            val content = targetTextView!!.text.toString() ?: ""
            if (!content.isBlank()) {
                /*identifyTargetText(content, targetLangugaeCode!!)*/

                translateTargetText(content!!, "auto"!!, targetLangugaeCode!!)
            }

        } else {

            Log.d("targetLangugaeCode", "false")

            targetLangSelector!!.setText("English")
            targetLangugaeCode = "en"

        }
    }

    private fun identifyLanguage(text: String) {

        val languageIdentifier = LanguageIdentification.getClient()
        languageIdentifier.identifyLanguage(text)
            .addOnSuccessListener { languageCode ->
                if (languageCode == "und") {

                    Log.i("TranslationActivity", "Can't identify language.")
                } else {

                    Log.i("TranslationActivity", "Language: $languageCode")

                    sourceLangugaeCode = languageCode
                    translateText(text!!, sourceLangugaeCode!!, targetLangugaeCode!!)
                }
            }
            .addOnFailureListener {
                identifyLanguage(text)
            }


    }

    private fun identifySrcText(text: String, target: String) {

        val languageIdentifier = LanguageIdentification.getClient()
        languageIdentifier.identifyLanguage(text)
            .addOnSuccessListener { languageCode ->
                if (languageCode == "und") {

                    Log.i("TranslationActivity", "Can't identify language.")
                } else {

                    Log.i("TranslationActivity", "Language: $languageCode")

                    translateSourceText(text!!, languageCode!!, target!!)
                }
            }
            .addOnFailureListener {
                identifyLanguage(text)
            }


    }

    private fun identifyTargetText(text: String, target: String) {

        val languageIdentifier = LanguageIdentification.getClient()
        languageIdentifier.identifyLanguage(text)
            .addOnSuccessListener { languageCode ->
                if (languageCode == "und") {

                    Log.i("TranslationActivity", "Can't identify language.")
                } else {

                    Log.i("TranslationActivity", "Language: $languageCode")

                    translateTargetText(text!!, languageCode!!, target!!)
                }
            }
            .addOnFailureListener {
                identifyLanguage(text)
            }


    }


    private fun translateText(totranslate: String, sourcelanguage: String, targetLanguage: String) {

        if (totranslate!!.isBlank()) {
            showSnackbar(this@TranslationActivity, R.string.cannot_tranlsate_empty_text_msg)
            return
        }

        if (sourcelanguage!!.isBlank()) {
            showSnackbar(this@TranslationActivity, R.string.cannot_tranlsate_empty_source_language_text_msg)
            return
        }

        if (targetLanguage!!.isBlank()) {
            showSnackbar(this@TranslationActivity, R.string.cannot_tranlsate_empty_target_language_text_msg)
            return
        }


        val thread = Thread {
            //dialog.dismiss();

            var url: String? = null
            try {
                url =
                    "https://translate.google.com/m?hl=en&sl=" + sourcelanguage + "&tl=" + targetLanguage + "&q=" + URLEncoder.encode(
                        totranslate,
                        "UTF-8"
                    )
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }

            val doc: Document
            try {
                doc = Jsoup.connect(url).get()
                val elements: Elements = doc.getElementsByClass("result-container")
                if (elements.text() !== "" && !TextUtils.isEmpty(elements.text())) {

                    runOnUiThread { targetTextView!!.setText(elements.text()) }
                    Log.i("TranslationActivity", "Reult is" + translateText)

                } else {
                    Log.i("TranslationActivity", "Reult is empty")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

    private fun translateSourceText(
        totranslate: String,
        sourcelanguage: String,
        targetLanguage: String
    ) {


        val thread = Thread {
            //dialog.dismiss();

            var url: String? = null
            try {
                url =
                    "https://translate.google.com/m?hl=en&sl=" + sourcelanguage + "&tl=" + targetLanguage + "&q=" + URLEncoder.encode(
                        totranslate,
                        "UTF-8"
                    )
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }

            val doc: Document
            try {
                doc = Jsoup.connect(url).get()
                val elements: Elements = doc.getElementsByClass("result-container")
                if (elements.text() !== "" && !TextUtils.isEmpty(elements.text())) {

                    runOnUiThread { srcTextView!!.setText(elements.text()) }
                    Log.i("TranslationActivity", "Reult is" + translateText)

                } else {
                    Log.i("TranslationActivity", "Reult is empty")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

    private fun translateTargetText(
        totranslate: String,
        sourcelanguage: String,
        targetLanguage: String
    ) {

        val thread = Thread {
            //dialog.dismiss();

            var url: String? = null
            try {
                url =
                    "https://translate.google.com/m?hl=en&sl=" + sourcelanguage + "&tl=" + targetLanguage + "&q=" + URLEncoder.encode(
                        totranslate,
                        "UTF-8"
                    )
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }

            val doc: Document
            try {
                doc = Jsoup.connect(url).get()
                val elements: Elements = doc.getElementsByClass("result-container")
                if (elements.text() !== "" && !TextUtils.isEmpty(elements.text())) {

                    runOnUiThread { targetTextView!!.setText(elements.text()) }
                    Log.i("TranslationActivity", "Reult is" + translateText)

                } else {
                    Log.i("TranslationActivity", "Reult is empty")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

    private fun setProgressText(tv: TextView) {
        tv.text = getString(R.string.translate_progress)
    }

    private fun setSpeakerReady() {
        val speaker = speaker ?: return
        speaker.ready = true

        // Set the preferred voice if one has been set in the preferences.
        val tts = speaker.tts
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val voiceName = prefs.getString("pref_tts_voice", null)
        if (voiceName != null) {
            val voices = speaker.voices.toList().filterNotNull()
            if (voices.isNotEmpty()) {
                val voiceNames = voices.map { it.name }
                val voiceIndex = voiceNames.indexOf(voiceName)
                tts.voice = if (voiceIndex == -1) {
                    speaker.voice ?: speaker.defaultVoice
                } else voices[voiceIndex]
            }
        }

        // Set the preferred pitch if one has been set in the preferences.
        val preferredPitch = prefs.getFloat("pref_tts_pitch", -1.0f)
        if (preferredPitch > 0) {
            tts.setPitch(preferredPitch)
        }

        // Set the preferred speech rate if one has been set in the preferences.
        val preferredSpeechRate = prefs.getFloat("pref_tts_speech_rate", -1.0f)
        if (preferredSpeechRate > 0) {
            tts.setSpeechRate(preferredSpeechRate)
        }
    }

    override fun onInit(status: Int) {
        // Handle errors.
        val speaker = speaker
        val tts = speaker?.tts
        myApplication.errorMessageId = null
        if (status == TextToSpeech.ERROR || tts == null) {
            // Check the number of available TTS engines and set an appropriate
            // error message.
            val engines = speaker?.tts?.engines ?: listOf()
            val messageId = if (engines.isEmpty()) {
                // No usable TTS engines.
                R.string.no_engine_available_message
            } else {
                // General TTS initialisation failure.
                R.string.tts_initialisation_failure_msg
            }
            runOnUiThread { showSnackbar(this@TranslationActivity,messageId) }

            // Save the error message ID for later use, free the Speaker and return.
            myApplication.errorMessageId = messageId
            myApplication.freeSpeaker()
            return
        }

        // Check if the language is available.
        val systemLocale = myApplication.currentSystemLocale

        @Suppress("deprecation")
        val language = speaker.voice?.locale ?: tts.language ?: systemLocale
        when (tts.isLanguageAvailable(language)) {
            // Set the language if it is available and there is no current voice.
            TextToSpeech.LANG_AVAILABLE, TextToSpeech.LANG_COUNTRY_AVAILABLE,
            TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE -> {
                if (speaker.voice == null)
                    tts.language = language

                // The Speaker is now ready to process text into speech.
                setSpeakerReady()
            }

            // Install missing voice data if required.
            TextToSpeech.LANG_MISSING_DATA -> {
                runOnUiThread {
                    showNoTTSDataDialog()
                }
            }

            // Inform the user that the selected language is not available.
            TextToSpeech.LANG_NOT_SUPPORTED -> {
                // Attempt to fall back on the system language.
                when (tts.isLanguageAvailable(systemLocale)) {
                    TextToSpeech.LANG_AVAILABLE,
                    TextToSpeech.LANG_COUNTRY_AVAILABLE,
                    TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE -> {
                        runOnUiThread {

                            showSnackbar(this@TranslationActivity, R.string.tts_language_not_available_msg1)
                        }
                        tts.language = systemLocale

                        // The Speaker is now ready to process text into speech.
                        setSpeakerReady()
                    }

                    else -> {
                        // Neither the selected nor the default languages are
                        // available.
                        val messageId = R.string.tts_language_not_available_msg2
                        myApplication.errorMessageId = messageId
                        runOnUiThread {
                            showSnackbar(this@TranslationActivity, messageId)
                        }
                    }
                }
            }
        }
    }

    fun showSpeakerNotReadyMessage() {
        showSnackbar(this@TranslationActivity, R.string.speaker_not_ready_message)
    }

    fun openSystemTTSSettings() {
        // Got this from: https://stackoverflow.com/a/8688354
        val intent = Intent()
        intent.action = "com.android.settings.TTS_SETTINGS"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun showNoTTSDataDialog() {
        AlertDialogBuilder(this).apply {
            title(R.string.no_tts_data_alert_title)
            message(R.string.no_tts_data_alert_message)
            positiveButton(R.string.alert_positive_message) {
                noTTSDataDialogPositiveButton()
            }
            negativeButton(R.string.alert_negative_message1) {
                onDoNotInstallTTSData()
            }
            show()
        }
    }

    open fun noTTSDataDialogPositiveButton() {
        val install = Intent()
        install.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
        startActivityForResult(install, INSTALL_TTS_DATA)
    }

    open fun onDoNotInstallTTSData() {}

    private fun showTTSInstallFailureDialog() {
        // Show a dialog if installing TTS data failed.
        AlertDialogBuilder(this).apply {
            title(R.string.failed_to_get_tts_data_title)
            message(R.string.failed_to_get_tts_data_msg)
            positiveButton(R.string.alert_positive_message) {
                onTTSInstallFailureDialogExit()
            }
            onCancel { onTTSInstallFailureDialogExit() }
            show()
        }
    }

    open fun onTTSInstallFailureDialogExit() {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            INSTALL_TTS_DATA -> {
                if (resultCode == TextToSpeech.SUCCESS) {
                    speaker?.ready = true
                } else {
                    showTTSInstallFailureDialog()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        const val INSTALL_TTS_DATA = 1
    }


}