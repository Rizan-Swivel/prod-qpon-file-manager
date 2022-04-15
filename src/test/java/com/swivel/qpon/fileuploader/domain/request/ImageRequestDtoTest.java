package com.swivel.qpon.fileuploader.domain.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This class test the {@link ImageRequestDto} class
 */
class ImageRequestDtoTest {

    @Test
    void Should_ReturnTrue_When_ImageUrlIsAvailable() {
        ImageRequestDto imageRequestDto = new ImageRequestDto("imageUrl");
        assertTrue(imageRequestDto.isRequiredAvailable());
    }

    @Test
    void Should_ReturnFalse_When_ImageUrlIsNull() {
        ImageRequestDto imageRequestDto = new ImageRequestDto();
        assertFalse(imageRequestDto.isRequiredAvailable());
    }

    @Test
    void Should_ReturnFalse_When_ImageUrlIsEmpty() {
        ImageRequestDto imageRequestDto = new ImageRequestDto(" ");
        assertFalse(imageRequestDto.isRequiredAvailable());
    }
}