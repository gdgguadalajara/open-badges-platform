package com.gdgguadalajara.storage.application;

import com.gdgguadalajara.assertion.model.Assertion;

import jakarta.enterprise.context.ApplicationScoped;
import javax.imageio.*;
import javax.imageio.metadata.*;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;

@ApplicationScoped
public class BakeImage {

    public byte[] bakePng(byte[] originalImage, Assertion assertion) throws Exception {
        InputStream is = new ByteArrayInputStream(originalImage);
        ImageReader reader = ImageIO.getImageReadersByFormatName("png").next();
        reader.setInput(ImageIO.createImageInputStream(is));

        BufferedImage image = reader.read(0);
        IIOMetadata metadata = reader.getImageMetadata(0);
        String formatName = "javax_imageio_png_1.0";

        IIOMetadataNode textNode = new IIOMetadataNode("tEXt");

        textNode.appendChild(createTextEntry("openbadges", assertion.jsonPayload));
        textNode.appendChild(createTextEntry("Description", "Badge: " + assertion.badgeClass.name));
        textNode.appendChild(createTextEntry("Comment", "Insignia oficial de " + assertion.badgeClass.issuer.name));

        IIOMetadataNode root = new IIOMetadataNode(formatName);
        root.appendChild(textNode);
        metadata.mergeTree(formatName, root);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(os)) {
            writer.setOutput(ios);
            IIOImage iioImage = new IIOImage(image, null, metadata);
            writer.write(null, iioImage, null);
        } finally {
            writer.dispose();
            reader.dispose();
        }

        return os.toByteArray();
    }

    private IIOMetadataNode createTextEntry(String keyword, String value) {
        IIOMetadataNode entry = new IIOMetadataNode("tEXtEntry");
        entry.setAttribute("keyword", keyword);
        entry.setAttribute("value", value);
        return entry;
    }
}
