package com.cryo.cache.loaders;

import com.cryo.Website;
import com.cryo.cache.Cache;
import com.cryo.cache.IndexType;
import com.cryo.cache.io.InputStream;
import com.cryo.cache.loaders.model.material.MaterialDefinitions;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.annotations.Endpoint;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.sections.Overview;
import com.google.common.io.ByteStreams;
import com.google.common.net.MediaType;
import lombok.Cleanup;
import org.apache.commons.lang3.math.NumberUtils;
import spark.Request;
import spark.Response;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Properties;

import static com.cryo.utils.Utilities.error;

@EndpointSubscriber
public class TextureDefinitions {

    public int id;
    public boolean isGroundMesh;
    public boolean isHalfSize;
    public boolean skipTriangles;
    public int brightness;
    public int shadowFactor;
    public int effectId;
    public int effectParam1;
    public int colour;
    public int textureSpeedU;
    public int textureSpeedV;
    public boolean aBool2087;
    public boolean isBrickTile;
    public int useMipmaps;
    public boolean repeatS;
    public boolean repeatT;
    public boolean hdr;
    public int combineMode;
    public int effectParam2;
    public int blendType;
    
    private static TextureDefinitions[] textures;

    private TextureDefinitions(int id) {
        this.id = id;
    }

    public static void parseTextureDefs() {

        byte[] data = Cache.STORE.getIndex(IndexType.TEXTURE_DEFINITIONS).getFile(0, 0);
        InputStream stream = new InputStream(data);
        int textureDefSize = stream.readUnsignedShort();
		textures = new TextureDefinitions[textureDefSize];
		
		for(int i = 0; i < textures.length; i++)
			if(stream.readUnsignedByte() == 1)
				textures[i] = new TextureDefinitions(i);

        for (int i = 0; i < textures.length; i++) {
            if (textures[i] != null)
                textures[i].isGroundMesh = stream.readUnsignedByte() == 0;
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].isHalfSize = stream.readUnsignedByte() == 1;
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].skipTriangles = stream.readUnsignedByte() == 1;
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].brightness = stream.readByte();
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].shadowFactor = stream.readByte();
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].effectId = stream.readByte();
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].effectParam1 = stream.readByte();
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].colour = stream.readUnsignedShort();
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].textureSpeedU = stream.readByte();
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].textureSpeedV = stream.readByte();
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].aBool2087 = stream.readUnsignedByte() == 1;
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].isBrickTile = stream.readUnsignedByte() == 1;
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].useMipmaps = stream.readByte();
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].repeatS = stream.readUnsignedByte() == 1;
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].repeatT = stream.readUnsignedByte() == 1;
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].hdr = stream.readUnsignedByte() == 1;
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].combineMode = stream.readUnsignedByte();
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].effectParam2 = stream.readInt();
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].blendType = stream.readUnsignedByte();
        }
    }

    @Endpoint(method="POST", endpoint="/textures/defs/:id")
    public static String getTextureDefinitions(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        if (!NumberUtils.isDigits(request.params(":id")))
            return error("Invalid id. Please try again."+request.params(":id"));
        int id = Integer.parseInt(request.params(":id"));
        TextureDefinitions textureDefinitions = TextureDefinitions.getDefinitions(id);
        if(textureDefinitions == null)
            return error("Unable to load texture details. Please try again.");
        Properties prop = new Properties();
        prop.put("success", true);
        prop.put("defs", Overview.getGson().toJson(textureDefinitions));
        return Website.getGson().toJson(prop);
    }

    @Endpoint(method="GET", endpoint="/material/:id")
    public static String renderMaterialImage(Request request, Response response) {
        try {
            if (!NumberUtils.isDigits(request.params(":id")))
                return error("Invalid id. Please try again.");
            int id = Integer.parseInt(request.params(":id"));
            MaterialDefinitions defs = MaterialDefinitions.getMaterialDefinitions(id);
            if (defs == null)
                return error("Unable to load material. Please try again.");
            TextureDefinitions textureDefinitions = TextureDefinitions.getDefinitions(id);
            if(textureDefinitions == null)
                return error("Unable to load texture details. Please try again.");
            int resolution = textureDefinitions.isHalfSize ? 64 : 128;
            int[] pixels = defs.renderIntPixels((float) 0.7, resolution, resolution, false);
            BufferedImage image = new BufferedImage(resolution, resolution, BufferedImage.TYPE_INT_RGB);
            image.setRGB(0, 0, resolution, resolution, pixels, 0, resolution);
            image.flush();
            try {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                ImageIO.write(image, "png", outStream);
                java.io.InputStream in = new ByteArrayInputStream(outStream.toByteArray());
                @Cleanup OutputStream out = new BufferedOutputStream(response.raw().getOutputStream());
                response.raw().setContentType(MediaType.PNG.toString());
                response.status(200);
                ByteStreams.copy(in, out);
                out.flush();
                return "";
            } catch (FileNotFoundException ex) {
                response.status(404);
                return ex.getMessage();
            } catch (IOException ex) {
                response.status(500);
                return ex.getMessage();
            }
        } catch(Exception e) {
            e.printStackTrace();
            return error("Error loading.");
        }
    }

    public static TextureDefinitions getDefinitions(int id) {
       	if(textures == null)
       		parseTextureDefs();
       	return textures[id];
    }
}
