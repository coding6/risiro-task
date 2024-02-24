    package com.risirotask.handler;

    import lombok.Getter;
    import lombok.Setter;
    import org.springframework.beans.BeansException;
    import org.springframework.beans.factory.config.BeanDefinition;
    import org.springframework.beans.factory.support.BeanDefinitionRegistry;
    import org.springframework.beans.factory.support.BeanNameGenerator;
    import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
    import org.springframework.context.ApplicationContext;
    import org.springframework.context.ApplicationContextAware;
    import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
    import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
    import org.springframework.core.type.AnnotationMetadata;
    import org.springframework.core.type.filter.AssignableTypeFilter;

    import java.util.Set;

    @Getter
    @Setter
    public class TaskHandlerRegistrar implements ImportBeanDefinitionRegistrar, ApplicationContextAware {

        private ApplicationContext applicationContext;

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }

        @Override
        public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
            ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
            provider.addIncludeFilter(new AssignableTypeFilter(TaskRunnableHandler.class));

            // 可以通过metadata获取用户使用@AutoRisiroTask注解的类的信息，比如包路径，从而缩小扫描范围。
            // 扫描注册符合条件的类
            Set<BeanDefinition> components = provider.findCandidateComponents("org.example");
            BeanNameGenerator beanNameGenerator = new DefaultBeanNameGenerator();

            for (BeanDefinition component : components) {
                String beanName = beanNameGenerator.generateBeanName(component, registry);
                registry.registerBeanDefinition(beanName, component);
            }
        }
    }
