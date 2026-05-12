import builder.RateLimiterConfigBuilder;
import config.RateLimiterConfig;
import demo.RateLimiterDemo;
import enums.RateLimiterType;
import service.RateLimiterService;

public class Main {
    public static void main(String[] args) {
        RateLimiterService service=new RateLimiterService();

        service.registerProfile("SLIDING_WINDOW_LOG",
                new RateLimiterConfigBuilder()
                        .type(RateLimiterType.SLIDING_WINDOW_LOG)
                        .capacity(5)
                        .rateOrWindowSeconds(2)
                        .build()
        );

        RateLimiterDemo.demoSequential(service,"SLIDING_WINDOW_LOG","US-2901",8);
    }
}
