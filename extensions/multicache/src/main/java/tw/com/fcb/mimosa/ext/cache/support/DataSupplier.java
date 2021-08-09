package tw.com.fcb.mimosa.ext.cache.support;

/**
 * @author Steven Wang <steven.wang@softleader.com.tw>
 * @since 1.0.0
 */
@FunctionalInterface
public interface DataSupplier<T> {

  T create();
}
