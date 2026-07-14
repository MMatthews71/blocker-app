package com.blockingapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.blockingapp.BlockerRepository
import com.blockingapp.databinding.ActivityBlockBinding
import com.blockingapp.service.AppBlockerAccessibilityService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BlockActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBlockBinding
    private val repository by lazy { BlockerRepository.getInstance(this) }
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var confirmJob: Job? = null
    private var isConfirming = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isConfirming) cancelConfirmation() else goHome()
            }
        })

        binding.btnBreak.setOnClickListener {
            if (isConfirming) cancelConfirmation() else startConfirmation()
        }
        binding.btnGoHome.setOnClickListener { goHome() }
    }

    override fun onResume() {
        super.onResume()
        scope.launch {
            if (repository.isOnBreak()) { finish(); return@launch }
            updateUI()
        }
    }

    private fun updateUI() {
        scope.launch {
            val state = repository.getDailyState()
            val remaining = BlockerRepository.MAX_BREAKS - state.breaksUsed
            val mins = BlockerRepository.BREAK_DURATION_MS / 60000

            binding.tvBreaksRemaining.text =
                "Breaks remaining today: $remaining / ${BlockerRepository.MAX_BREAKS}"

            if (remaining > 0) {
                binding.btnBreak.isEnabled = true
                binding.btnBreak.text = "Take ${mins}-Minute Break"
                binding.tvStatus.visibility = View.GONE
            } else {
                binding.btnBreak.isEnabled = false
                binding.btnBreak.text = "No Breaks Left"
                binding.tvStatus.text =
                    "You've used all ${BlockerRepository.MAX_BREAKS} breaks today.\nBreaks reset at midnight."
                binding.tvStatus.visibility = View.VISIBLE
            }
        }
    }

    // Show a CONFIRM_SECONDS countdown. The user must wait it out — they can
    // cancel, but they cannot skip it. This creates friction against impulsive breaks.
    private fun startConfirmation() {
        isConfirming = true
        confirmJob = scope.launch {
            for (i in CONFIRM_SECONDS downTo 1) {
                binding.btnBreak.text = "Cancel ($i s)"
                binding.tvStatus.text = "Are you sure?\nBreak starts in $i seconds…"
                binding.tvStatus.visibility = View.VISIBLE
                delay(1000)
            }
            isConfirming = false
            activateBreak()
        }
    }

    private fun cancelConfirmation() {
        confirmJob?.cancel()
        isConfirming = false
        updateUI()
    }

    private fun activateBreak() {
        scope.launch {
            binding.btnBreak.isEnabled = false
            val breakEndsAt = repository.startBreak()
            if (breakEndsAt < 0) { updateUI(); return@launch }
            AppBlockerAccessibilityService.instance?.notifyBreakStarted(breakEndsAt)
            finish()
        }
    }

    private fun goHome() {
        if (isConfirming) cancelConfirmation()
        startActivity(Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    override fun onDestroy() {
        confirmJob?.cancel()
        scope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val CONFIRM_SECONDS = 30
    }
}
