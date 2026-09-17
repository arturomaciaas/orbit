package com.orbit.blocker.di;

import com.orbit.blocker.data.db.AccessGrantDao;
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
public final class DatabaseModule_ProvideAccessGrantDaoFactory implements Factory<AccessGrantDao> {
  private final Provider<OrbitDatabase> dbProvider;

  public DatabaseModule_ProvideAccessGrantDaoFactory(Provider<OrbitDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public AccessGrantDao get() {
    return provideAccessGrantDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideAccessGrantDaoFactory create(
      Provider<OrbitDatabase> dbProvider) {
    return new DatabaseModule_ProvideAccessGrantDaoFactory(dbProvider);
  }

  public static AccessGrantDao provideAccessGrantDao(OrbitDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideAccessGrantDao(db));
  }
}
