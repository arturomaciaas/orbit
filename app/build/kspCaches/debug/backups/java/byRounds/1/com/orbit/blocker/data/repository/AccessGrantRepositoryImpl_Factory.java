package com.orbit.blocker.data.repository;

import com.orbit.blocker.data.db.AccessGrantDao;
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
public final class AccessGrantRepositoryImpl_Factory implements Factory<AccessGrantRepositoryImpl> {
  private final Provider<AccessGrantDao> daoProvider;

  public AccessGrantRepositoryImpl_Factory(Provider<AccessGrantDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public AccessGrantRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static AccessGrantRepositoryImpl_Factory create(Provider<AccessGrantDao> daoProvider) {
    return new AccessGrantRepositoryImpl_Factory(daoProvider);
  }

  public static AccessGrantRepositoryImpl newInstance(AccessGrantDao dao) {
    return new AccessGrantRepositoryImpl(dao);
  }
}
