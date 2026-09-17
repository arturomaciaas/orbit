package com.orbit.blocker.data.repository;

import com.orbit.blocker.data.db.NotificationRecordDao;
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
public final class NotificationRepositoryImpl_Factory implements Factory<NotificationRepositoryImpl> {
  private final Provider<NotificationRecordDao> daoProvider;

  public NotificationRepositoryImpl_Factory(Provider<NotificationRecordDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public NotificationRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static NotificationRepositoryImpl_Factory create(
      Provider<NotificationRecordDao> daoProvider) {
    return new NotificationRepositoryImpl_Factory(daoProvider);
  }

  public static NotificationRepositoryImpl newInstance(NotificationRecordDao dao) {
    return new NotificationRepositoryImpl(dao);
  }
}
