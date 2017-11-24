## Notes
Reading many articles here: 
https://www.scratchapixel.com/index.php
https://www.ics.uci.edu/~gopi/CS211B/RayTracing%20tutorial.pdf
https://github.com/mmp/pbrt-v3/tree/master/src/core

I'm trying to figure out more of the math behind all of this, especially in BRDFs. Also into how to speed this up. Maybe I'll write another raytracer (in C++) just to explore different rendering methods. Also am looking at the Physically Based Rendering book.

Can I modify the hemisphere distribution for plastic like objects (for specular)? However, the specular shouldn't be tinted with the diffuse albedo so maybe the specular can go into the emitted so it is added(which doesn't seem right either).

Can I also vary the number of samples per pixel so that pixels that are on average darker get more samples to reduce noise in those regions faster? Points that easily access light sources should require less sampling (I'm just thinking about this after looking at biased raytracing; not sure if this really works.).

Now reading the third book:
The equations are now making a ton more sense. From other sites (the scratchapixel website), I've been looking at how monte carlo integration works and saw the summing equation that get us the approximation of an integral. We we sample with a non-uniform distribution (e.g. sample more frequently in directions that contribute more), we must divide by the probability of sampling in that direction to get the correct integral.

### [Cook-Torrance paper](https://www.cs.cornell.edu/~srm/publications/EGSR07-btdf.pdf):

specular BRDF = DFG/(4*(wo.n)(wi.n))

m in the below is the normal for the microfacet

D(m) is the distribution of microfacets on the surface; units of 1/steradian
G(i,o,m) is the shadowing function-masking function which returns the fraction of microsurfaces with normal m are visible in i and o
F is the fresnel term

### Log
- Trying to fix the BVH, for some reason the Cornell Box scene doesn't render correctly most of the time (see the samples in `samples/buggy`) and triangles in my triangle test scene don't render consistently (sometimes no triangles or only one of the two show up). When I load STL files I also get weird missing triangles that change every time I run the same render. The only randomness could be from choosing which axis to split on.
- When I looked at the two triangle test scene's BVH tree structure during different trials, I noticed that any time the two triangles were children of the same node, they both didn't show up. Only one showes up if one is in a node on its own (occupies both children) and the other is with a sphere. Both show up when they are in their own nodes with a sphere (don't occupy both children).
- Updated the axis splitting algorithm to find the axis that had the largest range of the centroids of the objects in the list. I no longer get missing triangles (still trying to figure out why they disappeared). However, my modified Cornell box still doesn't render correctly with the BVH.
- I wrote a simple scene tester in `AccelTester.java` to see if two scenes have the same intersection properties. It randomly generates rays in the bounding box of the scene and tests to see if they have the same intersections in both scenes. By loading up one scene with the non-accelerated `HittableList` and the other with the `BVHNode`, I can see where any errors could arise.
- Changing the Cornell Box scene to just have the boxes rotated on the y-axis passes the acceleration tester. My modified scene with the box rotated on multiple axes doesn't. Time to fix the `Rotate` class's bounding boxes.
- Turns out I had some wrong signs in the rotation transform. Doh.