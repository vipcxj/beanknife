package io.github.vipcxj.beanknfie.jpa.examples.dto;

import io.github.vipcxj.beanknife.jpa.runtime.annotations.AddJpaSupport;
import io.github.vipcxj.beanknife.runtime.annotations.ViewGenNameMapper;
import io.github.vipcxj.beanknife.runtime.annotations.ViewPropertiesIncludePattern;

@ViewPropertiesIncludePattern(".*")
@ViewGenNameMapper("${name}Info")
@AddJpaSupport
public class BaseDtoConfiguration {
}
