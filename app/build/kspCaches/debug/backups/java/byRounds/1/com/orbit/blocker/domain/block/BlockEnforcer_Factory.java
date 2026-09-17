package com.orbit.blocker.domain.block;

import com.orbit.blocker.data.repository.AccessGrantRepository;
import com.orbit.blocker.data.repository.BlockRepository;
import com.orbit.blocker.domain.focus.FocusSessionManager;
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
public final class BlockEnforcer_Factory implements Factory<BlockEnforcer> {
  private final Provider<BlockRepository> blockRepositoryProvider;

  private final Provider<AccessGrantRepository> accessGrantRepositoryProvider;

  private final Provider<FocusSessionManager> focusSessionManagerProvider;

  public BlockEnforcer_Factory(Provider<BlockRepository> blockRepositoryProvider,
      Provider<AccessGrantRepository> accessGrantRepositoryProvider,
      Provider<FocusSessionManager> focusSessionManagerProvider) {
    this.blockRepositoryProvider = blockRepositoryProvider;
    this.accessGrantRepositoryProvider = accessGrantRepositoryProvider;
    this.focusSessionManagerProvider = focusSessionManagerProvider;
  }

  @Override
  public BlockEnforcer get() {
    return newInstance(blockRepositoryProvider.get(), accessGrantRepositoryProvider.get(), focusSessionManagerProvider.get());
  }

  public static BlockEnforcer_Factory create(Provider<BlockRepository> blockRepositoryProvider,
      Provider<AccessGrantRepository> accessGrantRepositoryProvider,
      Provider<FocusSessionManager> focusSessionManagerProvider) {
    return new BlockEnforcer_Factory(blockRepositoryProvider, accessGrantRepositoryProvider, focusSessionManagerProvider);
  }

  public static BlockEnforcer newInstance(BlockRepository blockRepository,
      AccessGrantRepository accessGrantRepository, FocusSessionManager focusSessionManager) {
    return new BlockEnforcer(blockRepository, accessGrantRepository, focusSessionManager);
  }
}
