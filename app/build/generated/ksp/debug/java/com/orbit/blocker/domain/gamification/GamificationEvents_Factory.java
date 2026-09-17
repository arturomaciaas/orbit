package com.orbit.blocker.domain.gamification;

import com.orbit.blocker.data.repository.GalaxyRepository;
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
public final class GamificationEvents_Factory implements Factory<GamificationEvents> {
  private final Provider<GalaxyRepository> galaxyRepositoryProvider;

  public GamificationEvents_Factory(Provider<GalaxyRepository> galaxyRepositoryProvider) {
    this.galaxyRepositoryProvider = galaxyRepositoryProvider;
  }

  @Override
  public GamificationEvents get() {
    return newInstance(galaxyRepositoryProvider.get());
  }

  public static GamificationEvents_Factory create(
      Provider<GalaxyRepository> galaxyRepositoryProvider) {
    return new GamificationEvents_Factory(galaxyRepositoryProvider);
  }

  public static GamificationEvents newInstance(GalaxyRepository galaxyRepository) {
    return new GamificationEvents(galaxyRepository);
  }
}
