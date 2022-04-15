// application related bean definitions such as model mappers
package com.swivel.qpon.fileuploader.configurations;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileServiceConfigurations {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
