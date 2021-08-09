package tw.com.fcb.mimosa.dddstructure.sharedkernal.service;

/** @author Matt Ho */
public interface EventStoreRepository {
  Event save(Event event);
}
