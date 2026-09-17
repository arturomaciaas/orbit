package com.orbit.blocker.domain.focus;

import com.orbit.blocker.data.repository.FocusSessionRepository;
import com.orbit.blocker.domain.gamification.GamificationEvents;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class FocusController_Factory implements Factory<FocusController> {
  private final Provider<FocusSessionManager> focusSessionManagerProvider;

  private final Provider<FocusSessionRepository> focusSessionRepositoryProvider;

  private final Provider<GamificationEvents> gamificationEventsProvider;

  public FocusController_Factory(Provider<FocusSessionManager> focusSessionManagerProvider,
      Provider<FocusSessionRepository> focusSessionRepositoryProvider,
      Provider<GamificationEvents> gamificationEventsProvider) {
    this.focusSessionManagerProvider = focusSessionManagerProvider;
    this.focusSessionRepositoryProvider = focusSessionRepositoryProvider;
    this.gamificationEventsProvider = gamificationEventsProvider;
  }

  @Override
  public FocusController get() {
    return newInstance(focusSessionManagerProvider.get(), focusSessionRepositoryProvider.get(), gamificationEventsProvider.get());
  }

  public static FocusController_Factory create(
      Provider<FocusSessionManager> focusSessionManagerProvider,
      Provider<FocusSessionRepository> focusSessionRepositoryProvider,
      Provider<GamificationEvents> gamificationEventsProvider) {
    return new FocusController_Factory(focusSessionManagerProvider, focusSessionRepositoryProvider, gamificationEventsProvider);
  }

  public static FocusController newInstance(FocusSessionManager focusSessionManager,
      FocusSessionRepository focusSessionRepository, GamificationEvents gamificationEvents) {
    return new FocusController(focusSessionManager, focusSessionRepository, gamificationEvents);
  }
}
