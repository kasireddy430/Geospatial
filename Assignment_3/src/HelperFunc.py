import math

EarthRadius = 6378137
minimum_latitude = -85.05112878
maximum_latitude = 85.05112878
MinLongitude = -180
MaxLongitude = 180
screenDpi = 1024



def clip(n, minimumValue, maximumValue):
    return min(max(n, minimumValue), maximumValue)

def mapSize(detailLevel):
    return 256 << detailLevel

def groundResolution(lat, levelDetail):
    latitude = clip(lat, minimum_latitude, maximum_latitude)
    return math.Cos(latitude * math.PI / 180) * 2 * math.PI * EarthRadius / mapSize(levelDetail);

def mapScale(latitude, levelDetail, screenResolutinDPI):
    return groundResolution(latitude, levelDetail) * screenResolutinDPI / 0.0254

def latLongToPixelXY(cal_latitude, cal_longitude, levelDetails):
    cal_latitude = clip(cal_latitude, minimum_latitude, maximum_latitude)
    cal_longitude = clip(cal_longitude, MinLongitude, MaxLongitude)

    x = (cal_longitude + 180) / 360
    sin_latitude = math.sin(cal_latitude * math.pi / 180)
    y = 0.5 - math.log((1 + sin_latitude) / (1 - sin_latitude)) / (4 * math.pi)

    map_size = mapSize(levelDetails)
    pixel_x = clip(x * map_size + 0.5, 0, map_size - 1)
    pixel_y = clip(y * map_size + 0.5, 0, map_size - 1)

    return pixel_x, pixel_y


def pixelXYToLatLong(pixel_Xaxis, pixel_Yaxis, levelDetail):
    mapsize = mapSize(levelDetail)
    x = (clip(pixel_Xaxis, 0, mapsize - 1) / mapsize) - 0.5
    y = 0.5 - (clip(pixel_Yaxis, 0, mapsize - 1) / mapsize)

    latitude = 90 - 360 * math.atan(math.exp(-y * 2 * math.pi)) / math.pi
    longitude = 360 * x

    return latitude, longitude

def pixelXYToTileXY(pixel_Xaxis, pixel_Yaxis):
    tile_Xaxis = int(pixel_Xaxis / 256)
    tile_Yaxis = int(pixel_Yaxis / 256)

    return tile_Xaxis, tile_Yaxis

def tileXYToPixelXY(tileX, tileY):
    pixelX = tileX * 256
    pixelY = tileY * 256

    return pixelX, pixelY


def tileXYToQuadKey(tile_Xaxis, tile_Yaxis, levelDetail):
    quad = ""
    for i in range(levelDetail, 0, -1):
        digit = '0'
        mask = (1 << (i - 1))

        if (tile_Xaxis & mask) != 0:
            digit = chr(ord(digit) + 1)

        if (tile_Yaxis & mask) != 0:
            digit = chr(ord(digit) + 1)
            digit = chr(ord(digit) + 1)
        quad += digit
    return quad


def latLongToQuad(lat, longi, levelDetail):
    print ("\n")
    print ("Level Details : %s" % (levelDetail))
    print ("Latitude, Longitude: %s, %s " % (lat, longi))

    pixel_Xaxis, pixel_Yaxis = latLongToPixelXY(lat, longi, levelDetail)
    print ("Pixels: %s, %s " % (pixel_Xaxis, pixel_Yaxis))
    tile_Xaxis, tile_Yaxis = pixelXYToTileXY(pixel_Xaxis, pixel_Yaxis)
    print ("Tiles: %s, %s " % (tile_Xaxis, tile_Yaxis))
    quadrant_Key = tileXYToQuadKey(tile_Xaxis, tile_Yaxis, levelDetail)
    print ("Generated Quad: %s " % (quadrant_Key))
    return str(quadrant_Key)


def latLongToTiles(lat, longi, levelDetail):
    print ("\n")
    print ("Level Detail : %s" % (levelDetail))
    print ("Latitude, Longitude: %s, %s " % (lat, longi))
    pixel_Xaxis, pixel_Yaxis = latLongToPixelXY(lat, longi, levelDetail)
    print ("Pixels: %s, %s " % (pixel_Xaxis, pixel_Yaxis))
    tile_Xaxis, tile_Yaxis = pixelXYToTileXY(pixel_Xaxis, pixel_Yaxis)
    print ("Tiles: %s, %s " % (tile_Xaxis, tile_Yaxis))
    return tile_Xaxis, tile_Yaxis


