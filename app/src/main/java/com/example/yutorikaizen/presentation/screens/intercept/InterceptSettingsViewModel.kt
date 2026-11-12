package com.example.yutorikaizen.presentation.screens.intercept

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yutorikaizen.data.local.entity.InterceptConfigEntity
import com.example.yutorikaizen.domain.model.SocialMediaApps
import com.example.yutorikaizen.domain.repository.InterceptConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class InstalledAppItem(
    val packageName: String,
    val label: String,
    val isSocial: Boolean = false
)

data class InterceptSettingsUiState(
    val configs: List<InterceptConfigEntity> = emptyList(),
    val installedApps: List<InstalledAppItem> = emptyList(),
    val searchQuery: String = "",
    val isLoadingApps: Boolean = true,
    /** The package currently being edited in the add/edit bottom sheet. */
    val editingPackage: String? = null,
    val editingLabel: String = "",
    val editingPrompt: String = "",
    val editingDelay: Int = 5,
    val showSheet: Boolean = false
)

@HiltViewModel
class InterceptSettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: InterceptConfigRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InterceptSettingsUiState())
    val uiState: StateFlow<InterceptSettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeAll().collect { configs ->
                _uiState.update { it.copy(configs = configs) }
            }
        }
        loadInstalledApps()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val pm = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = pm.queryIntentActivities(intent, 0)
            val myPackage = context.packageName

            val apps = resolveInfos
                .mapNotNull { resolveInfo ->
                    val pkg = resolveInfo.activityInfo.packageName
                    if (pkg == myPackage) return@mapNotNull null
                    val label = resolveInfo.loadLabel(pm)?.toString() ?: pkg
                    InstalledAppItem(
                        packageName = pkg,
                        label = label,
                        isSocial = SocialMediaApps.isSocialMedia(pkg)
                    )
                }
                .distinctBy { it.packageName }
                .sortedWith(
                    compareByDescending<InstalledAppItem> { it.isSocial }
                        .thenBy { it.label.lowercase() }
                )

            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(installedApps = apps, isLoadingApps = false) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    // ── Sheet management ─────────────────────────────────────────────────────

    fun openAddSheet(packageName: String, appLabel: String) {
        val existing = _uiState.value.configs.find { it.packageName == packageName }
        _uiState.update {
            it.copy(
                editingPackage = packageName,
                editingLabel   = appLabel,
                editingPrompt  = existing?.prompt ?: "",
                editingDelay   = existing?.delaySeconds ?: 5,
                showSheet      = true
            )
        }
    }

    fun openEditSheet(config: InterceptConfigEntity) {
        _uiState.update {
            it.copy(
                editingPackage = config.packageName,
                editingLabel   = config.appLabel,
                editingPrompt  = config.prompt,
                editingDelay   = config.delaySeconds,
                showSheet      = true
            )
        }
    }

    fun dismissSheet() {
        _uiState.update { it.copy(showSheet = false, editingPackage = null) }
    }

    fun onPromptChange(text: String)  { _uiState.update { it.copy(editingPrompt = text) } }
    fun onDelayChange(seconds: Int)   { _uiState.update { it.copy(editingDelay = seconds) } }

    // ── Persistence ──────────────────────────────────────────────────────────

    fun saveConfig() {
        val state = _uiState.value
        val pkg   = state.editingPackage ?: return
        viewModelScope.launch {
            repository.upsert(
                InterceptConfigEntity(
                    packageName   = pkg,
                    appLabel      = state.editingLabel,
                    prompt        = state.editingPrompt.trim().ifEmpty { "Pause for a moment." },
                    enabled       = true,
                    delaySeconds  = state.editingDelay
                )
            )
        }
        dismissSheet()
    }

    fun toggleEnabled(config: InterceptConfigEntity) {
        viewModelScope.launch {
            repository.setEnabled(config.packageName, !config.enabled)
        }
    }

    fun deleteConfig(config: InterceptConfigEntity) {
        viewModelScope.launch {
            repository.delete(config)
        }
    }

    /** Unconfigured installed apps filtered by current search query. */
    val filteredAvailableApps: List<InstalledAppItem>
        get() {
            val configured = _uiState.value.configs.map { it.packageName }.toSet()
            val query = _uiState.value.searchQuery.trim().lowercase()
            return _uiState.value.installedApps
                .filter { it.packageName !in configured }
                .filter {
                    query.isEmpty() ||
                    it.label.lowercase().contains(query) ||
                    it.packageName.lowercase().contains(query)
                }
        }
}
