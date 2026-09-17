package com.orbit.blocker.ui.settings;

import com.orbit.blocker.data.backup.BackupManager;
import com.orbit.blocker.data.settings.OrbitSettings;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<OrbitSettings> settingsProvider;

  private final Provider<BackupManager> backupManagerProvider;

  public SettingsViewModel_Factory(Provider<OrbitSettings> settingsProvider,
      Provider<BackupManager> backupManagerProvider) {
    this.settingsProvider = settingsProvider;
    this.backupManagerProvider = backupManagerProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(settingsProvider.get(), backupManagerProvider.get());
  }

  public static SettingsViewModel_Factory create(Provider<OrbitSettings> settingsProvider,
      Provider<BackupManager> backupManagerProvider) {
    return new SettingsViewModel_Factory(settingsProvider, backupManagerProvider);
  }

  public static SettingsViewModel newInstance(OrbitSettings settings, BackupManager backupManager) {
    return new SettingsViewModel(settings, backupManager);
  }
}
