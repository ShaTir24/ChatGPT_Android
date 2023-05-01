import cv2
import pytesseract
from PIL import Image
from matplotlib import pyplot as plt
import numpy as np
pytesseract.pytesseract.tesseract_cmd = "C:/Program Files/Tesseract-OCR/tesseract.exe"

def main(path):

    img = cv2.imread(path)

    def ocr_core(img):
        text = pytesseract.image_to_string(img)
        return text

    #print(ocr_core(img))

    #inverting image color
    def get_inverted(img):
        return cv2.bitwise_not(img)

    # get the grayscale image
    def get_grayscale(img):
        return cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    
    #checking for white background
    def is_whiteBack(img):
        thresh = cv2.threshold(img, 240, 255, cv2.THRESH_BINARY)[1]
        num_white_pixels = cv2.countNonZero(thresh)
        total_pixels = thresh.shape[0] * thresh.shape[1]
        percent_white = (num_white_pixels / total_pixels) * 100
        if(percent_white > 90):
            return True
        return False
    
    #method to determine threshold automatically
    def otsu_threshold(img):
        hist = cv2.calcHist([img],[0],None,[256],[0,256])    # calculate image histogram
        hist_norm = hist.ravel()/hist.sum()
        Q = hist_norm.cumsum()

        bins = np.arange(256)
        fn_min = np.inf
        thresh = -1
    
        for i in range(1,256):
            p1, p2 = np.hsplit(hist_norm,[i]) 
            # probabilities
            q1, q2 = Q[i],Q[255]-Q[i]                          # cum sum
            b1, b2 = np.hsplit(bins,[i])                       # weights
        
            # Finding means and variances
            m1, m2 = np.sum(p1*b1)/q1, np.sum(p2*b2)/q2
            v1, v2 = np.sum(((b1-m1)**2)*p1)/q1, np.sum(((b2-m2)**2)*p2)/q2
        
            fn = v1*q1 + v2*q2
        
            if fn < fn_min:
                fn_min = fn
                thresh = i
    
        return thresh

    #setting threshold for binarization
    def get_binarized(img, thresh):
        _, im_bw = cv2.threshold(img, thresh, 255, cv2.THRESH_BINARY)
        return im_bw

    #noise removal (distortion free)
    def remove_noise(img):
        kernel = np.ones((1, 1), np.uint8)
        img = cv2.dilate(img, kernel, iterations=1)
        img = cv2.erode(img, kernel, iterations=1)
        img = cv2.morphologyEx(img, cv2.MORPH_CLOSE, kernel)
        img = cv2.medianBlur(img, 3)
        return (img)
    
    #for erosion of thin fonts
    def thin_font(img):
        img = cv2.bitwise_not(img)
        kernel = np.ones((2,2),np.uint8)
        img = cv2.erode(img, kernel, iterations=1)
        img = cv2.bitwise_not(img)
        return (img)
    
    #for dilation of thick fonts
    def thick_font(img):
        img = cv2.bitwise_not(img)
        kernel = np.ones((2, 2), np.uint8)
        img = cv2.dilate(img, kernel, iterations=1)
        img = cv2.bitwise_not(img)
        return (img)
    
    #removing borders in the image
    def remove_borders(img):
        contours, heiarchy = cv2.findContours(img, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        cntsSorted = sorted(contours, key=lambda x:cv2.contourArea(x))
        cnt = cntsSorted[-1]
        x, y, w, h = cv2.boundingRect(cnt)
        crop = img[y:y+h, x:x+w]
        return (crop)

    img = get_inverted(img)
    img = get_grayscale(img)

    thresh = otsu_threshold(img)
    img = get_binarized(img, thresh)

    img = remove_noise(img)
    img = thin_font(img)
    img = thick_font(img)
    #img = remove_borders(img)


    #print(ocr_core(img))
    return ocr_core(img)

print(main('sample_img.jpg'))