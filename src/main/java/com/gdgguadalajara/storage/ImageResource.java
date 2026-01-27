package com.gdgguadalajara.storage;

import java.util.UUID;

import com.gdgguadalajara.storage.model.Image;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/api/storage/images")
public class ImageResource {

    @GET
    @Path("/{uuid}")
    public Response read(UUID uuid) {
        var image = Image.<Image>findById(uuid);
        if (image == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(image.data)
                .header("Content-Type", image.contentType)
                .header("Cache-Control", "public, max-age=86400, immutable")
                .build();
    }
}
