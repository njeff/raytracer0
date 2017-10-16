# Raytracing in One Weekend and the Next Week

This is a Java implementation of the raytracer from the book *Ray Tracing in One Weekend* by Peter Shirley. I added texture mapping for the spheres and emitters. I think there is a bug for the reflection/refraction of dielectrics but I'm not sure.

Why in Java? When I followed the first book I was trying to better learn Java and didn't want to copy paste (already knew C++ better too). Turns out the lack of operator overloading and pass by reference also gets really gross. 

The initial raytracer with my modifications is found in the `weekend` folder.
I'm currently implementing the continuation of the ray tracer in the next book, *Ray Tracing: The Next Week*, in the folder `week`. I've added triangle objects and ASCII STL file loading to render more complicated objects.

The models in the `objects` folder are from ![this site](http://people.sc.fsu.edu/~jburkardt/data/stla/stla.html).

## Samples from the first raytracer (and my additions)
<p align="center">
  <img src="https://github.com/njeff/raytracer0/blob/master/samples/output.png" alt="Book cover." width="700px"/>
  <br>
  <img src="https://github.com/njeff/raytracer0/blob/master/samples/output_box5.png" alt="Cornell box." width="700px"/>
  <br>
  <img src="https://github.com/njeff/raytracer0/blob/master/samples/0053rot.jpg" alt="Texture map." width="700px"/>
</p>

## Samples from the next week raytracer
<p align="center">
  <img src="https://github.com/njeff/raytracer0/blob/master/samples/cornell_box.png" width="700px"/>
  <img src="https://github.com/njeff/raytracer0/blob/master/samples/cornell_volume2.png" width="700px"/>
  <img src="https://github.com/njeff/raytracer0/blob/master/samples/box_scene.png" width="700px"/>
  <img src="https://github.com/njeff/raytracer0/blob/master/samples/pot_metal.png" width="700px">
</p>