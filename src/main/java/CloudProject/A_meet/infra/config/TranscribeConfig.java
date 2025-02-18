//package CloudProject.A_meet.infra.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
//import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.transcribe.TranscribeClient;
//
//@Configuration
//public class TranscribeConfig {
//
//    @Value("${cloud.aws.credentials.access-key}")
//    private String accessKey;
//
//    @Value("${cloud.aws.credentials.secret-key}")
//    private String secretKey;
//
//    @Value("${cloud.aws.region.static}")
//    private String region;
//
//    @Bean
//    public TranscribeClient transcribeClient() {
//        return TranscribeClient.builder()
//            .region(Region.of(region))
//            .credentialsProvider(
//                StaticCredentialsProvider.create(
//                    AwsBasicCredentials.create(accessKey, secretKey)
//                )
//            )
//            .build();
//    }
//}
