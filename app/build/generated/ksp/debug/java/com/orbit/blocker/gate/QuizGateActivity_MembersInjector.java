package com.orbit.blocker.gate;

import com.orbit.blocker.data.repository.AccessGrantRepository;
import com.orbit.blocker.data.settings.OrbitSettings;
import com.orbit.blocker.domain.gamification.GamificationEvents;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class QuizGateActivity_MembersInjector implements MembersInjector<QuizGateActivity> {
  private final Provider<AccessGrantRepository> accessGrantRepositoryProvider;

  private final Provider<OrbitSettings> settingsProvider;

  private final Provider<GamificationEvents> gamificationEventsProvider;

  public QuizGateActivity_MembersInjector(
      Provider<AccessGrantRepository> accessGrantRepositoryProvider,
      Provider<OrbitSettings> settingsProvider,
      Provider<GamificationEvents> gamificationEventsProvider) {
    this.accessGrantRepositoryProvider = accessGrantRepositoryProvider;
    this.settingsProvider = settingsProvider;
    this.gamificationEventsProvider = gamificationEventsProvider;
  }

  public static MembersInjector<QuizGateActivity> create(
      Provider<AccessGrantRepository> accessGrantRepositoryProvider,
      Provider<OrbitSettings> settingsProvider,
      Provider<GamificationEvents> gamificationEventsProvider) {
    return new QuizGateActivity_MembersInjector(accessGrantRepositoryProvider, settingsProvider, gamificationEventsProvider);
  }

  @Override
  public void injectMembers(QuizGateActivity instance) {
    injectAccessGrantRepository(instance, accessGrantRepositoryProvider.get());
    injectSettings(instance, settingsProvider.get());
    injectGamificationEvents(instance, gamificationEventsProvider.get());
  }

  @InjectedFieldSignature("com.orbit.blocker.gate.QuizGateActivity.accessGrantRepository")
  public static void injectAccessGrantRepository(QuizGateActivity instance,
      AccessGrantRepository accessGrantRepository) {
    instance.accessGrantRepository = accessGrantRepository;
  }

  @InjectedFieldSignature("com.orbit.blocker.gate.QuizGateActivity.settings")
  public static void injectSettings(QuizGateActivity instance, OrbitSettings settings) {
    instance.settings = settings;
  }

  @InjectedFieldSignature("com.orbit.blocker.gate.QuizGateActivity.gamificationEvents")
  public static void injectGamificationEvents(QuizGateActivity instance,
      GamificationEvents gamificationEvents) {
    instance.gamificationEvents = gamificationEvents;
  }
}
