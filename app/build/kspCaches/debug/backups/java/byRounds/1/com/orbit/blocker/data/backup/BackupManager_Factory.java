package com.orbit.blocker.data.backup;

import android.content.Context;
import com.orbit.blocker.data.db.OrbitDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class BackupManager_Factory implements Factory<BackupManager> {
  private final Provider<Context> contextProvider;

  private final Provider<OrbitDatabase> dbProvider;

  public BackupManager_Factory(Provider<Context> contextProvider,
      Provider<OrbitDatabase> dbProvider) {
    this.contextProvider = contextProvider;
    this.dbProvider = dbProvider;
  }

  @Override
  public BackupManager get() {
    return newInstance(contextProvider.get(), dbProvider.get());
  }

  public static BackupManager_Factory create(Provider<Context> contextProvider,
      Provider<OrbitDatabase> dbProvider) {
    return new BackupManager_Factory(contextProvider, dbProvider);
  }

  public static BackupManager newInstance(Context context, OrbitDatabase db) {
    return new BackupManager(context, db);
  }
}
