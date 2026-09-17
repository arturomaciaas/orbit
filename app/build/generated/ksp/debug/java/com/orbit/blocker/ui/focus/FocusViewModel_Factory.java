package com.orbit.blocker.ui.focus;

import com.orbit.blocker.data.repository.BlockRepository;
import com.orbit.blocker.domain.focus.FocusSessionManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class FocusViewModel_Factory implements Factory<FocusViewModel> {
  private final Provider<BlockRepository> blockRepositoryProvider;

  private final Provider<FocusSessionManager> focusSessionManagerProvider;

  public FocusViewModel_Factory(Provider<BlockRepository> blockRepositoryProvider,
      Provider<FocusSessionManager> focusSessionManagerProvider) {
    this.blockRepositoryProvider = blockRepositoryProvider;
    this.focusSessionManagerProvider = focusSessionManagerProvider;
  }

  @Override
  public FocusViewModel get() {
    return newInstance(blockRepositoryProvider.get(), focusSessionManagerProvider.get());
  }

  public static FocusViewModel_Factory create(Provider<BlockRepository> blockRepositoryProvider,
      Provider<FocusSessionManager> focusSessionManagerProvider) {
    return new FocusViewModel_Factory(blockRepositoryProvider, focusSessionManagerProvider);
  }

  public static FocusViewModel newInstance(BlockRepository blockRepository,
      FocusSessionManager focusSessionManager) {
    return new FocusViewModel(blockRepository, focusSessionManager);
  }
}
