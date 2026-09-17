package com.orbit.blocker.ui.digest;

import com.orbit.blocker.data.repository.NotificationRepository;
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
public final class DigestViewModel_Factory implements Factory<DigestViewModel> {
  private final Provider<NotificationRepository> repositoryProvider;

  public DigestViewModel_Factory(Provider<NotificationRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public DigestViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static DigestViewModel_Factory create(
      Provider<NotificationRepository> repositoryProvider) {
    return new DigestViewModel_Factory(repositoryProvider);
  }

  public static DigestViewModel newInstance(NotificationRepository repository) {
    return new DigestViewModel(repository);
  }
}
