package com.orbit.blocker.ui;

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
public final class RootViewModel_Factory implements Factory<RootViewModel> {
  private final Provider<OrbitSettings> settingsProvider;

  public RootViewModel_Factory(Provider<OrbitSettings> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public RootViewModel get() {
    return newInstance(settingsProvider.get());
  }

  public static RootViewModel_Factory create(Provider<OrbitSettings> settingsProvider) {
    return new RootViewModel_Factory(settingsProvider);
  }

  public static RootViewModel newInstance(OrbitSettings settings) {
    return new RootViewModel(settings);
  }
}
