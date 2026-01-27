package com.gdgguadalajara.storage.application;

import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import com.gdgguadalajara.common.model.DomainException;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ImageValidator {

    public void run(byte[] imageBytes) {
        try {
            var img = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if (img.getWidth() > 500 || img.getHeight() > 500)
                throw DomainException.badRequest("La imagen excede las dimensiones máximas de 500x500 píxeles");
            if (img.getWidth() != img.getHeight())
                throw DomainException.badRequest("La imagen debe ser cuadrada (proporción 1:1)");
        } catch (Exception e) {
            if (e instanceof DomainException)
                throw (DomainException) e;
            throw DomainException.badRequest("Imagen inválida");
        }
    }
}
