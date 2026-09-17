package com.orbit.blocker.di;

import com.orbit.blocker.data.db.FocusSessionDao;
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
public final class DatabaseModule_ProvideFocusSessionDaoFactory implements Factory<FocusSessionDao> {
  private final Provider<OrbitDatabase> dbProvider;

  public DatabaseModule_ProvideFocusSessionDaoFactory(Provider<OrbitDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public FocusSessionDao get() {
    return provideFocusSessionDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideFocusSessionDaoFactory create(
      Provider<OrbitDatabase> dbProvider) {
    return new DatabaseModule_ProvideFocusSessionDaoFactory(dbProvider);
  }

  public static FocusSessionDao provideFocusSessionDao(OrbitDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideFocusSessionDao(db));
  }
}
