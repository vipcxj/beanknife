package io.github.vipcxj.beanknife.demo;

@TestAnnotation
public class WrongClass implements AInterface<InvalidClass1, InvalidClass2> {

    public InvalidClass3<? extends InvalidClass4> test() {
        return null;
    }
}