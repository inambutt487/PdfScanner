package me.sid.smartcropper.views.activities

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_privacy.*
import me.sid.smartcropper.R

class TermsActivity : BaseActivity() {

    var html="<div class=\"CjVfdc\"><span class=\" Ztu2ge\"><h1>\n" +
            "  Terms &amp; Conditions\n" +
            "  </h1></span></div>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">By downloading or using the app, these terms will automatically apply to you &ndash; you should make sure therefore that you read them carefully before using the app. You&rsquo;re not allowed to copy, or modify the app, any part of the app, or our trademarks in any way. You&rsquo;re not allowed to attempt to extract the source code of the app, and you also shouldn&rsquo;t try to translate the app into other languages, or make derivative versions. The app itself, and all the trade marks, copyright, database rights and other intellectual property rights related to it, still belong to Hamza.</p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">Hamza is committed to ensuring that the app is as useful and efficient as possible. For that reason, we reserve the right to make changes to the app or to charge for its services, at any time and for any reason. We will never charge you for the app or its services without making it very clear to you exactly what you&rsquo;re paying for.</p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">The PDF Scanner app stores and processes personal data that you have provided to us, in order to provide my Service. It&rsquo;s your responsibility to keep your phone and access to the app secure. We therefore recommend that you do not jailbreak or root your phone, which is the process of removing software restrictions and limitations imposed by the official operating system of your device. It could make your phone vulnerable to malware/viruses/malicious programs, compromise your phone&rsquo;s security features and it could mean that the PDF Scanner app won&rsquo;t work properly or at all.</p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">The app does use third party services that declare their own Terms and Conditions.</p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">Link to Terms and Conditions of third party service providers used by the app</p>\n" +
            "<ul class=\"n8H08c UVNKR\">\n" +
            "<li class=\"TYR86d wXCUfe zfr3Q\" dir=\"ltr\">\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\"><a class=\"XqQF9c rXJpyf\" href=\"https://policies.google.com/terms\" target=\"_blank\" rel=\"noopener\">Google Play Services</a></p>\n" +
            "</li>\n" +
            "<li class=\"TYR86d wXCUfe zfr3Q\" dir=\"ltr\">\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\"><a class=\"XqQF9c rXJpyf\" href=\"https://developers.google.com/admob/terms\" target=\"_blank\" rel=\"noopener\">AdMob</a></p>\n" +
            "</li>\n" +
            "<li class=\"TYR86d wXCUfe zfr3Q\" dir=\"ltr\">\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\"><a class=\"XqQF9c rXJpyf\" href=\"https://firebase.google.com/terms/analytics\" target=\"_blank\" rel=\"noopener\">Google Analytics for Firebase</a></p>\n" +
            "</li>\n" +
            "<li class=\"TYR86d wXCUfe zfr3Q\" dir=\"ltr\">\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\"><a class=\"XqQF9c rXJpyf\" href=\"https://firebase.google.com/terms/crashlytics\" target=\"_blank\" rel=\"noopener\">Firebase Crashlytics</a></p>\n" +
            "</li>\n" +
            "<li class=\"TYR86d wXCUfe zfr3Q\" dir=\"ltr\">\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\"><a class=\"XqQF9c rXJpyf\" href=\"https://www.google.com/url?q=https%3A%2F%2Fwww.facebook.com%2Flegal%2Fterms%2Fplain_text_terms&amp;sa=D&amp;sntz=1&amp;usg=AFQjCNF42frW4GK7M1xpkdQND_WdGlV6lA\" target=\"_blank\" rel=\"noopener\">Facebook</a></p>\n" +
            "</li>\n" +
            "</ul>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">You should be aware that there are certain things that Hamza will not take responsibility for. Certain functions of the app will require the app to have an active internet connection. The connection can be Wi-Fi, or provided by your mobile network provider, but Hamza cannot take responsibility for the app not working at full functionality if you don&rsquo;t have access to Wi-Fi, and you don&rsquo;t have any of your data allowance left.</p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">If you&rsquo;re using the app outside of an area with Wi-Fi, you should remember that your terms of the agreement with your mobile network provider will still apply. As a result, you may be charged by your mobile provider for the cost of data for the duration of the connection while accessing the app, or other third party charges. In using the app, you&rsquo;re accepting responsibility for any such charges, including roaming data charges if you use the app outside of your home territory (i.e. region or country) without turning off data roaming. If you are not the bill payer for the device on which you&rsquo;re using the app, please be aware that we assume that you have received permission from the bill payer for using the app.</p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">Along the same lines, Hamza cannot always take responsibility for the way you use the app i.e. You need to make sure that your device stays charged &ndash; if it runs out of battery and you can&rsquo;t turn it on to avail the Service, Hamza cannot accept responsibility.</p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">With respect to Hamza&rsquo;s responsibility for your use of the app, when you&rsquo;re using the app, it&rsquo;s important to bear in mind that although we endeavour to ensure that it is updated and correct at all times, we do rely on third parties to provide information to us so that we can make it available to you. Hamza accepts no liability for any loss, direct or indirect, you experience as a result of relying wholly on this functionality of the app.</p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">At some point, we may wish to update the app. The app is currently available on Android &ndash; the requirements for system(and for any additional systems we decide to extend the availability of the app to) may change, and you&rsquo;ll need to download the updates if you want to keep using the app. Hamza does not promise that it will always update the app so that it is relevant to you and/or works with the Android version that you have installed on your device. However, you promise to always accept updates to the application when offered to you, We may also wish to stop providing the app, and may terminate use of it at any time without giving notice of termination to you. Unless we tell you otherwise, upon any termination, (a) the rights and licenses granted to you in these terms will end; (b) you must stop using the app, and (if needed) delete it from your device.</p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\"><strong>Changes to This Terms and Conditions</strong></p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">I may update our Terms and Conditions from time to time. Thus, you are advised to review this page periodically for any changes. I will notify you of any changes by posting the new Terms and Conditions on this page.</p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">These terms and conditions are effective as of 2021-03-19</p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\"><strong>Contact Us</strong></p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">If you have any questions or suggestions about my Terms and Conditions, do not hesitate to contact me at inteliwere@gmail.com.</p>"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)

        webview.loadData(html, "text/html; charset=utf-8", "UTF-8")
    }
}