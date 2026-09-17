package com.orbit.blocker;

import androidx.hilt.work.HiltWorkerFactory;
import com.orbit.blocker.data.repository.GalaxyRepository;
import com.orbit.blocker.data.seed.QuestionSeeder;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class OrbitApplication_MembersInjector implements MembersInjector<OrbitApplication> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  private final Provider<QuestionSeeder> questionSeederProvider;

  private final Provider<GalaxyRepository> galaxyRepositoryProvider;

  public OrbitApplication_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider,
      Provider<QuestionSeeder> questionSeederProvider,
      Provider<GalaxyRepository> galaxyRepositoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
    this.questionSeederProvider = questionSeederProvider;
    this.galaxyRepositoryProvider = galaxyRepositoryProvider;
  }

  public static MembersInjector<OrbitApplication> create(
      Provider<HiltWorkerFactory> workerFactoryProvider,
      Provider<QuestionSeeder> questionSeederProvider,
      Provider<GalaxyRepository> galaxyRepositoryProvider) {
    return new OrbitApplication_MembersInjector(workerFactoryProvider, questionSeederProvider, galaxyRepositoryProvider);
  }

  @Override
  public void injectMembers(OrbitApplication instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
    injectQuestionSeeder(instance, questionSeederProvider.get());
    injectGalaxyRepository(instance, galaxyRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.orbit.blocker.OrbitApplication.workerFactory")
  public static void injectWorkerFactory(OrbitApplication instance,
      HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }

  @InjectedFieldSignature("com.orbit.blocker.OrbitApplication.questionSeeder")
  public static void injectQuestionSeeder(OrbitApplication instance,
      QuestionSeeder questionSeeder) {
    instance.questionSeeder = questionSeeder;
  }

  @InjectedFieldSignature("com.orbit.blocker.OrbitApplication.galaxyRepository")
  public static void injectGalaxyRepository(OrbitApplication instance,
      GalaxyRepository galaxyRepository) {
    instance.galaxyRepository = galaxyRepository;
  }
}
