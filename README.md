# Text Fragment Stitching Algorithm

This is somewhat like gene sequencing, in that you have many fragments of copies of a document (an analogue for DNA), and you use repeated overlaps to reconstitute the sequence. DNA sequencing, on the other hand, usually involves a more complex algorithm, with some extra heuristics.

Not the most efficient solution, to do that would probably need to incorporate something like a graph data-structure and potentially the process would involve traversing it and updating edges as merges occur. Maybe I'll get round to that. This approach fairly nicely sidesteps potentially cyclic matches, or rather cyclic ambiguities. In a graph-based solution, handling the cyclic cases will need some finessing.

In this algorithm, the implicit graph edges are given a weight which is the overlap between pairs of strings. Briefly reading about DNA sequencing suggests that when the graph is constructed it is decorated with more data.

## Copyright

Copyright (c) 2026 Sean Davies. All rights reserved. 
Provided for viewing purposes only. No usage, modification, or distribution rights are granted.