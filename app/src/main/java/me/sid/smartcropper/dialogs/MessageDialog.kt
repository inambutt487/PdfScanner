package me.sid.smartcropper.dialogs

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import kotlinx.android.synthetic.main.message_dialog.*
import me.sid.smartcropper.R


class MessageDialog(internal var _activity: Activity, message: String, desc: String) :
    Dialog(_activity) {

    var message : String?= message
    var desc : String?= desc

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.message_dialog)

        text1!!.setText(message)
        text2!!.setText(desc)
        publishBtn.setOnClickListener {
            dismiss()
        }


    }

}