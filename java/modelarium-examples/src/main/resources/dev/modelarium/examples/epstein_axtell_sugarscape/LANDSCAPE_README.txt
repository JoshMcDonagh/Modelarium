Sugarscape capacity-map provenance
=================================

Epstein & Axtell (1996) specify the canonical 50x50 Sugarscape as a torus with two resource mountains, capacities
0..4, a valley between the peaks and surrounding sugarless desert, but publish the terrain as a figure rather than
as a machine-readable data file. Later implementations therefore differ slightly in their extracted/reconstructed
maps.

The sugar-map.txt distributed with this Modelarium example is an independent radial-contour reconstruction rather
than a copied map from another simulation toolkit. Its two peaks are centred at (15,35) and (35,15). The nearest-peak
toroidal Euclidean distance determines capacity as follows:

  distance <=  6 : 4
  distance <= 11 : 3
  distance <= 16 : 2
  distance <= 19 : 1
  otherwise      : 0

This produces a twin-peaked landscape with the same qualitative geography and very similar capacity-region sizes to
common reconstructions, while making the exact terrain used by the benchmark explicit and reproducible.
