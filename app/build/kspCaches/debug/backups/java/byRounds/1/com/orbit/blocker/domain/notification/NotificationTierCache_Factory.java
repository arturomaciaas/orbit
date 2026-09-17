package com.orbit.blocker.domain.notification;

import com.orbit.blocker.data.repository.BlockRepository;
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
public final class NotificationTierCache_Factory implements Factory<NotificationTierCache> {
  private final Provider<BlockRepository> blockRepositoryProvider;

  public NotificationTierCache_Factory(Provider<BlockRepository> blockRepositoryProvider) {
    this.blockRepositoryProvider = blockRepositoryProvider;
  }

  @Override
  public NotificationTierCache get() {
    return newInstance(blockRepositoryProvider.get());
  }

  public static NotificationTierCache_Factory create(
      Provider<BlockRepository> blockRepositoryProvider) {
    return new NotificationTierCache_Factory(blockRepositoryProvider);
  }

  public static NotificationTierCache newInstance(BlockRepository blockRepository) {
    return new NotificationTierCache(blockRepository);
  }
}
