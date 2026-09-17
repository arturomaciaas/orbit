package com.orbit.blocker.ui.blocks;

import com.orbit.blocker.data.apps.InstalledAppsProvider;
import com.orbit.blocker.data.repository.BlockRepository;
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
public final class BlocksViewModel_Factory implements Factory<BlocksViewModel> {
  private final Provider<BlockRepository> blockRepositoryProvider;

  private final Provider<InstalledAppsProvider> installedAppsProvider;

  public BlocksViewModel_Factory(Provider<BlockRepository> blockRepositoryProvider,
      Provider<InstalledAppsProvider> installedAppsProvider) {
    this.blockRepositoryProvider = blockRepositoryProvider;
    this.installedAppsProvider = installedAppsProvider;
  }

  @Override
  public BlocksViewModel get() {
    return newInstance(blockRepositoryProvider.get(), installedAppsProvider.get());
  }

  public static BlocksViewModel_Factory create(Provider<BlockRepository> blockRepositoryProvider,
      Provider<InstalledAppsProvider> installedAppsProvider) {
    return new BlocksViewModel_Factory(blockRepositoryProvider, installedAppsProvider);
  }

  public static BlocksViewModel newInstance(BlockRepository blockRepository,
      InstalledAppsProvider installedAppsProvider) {
    return new BlocksViewModel(blockRepository, installedAppsProvider);
  }
}
