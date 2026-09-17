package com.orbit.blocker.di;

import com.orbit.blocker.data.db.NotificationRecordDao;
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
public final class DatabaseModule_ProvideNotificationDaoFactory implements Factory<NotificationRecordDao> {
  private final Provider<OrbitDatabase> dbProvider;

  public DatabaseModule_ProvideNotificationDaoFactory(Provider<OrbitDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public NotificationRecordDao get() {
    return provideNotificationDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideNotificationDaoFactory create(
      Provider<OrbitDatabase> dbProvider) {
    return new DatabaseModule_ProvideNotificationDaoFactory(dbProvider);
  }

  public static NotificationRecordDao provideNotificationDao(OrbitDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideNotificationDao(db));
  }
}
