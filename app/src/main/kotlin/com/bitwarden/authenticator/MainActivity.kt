package com.bitwarden.authenticator

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.core.content.IntentSanitizer
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bitwarden.authenticator.ui.platform.feature.rootnav.RootNavScreen
import com.bitwarden.authenticator.ui.platform.theme.AuthenticatorTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Primary entry point for the application.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        intent = sanitizeIntent(intent)
        var shouldShowSplashScreen = true
        installSplashScreen().setKeepOnScreenCondition { shouldShowSplashScreen }
        super.onCreate(savedInstanceState)

        observeViewModelEvents()

        if (savedInstanceState == null) {
            mainViewModel.trySendAction(
                MainAction.ReceiveFirstIntent(
                    intent = intent,
                ),
            )
        }

        setContent {
            val state by mainViewModel.stateFlow.collectAsStateWithLifecycle()

            AuthenticatorTheme(
                theme = state.theme,
            ) {
                RootNavScreen(
                    onSplashScreenRemoved = { shouldShowSplashScreen = false },
                    onExitApplication = { finishAffinity() },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        val sanitizedIntent = sanitizeIntent(intent)
        super.onNewIntent(sanitizeIntent(intent))
        mainViewModel.trySendAction(
            MainAction.ReceiveNewIntent(intent = sanitizedIntent),
        )
    }

    private fun sanitizeIntent(intent: Intent): Intent = IntentSanitizer.Builder()
        .build()
        .sanitize(intent) {
            // Intent is unclean. Ignore it and continue with the sanitized one.
        }

    private fun observeViewModelEvents() {
        mainViewModel
            .eventFlow
            .onEach { event ->
                when (event) {
                    is MainEvent.ScreenCaptureSettingChange -> {
                        handleScreenCaptureSettingChange(event)
                    }
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun handleScreenCaptureSettingChange(event: MainEvent.ScreenCaptureSettingChange) {
        if (event.isAllowed) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}
