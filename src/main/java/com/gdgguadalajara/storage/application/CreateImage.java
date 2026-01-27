package com.gdgguadalajara.storage.application;

import java.util.Base64;

import com.gdgguadalajara.storage.model.Image;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class CreateImage {

    private final ImageValidator imageValidator;

    public Image fromBase64(String base64) {
        var parts = base64.split(",");
        var contentType = parts[0].split(":")[1].split(";")[0];
        var bytes = Base64.getDecoder().decode(parts[1]);
        return run(bytes, contentType);
    }

    @Transactional
    public Image run(byte[] bytes, String contentType) {
        imageValidator.run(bytes);
        Image image = new Image();
        image.data = bytes;
        image.contentType = contentType;
        image.persistAndFlush();
        return image;
    }
}
