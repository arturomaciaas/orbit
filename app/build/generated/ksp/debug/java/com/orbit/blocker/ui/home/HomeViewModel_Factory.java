package com.orbit.blocker.ui.home;

import com.orbit.blocker.data.repository.GalaxyRepository;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<GalaxyRepository> galaxyRepositoryProvider;

  public HomeViewModel_Factory(Provider<GalaxyRepository> galaxyRepositoryProvider) {
    this.galaxyRepositoryProvider = galaxyRepositoryProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(galaxyRepositoryProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<GalaxyRepository> galaxyRepositoryProvider) {
    return new HomeViewModel_Factory(galaxyRepositoryProvider);
  }

  public static HomeViewModel newInstance(GalaxyRepository galaxyRepository) {
    return new HomeViewModel(galaxyRepository);
  }
}
