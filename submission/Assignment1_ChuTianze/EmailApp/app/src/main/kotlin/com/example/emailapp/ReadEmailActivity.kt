package com.example.emailapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.emailapp.databinding.ActivityReadEmailBinding

/**
 * Read-only preview of the composed email. Started by an explicit intent.
 * Bcc is kept for sending, but it is not shown on this screen.
 */
class ReadEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReadEmailBinding
    private var toAddress = ""
    private var ccAddress = ""
    private var bccAddress = ""
    private var subject = ""
    private var body = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val source = intent
        val from = source.extra(EmailContract.EXTRA_FROM)
        toAddress = source.extra(EmailContract.EXTRA_TO)
        ccAddress = source.extra(EmailContract.EXTRA_CC)
        bccAddress = source.extra(EmailContract.EXTRA_BCC)
        subject = source.extra(EmailContract.EXTRA_SUBJECT)
        body = source.extra(EmailContract.EXTRA_BODY)

        showField(binding.textFrom, from)
        showField(binding.textTo, toAddress)
        showField(binding.textCc, ccAddress)
        showField(binding.textSubject, subject)
        showField(binding.textBody, body)

        binding.buttonEditEmail.setOnClickListener { finish() }
        binding.buttonSendEmail.setOnClickListener { sendEmail() }
    }

    private fun showField(textView: TextView, value: String) {
        textView.text = value.ifEmpty { getString(R.string.empty_value) }
    }

    private fun sendEmail() {
        if (toAddress.isBlank()) {
            Toast.makeText(this, R.string.error_missing_recipient, Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, parseAddresses(toAddress))
            if (ccAddress.isNotBlank()) {
                putExtra(Intent.EXTRA_CC, parseAddresses(ccAddress))
            }
            if (bccAddress.isNotBlank()) {
                putExtra(Intent.EXTRA_BCC, parseAddresses(bccAddress))
            }
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }

        try {
            startActivity(Intent.createChooser(intent, getString(R.string.chooser_send_email)))
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, R.string.error_no_email_client, Toast.LENGTH_SHORT).show()
        }
    }

    private fun Intent.extra(key: String): String = getStringExtra(key).orEmpty()

    private fun parseAddresses(raw: String): Array<String> {
        return raw.split(',', ';')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toTypedArray()
    }
}
