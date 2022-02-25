package me.sid.smartcropper.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.feedback_dialog.*
import me.sid.smartcropper.R


class FeedbackDialog(internal var _activity: Activity) : Dialog(_activity) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.feedback_dialog)


        publishBtn.setOnClickListener {


            if (descEd.text.toString().isEmpty()) {
                Toast.makeText(_activity, "Please Write your Feedback.", Toast.LENGTH_SHORT).show()
            } else {


                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "*/*"
                intent.setPackage("com.google.android.gm")
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("inteliwere@gmail.com"))
                intent.putExtra(Intent.EXTRA_SUBJECT, titleEd.text.toString())
                intent.putExtra(Intent.EXTRA_TEXT, descEd.text.toString())
                context.startActivity(Intent.createChooser(intent, "Share PDF File"))


              /*  val gmail = Intent(Intent.ACTION_VIEW)
                gmail.setClassName(
                    "com.google.android.gm",
                    "com.google.android.gm.ComposeActivityGmail"
                )
                gmail.putExtra(Intent.EXTRA_EMAIL, arrayOf("inteliwere@gmail.com"))
                gmail.data = Uri.parse("jckdsilva@gmail.com")
                gmail.putExtra(Intent.EXTRA_SUBJECT, titleEd.text.toString())
                gmail.type = "plain/text"
                gmail.putExtra(Intent.EXTRA_TEXT, descEd.text.toString())
                gmail.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                gmail.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                _activity.startActivity(gmail)*/



                dismiss()

            }


        }


    }

    interface PermissionGranted {
        fun granted()
    }


}