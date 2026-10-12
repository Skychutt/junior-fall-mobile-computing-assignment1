package com.example.emailapp

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.emailapp.databinding.ActivityComposeEmailBinding

/**
 * Email composition screen. This is the activity the app starts in.
 */
class ComposeEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityComposeEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComposeEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState != null) {
            restoreFromBundle(savedInstanceState)
        } else {
            restoreDraft()
        }

        binding.buttonClear.setOnClickListener { clearFields() }
        binding.buttonPreview.setOnClickListener { openPreview() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EmailContract.EXTRA_FROM, binding.editFrom.value())
        outState.putString(EmailContract.EXTRA_TO, binding.editTo.value())
        outState.putString(EmailContract.EXTRA_CC, binding.editCc.value())
        outState.putString(EmailContract.EXTRA_BCC, binding.editBcc.value())
        outState.putString(EmailContract.EXTRA_SUBJECT, binding.editSubject.value())
        outState.putString(EmailContract.EXTRA_BODY, binding.editBody.value())
    }

    override fun onPause() {
        super.onPause()
        saveDraft()
    }

    private fun openPreview() {
        saveDraft()
        val intent = Intent(this, ReadEmailActivity::class.java).apply {
            putExtra(EmailContract.EXTRA_FROM, binding.editFrom.value())
            putExtra(EmailContract.EXTRA_TO, binding.editTo.value())
            putExtra(EmailContract.EXTRA_CC, binding.editCc.value())
            putExtra(EmailContract.EXTRA_BCC, binding.editBcc.value())
            putExtra(EmailContract.EXTRA_SUBJECT, binding.editSubject.value())
            putExtra(EmailContract.EXTRA_BODY, binding.editBody.value())
        }
        startActivity(intent)
    }

    private fun clearFields() {
        binding.editFrom.setText("")
        binding.editTo.setText("")
        binding.editCc.setText("")
        binding.editBcc.setText("")
        binding.editSubject.setText("")
        binding.editBody.setText("")
        saveDraft()
    }

    private fun restoreFromBundle(savedInstanceState: Bundle) {
        binding.editFrom.setText(savedInstanceState.getString(EmailContract.EXTRA_FROM, ""))
        binding.editTo.setText(savedInstanceState.getString(EmailContract.EXTRA_TO, ""))
        binding.editCc.setText(savedInstanceState.getString(EmailContract.EXTRA_CC, ""))
        binding.editBcc.setText(savedInstanceState.getString(EmailContract.EXTRA_BCC, ""))
        binding.editSubject.setText(savedInstanceState.getString(EmailContract.EXTRA_SUBJECT, ""))
        binding.editBody.setText(savedInstanceState.getString(EmailContract.EXTRA_BODY, ""))
    }

    private fun restoreDraft() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        binding.editFrom.setText(prefs.getString(EmailContract.EXTRA_FROM, ""))
        binding.editTo.setText(prefs.getString(EmailContract.EXTRA_TO, ""))
        binding.editCc.setText(prefs.getString(EmailContract.EXTRA_CC, ""))
        binding.editBcc.setText(prefs.getString(EmailContract.EXTRA_BCC, ""))
        binding.editSubject.setText(prefs.getString(EmailContract.EXTRA_SUBJECT, ""))
        binding.editBody.setText(prefs.getString(EmailContract.EXTRA_BODY, ""))
    }

    private fun saveDraft() {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putString(EmailContract.EXTRA_FROM, binding.editFrom.value())
            .putString(EmailContract.EXTRA_TO, binding.editTo.value())
            .putString(EmailContract.EXTRA_CC, binding.editCc.value())
            .putString(EmailContract.EXTRA_BCC, binding.editBcc.value())
            .putString(EmailContract.EXTRA_SUBJECT, binding.editSubject.value())
            .putString(EmailContract.EXTRA_BODY, binding.editBody.value())
            .commit()
    }

    private fun EditText.value(): String = text?.toString().orEmpty()

    companion object {
        private const val PREFS_NAME = "email_draft"
    }
}
