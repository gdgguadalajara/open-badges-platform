package com.gdgguadalajara.storage.application;

import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import com.gdgguadalajara.common.model.DomainException;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ImageValidator {

    private static final byte[] PNG_SIGNATURE = new byte[] {
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
    };

    public void run(byte[] imageBytes) {
        try {
            var img = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if (img.getWidth() > 500 || img.getHeight() > 500)
                throw DomainException.badRequest("La imagen excede las dimensiones máximas de 500x500 píxeles");
            if (img.getWidth() != img.getHeight())
                throw DomainException.badRequest("La imagen debe ser cuadrada (proporción 1:1)");
            for (int i = 0; i < PNG_SIGNATURE.length; i++)
                if (imageBytes[i] != PNG_SIGNATURE[i])
                    throw DomainException.badRequest("La imagen debe estar en formato PNG");
        } catch (Exception e) {
            if (e instanceof DomainException)
                throw (DomainException) e;
            throw DomainException.badRequest("Imagen inválida");
        }
    }
}
