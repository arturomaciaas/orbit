package com.orbit.blocker.di;

import android.content.Context;
import com.orbit.blocker.data.settings.OrbitSettings;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class RepositoryModule_Companion_ProvideSettingsFactory implements Factory<OrbitSettings> {
  private final Provider<Context> contextProvider;

  public RepositoryModule_Companion_ProvideSettingsFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public OrbitSettings get() {
    return provideSettings(contextProvider.get());
  }

  public static RepositoryModule_Companion_ProvideSettingsFactory create(
      Provider<Context> contextProvider) {
    return new RepositoryModule_Companion_ProvideSettingsFactory(contextProvider);
  }

  public static OrbitSettings provideSettings(Context context) {
    return Preconditions.checkNotNullFromProvides(RepositoryModule.Companion.provideSettings(context));
  }
}
