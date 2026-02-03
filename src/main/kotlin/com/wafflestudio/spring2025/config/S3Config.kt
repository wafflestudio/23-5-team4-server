package com.wafflestudio.spring2025.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.presigner.S3Presigner

@Configuration
class S3Config {
    @Bean
    fun s3Client(props: AwsS3Properties): S3Client =
        S3Client
            .builder()
            .region(Region.of(props.region))
            .build()

    @Bean
    fun s3Presigner(props: AwsS3Properties): S3Presigner =
        S3Presigner
            .builder()
            .region(Region.of(props.region))
            .build()
}
