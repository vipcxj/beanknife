package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.*;

@ViewSerializable(true)
@ViewReadConstructor(Access.NONE)
@ViewSetters(Access.PROTECTED)
@ViewGenNameMapper("${name}Dto")
@ViewPropertiesExclude("a")
public class GrandparentViewConfigure {
}
