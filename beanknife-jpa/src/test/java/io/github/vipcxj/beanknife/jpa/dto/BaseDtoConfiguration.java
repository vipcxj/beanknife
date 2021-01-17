package io.github.vipcxj.beanknife.jpa.dto;

import io.github.vipcxj.beanknife.runtime.annotations.ViewGenNameMapper;
import io.github.vipcxj.beanknife.runtime.annotations.ViewPropertiesIncludePattern;

@ViewPropertiesIncludePattern(".*")
@ViewGenNameMapper("${name}Info")
public class BaseDtoConfiguration {
}
