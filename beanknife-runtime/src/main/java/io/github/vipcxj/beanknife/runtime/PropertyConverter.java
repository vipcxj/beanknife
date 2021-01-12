package io.github.vipcxj.beanknife.runtime;

/**
 * All type converter should implement this interface.
 * And the implementation should instantiate the generic parameters.<br/>
 * Such as:<br/><br/>
 * <pre>
 * public class MyConverter implements PropertyConverter&lt;Integer, String&gt; {
 *     String convert(Integer from) {
 *         ...
 *     }
 * }
 * </pre>
 * In other words, the implementation shouldn't has generic parameters.
 * Or the library can't detect what <code>FromType<code/> and <code>ToType<code/> really are.
 * @param <FromType> from type
 * @param <ToType> to type
 */
public interface PropertyConverter<FromType, ToType> {
    ToType convert(FromType value);
    FromType convertBack(ToType value);
}
