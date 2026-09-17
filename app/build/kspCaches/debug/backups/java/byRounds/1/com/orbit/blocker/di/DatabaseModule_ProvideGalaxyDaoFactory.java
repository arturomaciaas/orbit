package com.orbit.blocker.di;

import com.orbit.blocker.data.db.GalaxyProgressDao;
import com.orbit.blocker.data.db.OrbitDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideGalaxyDaoFactory implements Factory<GalaxyProgressDao> {
  private final Provider<OrbitDatabase> dbProvider;

  public DatabaseModule_ProvideGalaxyDaoFactory(Provider<OrbitDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public GalaxyProgressDao get() {
    return provideGalaxyDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideGalaxyDaoFactory create(Provider<OrbitDatabase> dbProvider) {
    return new DatabaseModule_ProvideGalaxyDaoFactory(dbProvider);
  }

  public static GalaxyProgressDao provideGalaxyDao(OrbitDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideGalaxyDao(db));
  }
}
