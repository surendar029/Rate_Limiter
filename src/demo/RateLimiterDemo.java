package demo;

import limiters.RateLimiter;
import model.RateLimitResult;
import service.RateLimiterService;

public class RateLimiterDemo{
    public static void demoSequential(RateLimiterService service,String profile,String userID,int reqCount){
        System.out.printf("%n┌──────────────────────────────────────────────────────────────┐%n");
        System.out.printf("│  Algorithm : %-48s│%n", service.getAlgorithmName(profile));
        System.out.printf("│  Profile   : %-48s│%n", profile);
        System.out.printf("│  User      : %-48s│%n", userID);
        System.out.printf("└──────────────────────────────────────────────────────────────┘%n");
        for (int i = 1; i <= reqCount; i++) {
            RateLimitResult result=service.allowRequest(profile,userID);
            System.out.printf("  Request %2d : %s%n", i, result);
        }
    }
}
