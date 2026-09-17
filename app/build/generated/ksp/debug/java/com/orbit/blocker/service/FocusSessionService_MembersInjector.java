package com.orbit.blocker.service;

import com.orbit.blocker.domain.focus.FocusController;
import com.orbit.blocker.domain.focus.FocusSessionManager;
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
public final class FocusSessionService_MembersInjector implements MembersInjector<FocusSessionService> {
  private final Provider<FocusController> focusControllerProvider;

  private final Provider<FocusSessionManager> focusSessionManagerProvider;

  public FocusSessionService_MembersInjector(Provider<FocusController> focusControllerProvider,
      Provider<FocusSessionManager> focusSessionManagerProvider) {
    this.focusControllerProvider = focusControllerProvider;
    this.focusSessionManagerProvider = focusSessionManagerProvider;
  }

  public static MembersInjector<FocusSessionService> create(
      Provider<FocusController> focusControllerProvider,
      Provider<FocusSessionManager> focusSessionManagerProvider) {
    return new FocusSessionService_MembersInjector(focusControllerProvider, focusSessionManagerProvider);
  }

  @Override
  public void injectMembers(FocusSessionService instance) {
    injectFocusController(instance, focusControllerProvider.get());
    injectFocusSessionManager(instance, focusSessionManagerProvider.get());
  }

  @InjectedFieldSignature("com.orbit.blocker.service.FocusSessionService.focusController")
  public static void injectFocusController(FocusSessionService instance,
      FocusController focusController) {
    instance.focusController = focusController;
  }

  @InjectedFieldSignature("com.orbit.blocker.service.FocusSessionService.focusSessionManager")
  public static void injectFocusSessionManager(FocusSessionService instance,
      FocusSessionManager focusSessionManager) {
    instance.focusSessionManager = focusSessionManager;
  }
}
