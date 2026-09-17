package com.orbit.blocker.data.repository;

import com.orbit.blocker.data.db.BlockRuleDao;
import com.orbit.blocker.data.db.BlockedAppDao;
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
public final class BlockRepositoryImpl_Factory implements Factory<BlockRepositoryImpl> {
  private final Provider<BlockedAppDao> blockedAppDaoProvider;

  private final Provider<BlockRuleDao> blockRuleDaoProvider;

  public BlockRepositoryImpl_Factory(Provider<BlockedAppDao> blockedAppDaoProvider,
      Provider<BlockRuleDao> blockRuleDaoProvider) {
    this.blockedAppDaoProvider = blockedAppDaoProvider;
    this.blockRuleDaoProvider = blockRuleDaoProvider;
  }

  @Override
  public BlockRepositoryImpl get() {
    return newInstance(blockedAppDaoProvider.get(), blockRuleDaoProvider.get());
  }

  public static BlockRepositoryImpl_Factory create(Provider<BlockedAppDao> blockedAppDaoProvider,
      Provider<BlockRuleDao> blockRuleDaoProvider) {
    return new BlockRepositoryImpl_Factory(blockedAppDaoProvider, blockRuleDaoProvider);
  }

  public static BlockRepositoryImpl newInstance(BlockedAppDao blockedAppDao,
      BlockRuleDao blockRuleDao) {
    return new BlockRepositoryImpl(blockedAppDao, blockRuleDao);
  }
}
