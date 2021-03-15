package com.cryo.cache.tools.mapdumper;

import com.cryo.cache.Cache;
import com.cryo.cache.IndexType;
import com.cryo.cache.loaders.*;
import com.cryo.cache.region.Location;
import com.cryo.cache.region.Region;
import com.cryo.cache.store.Index;
import com.cryo.cache.store.ReferenceTable;
import com.cryo.cache.store.Store;
import com.cryo.entities.annotations.Endpoint;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.utils.BigBufferedImage;
import com.cryo.utils.MapArchiveKeys;
import com.google.common.io.ByteStreams;
import com.google.common.net.MediaType;
import lombok.Cleanup;
import lombok.Getter;
import org.apache.commons.lang3.math.NumberUtils;
import spark.Request;
import spark.Response;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.cryo.utils.Utilities.error;

@EndpointSubscriber
public class MapImageDumper {

    private static int PIXELS_PER_TILE = 4;

    private final List<Integer> flags = new ArrayList<>();
    private Region region;
    private Store store;

    private BufferedImage baseImage;
    @Getter
    private BufferedImage fullImage;

    private int x, y, plane;

    public MapImageDumper(int x, int y, int plane) {
        this.x = x;
        this.y = y;
        this.plane = plane;
        region = new Region(x, y, plane);
        store = Cache.STORE;
    }

    public void dumpMapImage() {

        List<Integer> flags = new ArrayList<>();

        int map = store.getIndex(IndexType.MAPS).getArchiveId(region.getTerrainIdentifier());
        int loc = store.getIndex(IndexType.MAPS).getArchiveId(region.getLocationsIdentifier());

        if (map == -1 && loc == -1) {
            System.out.println("Error loading map data. map&loc=-1");
            return;
        }

        if (map != -1)
            region.loadTerrain(ByteBuffer.wrap(store.getIndex(IndexType.MAPS).getFile(map, 0)));

        if (loc != -1) {
            byte[] data = store.getIndex(IndexType.MAPS).getFile(loc, 0, MapArchiveKeys.getMapKeys(region.getRegionID()));
            try {
                region.loadLocations(ByteBuffer.wrap(data));
            } catch (Exception e) {
                System.out.println("Error loading map data. map&loc=-1");
                return;
            }
        }

        draw();

    }

