import urllib.request
import sys
import matplotlib.pyplot as plt
import HelperFunc
import cv2




# Download desired image from web server
def downloadImage(quadrantKey,column):
    url = 'http://h0.ortho.tiles.virtualearth.net/tiles/h' + quadrantKey + '.jpeg?g=131'
    print("Quad Key: " + quadrantKey + "\t| Tile URL: " + url)
    try:
        file,header = urllib.request.urlretrieve(url)
        image = cv2.imread(file)
        if isNullImage(image):
            print("Image doesn't exist at this level")
            return False
        else:
            column.append(image)
            return True
    except Exception as e:
        print(' Exception Thrown while downloading Image : '+e)
        return False


def isNullImage(image):
    nullObj = cv2.imread('null.jpg')
    if nullObj.shape == image.shape:
        difference = cv2.subtract(nullObj, image)
        b, g, r = cv2.split(difference)
        if cv2.countNonZero(b) == 0 and cv2.countNonZero(g) == 0 and cv2.countNonZero(r) == 0:
            return True
    return False



# This function will get neighbouring tiles,
# download them in a folder, renaming using hash function,
# and display them
def getBoundingTileImages(tx1, ty1, tx2, ty2, LoD):
    tup = []
    for y in range(ty1, ty2 + 1):
        col = []
        for x in range(tx1, tx2 + 1):
            print ("Bounding tiles: %s, %s " % (x, y))
            quadKey = HelperFunc.tileXYToQuadKey(x, y, LoD)
            # Hash the file name for convenience in stitching
            if not downloadImage(quadKey,col):
                return tup,False

        tup.append(col)
    print("Best detail found at Level: %s" % (LoD))
    return tup, True


def validateCoordinates(latitude1, longititude1, latitude2, longitude2, maximumLoad):
    for i in range(maximumLoad, 0, -1):
        print("Co-ordinates of first location : ")
        tiles_x1, tiles_y1 = HelperFunc.latLongToTiles(latitude1, longititude1, i)
        print("Co-ordinates of second location : ")
        tiles_x2, tiles_y2 = HelperFunc.latLongToTiles(latitude2, longitude2, i)
        print("\n")

        if tiles_y1 > tiles_y2:
            tiles_y1, tiles_y2 = tiles_y2, tiles_y1

        if tiles_x1 > tiles_x2:
            tiles_x1, tiles_x2 = tiles_x2, tiles_x1

        if (tiles_x2 - tiles_x1 >= 1) and (tiles_y2 - tiles_y1 >= 1):
            return tiles_x1, tiles_y1, tiles_x2, tiles_y2, i

    print ("Program Interruption: Coordinates are on the same tiles...No image will be downloaded/n")
    sys.exit()

def stitchImages(tup):
    im_tile_resize = cv2.vconcat([cv2.hconcat(im_list_h) for im_list_h in tup])
    cv2.imwrite('output.jpg', im_tile_resize)
    return im_tile_resize

def main(latitude1, longititude1, latitude2, longitude2):
    maximumLoad = 23
    found = False
    while (not found):
        tx1, ty1, tx2, ty2, LoD = validateCoordinates(latitude1, longititude1, latitude2, longitude2, maximumLoad)
        tup, found = getBoundingTileImages(tx1, ty1, tx2, ty2, LoD)
        maximumLoad = maximumLoad - 1
        if maximumLoad < 0:
            break

            # Stitch from directory
    fs = stitchImages(tup)
    print("Bounding of images is complete. Displaying image...")
    plt.imshow(fs)
    plt.show()

#main(48.859261, 2.293380, 48.856953, 2.296294) #Effiel Tower
#main(41.839128, -87.628503, 41.838744, -87.626847)  # IIT Stuart Building Coordinates
main(41.882981, -87.623496, 41.882397, -87.623076)	# Chicago Bean
#main(41.892026, -87.608168, 41.891387, -87.606156)  # Chicago Navy Pier
#main(40.690135, -74.045679, 40.688524, -74.043366)  # Statue of Liberty, NY