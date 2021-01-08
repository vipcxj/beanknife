package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.ViewGenNameMapper;
import io.github.vipcxj.beanknife.runtime.annotations.ViewPropertiesIncludePattern;

@ViewGenNameMapper("ViewOf${name}")
@ViewPropertiesIncludePattern(".*")
public class Parent1ViewConfigure extends GrandparentViewConfigure {
}
