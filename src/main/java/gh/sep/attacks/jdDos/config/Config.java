package gh.sep.attacks.jdDos.config;

import gh.sep.attacks.jdDos.helper.RandomUserAgent;
import gh.sep.attacks.jdDos.thread.RequestThreadGroupRunner;
import gh.sep.attacks.jdDos.thread.RequestThreadGroupRunnersBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Configuration
public class Config {

    @Bean
    @Scope("singleton")
    public RandomUserAgent randomUserAgent(){
        return new RandomUserAgent();
    }

    @Bean
    @Scope("singleton")
    public RequestThreadGroupRunnersBuilder requestThreadGroupRunnersBuilder(){
        return new RequestThreadGroupRunnersBuilder(randomUserAgent());
    }

    @Bean("threadGroupesReference")
    @Scope("singleton")
    public AtomicReference<List<RequestThreadGroupRunner>> threadGroupesReference(){
        return new AtomicReference<>();
    }
}
