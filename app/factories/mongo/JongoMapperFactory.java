package factories.mongo;

import org.jongo.Mapper;
import org.jongo.marshall.jackson.JacksonMapper;

public interface JongoMapperFactory {

    /**
     * Create the {@link Mapper} that shall be used by jongo.
     */
    Mapper create();

    static class DefaultFactory implements JongoMapperFactory {
        @Override
        public Mapper create() {
            return new JacksonMapper.Builder().build();
        }
    }

}
