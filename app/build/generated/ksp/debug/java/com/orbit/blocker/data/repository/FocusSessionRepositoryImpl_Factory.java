package com.orbit.blocker.data.repository;

import com.orbit.blocker.data.db.FocusSessionDao;
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
public final class FocusSessionRepositoryImpl_Factory implements Factory<FocusSessionRepositoryImpl> {
  private final Provider<FocusSessionDao> daoProvider;

  public FocusSessionRepositoryImpl_Factory(Provider<FocusSessionDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public FocusSessionRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static FocusSessionRepositoryImpl_Factory create(Provider<FocusSessionDao> daoProvider) {
    return new FocusSessionRepositoryImpl_Factory(daoProvider);
  }

  public static FocusSessionRepositoryImpl newInstance(FocusSessionDao dao) {
    return new FocusSessionRepositoryImpl(dao);
  }
}
