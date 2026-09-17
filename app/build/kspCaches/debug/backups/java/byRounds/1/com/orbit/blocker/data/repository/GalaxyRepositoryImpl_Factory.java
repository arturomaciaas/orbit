package com.orbit.blocker.data.repository;

import com.orbit.blocker.data.db.GalaxyProgressDao;
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
public final class GalaxyRepositoryImpl_Factory implements Factory<GalaxyRepositoryImpl> {
  private final Provider<GalaxyProgressDao> daoProvider;

  public GalaxyRepositoryImpl_Factory(Provider<GalaxyProgressDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public GalaxyRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static GalaxyRepositoryImpl_Factory create(Provider<GalaxyProgressDao> daoProvider) {
    return new GalaxyRepositoryImpl_Factory(daoProvider);
  }

  public static GalaxyRepositoryImpl newInstance(GalaxyProgressDao dao) {
    return new GalaxyRepositoryImpl(dao);
  }
}
