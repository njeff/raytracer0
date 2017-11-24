# Raytracing in One Weekend, the Next Week, and the Rest of Your Life

This is a Java implementation of the raytracer from the book *Ray Tracing in One Weekend* by Peter Shirley. I added texture mapping for the spheres and light emitters.

Why in Java and not C++? Why go with something slower and more memory hungry? When I followed the first book I was trying to better learn Java and didn't want to copy paste (already knew C++ better then too, but now I'm probably bad at C++). Turns out the lack of operator overloading and pointers also gets really gross. 

The initial raytracer with my modifications is found in the `weekend` folder.
My continuation of the raytracer from the next book, *Ray Tracing: The Next Week*, is found in the folder `week`. I've added triangle objects and STL file loading to render more complicated objects. The bounding volume hierarchies are really nice and are necessary for high polygon count STLs. I still need to find ways to make this raytracer more efficient and am reading up more on other raytracers and lighting algorithms.

I've just gotten the third book in the series. Time to read that along with the other articles I've been trawling.

I'm currently working on implementing the Cook-Torrance BSDF. Running into some issues though, but I think I have the specualr component correct.

The cube, magnolia, sphere, and teapot models in the `objects` folder are from [this site](http://people.sc.fsu.edu/~jburkardt/data/stla/stla.html). The [Pokeball](https://grabcad.com/library/pokemon-with-magnemite-1) and [Turners Cube](https://grabcad.com/library/turners-cube-6) are from GrabCad. Other teapot from [here](https://www.thingiverse.com/thing:17897). The earth and moon textures in `textures` were the first ones that showed up when I Googled.

## How to use this program
The file `Tracer.java` has the `main` for this raytracer. You can set the x and y resolution of the output with `nx` and `ny` and the number of samples per pixel with `ns`. Set `world` to a `HittableList` of the objects you want to render and `cam` to the camera you want to render the scene from. The `MAX_DEPTH` value sets the max recursion depth for the `color` method. After about 5-10 bounces most images don't change very much so don't set this too high (especially with many mirror surfaces). When working with light sources and a black background (background emits no light), the number of samples per pixel needed to get a fairly clean image is usually around 10,000. However, this is very computationally intense and can take quite a bit of time to fully render.

During the rendering process, the intermediate image is displayed in a `DrawingPanel` (which was borrowed from APCS) one row at a time. You can save the output image from here or just use `ImageIO.save` to save the `BufferedImage` in the program.

The file `AccelTester.java` can be used to test if two scenes have the same intersection properties. This is useful for testing if the implementation of acceleration structures like the BVH actually work. It generates random rays in the bounding box of the scene, and checks that they have the same intersection properties.

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
  <br>
  <img src="https://github.com/njeff/raytracer0/blob/master/samples/pokeball2.png" alt="Pokeball." width="700px">
</p>