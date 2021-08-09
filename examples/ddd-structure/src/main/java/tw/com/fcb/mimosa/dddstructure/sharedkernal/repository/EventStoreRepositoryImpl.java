package tw.com.fcb.mimosa.dddstructure.sharedkernal.repository;

import org.springframework.data.repository.CrudRepository;
import tw.com.fcb.mimosa.dddstructure.sharedkernal.service.Event;
import tw.com.fcb.mimosa.dddstructure.sharedkernal.service.EventStoreRepository;

/** @author Matt Ho */
public interface EventStoreRepositoryImpl
    extends EventStoreRepository, CrudRepository<Event, Long> {
}