    private void draw() {

        int dimX = Region.WIDTH;
        int dimY = Region.HEIGHT;

        int boundX = dimX - 1;
        int boundY = dimY - 1;

        dimX *= PIXELS_PER_TILE;
        dimY *= PIXELS_PER_TILE;

        baseImage = BigBufferedImage.create(dimX, dimY, BufferedImage.TYPE_INT_RGB);
        fullImage = BigBufferedImage.create(dimX, dimY, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = fullImage.createGraphics();

        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        drawUnderlay();
        blendUnderlay(boundX, boundY);
        drawOverlay();
        drawLocations(graphics);
        drawWalls(graphics);
        drawIcons(graphics);
        markLocation(graphics);

        graphics.dispose();

        try {
            ImageIO.write(baseImage, "png", new File("base_image_" + plane + ".png"));
            ImageIO.write(fullImage, "png", new File("full_image_" + plane + ".png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void drawUnderlay() {

        for (int x = 0; x < Region.WIDTH; ++x) {
            for (int y = 0; y < Region.HEIGHT; ++y) {

                int drawY = ((Region.HEIGHT - 1) - y);

                int underlayId = region.getUnderlayId(plane, x, y) - 1;

                int rgb = Color.CYAN.getRGB();

                if (underlayId > -1) {

                    UnderlayDefinitions defs = UnderlayDefinitions.getUnderlayDefinitions(underlayId);
                    rgb = defs.rgb;
                }

                drawMapSquare(baseImage, x, drawY, rgb, -1, -1);
            }
        }

    }

    private void blendUnderlay(int boundX, int boundY) {
        try {
            for (int x = 0; x < Region.WIDTH; ++x) {
                for (int y = 0; y < Region.HEIGHT; ++y) {
                    int drawY = (Region.HEIGHT - 1) - y;

                    Color c = getMapSquare(baseImage, x, drawY);

                    if (c.equals(Color.CYAN)) continue;

                    int tRed = 0, tGreen = 0, tBlue = 0;
                    int count = 0;

                    int maxDY = Math.min(boundY, drawY + 3);
                    int maxDX = Math.min(boundX, x + 3);
                    int minDY = Math.max(0, drawY - 3);
                    int minDX = Math.max(0, x - 3);


                    for (int dy = minDY; dy < maxDY; dy++) {
                        for (int dx = minDX; dx < maxDX; dx++) {
                            c = getMapSquare(baseImage, dx, dy);

                            if (c == null) {
                                System.out.println("NULL COLOR");
                                continue;
                            }

                            if (c.equals(Color.CYAN)) {
                                continue;
                            }

                            tRed += c.getRed();
                            tGreen += c.getGreen();
                            tBlue += c.getBlue();
                            count++;
                        }
                    }

                    if (count > 0) {
                        c = new Color(tRed / count, tGreen / count, tBlue / count);
                        drawMapSquare(fullImage, x, drawY, c.getRGB(), -1, -1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawOverlay() {
        for(int x = 0; x < Region.WIDTH; ++x) {
            for(int y = 0; y < Region.HEIGHT; ++y) {
                int drawY = (Region.HEIGHT-1) - y;
                if (plane == 0 || (!region.isLinkedBelow(plane, x, y) && !region.isVisibleBelow(plane, x, y))) {
                    int overlayId = region.getOverlayId(plane, x, y) - 1;
                    if (overlayId > -1) {
                        int rgb = getOverLayColour(overlayId);
                        drawMapSquare(fullImage, x, drawY, rgb, region.getOverlayPath(plane, x, y), region.getOverlayRotation(plane, x, y));
                    }
                }

                if (plane < 3 && (region.isLinkedBelow(plane + 1, x, y) || region.isVisibleBelow(plane + 1, x, y))) {
                    int overlayAboveId = region.getOverlayId(plane + 1, x, y) - 1;
                    if (overlayAboveId > -1) {
                        int rgb = getOverLayColour(overlayAboveId);
                        drawMapSquare(fullImage, x, drawY, rgb, region.getOverlayPath(plane + 1, x, y), region.getOverlayRotation(plane + 1, x, y));
                    }
                }
            }
        }
    }

    private void drawLocations(Graphics2D graphics) {
        for(Location location : region.getLocations()) {
            int localX = location.getPosition().getX() - region.getBaseX();
            int localY = location.getPosition().getY() - region.getBaseY();

            if (!canDrawLocation(region, location, plane, localX, localY)) {
                continue;
            }

            ObjectDefinitions defs = ObjectDefinitions.getDefs(location.getId());

            int drawY = (Region.HEIGHT - 1) - localY;

            if(defs.mapSpriteId != -1) {
                int spriteId = MapSpriteDefinitions.getMapSpriteDefinitions(defs.mapSpriteId).spriteId;
                if (spriteId == -1)
                    continue;
                SpriteDefinitions spriteDefinitions = SpriteDefinitions.getSprite(spriteId, 0);
                if(spriteDefinitions == null)
                    continue;
                Image spriteImage = spriteDefinitions.image;
                graphics.drawImage(spriteImage, localX * PIXELS_PER_TILE, drawY * PIXELS_PER_TILE, null);
            }
        }
    }

    private void drawWalls(Graphics2D graphics) {
        for(Location location : region.getLocations()) {
            graphics.setColor(Color.WHITE);

            int localX = location.getPosition().getX() - region.getBaseX();
            int localY = location.getPosition().getY() - region.getBaseY();

            if (!canDrawLocation(region, location, plane, localX, localY))
                continue;

            ObjectDefinitions defs = ObjectDefinitions.getDefs(location.getId());

            if(defs.mapSpriteId == 22)
                continue;

            String name = defs.getName().toLowerCase();

            if(name.contains("door") || name.contains("gate"))
                graphics.setColor(Color.red);

            int drawX = localX;
            int drawY = (Region.HEIGHT - 1) - localY;

            drawX *= PIXELS_PER_TILE;
            drawY *= PIXELS_PER_TILE;

            if (location.getType() == 0) { // Straight walls
                if (location.getOrientation() == 0) { // West
                    graphics.drawLine(drawX, drawY, drawX, drawY + PIXELS_PER_TILE);
                } else if (location.getOrientation() == 1) { // South
                    graphics.drawLine(drawX, drawY, drawX + PIXELS_PER_TILE, drawY);
                } else if (location.getOrientation() == 2) { // East
                    graphics.drawLine(drawX + PIXELS_PER_TILE, drawY, drawX + PIXELS_PER_TILE, drawY + PIXELS_PER_TILE);
                } else if (location.getOrientation() == 3) { // North
                    graphics.drawLine(drawX, drawY + PIXELS_PER_TILE, drawX + PIXELS_PER_TILE, drawY + PIXELS_PER_TILE);
                }
            } else if (location.getType() == 2) { // Corner walls
                if (location.getOrientation() == 0) { // West & South
                    graphics.drawLine(drawX, drawY, drawX, drawY + PIXELS_PER_TILE);
                    graphics.drawLine(drawX, drawY, drawX + PIXELS_PER_TILE, drawY);
                } else if (location.getOrientation() == 1) { // South & East
                    graphics.drawLine(drawX, drawY, drawX + PIXELS_PER_TILE, drawY);
                    graphics.drawLine(drawX + PIXELS_PER_TILE, drawY, drawX + PIXELS_PER_TILE, drawY + PIXELS_PER_TILE);
                } else if (location.getOrientation() == 2) { // East & North
                    graphics.drawLine(drawX + PIXELS_PER_TILE, drawY, drawX + PIXELS_PER_TILE, drawY + PIXELS_PER_TILE);
                    graphics.drawLine(drawX, drawY + PIXELS_PER_TILE, drawX + PIXELS_PER_TILE, drawY + PIXELS_PER_TILE);
                } else if (location.getOrientation() == 3) { // North & West
                    graphics.drawLine(drawX, drawY + PIXELS_PER_TILE, drawX + PIXELS_PER_TILE, drawY + PIXELS_PER_TILE);
                    graphics.drawLine(drawX, drawY, drawX, drawY + PIXELS_PER_TILE);
                }
            } else if (location.getType() == 3) { // Single points
                if (location.getOrientation() == 0) { // West
                    graphics.drawLine(drawX, drawY + 1, drawX, drawY + 1);
                } else if (location.getOrientation() == 1) { // South
                    graphics.drawLine(drawX + 3, drawY + 1, drawX + 3, drawY + 1);
                } else if (location.getOrientation() == 2) { // East
                    graphics.drawLine(drawX + 3, drawY + 4, drawX + 3, drawY + 4);
                } else if (location.getOrientation() == 3) { // North
                    graphics.drawLine(drawX, drawY + 3, drawX, drawY + 3);
                }
            } else if (location.getType() == 9) { // Diagonal walls
                if (location.getOrientation() == 0 || location.getOrientation() == 2) { // West or East
                    graphics.drawLine(drawX, drawY + PIXELS_PER_TILE, drawX + PIXELS_PER_TILE, drawY);
                } else if (location.getOrientation() == 1 || location.getOrientation() == 3) { // South or South
                    graphics.drawLine(drawX, drawY, drawX + PIXELS_PER_TILE, drawY + PIXELS_PER_TILE);
                }
            }
        }
    }

    private void drawIcons(Graphics2D graphics) {
        for (Location location : region.getLocations()) {
            int localX = location.getPosition().getX() - region.getBaseX();
            int localY = location.getPosition().getY() - region.getBaseY();

            if (!canDrawLocation(region, location, plane, localX, localY))
                continue;

            ObjectDefinitions defs = ObjectDefinitions.getDefs(location.getId());

            int drawY = (63 - localY);

            if(defs.mapIcon != -1) {
                AreaDefinitions area = AreaDefinitions.getDefinitions(defs.mapIcon);
                if(area.spriteId != -1) {
                    SpriteDefinitions spriteDefinitions = SpriteDefinitions.getSprite(area.spriteId, 0);
                    if(spriteDefinitions == null)
                        continue;
                    Image spriteImage = spriteDefinitions.image;
                    graphics.drawImage(spriteImage, (localX - 1) * PIXELS_PER_TILE, (drawY - 1) * PIXELS_PER_TILE, null);
                }
            }
        }
    }

    private void markLocation(Graphics2D graphics) {
        graphics.setColor(Color.red);

        int localX = x - region.getBaseX();
        int localY = y - region.getBaseY();

        int drawY = 63 - localY;

        graphics.setStroke(new BasicStroke(5));

        graphics.drawLine(((localX - 1) * PIXELS_PER_TILE) - 7, ((drawY - 1) * PIXELS_PER_TILE) - 7, ((localX - 1) * PIXELS_PER_TILE) + 7, ((drawY - 1) * PIXELS_PER_TILE) + 7);
        graphics.drawLine(((localX - 1) * PIXELS_PER_TILE) + 7, ((drawY - 1) * PIXELS_PER_TILE) - 7, ((localX - 1) * PIXELS_PER_TILE) - 7, ((drawY - 1) * PIXELS_PER_TILE) + 7);
    }

    private boolean canDrawLocation(Region region, Location location, int z, int x, int y) {
        if (region.isLinkedBelow(z, x, y) || region.isVisibleBelow(z, x, y)) {
            return false;
        }
        if (location.getPosition().getHeight() == z + 1 && (region.isLinkedBelow(z + 1, x, y) || region.isVisibleBelow(z + 1, x, y))) {
            return true;
        }
        return z == location.getPosition().getHeight();
    }

    private void drawMapSquare(BufferedImage image, int x, int y, int overlayRGB, int shape, int rotation) {
        if (shape > -1) {
            int[] shapeMatrix = MapConstants.TILE_SHAPES[shape];
            int[] rotationMatrix = MapConstants.TILE_ROTATIONS[rotation & 0x3];
            int shapeIndex = 0;
            for (int tilePixelY = 0; tilePixelY < PIXELS_PER_TILE; tilePixelY++) {
                for (int tilePixelX = 0; tilePixelX < PIXELS_PER_TILE; tilePixelX++) {
                    int drawx = x * PIXELS_PER_TILE + tilePixelX;
                    int drawy = y * PIXELS_PER_TILE + tilePixelY;

                    if (shapeMatrix[rotationMatrix[shapeIndex++]] != 0) {
                        image.setRGB(drawx, drawy, overlayRGB);
                    }
                }
            }
        } else {
            for (int tilePixelY = 0; tilePixelY < PIXELS_PER_TILE; tilePixelY++) {
                for (int tilePixelX = 0; tilePixelX < PIXELS_PER_TILE; tilePixelX++) {
                    int drawx = x * PIXELS_PER_TILE + tilePixelX;
                    int drawy = y * PIXELS_PER_TILE + tilePixelY;
                    image.setRGB(drawx, drawy, overlayRGB);
                }
            }
        }
    }

    public Color getMapSquare(BufferedImage image, int x, int y) {
        x *= PIXELS_PER_TILE;
        y *= PIXELS_PER_TILE;

        return new Color(image.getRGB(x, y));
    }

    private int getOverLayColour(int overlayID) {
        OverlayDefinitions defs = OverlayDefinitions.getOverlayDefinitions(overlayID);
        return defs.getOverlayRGB();
    }

    @Endpoint(method = "GET", endpoint = "/map")
    public static String viewImage(Request request, Response response) {
        if(!request.queryParams().contains("x") || !NumberUtils.isDigits(request.queryParams("x")))
            return error("Invalid x value. Please try again.");
        if(!request.queryParams().contains("y") || !NumberUtils.isDigits(request.queryParams("y")))
            return error("Invalid y value. Please try again.");
        if(!request.queryParams().contains("plane") || !NumberUtils.isDigits(request.queryParams("plane")))
            return error("Invalid plane value. Please try again.");
        int x = Integer.parseInt(request.queryParams("x"));
        int y = Integer.parseInt(request.queryParams("y"));
        int plane = Integer.parseInt(request.queryParams("plane"));
        MapImageDumper dumper = new MapImageDumper(x, y, plane);
        dumper.dumpMapImage();
        BufferedImage image = dumper.getFullImage();
        try {

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outStream);
            InputStream in = new ByteArrayInputStream(outStream.toByteArray());
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
    }
}
