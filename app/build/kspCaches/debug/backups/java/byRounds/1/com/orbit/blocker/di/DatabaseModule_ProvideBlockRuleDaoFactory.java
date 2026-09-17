package com.orbit.blocker.di;

import com.orbit.blocker.data.db.BlockRuleDao;
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
public final class DatabaseModule_ProvideBlockRuleDaoFactory implements Factory<BlockRuleDao> {
  private final Provider<OrbitDatabase> dbProvider;

  public DatabaseModule_ProvideBlockRuleDaoFactory(Provider<OrbitDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public BlockRuleDao get() {
    return provideBlockRuleDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideBlockRuleDaoFactory create(
      Provider<OrbitDatabase> dbProvider) {
    return new DatabaseModule_ProvideBlockRuleDaoFactory(dbProvider);
  }

  public static BlockRuleDao provideBlockRuleDao(OrbitDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideBlockRuleDao(db));
  }
}
