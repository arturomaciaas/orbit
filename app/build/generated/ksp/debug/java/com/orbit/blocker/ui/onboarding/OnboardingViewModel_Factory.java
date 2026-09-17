package com.orbit.blocker.ui.onboarding;

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
public final class OnboardingViewModel_Factory implements Factory<OnboardingViewModel> {
  private final Provider<OrbitSettings> settingsProvider;

  public OnboardingViewModel_Factory(Provider<OrbitSettings> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public OnboardingViewModel get() {
    return newInstance(settingsProvider.get());
  }

  public static OnboardingViewModel_Factory create(Provider<OrbitSettings> settingsProvider) {
    return new OnboardingViewModel_Factory(settingsProvider);
  }

  public static OnboardingViewModel newInstance(OrbitSettings settings) {
    return new OnboardingViewModel(settings);
  }
}
