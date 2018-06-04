from io import open
from os import environ, path
from argparse import ArgumentParser
from google.cloud import vision
from google.cloud.vision import types

environ["GOOGLE_APPLICATION_CREDENTIALS"] = "C:/google_vision_key/middleware-e5ce290ef922.json"

# Instantiates a client
client = vision.ImageAnnotatorClient()

# Get file name
parser = ArgumentParser()
parser.add_argument("name", help="Type name of the file inside images folder")
args = parser.parse_args()
image_path = path.join('images', args.name) + '.jpg'
print("\nReading ", image_path)

with open(image_path, 'rb') as image_file:
    content = image_file.read()

image = types.Image(content=content)

# Performs label detection on the image file
response = client.text_detection(image=image)
texts = response.text_annotations

# print('Extracted Content:\n', texts[0].description)
text = texts[0].description

if 'walmart' in args.name.lower():
    print('\nIt is a Walmart Item')
    text_list = text.splitlines()
    print('Item Name: ', text_list.pop(0))
    price = [item for item in text_list if '$' in item]
    print('Price: ', price[0])

elif 'tj' in args.name.lower():
    print('Extracted Content:\n', texts[0].description)

elif 'target' in args.name.lower():
    print('Extracted Content:\n', texts[0].description)