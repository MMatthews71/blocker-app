package com.blockingapp.ui

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.blockingapp.BlockerRepository
import com.blockingapp.FeatureBlocks
import com.blockingapp.data.BlockedApp
import com.blockingapp.data.DailyFocusState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BlockerRepository.getInstance(application)

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps

    val dailyState: LiveData<DailyFocusState?> = repository.dailyStateFlow.asLiveData()

    fun loadApps(pm: PackageManager) {
        viewModelScope.launch(Dispatchers.IO) {
            val blockedApps = repository.getAllBlockedApps().associateBy { it.packageName }

            // Use launcher intent so we get all apps visible in the launcher,
            // including pre-installed ones (Google, Samsung, etc.).
            val launcherIntent = android.content.Intent(android.content.Intent.ACTION_MAIN).apply {
                addCategory(android.content.Intent.CATEGORY_LAUNCHER)
            }
            val list = pm.queryIntentActivities(launcherIntent, 0)
                .mapNotNull { resolveInfo ->
                    val pkg = resolveInfo.activityInfo.packageName
                    if (pkg == "com.blockingapp") return@mapNotNull null
                    runCatching {
                        val entry = blockedApps[pkg]
                        val mode = entry?.mode
                        AppInfo(
                            name = resolveInfo.loadLabel(pm).toString(),
                            packageName = pkg,
                            icon = resolveInfo.loadIcon(pm),
                            // "Blocked" = full app block only; a section block is shown separately.
                            isBlocked = mode == BlockedApp.MODE_BLOCK,
                            isPendingRemoval = entry?.pendingRemoval == true,
                            blockedFeature = if (mode == BlockedApp.MODE_FEATURE) {
                                FeatureBlocks.forPackage(pkg)?.name ?: "Section"
                            } else null
                        )
                    }.getOrNull()
                }
                .distinctBy { it.packageName }
                .sortedBy { it.name.lowercase() }

            _apps.value = list
        }
    }

    fun toggleBlock(packageName: String) {
        viewModelScope.launch {
            repository.toggleBlock(packageName)
            loadApps(getApplication<Application>().packageManager)
        }
    }

    fun addApp(packageName: String, mode: String) {
        viewModelScope.launch {
            repository.addApp(packageName, mode)
            loadApps(getApplication<Application>().packageManager)
        }
    }
}
