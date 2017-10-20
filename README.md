# Raytracing in One Weekend and the Next Week

This is a Java implementation of the raytracer from the book *Ray Tracing in One Weekend* by Peter Shirley. I added texture mapping for the spheres and light emitters.

Why in Java? When I followed the first book I was trying to better learn Java and didn't want to copy paste (already knew C++ better too). Turns out the lack of operator overloading and pointers also gets really gross. 

The initial raytracer with my modifications is found in the `weekend` folder.
My continuation of the raytracer from the next book, *Ray Tracing: The Next Week*, is found in the folder `week`. I've added triangle objects and ASCII STL file loading to render more complicated objects. I think the bounding volume hierarchies are working, but the bounding box code for the `Rotate` Hittable is not right. I get different, usually incorrect renders each time I run a scene with it.

The models in the `objects` folder are from ![this site](http://people.sc.fsu.edu/~jburkardt/data/stla/stla.html). The earth and moon textures in `textures` were the first ones that showed up when I Googled.

## How to use this program
The file `Tracer.java` has the `main` for this raytracer. You can set the x and y resolution of the output with `nx` and `ny` and the number of samples per pixel with `ns`. Set `world` to a `HittableList` of the objects you want to render and `cam` to the camera you want to render the scene from. Inside the `color` method you can change the max recursion depth, but after about 5-10 bounces most images don't change very much. When working with light sources and a black background (background emits no light), the number of samples per pixel needed to get a fairly clean image is usually around 10,000. However, this is very computationally intense and can take quite a bit of time to fully render.

During the rendering process, the intermediate image is displayed in a `DrawingPanel` (which was borrowed from APCS) one row at a time. You can save the output image from here or just use `ImageIO.save` to save the `BufferedImage` in the program.

`TracerV.java` was used to render a video using the first version of the raytracer. The camera was moved as a function of time and each rendered frame was then put together into a final video using ImageMagick. There is an example below.

## Samples from the first raytracer (and my additions)
<p align="center">
  <img src="https://github.com/njeff/raytracer0/blob/master/samples/output.png" alt="Book cover." width="700px"/>
  <br>
  <img src="https://github.com/njeff/raytracer0/blob/master/samples/output_box5.png" alt="Cornell box." width="700px"/>
  <br>
  <img src="https://github.com/njeff/raytracer0/blob/master/samples/0053rot.jpg" alt="Texture map." width="700px"/>
  <br>
  <img src="https://github.com/njeff/raytracer0/blob/master/samples/rotate.gif" alt="Video." width="480px"/>
</p>

## Samples from the next week raytracer
<p align="center">
  <img src="https://github.com/njeff/raytracer0/blob/master/samples/cornell_box.png" alt="Cornell box." width="700px"/>
  <br>
  <img src="https://github.com/njeff/raytracer0/blob/master/samples/cornell_volume2.png" alt="Volumes." width="700px"/>
  <br>
  <img src="https://github.com/njeff/raytracer0/blob/master/samples/box_scene.png" alt="A bit of everything." width="700px"/>
  <br>
  <img src="https://github.com/njeff/raytracer0/blob/master/samples/pot_metal.png" alt="STL sample." width="700px">
</p>